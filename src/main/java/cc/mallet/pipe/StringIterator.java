/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
   This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
   http://www.cs.umass.edu/~mccallum/mallet
   This software is provided under the terms of the Common Public License,
   version 1.0, as published by http://www.opensource.org.  For further
   information, see the file `LICENSE' included with this distribution. */

package cc.mallet.pipe;

import java.util.Objects;

/**
 * A utility class for iterating through a string with navigation
 * and inspection methods for parsing and text processing.
 */
public class StringIterator {

	private final String text;
	private int pos;

	public StringIterator(String text) {
		Objects.requireNonNull(text);
		this.text = text;
		this.pos = 0;
	}

	/** Returns the underlying string. */
	public String string() {
		return text;
	}

	/** Returns the current position in the string. */
	public int position() {
		return pos;
	}

	/** Returns the number of characters remaining from the current position. */
	public int remaining() {
		return Math.max(0, text.length() - pos);
	}

	/** Returns true if the position is at or past the end of the string. */
	public boolean isEndOfText() {
		return pos >= text.length();
	}

	/** Returns true if there are more characters to read. */
	public boolean hasNext() {
		return pos < text.length();
	}

	/** Returns the next character and advances the position by one, or 0 if at end. */
	public char next() {
		if (pos >= text.length()) return 0;
		return text.charAt(pos++);
	}

	/** Returns the character at the current position without advancing, or 0 if at end. */
	public char peek() {
		if (pos >= text.length()) return 0;
		return text.charAt(pos);
	}

	/** Returns the character at offset positions ahead of current position, or 0 if out of bounds. */
	public char peek(int offset) {
		int idx = pos + offset;
		if (idx < 0 || idx >= text.length()) return 0;
		return text.charAt(idx);
	}

	/** Extracts a substring from the given start index to the end of the string. */
	public String extract(int start) {
		if (start >= text.length()) return "";
		return text.substring(start);
	}

	/** Extracts a substring from start (inclusive) to end (exclusive). */
	public String extract(int start, int end) {
		if (start >= text.length()) return "";
		return text.substring(start, Math.min(end, text.length()));
	}

	/** Advances the position by one. */
	public void moveAhead() {
		if (pos < text.length()) pos++;
	}

	/** Advances the position by the given count. */
	public void moveAhead(int count) {
		pos = Math.min(pos + count, text.length());
	}

	/** Moves the position to the start of the first occurrence of the target string
	 *  searching forward from the current position. If not found, moves to end. */
	public void moveTo(String target) {
		if (target.isEmpty()) return;
		int idx = text.indexOf(target, pos);
		if (idx >= 0) {
			pos = idx;
		} else {
			pos = text.length();
		}
	}

	/** Moves the position to the first occurrence of the target character
	 *  searching forward from the current position. If not found, moves to end. */
	public void moveTo(char target) {
		int idx = text.indexOf(target, pos);
		if (idx >= 0) {
			pos = idx;
		} else {
			pos = text.length();
		}
	}

	/** Moves the position to the first occurrence of any of the target characters
	 *  searching forward from the current position. If not found, moves to end. */
	public void moveTo(char[] targets) {
		int best = text.length();
		for (char c : targets) {
			int idx = text.indexOf(c, pos);
			if (idx >= 0 && idx < best) {
				best = idx;
			}
		}
		pos = best;
	}

	/** Moves the position past any occurrences of the given characters at the current position. */
	public void movePast(char[] targets) {
		while (pos < text.length()) {
			char c = text.charAt(pos);
			boolean found = false;
			for (char t : targets) {
				if (c == t) { found = true; break; }
			}
			if (!found) break;
			pos++;
		}
	}

	/** Moves the position to the end of the current line (stops at '\n' or end of text). */
	public void moveToEndOfLine() {
		while (pos < text.length() && text.charAt(pos) != '\n') {
			pos++;
		}
	}

	/** Moves the position past any whitespace characters at the current position. */
	public void movePastWhitespace() {
		while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
			pos++;
		}
	}

	/** Returns true if the string contains only lowercase letters and whitespace. */
	public static boolean isLowerCase(String s) {
		boolean hasLetter = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (Character.isLetter(c)) {
				hasLetter = true;
				if (!Character.isLowerCase(c)) return false;
			}
		}
		return hasLetter;
	}

	/** Returns true if the string contains only uppercase letters and whitespace. */
	public static boolean isUpperCase(String s) {
		boolean hasLetter = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (Character.isLetter(c)) {
				hasLetter = true;
				if (!Character.isUpperCase(c)) return false;
			}
		}
		return hasLetter;
	}

	/** Returns true if the string starts with an uppercase letter (after optional whitespace)
	 *  followed by only lowercase letters. */
	public static boolean isCapitalized(String s) {
		boolean foundFirst = false;
		boolean foundSecond = false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if (Character.isLetter(c)) {
				if (!foundFirst) {
					if (!Character.isUpperCase(c)) return false;
					foundFirst = true;
				} else {
					foundSecond = true;
					if (!Character.isLowerCase(c)) return false;
				}
			}
		}
		return foundFirst && foundSecond;
	}

	/** Returns true if the string contains only whitespace characters. */
	public static boolean isBlank(String s) {
		for (int i = 0; i < s.length(); i++) {
			if (!Character.isWhitespace(s.charAt(i))) return false;
		}
		return true;
	}

	/** Trims leading whitespace from the string. */
	public static String trimLeft(String s) {
		int start = 0;
		while (start < s.length() && Character.isWhitespace(s.charAt(start))) {
			start++;
		}
		return s.substring(start);
	}

	/** Trims trailing whitespace from the string. */
	public static String trimRight(String s) {
		int end = s.length();
		while (end > 0 && Character.isWhitespace(s.charAt(end - 1))) {
			end--;
		}
		return s.substring(0, end);
	}
}
