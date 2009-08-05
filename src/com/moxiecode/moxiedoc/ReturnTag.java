package com.moxiecode.moxiedoc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReturnTag extends Tag {
	protected String[] types;

	public ReturnTag(String name, String text, SourcePosition pos) {
		super(name, text, pos);
	}

	public String[] getTypes() {
		return this.types;
	}

	public String toString() {
		return this.name + "=" + this.getTypes() + "," + this.getText() + " (" + this.sourcePosition + ")";
	}

	// Package methods

	void setRawText(String raw_text) {
		super.setRawText(raw_text);

		Pattern tagPattern = Pattern.compile("^\\{([^}]+)\\}\\s+(.+)$");
		Matcher tagMatcher = tagPattern.matcher(text);

		if (tagMatcher.matches()) {
			this.types = tagMatcher.group(1).trim().split("/");
			this.text = tagMatcher.group(2).trim();
		}
	}
}
