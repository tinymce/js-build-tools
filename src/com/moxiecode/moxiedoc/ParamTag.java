package com.moxiecode.moxiedoc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParamTag extends Tag {
	protected String type;
	protected String paramName;
	protected String paramText;

	public ParamTag(String name, String text, SourcePosition pos) {
		super(name, text, pos);
	}

	public String getType() {
		return this.type;
	}

	public String getParameterName() {
		return this.paramName;
	}

	public String toString() {
		return this.name + "=" + this.getType() + "," + this.getParameterName() + "," + this.getText() + " (" + this.sourcePosition + ")";
	}

	// Package methods

	void setRawText(String raw_text) {
		super.setRawText(raw_text);

		Pattern tagPattern = Pattern.compile("^\\{([^}]+)\\}\\s+([^\\s]+)\\s+(.+)$");
		Matcher tagMatcher = tagPattern.matcher(text);

		if (tagMatcher.matches()) {
			this.type = tagMatcher.group(1).trim();
			this.paramName = tagMatcher.group(2).trim();
			this.text = tagMatcher.group(3).trim();
		}
	}
}
