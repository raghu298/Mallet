/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
   This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
   http://www.cs.umass.edu/~mccallum/mallet
   This software is provided under the terms of the Common Public License,
   version 1.0, as published by http://www.opensource.org.  For further
   information, see the file `LICENSE' included with this distribution. */

package cc.mallet.pipe;

import java.io.*;

import cc.mallet.types.Instance;
import cc.mallet.types.Token;
import cc.mallet.types.TokenSequence;

/**
 * Pipe that applies the Porter stemming algorithm to each token in a
 * TokenSequence, replacing the token text with its stem.
 *
 * Implementation of the Porter Stemming Algorithm as described in:
 * Porter, M.F., "An algorithm for suffix stripping", Program 14(3), 1980, pp. 130-137.
 */
public class TokenSequence2PorterStems extends Pipe implements Serializable {

	public Instance pipe(Instance carrier) {
		TokenSequence ts = (TokenSequence) carrier.getData();
		for (int i = 0; i < ts.size(); i++) {
			Token t = ts.get(i);
			t.setText(stem(t.getText()));
		}
		return carrier;
	}

	/**
	 * Apply the Porter stemming algorithm to a word.
	 */
	public static String stem(String word) {
		if (word.length() < 3) return word;

		char[] b = word.toCharArray();
		int k = b.length - 1;

		// Step 1a
		if (b[k] == 's') {
			if (endsWith(b, k, "sses")) k -= 2;
			else if (endsWith(b, k, "ies")) { k -= 2; b[k] = 'i'; }
			else if (b[k - 1] != 's') k--;
		}

		// Step 1b
		if (endsWith(b, k, "eed")) {
			if (measure(b, 0, k - 3) > 0) k--;
		} else {
			boolean found = false;
			int kOrig = k;
			if (endsWith(b, k, "ed")) {
				k -= 2;
				found = containsVowel(b, 0, k);
			} else if (endsWith(b, k, "ing")) {
				k -= 3;
				found = containsVowel(b, 0, k);
			}
			if (found) {
				if (endsWith(b, k, "at") || endsWith(b, k, "bl") || endsWith(b, k, "iz")) {
					k++;
					b = ensureCapacity(b, k + 1);
					b[k] = 'e';
				} else if (k > 0 && b[k] == b[k - 1] && !isVowel(b, k) && b[k] != 'l' && b[k] != 's' && b[k] != 'z') {
					k--;
				} else if (measure(b, 0, k) == 1 && cvc(b, k)) {
					k++;
					b = ensureCapacity(b, k + 1);
					b[k] = 'e';
				}
			} else {
				k = kOrig;
			}
		}

		// Step 1c
		if (k > 0 && b[k] == 'y' && containsVowel(b, 0, k - 1)) {
			b[k] = 'i';
		}

		// Step 2
		k = step2(b, k);

		// Step 3
		k = step3(b, k);

		// Step 4
		k = step4(b, k);

		// Step 5a
		if (b[k] == 'e') {
			int m = measure(b, 0, k - 1);
			if (m > 1 || (m == 1 && !cvc(b, k - 1))) k--;
		}

		// Step 5b
		if (k > 0 && b[k] == 'l' && b[k - 1] == 'l' && measure(b, 0, k - 1) > 1) k--;

		return new String(b, 0, k + 1);
	}

	private static int step2(char[] b, int k) {
		if (k < 1) return k;
		switch (b[k - 1]) {
			case 'a':
				if (endsWith(b, k, "ational")) return replace(b, k, "ational", "ate");
				if (endsWith(b, k, "tional")) return replace(b, k, "tional", "tion");
				break;
			case 'c':
				if (endsWith(b, k, "enci")) return replace(b, k, "enci", "ence");
				if (endsWith(b, k, "anci")) return replace(b, k, "anci", "ance");
				break;
			case 'e':
				if (endsWith(b, k, "izer")) return replace(b, k, "izer", "ize");
				break;
			case 'l':
				if (endsWith(b, k, "abli")) return replace(b, k, "abli", "able");
				if (endsWith(b, k, "alli")) return replace(b, k, "alli", "al");
				if (endsWith(b, k, "entli")) return replace(b, k, "entli", "ent");
				if (endsWith(b, k, "eli")) return replace(b, k, "eli", "e");
				if (endsWith(b, k, "ousli")) return replace(b, k, "ousli", "ous");
				break;
			case 'o':
				if (endsWith(b, k, "ization")) return replace(b, k, "ization", "ize");
				if (endsWith(b, k, "ation")) return replace(b, k, "ation", "ate");
				if (endsWith(b, k, "ator")) return replace(b, k, "ator", "ate");
				break;
			case 's':
				if (endsWith(b, k, "alism")) return replace(b, k, "alism", "al");
				if (endsWith(b, k, "iveness")) return replace(b, k, "iveness", "ive");
				if (endsWith(b, k, "fulness")) return replace(b, k, "fulness", "ful");
				if (endsWith(b, k, "ousness")) return replace(b, k, "ousness", "ous");
				break;
			case 't':
				if (endsWith(b, k, "aliti")) return replace(b, k, "aliti", "al");
				if (endsWith(b, k, "iviti")) return replace(b, k, "iviti", "ive");
				if (endsWith(b, k, "biliti")) return replace(b, k, "biliti", "ble");
				break;
		}
		return k;
	}

	private static int step3(char[] b, int k) {
		switch (b[k]) {
			case 'e':
				if (endsWith(b, k, "icate")) return replace(b, k, "icate", "ic");
				if (endsWith(b, k, "ative")) return replace(b, k, "ative", "");
				if (endsWith(b, k, "alize")) return replace(b, k, "alize", "al");
				break;
			case 'i':
				if (endsWith(b, k, "iciti")) return replace(b, k, "iciti", "ic");
				break;
			case 'l':
				if (endsWith(b, k, "ical")) return replace(b, k, "ical", "ic");
				if (endsWith(b, k, "ful")) return replace(b, k, "ful", "");
				break;
			case 's':
				if (endsWith(b, k, "ness")) return replace(b, k, "ness", "");
				break;
		}
		return k;
	}

	private static int step4(char[] b, int k) {
		if (k < 1) return k;
		switch (b[k - 1]) {
			case 'a':
				if (endsWith(b, k, "al") && measure(b, 0, k - 2) > 1) return k - 2;
				break;
			case 'c':
				if ((endsWith(b, k, "ance") || endsWith(b, k, "ence")) && measure(b, 0, k - 4) > 1) return k - 4;
				break;
			case 'e':
				if (endsWith(b, k, "er") && measure(b, 0, k - 2) > 1) return k - 2;
				break;
			case 'i':
				if (endsWith(b, k, "ic") && measure(b, 0, k - 2) > 1) return k - 2;
				break;
			case 'l':
				if (endsWith(b, k, "able") && measure(b, 0, k - 4) > 1) return k - 4;
				if (endsWith(b, k, "ible") && measure(b, 0, k - 4) > 1) return k - 4;
				break;
			case 'n':
				if (endsWith(b, k, "ant") && measure(b, 0, k - 3) > 1) return k - 3;
				if (endsWith(b, k, "ement") && measure(b, 0, k - 5) > 1) return k - 5;
				if (endsWith(b, k, "ment") && measure(b, 0, k - 4) > 1) return k - 4;
				if (endsWith(b, k, "ent") && measure(b, 0, k - 3) > 1) return k - 3;
				break;
			case 'o':
				if (endsWith(b, k, "ion") && k >= 3 && (b[k - 3] == 's' || b[k - 3] == 't') && measure(b, 0, k - 3) > 1) return k - 3;
				if (endsWith(b, k, "ou") && measure(b, 0, k - 2) > 1) return k - 2;
				break;
			case 's':
				if (endsWith(b, k, "ism") && measure(b, 0, k - 3) > 1) return k - 3;
				break;
			case 't':
				if (endsWith(b, k, "ate") && measure(b, 0, k - 3) > 1) return k - 3;
				if (endsWith(b, k, "iti") && measure(b, 0, k - 3) > 1) return k - 3;
				break;
			case 'u':
				if (endsWith(b, k, "ous") && measure(b, 0, k - 3) > 1) return k - 3;
				break;
			case 'v':
				if (endsWith(b, k, "ive") && measure(b, 0, k - 3) > 1) return k - 3;
				break;
			case 'z':
				if (endsWith(b, k, "ize") && measure(b, 0, k - 3) > 1) return k - 3;
				break;
		}
		return k;
	}

	private static int replace(char[] b, int k, String suffix, String replacement) {
		int stemEnd = k - suffix.length();
		if (measure(b, 0, stemEnd) > 0) {
			for (int i = 0; i < replacement.length(); i++) {
				b[stemEnd + 1 + i] = replacement.charAt(i);
			}
			return stemEnd + replacement.length();
		}
		return k;
	}

	private static boolean endsWith(char[] b, int k, String s) {
		int len = s.length();
		if (len > k + 1) return false;
		for (int i = 0; i < len; i++) {
			if (b[k - len + 1 + i] != s.charAt(i)) return false;
		}
		return true;
	}

	private static boolean isVowel(char[] b, int i) {
		char c = b[i];
		if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u') return true;
		if (c == 'y' && i > 0 && !isVowel(b, i - 1)) return true;
		return false;
	}

	private static boolean containsVowel(char[] b, int start, int end) {
		for (int i = start; i <= end; i++) {
			if (isVowel(b, i)) return true;
		}
		return false;
	}

	/**
	 * Measure the number of consonant sequences in b[start..end].
	 * The measure is defined as the number of VC (vowel-consonant) transitions.
	 */
	private static int measure(char[] b, int start, int end) {
		if (end < start) return 0;
		int n = 0;
		int i = start;
		while (i <= end && !isVowel(b, i)) i++;
		while (i <= end) {
			while (i <= end && isVowel(b, i)) i++;
			if (i > end) break;
			n++;
			while (i <= end && !isVowel(b, i)) i++;
		}
		return n;
	}

	/**
	 * Returns true if b[i] is a consonant following a vowel following a consonant,
	 * and the consonant is not w, x, or y.
	 */
	private static boolean cvc(char[] b, int i) {
		if (i < 2) return false;
		char c = b[i];
		if (c == 'w' || c == 'x' || c == 'y') return false;
		return !isVowel(b, i) && isVowel(b, i - 1) && !isVowel(b, i - 2);
	}

	private static char[] ensureCapacity(char[] b, int minLength) {
		if (b.length >= minLength) return b;
		char[] newB = new char[minLength + 10];
		System.arraycopy(b, 0, newB, 0, b.length);
		return newB;
	}

	// Serialization
	private static final long serialVersionUID = 1;
	private static final int CURRENT_SERIAL_VERSION = 0;

	private void writeObject(ObjectOutputStream out) throws IOException {
		out.writeInt(CURRENT_SERIAL_VERSION);
	}

	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		int version = in.readInt();
	}
}
