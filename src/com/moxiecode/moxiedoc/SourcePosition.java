package com.moxiecode.moxiedoc;

public class SourcePosition {
	private String filePath;
	private int lineNumber;

	SourcePosition(String file_path, int line_num) {
		this.filePath = file_path;
		this.lineNumber = line_num;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public int getLineNumber() {
		return this.lineNumber;
	}

	public String toString() {
		return this.filePath + "#" + this.lineNumber;
	}

	void setLineNumber(int num) {
		this.lineNumber = num;
	}
}
