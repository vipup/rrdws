/* ============================================================
 * JRobin : Pure java implementation of RRDTool's functionality
 * ============================================================
 *
 * Project Info:  http://www.jrobin.org
 * Project Lead:  Sasa Markovic (saxon@jrobin.org);
 *
 * (C) Copyright 2003-2005, by Sasa Markovic.
 *
 * Developers:    Sasa Markovic (saxon@jrobin.org)
 *
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 */

package org.jrobin.cmd;

import org.jrobin.core.RrdException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class RrdCmdScanner {
	private LinkedList<String> words = new LinkedList<String>();
	private StringBuffer buff;

	RrdCmdScanner(String command) throws RrdException {
		String cmd = command.trim();
		// parse words
		char activeQuote = 0;
		for (int i = 0; i < cmd.length(); i++) {
			char c = cmd.charAt(i);
			if ((c == '"' || c == '\'') && activeQuote == 0) {
				// opening double or single quote
				initWord();
				activeQuote = c;
				continue;
			}
			if (c == activeQuote) {
				// closing quote
				activeQuote = 0;
				continue;
			}
			if (isSeparator(c) && activeQuote == 0) {
				// separator encountered
				finishWord();
				continue;
			}
			if (c == '\\' && activeQuote == '"' && i + 1 < cmd.length()) {
				// check for \" and \\ inside double quotes
				char c2 = cmd.charAt(i + 1);
				if (c2 == '\\' || c2 == '"') {
					appendWord(c2);
					i++;
					continue;
				}
			}
			// ordinary character
			appendWord(c);
		}
		if (activeQuote != 0) {
			throw new RrdException("End of command reached but " + activeQuote + " expected");
		}
		finishWord();
	}

	String getCmdType() {
		if (words.size() > 0) {
			return words.get(0);
		}
		else {
			return null;
		}
	}

	private void appendWord(char c) {
		if (buff == null) {
			buff = new StringBuffer("");
		}
		buff.append(c);
	}

	private void finishWord() {
		if (buff != null) {
			words.add(buff.toString());
			buff = null;
		}
	}

	private void initWord() {
		if (buff == null) {
			buff = new StringBuffer("");
		}
	}

	void dump() {
		for (String word : words) {
			System.out.println(word);
		}
	}

	String getOptionValue(String shortForm, String longForm, String defaultValue)
			throws RrdException {
		String value = null;
		if (shortForm != null) {
			value = getOptionValue("-" + shortForm);
		}
		if (value == null && longForm != null) {
			value = getOptionValue("--" + longForm);
		}
		if (value == null) {
			value = defaultValue;
		}
		return value;
	}

	String getOptionValue(String shortForm, String longForm)
			throws RrdException {
		return getOptionValue(shortForm, longForm, null);
	}

	private String getOptionValue(String fullForm) throws RrdException {
		Iterator<String> iter = words.listIterator();
		while (iter.hasNext()) {
			String word = iter.next();
			if (word.equals(fullForm)) {
				// full match, the value is in the next word
				if (iter.hasNext()) {
					iter.remove();
					String value = iter.next();
					iter.remove();
					return value;
				}
				else {
					throw new RrdException("Value for option " , fullForm + " expected but not found");
				}
			}
			if (word.startsWith(fullForm)) {
				int pos = fullForm.length();
				if (word.charAt(pos) == '=') {
					// skip '=' if present
					pos++;
				}
				iter.remove();
				return word.substring(pos);
			}
		}
		return null;
	}

	boolean getBooleanOption(String shortForm, String longForm) {
		Iterator<String> iter = words.listIterator();
		while (iter.hasNext()) {
			String word = iter.next();
			if ((shortForm != null && word.equals("-" + shortForm)) ||
					(longForm != null && word.equals("--" + longForm))) {
				iter.remove();
				return true;
			}
		}
		return false;
	}

	String[] getMultipleOptions(String shortForm, String longForm) throws RrdException {
		List<String> values = new ArrayList<String>();
		for (; ;) {
			String value = getOptionValue(shortForm, longForm, null);
			if (value == null) {
				break;
			}
			values.add(value);
		}
		return values.toArray(new String[values.size()]);
	}

	String[] getRemainingWords() {
		return words.toArray(new String[words.size()]);
	}

	boolean isSeparator(char c) {
		return Character.isWhitespace(c);
	}
}
