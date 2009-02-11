package com.moxiecode.ant.tasks;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.regex.MatchResult;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

public class PreProcessTask extends Task {
	protected String inFile, outFile;
	protected String defines[];

	public void PreProcess(InputStream in_stream, OutputStream out_stream, String defines[]) throws IOException {
		BufferedReader in = null;
		BufferedWriter out = null;
		String line;
		int currentLevel = 0, removeLevel = -1;

		try {
			in = new BufferedReader(new InputStreamReader(in_stream));
			out = new BufferedWriter(new OutputStreamWriter(out_stream));

			Pattern pattern = Pattern.compile("^\\s*\\/\\/\\s*#(ifdef|ifndef|endif)\\s*(\\w*)$");

			while ((line = in.readLine()) != null) {
				Matcher matcher = pattern.matcher(line);

				if (matcher.matches()) {
					MatchResult res = matcher.toMatchResult();

					// Handle ifdef
					if (res.group(1).equals("ifdef")) {
						String define = res.group(2);
						boolean found = false;

						// Find define
						for (int i = 0; i < defines.length; i++) {
							if (define.equals(defines[i])) {
								found = true;
								break;
							}
						}

						if (removeLevel == -1 && !found)
							removeLevel = currentLevel;

						currentLevel++;

						continue;
					}

					// Handle ifndef
					if (res.group(1).equals("ifndef")) {
						String define = res.group(2);
						boolean found = false;

						// Find define
						for (int i = 0; i < defines.length; i++) {
							if (define.equals(defines[i])) {
								found = true;
								break;
							}
						}

						if (removeLevel == -1 && found)
							removeLevel = currentLevel;

						currentLevel++;

						continue;
					}

					// Handle end if
					if (res.group(1).equals("endif")) {
						currentLevel--;

						if (currentLevel == removeLevel)
							removeLevel = -1;

						continue;
					}
				}

				// Output line
				if (removeLevel == -1 || currentLevel < removeLevel) {
					out.write(line);
					out.newLine();
				}
			}
		} finally {
			if (out != null)
				out.close();

			if (in != null)
				in.close();
		}
	}

	public void execute() throws BuildException {
		try {
			if (this.inFile == null || this.outFile == null || this.defines == null)
				throw new BuildException("Missing required: infile, outfile or defines parameters.");

			this.PreProcess(new FileInputStream(this.inFile), new FileOutputStream(this.outFile), this.defines);
		} catch (IOException ex) {
			throw new BuildException("I/O Error when preprocessing file", ex);
		}
	}

	public void setInFile(String in_file) {
		this.inFile = in_file;
	}

	public void setOutFile(String out_file) {
		this.outFile = out_file;
	}

	public void setDefines(String defines) {
		this.defines = defines.replaceAll("\\s+", "").split(",");
	}
}
