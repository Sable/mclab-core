package natlab;

import java.io.*;
import java.util.StringTokenizer;

import junit.framework.TestCase;
import ast.Program;
import beaver.Symbol;

/** 
 * Parent class of the generated NatlabParserPassTests class.
 * Provides helper methods to keep NatlabParserPassTests short.
 */
class ParserPassTestBase extends TestCase {

	/* Construct a scanner that will read from the specified file. */
	static NatlabScanner getScanner(String filename) throws FileNotFoundException {
		return new NatlabScanner(new BufferedReader(new FileReader(filename)));
	}

	/*
	 * Returns a structure containing the contents of the .out file
	 * Format:
	 *   start_line start_col
	 *   end_line end_col
	 *   {string representation of AST - see StructureString.jrag}
	 */
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
			structureBuf.append('\n');
		}

		in.close();

		return new Structure(structureBuf.toString(), startLine, startCol, endLine, endCol);
	}

  private static String readNonWhitespaceLine(BufferedReader reader) throws IOException {
    String line;
    do {
      line = reader.readLine();
    } while (line != null && line.trim().isEmpty());
    if (line != null) {
      line = line.trim();
    }
    return line;
  }

	/* Check deep equality of an AST and the contents of the .out file. */
	public static void assertEquiv(Program actual, Structure expected) {
		try {
			BufferedReader expectedReader = new BufferedReader(new StringReader(expected.getStructureString()));
			BufferedReader actualReader = new BufferedReader(new StringReader(actual.getStructureString()));
			while(true) {
        String expectedLine = readNonWhitespaceLine(expectedReader);
				String actualLine = readNonWhitespaceLine(actualReader);

				if(!equals(expectedLine, actualLine)) {
					StringBuffer buf = new StringBuffer();
					if(expectedLine == null) {
						buf.append("Actual AST is larger than expected AST:\n");
						buf.append(actualLine);
						buf.append('\n');
						appendRemainingToBuffer(actualReader, buf);
						fail(buf.toString());
					} else if(actualLine == null) {
						buf.append("Expected AST is larger than actual AST:\n");
						buf.append(expectedLine);
						buf.append('\n');
						appendRemainingToBuffer(expectedReader, buf);
						fail(buf.toString());
					} else {
						buf.append("ASTs do not match:\n");
						buf.append("Remaining expected:\n");
						buf.append(expectedLine);
						buf.append('\n');
						appendRemainingToBuffer(expectedReader, buf);
						buf.append('\n');
						buf.append("Remaining actual:\n");
						buf.append(actualLine);
						buf.append('\n');
						appendRemainingToBuffer(actualReader, buf);
						fail(buf.toString());
					}
				} else if(actualLine == null) {
					break;
				}
			}
		} catch(IOException e) {
			//this can't happen since we're reading strings
			e.printStackTrace();
		}

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

	private static void appendRemainingToBuffer(BufferedReader reader, StringBuffer buf) throws IOException {
		while(true) {
			String line = reader.readLine();
			if(line == null) {
				break;
			}
			buf.append(line);
			buf.append('\n');
		}
	}
	
	private static boolean equals(String str1, String str2) {
		if(str1 == null) {
			return str2 == null;
		} else if(str2 == null) {
			return str1 == null;
		} else {
			return str1.equals(str2);
		}
	}

	/* struct for storing .out file contents. */
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
