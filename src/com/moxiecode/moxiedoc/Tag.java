package com.moxiecode.moxiedoc;

public class Tag {
	protected String name;
	protected String text;
	protected String rawText;
	protected SourcePosition sourcePosition;

	public Tag(String name, String raw_text, SourcePosition pos) {
		this.name = name;
		this.sourcePosition = pos;
		this.setRawText(raw_text);
	}

	public String getName() {
		return this.name;
	}

	public String getText() {
		return this.text;
	}

	public SourcePosition getSourcePosition() {
		return this.sourcePosition;
	}

	public String toString() {
		return this.name + "=" + this.text + " (" + this.sourcePosition + ")";
	}

	// Package methods
	
	String getRawText() {
		return this.rawText;
	}

	void setRawText(String raw_text) {
		this.text = raw_text.trim();
		this.rawText = raw_text;
	}
}
