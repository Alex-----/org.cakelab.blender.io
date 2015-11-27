package org.cakelab.blender.model.gen;

import java.util.ArrayList;

import org.cakelab.blender.model.gen.code.CodeGenerator;

public class CodeSection extends CodeGenerator {
	ArrayList<String> lines = new ArrayList<String>();

	protected StringBuffer currentLine = new StringBuffer();
	
	protected CodeSection(int initialIndent) {
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
		lines.add(currentLine.toString());
		currentLine.setLength(0);
		return this;
	}

}
