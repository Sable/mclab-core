package matlab;

import java.io.IOException;
import java.io.PrintWriter;

/** Generates matlab.ExtractionTranslatorTests from the provided list file. */
public class TranslatorTestGenerator extends AbstractTestGenerator {
	private TranslatorTestGenerator() {
		super("/matlab/ExtractionTranslatorTests.java");
	}

	public static void main(String[] args) throws IOException {
		new TranslatorTestGenerator().generate(args);
	}

	protected void printHeader(PrintWriter testFileWriter) {
		testFileWriter.println("package matlab;");
		testFileWriter.println();
		testFileWriter.println("import matlab.ast.Program;");
		testFileWriter.println();
		testFileWriter.println("public class ExtractionTranslatorTests extends TranslatorTestBase {");
	}

	protected void printMethod(PrintWriter testFileWriter, String testName) {
		String methodName = "test_" + testName;
		String inFileName = "test/" + testName + ".in";
		String outFileName = "test/" + testName + ".out";
		testFileWriter.println();
		testFileWriter.println("	public void " + methodName + "() throws Exception {");
		testFileWriter.println("		ExtractionScanner scanner = getScanner(\"" + inFileName + "\");");
		testFileWriter.println("		ExtractionParser parser = new ExtractionParser();");
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
