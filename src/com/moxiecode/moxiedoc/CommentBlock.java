package com.moxiecode.moxiedoc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommentBlock {
	protected String rawText;
	protected String text;
	protected Tag[] tags;
	protected SourcePosition sourcePosition;

	CommentBlock(String raw_text, SourcePosition pos) {
		this.rawText = raw_text;
		this.sourcePosition = pos;

		this.parse(raw_text);
	}

	public String getText() {
		return this.text;
	}

	public Tag[] getTags() {
		return this.tags;
	}

	public Tag[] getTags(String tag_name) {
		Vector<Tag> tags = new Vector<Tag>();

		for (Tag tag : this.tags) {
			if (tag.getName().equals(tag_name))
				tags.add((Tag) tag);
		}

		Tag tagArray[] = new Tag[tags.size()];
		tags.toArray(tagArray);

		return tagArray;
	}

	public Tag getTag(String tag_name) {
		for (Tag tag : this.tags) {
			if (tag.getName().equals(tag_name))
				return tag;
		}

		return null;
	}

	public ParamTag[] getParams() {
		Vector<ParamTag> paramTags = new Vector<ParamTag>();

		for (Tag tag : this.tags) {
			if (tag.getName().equals("param"))
				paramTags.add((ParamTag) tag);
		}

		ParamTag paramTagArray[] = new ParamTag[paramTags.size()];
		paramTags.toArray(paramTagArray);

		return paramTagArray;
	}

	public OptionTag[] getOptions() {
		Vector<OptionTag> optionTags = new Vector<OptionTag>();

		for (Tag tag : this.tags) {
			if (tag.getName().equals("option"))
				optionTags.add((OptionTag) tag);
		}

		OptionTag optionTagArray[] = new OptionTag[optionTags.size()];
		optionTags.toArray(optionTagArray);

		return optionTagArray;
	}

	public Tag[] getExamples() {
		Vector<Tag> exampleTags = new Vector<Tag>();

		for (Tag tag : this.tags) {
			if (tag.getName().equals("example"))
				exampleTags.add((Tag) tag);
		}

		Tag examplesTagArray[] = new Tag[exampleTags.size()];
		exampleTags.toArray(examplesTagArray);

		return examplesTagArray;
	}

	public SeeTag[] getSeeTags() {
		Vector<Tag> seeTags = new Vector<Tag>();

		for (Tag tag : this.tags) {
			if (tag.getName().equals("see"))
				seeTags.add((SeeTag) tag);
		}

		SeeTag seeTagArray[] = new SeeTag[seeTags.size()];
		seeTags.toArray(seeTagArray);

		return seeTagArray;
	}

	public boolean hasTag(String tag_name) {
		return this.getTag(tag_name) != null;
	}

	public SourcePosition getSourcePosition() {
		return this.sourcePosition;
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append("Text: " + this.text + " (" + this.getSourcePosition() + ")\n");

		for (Tag tag : this.tags)
			buffer.append(" " + tag.toString() + "\n");

		return buffer.toString();
	}
	
	// Private methods

	private void parse(String raw_text) {
		BufferedReader in;
		String line = null;
		StringBuffer textBuffer = new StringBuffer();
		boolean foundTag = false;
		int lineNum = 0;
		Vector<Tag> tags = new Vector<Tag>();

		try {
			Pattern tagPattern = Pattern.compile("^\\s*@([^\\s]+)\\s*(.+)?$", Pattern.CASE_INSENSITIVE);
			Tag tag = null;

			in = new BufferedReader(new StringReader(raw_text));

			while ((line = in.readLine()) != null) {
				// Remove * prefix/suffixes
				line = line.replaceAll("^\\/?\\*+\\s?", "");
				line = line.replaceAll("\\**\\/$", "");
				//line = line.trim();

				Matcher tagMatcher = tagPattern.matcher(line);
				if (tagMatcher.matches()) {
					if (!foundTag) {
						this.text = textBuffer.toString();
						foundTag = true;
					}

					textBuffer = new StringBuffer();

					String tagName = tagMatcher.group(1);
					String tagText = tagMatcher.group(2);
					SourcePosition pos = new SourcePosition(
						this.sourcePosition.getFilePath(),
						this.sourcePosition.getLineNumber() + lineNum
					);

					// Make sure text isn't null
					if (tagText == null)
						tagText = "";

					if (tagName.equals("param"))
						tag = new ParamTag(tagName, tagText, pos);
					else if (tagName.equals("option"))
						tag = new OptionTag(tagName, tagText, pos);
					else if (tagName.equals("return"))
						tag = new ReturnTag(tagName, tagText, pos);
					else if (tagName.equals("see"))
						tag = new SeeTag(tagName, tagText, pos);
					else
						tag = new Tag(tagName, tagText, pos);

					tags.add(tag);
				} else {
					if (tag == null) {
						if (textBuffer.length() > 0)
							textBuffer.append('\n');

						textBuffer.append(line);
					} else {
						if (tag.getRawText().length() > 0)
							tag.setRawText(tag.getRawText() + "\n");

						tag.setRawText(tag.getRawText() + line);
					}
				}

				lineNum++;
			}
		} catch (IOException ex) {
			// Ignore
		}

		// Add options to params
		for (int i = 0; i < tags.size(); i++) {
			if (((Tag) tags.get(i)).getName().equals("param")) {
				ParamTag paramTag = (ParamTag) tags.get(i);

				// Find options after the param tag and add them to the param
				for (i = i + 1; i < tags.size(); i++) {
					Tag optionTag = (Tag) tags.get(i);

					if (optionTag.getName().equals("option"))
						paramTag.addOption((OptionTag) optionTag);
					else
						break;
				}
			}
		}

		// Add tags
		this.tags = new Tag[tags.size()];
		tags.toArray(this.tags);
	}
}
