package natlab;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;

import junit.framework.TestCase;
import natlab.ast.Root;
import beaver.Scanner;
import beaver.Symbol;

class ParserPassTestBase extends TestCase {
	static Scanner getScanner(String filename) throws FileNotFoundException {
		return new NatlabScanner(new BufferedReader(new FileReader(filename)));
	}

	static Structure parseStructure(String filename) throws IOException {
		StringBuffer structureBuf = new StringBuffer();
		BufferedReader in = new BufferedReader(new FileReader(filename));

		StringTokenizer startTokenizer = new StringTokenizer(in.readLine());
		int startLine = Integer.parseInt(startTokenizer.nextToken());
		int startCol = Integer.parseInt(startTokenizer.nextToken());

		StringTokenizer endTokenizer = new StringTokenizer(in.readLine());
		int endLine = Integer.parseInt(endTokenizer.nextToken());
		int endCol = Integer.parseInt(endTokenizer.nextToken());

		while(in.ready()) {
			structureBuf.append(in.readLine());
		}

		in.close();

		return new Structure(structureBuf.toString(), startLine, startCol, endLine, endCol);
	}

	public static void assertEquiv(Root actual, Structure expected) {
		assertEquals(expected.getStructureString(), actual.getStructureString());

		int actualStartPos = actual.getStart();
		int actualStartLine = Symbol.getLine(actualStartPos);
		int actualStartCol = Symbol.getColumn(actualStartPos);
		assertEquals(expected.getStartLine(), actualStartLine);
		assertEquals(expected.getStartCol(), actualStartCol);

		int actualEndPos = actual.getEnd();
		int actualEndLine = Symbol.getLine(actualEndPos);
		int actualEndCol = Symbol.getColumn(actualEndPos);
		assertEquals(expected.getEndLine(), actualEndLine);
		assertEquals(expected.getEndCol(), actualEndCol);
	}

	static class Structure {
		private final String structureString;
		private final int startLine;
		private final int startCol;
		private final int endLine;
		private final int endCol;

		protected Structure(String structureString, int startLine, int startCol, int endLine, int endCol) {
			this.structureString = structureString;
			this.startLine = startLine;
			this.startCol = startCol;
			this.endLine = endLine;
			this.endCol = endCol;
		}

		public String getStructureString() {
			return structureString;
		}

		public int getStartLine() {
			return startLine;
		}

		public int getStartCol() {
			return startCol;
		}

		public int getEndLine() {
			return endLine;
		}

		public int getEndCol() {
			return endCol;
		}
	}
}
