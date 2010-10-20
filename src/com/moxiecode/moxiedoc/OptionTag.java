package com.moxiecode.moxiedoc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OptionTag extends Tag {
	protected String[] types;
	protected String optionName;

	public OptionTag(String name, String text, SourcePosition pos) {
		super(name, text, pos);
	}

	public String[] getTypes() {
		return this.types;
	}

	public String getOptionName() {
		return this.optionName;
	}

	public String toString() {
		return this.name + "=" + this.getTypes() + "," + this.getOptionName() + "=" + this.getText() + " (" + this.sourcePosition + ")";
	}

	// Package methods

	void setRawText(String raw_text) {
		super.setRawText(raw_text);

		Pattern tagPattern = Pattern.compile("^\\{([^}]+)\\}\\s+([^\\s]+)\\s+(.+)$");
		Matcher tagMatcher = tagPattern.matcher(text);

		if (tagMatcher.matches()) {
			this.types = tagMatcher.group(1).trim().split("/");
			this.optionName = tagMatcher.group(2).trim();
			this.text = tagMatcher.group(3).trim();
		}
	}
} 