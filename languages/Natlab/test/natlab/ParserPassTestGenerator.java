package natlab;

import java.io.IOException;
import java.io.PrintWriter;

/** Generates natlab.NatlabParserPassTests from the provided list file. */
public class ParserPassTestGenerator extends AbstractTestGenerator {
	private ParserPassTestGenerator() {
		super("/natlab/NatlabParserPassTests.java");
	}

	public static void main(String[] args) throws IOException {
		new ParserPassTestGenerator().generate(args);
	}

	protected void printHeader(PrintWriter testFileWriter) {
		testFileWriter.println("package natlab;");
		testFileWriter.println();
		testFileWriter.println("import ast.Program;");
		testFileWriter.println();
		testFileWriter.println("public class NatlabParserPassTests extends ParserPassTestBase {");
	}

	protected void printMethod(PrintWriter testFileWriter, String testName) {
		String methodName = "test_" + testName;
		String inFileName = "test/" + testName + ".in";
		String outFileName = "test/" + testName + ".out";
		testFileWriter.println();
		testFileWriter.println("	public void " + methodName + "() throws Exception {");
		testFileWriter.println("		CommentBuffer commentBuffer = new CommentBuffer();");
		testFileWriter.println("		NatlabScanner scanner = getScanner(\"" + inFileName + "\");");
		testFileWriter.println("		scanner.setCommentBuffer(commentBuffer);");
		testFileWriter.println("		NatlabParser parser = new NatlabParser();");
		testFileWriter.println("		parser.setCommentBuffer(commentBuffer);");
		testFileWriter.println("		Program actual = (Program) parser.parse(scanner);");
		testFileWriter.println("		Structure expected = parseStructure(\"" + outFileName + "\");");
		testFileWriter.println("		assertEquiv(actual, expected);");
		testFileWriter.println("	}");
	}

	protected void printFooter(PrintWriter testFileWriter) {
		testFileWriter.println("}");
		testFileWriter.println();
	}
}
