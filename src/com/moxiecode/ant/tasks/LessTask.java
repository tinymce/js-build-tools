package com.moxiecode.ant.tasks;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

public class LessTask extends Task {
	protected String inFile, outFile, script;
	protected boolean compress;

	public void setScript(String file) {
		this.script = file;
	}

	public void setCompress(boolean compress) {
		this.compress = compress;
	}

	public void setInFile(String in_file) {
		this.inFile = in_file;
	}

	public void setOutFile(String out_file) {
		this.outFile = out_file;
	}

	public void execute() throws BuildException {
		String[] args = new String[4];

		args[0] = this.script;
		args[1] = this.compress ? "true" : "false";
		args[2] = this.inFile;
		args[3] = this.outFile;

		org.mozilla.javascript.tools.shell.Main.main(args);
	}
}
