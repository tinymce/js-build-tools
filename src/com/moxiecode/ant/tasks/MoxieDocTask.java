package com.moxiecode.ant.tasks;

import java.io.File;
import java.util.Vector;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.FileSet;
import com.moxiecode.moxiedoc.Processor;

public class MoxieDocTask extends Task {
	protected String outDir, templateDir, msIntelliSense;
	protected Vector<FileSet> filesets = new Vector<FileSet>();

	public void addFileSet(FileSet fileset) {
		if (!filesets.contains(fileset))
			filesets.add(fileset);
	}

	public void execute() throws BuildException {
		DirectoryScanner ds;
		File outDir;
		Processor processor = new Processor();
		int secs = (int) System.currentTimeMillis() / 1000;

		// Setup output dir
		outDir = new File(this.outDir);
		if (!outDir.exists())
			outDir.mkdir();

		processor.setOutDir(outDir);
		processor.setTemplateDir(new File(this.templateDir));

		if (this.msIntelliSense != null)
			processor.setMsIntelliSenseFile(new File(this.msIntelliSense));

		for (FileSet fileset : filesets) {
			ds = fileset.getDirectoryScanner(getProject());
			File dir = ds.getBasedir();
			String[] filesInSet = ds.getIncludedFiles();
			for (String filename : filesInSet) {
				File file = new File(dir, filename);

				processor.addFile(file);
			}
		}

		try {
			processor.process();
		} catch (Exception ex) {
			throw new BuildException("Processing of files failed.", ex);
		}

		secs = ((int) System.currentTimeMillis() / 1000) - secs;
		log("Processed " + processor.getProcessedFileCount() + " files in " + secs + " second(s).", Project.MSG_INFO);
	}

	public void setOutDir(String path) {
		this.outDir = path;
	}

	public void setTemplateDir(String path) {
		this.templateDir = path;
	}

	public void setMsIntelliSense(String path) {
		this.msIntelliSense = path;
	}
}
