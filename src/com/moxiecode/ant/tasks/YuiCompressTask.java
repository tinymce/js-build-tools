package com.moxiecode.ant.tasks;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;

import com.yahoo.platform.yui.compressor.JavaScriptCompressor;
import com.yahoo.platform.yui.compressor.CssCompressor;

import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;

public class YuiCompressTask extends Task {
	protected String charset = "ISO-8859-1", inFile, outFile, inType;
	protected int lineBreakPosition = -1;
	protected boolean munge = true;
	protected boolean warn = false;
	protected boolean preserveAllSemiColons = false;
	protected boolean optimize = true;

	public void execute() throws BuildException {
		InputStreamReader in = null;
		OutputStreamWriter out = null;

		try {
			in = new InputStreamReader(new FileInputStream(this.inFile), this.charset);
			out = new OutputStreamWriter(new FileOutputStream(this.outFile), this.charset);

			if (this.inFile == null || this.outFile == null)
				throw new BuildException("Missing required: infile or outfile parameter.");
				
			if (this.inType != null && this.inType.equals("css")) {
				CssCompressor cCompressor = new CssCompressor(in);
				cCompressor.compress(out, lineBreakPosition);
			} else {
				
				JavaScriptCompressor jCompressor = new JavaScriptCompressor(in, new ErrorReporter() {
					private String getMessage(String source, String message, int line, int lineOffset) {
						String logMessage;

						if (line < 0) {
							logMessage = (source != null) ? source + ":" : "" + message;
						} else {
							logMessage = (source != null) ? source + ":" : "" + line + ":" + lineOffset + ":" + message;
						}

						return logMessage;
					}

					public void warning(String message, String sourceName, int line, String lineSource, int lineOffset) {
						log(getMessage(sourceName, message, line, lineOffset), Project.MSG_WARN);
					}

					public void error(String message, String sourceName, int line, String lineSource, int lineOffset) {
						log(getMessage(sourceName, message, line, lineOffset), Project.MSG_ERR);
					}

					public EvaluatorException runtimeError(String message, String sourceName, int line, String lineSource, int lineOffset) {
						log(getMessage(sourceName, message, line, lineOffset), Project.MSG_ERR);

						return new EvaluatorException(message);
					}
				});

				jCompressor.compress(out, lineBreakPosition, munge, warn, preserveAllSemiColons, !optimize);
			}
		} catch (IOException ex) {
			throw new BuildException("I/O Error when preprocessing file", ex);
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException ex) {
				throw new BuildException("I/O Error when preprocessing file", ex);
			}

			try {
				if (out != null)
					out.close();
			} catch (IOException ex) {
				throw new BuildException("I/O Error when preprocessing file", ex);
			}
		}
	}

	public void setCharset(String charset) {
		this.charset = charset;
	}

	public void setLineBreakPosition(int lineBreakPosition) {
		this.lineBreakPosition = lineBreakPosition;
	}

	public void setMunge(boolean munge) {
		this.munge = munge;
	}

	public void setWarn(boolean warn) {
		this.warn = warn;
	}

	public void setPreserveAllSemiColons(boolean preserveAllSemiColons) {
		this.preserveAllSemiColons = preserveAllSemiColons;
	}

	public void setOptimize(boolean optimize) {
		this.optimize = optimize;
	}

	public void setInFile(String in_file) {
		this.inFile = in_file;
	}

	public void setOutFile(String out_file) {
		this.outFile = out_file;
	}
	public void setType(String in_type) {
		this.inType = in_type;
	}
	
}
