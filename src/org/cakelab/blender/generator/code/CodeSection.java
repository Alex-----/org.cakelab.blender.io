package org.cakelab.blender.generator.code;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CodeSection extends CodeGenerator {
	ArrayList<String> lines = new ArrayList<String>();

	protected StringBuffer currentLine = new StringBuffer();
	
	public CodeSection(int initialIndent) {
		super(initialIndent);
	}

	@Override
	public void reset() {
		lines.clear();
		currentLine.setLength(0);
	}
	
	public String toString(int indent) {
		if (currentLine.length() != 0) appendln();
		
		StringBuffer result = new StringBuffer();
		String prepend = getIndentString(indent);
		for (String line : lines) {
			result.append(prepend).append(line).append(NL);
		}
		return result.toString();
	}

	public CodeSection appendln(String line) {
		if (line.contains(NL)) {
			append(line);
		} else {
			currentLine.append(line);
		}
		return appendln();
	}

	public CodeSection append(String text) {
		boolean multiline = text.contains(NL);
		for (int endln = text.indexOf(NL); endln >= 0; endln = text.indexOf(NL)) {
			appendln(text.substring(0, endln));
			text = text.substring(endln+NL.length());
		}
		if (text.length() > 0 || !multiline) currentLine.append(text);
		return this;
	}

	public CodeSection appendln() {
		lines.add(indent + currentLine.toString());
		currentLine.setLength(0);
		return this;
	}

	public int numLines() {
		return lines.size();
	}
	
	public ArrayList<String> getLines() {
		return lines;
	}

	public CodeSection append(int i) {
		return append(Integer.toString(i));
	}

	/**
	 * Sorts existing lines by alphabetic order. Usefule to sort 
	 * for example import statements.
	 */
	public void sortLines() {
		Collections.sort(lines);
	}

	/**
	 * Sorts existing lines by alphabetic order. Usefule to sort 
	 * for example import statements.
	 */
	public void sortLines(Comparator<String> c) {
		Collections.sort(lines, c);
	}

}
