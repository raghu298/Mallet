/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
   This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
   http://www.cs.umass.edu/~mccallum/mallet
   This software is provided under the terms of the Common Public License,
   version 1.0, as published by http://www.opensource.org.  For further
   information, see the file `LICENSE' included with this distribution. */

package cc.mallet.pipe;

import java.io.*;
import java.text.Normalizer;

import cc.mallet.types.Instance;

/**
 * Pipe that removes diacritical marks (accents) from a CharSequence.
 * Uses Unicode normalization (NFD) to decompose characters and then
 * strips combining diacritical marks.
 */
public class CharSequenceNoDiacritics extends Pipe implements Serializable {

	public Instance pipe(Instance carrier) {
		if (carrier.getData() instanceof CharSequence) {
			String data = carrier.getData().toString();
			String normalized = Normalizer.normalize(data, Normalizer.Form.NFD);
			// Remove combining diacritical marks (Unicode block \p{InCombiningDiacriticalMarks})
			String stripped = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
			carrier.setData(stripped);
		} else {
			throw new IllegalArgumentException(
				"CharSequenceNoDiacritics expects a CharSequence, found a "
				+ carrier.getData().getClass());
		}
		return carrier;
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
