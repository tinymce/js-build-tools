/*

Background:
Using fingerprinting to dynamically enable caching:
For resources that change occasionally, you can have the browser cache the resource
until it changes on the server, at which point the server tells the browser that a 
new version is available. You accomplish this by embedding a fingerprint of the resource 
in its URL (i.e. the file path). When the resource changes, so does its fingerprint, 
and in turn, so does its URL. As soon as the URL changes, the browser is forced to re-fetch 
the resource. Fingerprinting allows you to set expiry dates long into the future even for 
resources that change more frequently than that. Of course, this technique requires that 
all of the pages that reference the resource know about the fingerprinted URL, which may 
or may not be feasible, depending on how your pages are coded.

What this task does:
 This ant task goes through a fileset, through each line of each file, looking for things like:
 <script type ="text/javascript" src= "/scripts/script.js"/>,
 <img alt = "whatever" src="/images/img.gif"></img>
 or
 <link href="style.css"/>
 
 and rewrites them like this
 
 <script type ="text/javascript" src= "/scripts/script.js?83784828"/>,
 <img alt = "whatever" src="/images/img.gif?88273627"></img>
 or
 <link href="style.css?8954739"/>
 
 where the number after ? is the checksum of the corresponding file.
 This solves the caching problem by letting the browser know when a file has changed by changing the url.
 
This code was written by Juan Ignacio Donoso: juan.ignacio@voxound.com & Agustin Feuerhake: agustin@voxound.com
based on other Tasks included in moxiecode js-build-tools, and has an MIT license.
*/

package com.moxiecode.ant.tasks;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.MatchResult;
import java.util.ArrayList;

public class CacheFingerprintTask extends Task {

    private Vector<FileSet> filesets = new Vector<FileSet>();
	protected String buildPath;
	private boolean processJS = true;
	private boolean processIMG = true;
	private boolean processCSS = true;
	private boolean fingerprintIMG = true;
	private String staticServers[] = null;	
	private int serverCount = 0;
	
	public void setBuildPath(String _buildPath) {
		this.buildPath = _buildPath;
	}
	
	public void setProcessJS(boolean _processJS){
		this.processJS = _processJS;
	}
	
	public void setProcessIMG(boolean _fingerprintIMG){
		this.processIMG = _fingerprintIMG;
	}

	public void setProcessCSS(boolean _processCSS){
		this.processCSS = _processCSS;
	}
	
	public void setStaticServers(String _serverNames){
		this.staticServers = _serverNames.replaceAll("\\s+|http:\\/\\/","").split(",");
	}

    public void addFileset(FileSet fileset) {
        filesets.add(fileset);
    }

    protected void validate() {
        if (filesets.size() < 1)
            throw new BuildException("fileset not set");
    }

    public void execute() {
		File srcFile;
		File destFile;
		BufferedReader srcFileBuffer;
		BufferedWriter destFileBuffer;
		FileInputStream srcInputStream;
		FileOutputStream destOutputStream;
		String line;
        validate();
        for (Iterator itFSets = filesets.iterator(); itFSets.hasNext();) {
            FileSet fs = (FileSet) itFSets.next();
            DirectoryScanner ds = fs.getDirectoryScanner(getProject());
            String[] includedFiles = ds.getIncludedFiles();			
            for (int i = 0; i < includedFiles.length; i++) {
                try {
					//Open src file
					ArrayList<String> tempFile = new ArrayList<String>(200);
					String srcFilename = includedFiles[i].replace('\\', '/');
					srcFilename = srcFilename.substring(srcFilename.lastIndexOf("/") + 1);
					srcFile = new File(ds.getBasedir(), includedFiles[i]);
                    log("Scanning " + srcFilename);
					srcInputStream = new FileInputStream(srcFile);
					srcFileBuffer = new BufferedReader(new InputStreamReader(srcInputStream));
					
					Pattern jsPattern = Pattern.compile("^\\s*<(?i:script)\\s+.*\\s+(?i:src)\\s*=\\s*\"([^\"]*)\".*$");
					Pattern imgPattern = Pattern.compile("^\\s*<(?i:img)\\s+.*\\s+(?i:src)\\s*=\\s*\"([^\"]*)\".*$");
					Pattern cssPattern = Pattern.compile("^\\s*<(?i:link)\\s+.*\\s+(?i:href)\\s*=\\s*\"([^\"]*)\".*$");
					
					// Read src file (by lines)
					while ((line = srcFileBuffer.readLine()) != null) {
						if(processJS) line = addFingerprint(jsPattern, line, true);
						if(processIMG) line = addFingerprint(imgPattern, line, true);
						if(processCSS) line = addFingerprint(cssPattern, line, true);
						tempFile.add(line);
					}	
					// Close src file
					srcFileBuffer.close();
					
					// Open destination file
					destFile = new File(ds.getBasedir(), includedFiles[i]);
					destOutputStream = new FileOutputStream(destFile);
					destFileBuffer = new BufferedWriter(new OutputStreamWriter(destOutputStream));
					
					// Write destination file
					for(int j=0;j<tempFile.size();j++){
						destFileBuffer.write((String)tempFile.get(j));
						destFileBuffer.newLine();
					}
					
					// Close destination file
					destFileBuffer.close();
						
						
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }
	
	private String addFingerprint(Pattern _pattern, String _line, boolean _addServer )  {	
		Matcher matcher = _pattern.matcher(_line);
		if (matcher.matches()) {
			MatchResult res = matcher.toMatchResult();
			String refFilename = buildPath + res.group(1);
			File refFile = new File(refFilename);
			if(refFile.exists()){
				String withFingerprint =  res.group(1)+"?" + getChecksum(refFile);
				if(_addServer) withFingerprint = addServer(withFingerprint);
				_line = _line.replaceFirst(res.group(1), withFingerprint);
				log("Added fingerprint: " + withFingerprint);
			}
			else {
				log("Referenced file not found : " + refFilename);
			}
		}
		return _line;
	}
	
	private String addServer(String _path)  {
		if(staticServers != null){
			String currentServer = staticServers[serverCount%staticServers.length];
			log("Server: " + currentServer);
			serverCount++;
			_path = "http://" + currentServer + _path;
		}
		return _path;
	}
	
	private String getChecksum(File _srcFile){
		try {
			// Calculate the CRC-32 checksum of this file
			CheckedInputStream cis = new CheckedInputStream(new FileInputStream(_srcFile), new CRC32());
			byte[] tempBuf = new byte[128];
			while (cis.read(tempBuf) >= 0) {
			}
			Long checksum = cis.getChecksum().getValue();
			return checksum.toString();
		}
		catch (IOException e) {
			e.printStackTrace();
			return "";
		}
	}
}
