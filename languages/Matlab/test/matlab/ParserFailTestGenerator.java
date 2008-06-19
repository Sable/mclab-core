package matlab;

import java.io.IOException;
import java.io.PrintWriter;

/** Generates matlab.ExtractionParserFailTests from the provided list file. */
public class ParserFailTestGenerator extends AbstractTestGenerator {
	private ParserFailTestGenerator() {
		super("/matlab/ExtractionParserFailTests.java");
	}

	public static void main(String[] args) throws IOException {
		new ParserFailTestGenerator().generate(args);
	}

	protected void printHeader(PrintWriter testFileWriter) {
		testFileWriter.println("package matlab;");
		testFileWriter.println();
		testFileWriter.println("import java.util.List;");
		testFileWriter.println();
		testFileWriter.println("import beaver.Scanner;");
		testFileWriter.println();
		testFileWriter.println("public class ExtractionParserFailTests extends ParserFailTestBase {");
	}

	protected void printMethod(PrintWriter testFileWriter, String testName) {
		String methodName = "test_" + testName;
		String inFileName = "test/" + testName + ".in";
		String outFileName = "test/" + testName + ".out";
		testFileWriter.println();
		testFileWriter.println("	public void " + methodName + "() throws Exception {");
		testFileWriter.println("		List<String> expectedErrors = readErrors(\"" + outFileName + "\");");
		testFileWriter.println("		ExtractionScanner scanner = getScanner(\"" + inFileName + "\");");
		testFileWriter.println("		ExtractionParser parser = new ExtractionParser();");
		testFileWriter.println("		parser.parse(scanner);");
		testFileWriter.println("		assertTrue(parser.hasError());");
		testFileWriter.println("		assertEquals(expectedErrors, parser.getErrors());");
		testFileWriter.println("	}");
	}

	protected void printFooter(PrintWriter testFileWriter) {
		testFileWriter.println("}");
		testFileWriter.println();
	}
}
