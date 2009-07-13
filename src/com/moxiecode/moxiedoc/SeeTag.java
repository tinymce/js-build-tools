package com.moxiecode.moxiedoc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeeTag extends Tag {
	protected String className, memberName;

	public SeeTag(String name, String text, SourcePosition pos) {
		super(name, text, pos);
	}

	public String getClassName() {
		return this.className;
	}

	public String getMemberName() {
		return this.memberName;
	}

	public String toString() {
		return this.name + "=" + this.getClassName() + "#" + this.getMemberName() + "," + this.getText() + " (" + this.sourcePosition + ")";
	}

	// Package methods

	void setRawText(String raw_text) {
		super.setRawText(raw_text);

		Pattern tagPattern = Pattern.compile("^([^#]*)#?(.*)$");
		Matcher tagMatcher = tagPattern.matcher(text);

		if (tagMatcher.matches()) {
			if (tagMatcher.group(1) != null)
				this.className = tagMatcher.group(1).trim();

			if (tagMatcher.group(2) != null)
				this.memberName = tagMatcher.group(2).trim();
		}
	}
}
