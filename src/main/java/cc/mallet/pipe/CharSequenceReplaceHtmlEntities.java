/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
   This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
   http://www.cs.umass.edu/~mccallum/mallet
   This software is provided under the terms of the Common Public License,
   version 1.0, as published by http://www.opensource.org.  For further
   information, see the file `LICENSE' included with this distribution. */

package cc.mallet.pipe;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.mallet.types.Instance;

/**
 * Pipe that replaces HTML entities (both named and numeric) with their
 * corresponding Unicode characters.
 */
public class CharSequenceReplaceHtmlEntities extends Pipe implements Serializable {

	private static final Pattern ENTITY_PATTERN = Pattern.compile("&(#?\\w+);");

	private static final Map<String, String> NAMED_ENTITIES = new HashMap<>();

	static {
		NAMED_ENTITIES.put("amp", "&");
		NAMED_ENTITIES.put("lt", "<");
		NAMED_ENTITIES.put("gt", ">");
		NAMED_ENTITIES.put("quot", "\"");
		NAMED_ENTITIES.put("apos", "'");
		NAMED_ENTITIES.put("nbsp", "\u00A0");
		NAMED_ENTITIES.put("iexcl", "\u00A1");
		NAMED_ENTITIES.put("cent", "\u00A2");
		NAMED_ENTITIES.put("pound", "\u00A3");
		NAMED_ENTITIES.put("curren", "\u00A4");
		NAMED_ENTITIES.put("yen", "\u00A5");
		NAMED_ENTITIES.put("brvbar", "\u00A6");
		NAMED_ENTITIES.put("sect", "\u00A7");
		NAMED_ENTITIES.put("uml", "\u00A8");
		NAMED_ENTITIES.put("copy", "\u00A9");
		NAMED_ENTITIES.put("ordf", "\u00AA");
		NAMED_ENTITIES.put("laquo", "\u00AB");
		NAMED_ENTITIES.put("not", "\u00AC");
		NAMED_ENTITIES.put("shy", "\u00AD");
		NAMED_ENTITIES.put("reg", "\u00AE");
		NAMED_ENTITIES.put("macr", "\u00AF");
		NAMED_ENTITIES.put("deg", "\u00B0");
		NAMED_ENTITIES.put("plusmn", "\u00B1");
		NAMED_ENTITIES.put("sup2", "\u00B2");
		NAMED_ENTITIES.put("sup3", "\u00B3");
		NAMED_ENTITIES.put("acute", "\u00B4");
		NAMED_ENTITIES.put("micro", "\u00B5");
		NAMED_ENTITIES.put("para", "\u00B6");
		NAMED_ENTITIES.put("middot", "\u00B7");
		NAMED_ENTITIES.put("cedil", "\u00B8");
		NAMED_ENTITIES.put("sup1", "\u00B9");
		NAMED_ENTITIES.put("ordm", "\u00BA");
		NAMED_ENTITIES.put("raquo", "\u00BB");
		NAMED_ENTITIES.put("frac14", "\u00BC");
		NAMED_ENTITIES.put("frac12", "\u00BD");
		NAMED_ENTITIES.put("frac34", "\u00BE");
		NAMED_ENTITIES.put("iquest", "\u00BF");
		NAMED_ENTITIES.put("Agrave", "\u00C0");
		NAMED_ENTITIES.put("Aacute", "\u00C1");
		NAMED_ENTITIES.put("Acirc", "\u00C2");
		NAMED_ENTITIES.put("Atilde", "\u00C3");
		NAMED_ENTITIES.put("Auml", "\u00C4");
		NAMED_ENTITIES.put("Aring", "\u00C5");
		NAMED_ENTITIES.put("AElig", "\u00C6");
		NAMED_ENTITIES.put("Ccedil", "\u00C7");
		NAMED_ENTITIES.put("Egrave", "\u00C8");
		NAMED_ENTITIES.put("Eacute", "\u00C9");
		NAMED_ENTITIES.put("Ecirc", "\u00CA");
		NAMED_ENTITIES.put("Euml", "\u00CB");
		NAMED_ENTITIES.put("Igrave", "\u00CC");
		NAMED_ENTITIES.put("Iacute", "\u00CD");
		NAMED_ENTITIES.put("Icirc", "\u00CE");
		NAMED_ENTITIES.put("Iuml", "\u00CF");
		NAMED_ENTITIES.put("ETH", "\u00D0");
		NAMED_ENTITIES.put("Ntilde", "\u00D1");
		NAMED_ENTITIES.put("Ograve", "\u00D2");
		NAMED_ENTITIES.put("Oacute", "\u00D3");
		NAMED_ENTITIES.put("Ocirc", "\u00D4");
		NAMED_ENTITIES.put("Otilde", "\u00D5");
		NAMED_ENTITIES.put("Ouml", "\u00D6");
		NAMED_ENTITIES.put("times", "\u00D7");
		NAMED_ENTITIES.put("Oslash", "\u00D8");
		NAMED_ENTITIES.put("Ugrave", "\u00D9");
		NAMED_ENTITIES.put("Uacute", "\u00DA");
		NAMED_ENTITIES.put("Ucirc", "\u00DB");
		NAMED_ENTITIES.put("Uuml", "\u00DC");
		NAMED_ENTITIES.put("Yacute", "\u00DD");
		NAMED_ENTITIES.put("THORN", "\u00DE");
		NAMED_ENTITIES.put("szlig", "\u00DF");
		NAMED_ENTITIES.put("agrave", "\u00E0");
		NAMED_ENTITIES.put("aacute", "\u00E1");
		NAMED_ENTITIES.put("acirc", "\u00E2");
		NAMED_ENTITIES.put("atilde", "\u00E3");
		NAMED_ENTITIES.put("auml", "\u00E4");
		NAMED_ENTITIES.put("aring", "\u00E5");
		NAMED_ENTITIES.put("aelig", "\u00E6");
		NAMED_ENTITIES.put("ccedil", "\u00E7");
		NAMED_ENTITIES.put("egrave", "\u00E8");
		NAMED_ENTITIES.put("eacute", "\u00E9");
		NAMED_ENTITIES.put("ecirc", "\u00EA");
		NAMED_ENTITIES.put("euml", "\u00EB");
		NAMED_ENTITIES.put("igrave", "\u00EC");
		NAMED_ENTITIES.put("iacute", "\u00ED");
		NAMED_ENTITIES.put("icirc", "\u00EE");
		NAMED_ENTITIES.put("iuml", "\u00EF");
		NAMED_ENTITIES.put("eth", "\u00F0");
		NAMED_ENTITIES.put("ntilde", "\u00F1");
		NAMED_ENTITIES.put("ograve", "\u00F2");
		NAMED_ENTITIES.put("oacute", "\u00F3");
		NAMED_ENTITIES.put("ocirc", "\u00F4");
		NAMED_ENTITIES.put("otilde", "\u00F5");
		NAMED_ENTITIES.put("ouml", "\u00F6");
		NAMED_ENTITIES.put("divide", "\u00F7");
		NAMED_ENTITIES.put("oslash", "\u00F8");
		NAMED_ENTITIES.put("ugrave", "\u00F9");
		NAMED_ENTITIES.put("uacute", "\u00FA");
		NAMED_ENTITIES.put("ucirc", "\u00FB");
		NAMED_ENTITIES.put("uuml", "\u00FC");
		NAMED_ENTITIES.put("yacute", "\u00FD");
		NAMED_ENTITIES.put("thorn", "\u00FE");
		NAMED_ENTITIES.put("yuml", "\u00FF");
		NAMED_ENTITIES.put("ndash", "\u2013");
		NAMED_ENTITIES.put("mdash", "\u2014");
		NAMED_ENTITIES.put("lsquo", "\u2018");
		NAMED_ENTITIES.put("rsquo", "\u2019");
		NAMED_ENTITIES.put("ldquo", "\u201C");
		NAMED_ENTITIES.put("rdquo", "\u201D");
		NAMED_ENTITIES.put("bull", "\u2022");
		NAMED_ENTITIES.put("hellip", "\u2026");
		NAMED_ENTITIES.put("euro", "\u20AC");
		NAMED_ENTITIES.put("trade", "\u2122");
	}

	public Instance pipe(Instance carrier) {
		if (carrier.getData() instanceof CharSequence) {
			String data = carrier.getData().toString();
			String previous;
			do {
				previous = data;
				StringBuffer result = new StringBuffer();
				Matcher matcher = ENTITY_PATTERN.matcher(data);
				while (matcher.find()) {
					String entity = matcher.group(1);
					String replacement = resolveEntity(entity);
					if (replacement != null) {
						matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
					}
				}
				matcher.appendTail(result);
				data = result.toString();
			} while (!data.equals(previous));
			carrier.setData(data);
		} else {
			throw new IllegalArgumentException(
				"CharSequenceReplaceHtmlEntities expects a CharSequence, found a "
				+ carrier.getData().getClass());
		}
		return carrier;
	}

	private String resolveEntity(String entity) {
		if (entity.startsWith("#")) {
			try {
				int codePoint;
				if (entity.startsWith("#x") || entity.startsWith("#X")) {
					codePoint = Integer.parseInt(entity.substring(2), 16);
				} else {
					codePoint = Integer.parseInt(entity.substring(1));
				}
				return new String(Character.toChars(codePoint));
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return NAMED_ENTITIES.get(entity);
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
