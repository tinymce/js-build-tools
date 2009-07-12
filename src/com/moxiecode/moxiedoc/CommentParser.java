package com.moxiecode.moxiedoc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Vector;

public class CommentParser {
	public CommentParser() {
	}

	public CommentBlock[] parse(File file) throws IOException {
		Vector<CommentBlock> comments = new Vector<CommentBlock>();
		boolean inComment = false;
		BufferedReader in = null;
		String line = null;
		StringBuffer buffer = null;
		int lineNum = 1;
		SourcePosition pos = new SourcePosition(file.getAbsolutePath(), 0);

		try {
			in = new BufferedReader(new FileReader(file.getAbsolutePath()));

			while ((line = in.readLine()) != null) {
				line = line.trim();

				// Detect doc comment start
				if (line.startsWith("/**") && !inComment) {
					inComment = true;
					buffer = new StringBuffer();
					pos.setLineNumber(lineNum);
				}

				// Process inside comment
				if (inComment) {
					if (buffer.length() > 0)
						buffer.append('\n');

					buffer.append(line);
				}

				// Detect doc comment end
				if (inComment && line.endsWith("*/")) {
					comments.add(new CommentBlock(buffer.toString(), pos));
					inComment = false;
				}
				
				lineNum++;
			}
		} finally {
			if (in != null)
				in.close();
		}

		// Add tags
		CommentBlock commentsArray[] = new CommentBlock[comments.size()];
		comments.toArray(commentsArray);

		return commentsArray;
	}
}
