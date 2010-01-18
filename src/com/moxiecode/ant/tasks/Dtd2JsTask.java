package com.moxiecode.ant.tasks;

import java.io.IOException;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import com.conradroche.matra.exception.DTDException;
import com.moxiecode.dtd2js.ValidElementsParser;

public class Dtd2JsTask extends Task {
	protected String charset = "ISO-8859-1", inFile, outFile, propertiesFile, exclude;
	protected boolean attributes = true;

	public void execute() throws BuildException {
		if (this.inFile == null || this.outFile == null)
			throw new BuildException("Missing required: infile, outfile file parameter(s).");

		try {
			ValidElementsParser vep = new ValidElementsParser(inFile, outFile, propertiesFile, exclude, attributes);

			vep.printValidElements();
		} catch (DTDException ex) {
			throw new BuildException("DTDException error when preprocessing file", ex);
		} catch (IOException ex){
			throw new BuildException("I/O Error when preprocessing file", ex);
		}
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void setInFile(String inFile) {
		this.inFile = inFile;
	}

	public void setOutFile(String outFile) {
		this.outFile = outFile;
	}

	public void setExclude(String exclude) {
		this.exclude = exclude;
	}

	public void setPropertiesFile(String propertiesFile) {
		this.propertiesFile = propertiesFile;
	}

	public void setAttributes(boolean attrs) {
		this.attributes = attrs;
	}
}
