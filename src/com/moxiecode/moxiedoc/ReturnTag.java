package com.moxiecode.moxiedoc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReturnTag extends Tag {
	protected String type;

	public ReturnTag(String name, String text, SourcePosition pos) {
		super(name, text, pos);
	}

	public String getType() {
		return this.type;
	}

	public String toString() {
		return this.name + "=" + this.getType() + "," + this.getText() + " (" + this.sourcePosition + ")";
	}

	// Package methods

	void setRawText(String raw_text) {
		super.setRawText(raw_text);

		Pattern tagPattern = Pattern.compile("^\\{([^}]+)\\}\\s+(.+)$");
		Matcher tagMatcher = tagPattern.matcher(text);

		if (tagMatcher.matches()) {
			this.type = tagMatcher.group(1).trim();
			this.text = tagMatcher.group(2).trim();
		}
	}
}
