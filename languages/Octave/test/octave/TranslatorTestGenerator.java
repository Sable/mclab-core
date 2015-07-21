package octave;

import java.io.IOException;
import java.io.PrintWriter;

/** Generates octave.OctaveTranslatorTests from the provided list file. */
public class TranslatorTestGenerator extends AbstractTestGenerator {
	private TranslatorTestGenerator() {
		super("/octave/OctaveTranslatorTests.java");
	}

	public static void main(String[] args) throws IOException {
		new TranslatorTestGenerator().generate(args);
	}

	protected void printHeader(PrintWriter testFileWriter) {
		testFileWriter.println("package octave;");
		testFileWriter.println();
		testFileWriter.println("public class OctaveTranslatorTests extends TranslatorTestBase {");
	}

	protected void printMethod(PrintWriter testFileWriter, String testName) {
		String methodName = "test_" + testName;
		String inFileName = "test/" + testName + ".in";
		String outFileName = "test/" + testName + ".out";
		testFileWriter.println();
		testFileWriter.println("	public void " + methodName + "() throws Exception {");
		testFileWriter.println("		ActualTranslation actual = parseActualTranslation(\"" + inFileName + "\");");
		testFileWriter.println("		ExpectedTranslation expected = parseExpectedTranslation(\"" + outFileName + "\");");
		testFileWriter.println("		assertEquiv(actual, expected);");
		testFileWriter.println("	}");
	}

	protected void printFooter(PrintWriter testFileWriter) {
		testFileWriter.println("}");
		testFileWriter.println();
	}
}
