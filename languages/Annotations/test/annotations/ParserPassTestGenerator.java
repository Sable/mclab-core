package annotations;


import java.io.IOException;
import java.io.PrintWriter;

/** Generates annotations.AnnotationParserPassTests from the provided list file. */
public class ParserPassTestGenerator extends AbstractTestGenerator {
	private ParserPassTestGenerator() {
		super("/annotations/AnnotationParserPassTests.java");
	}

	public static void main(String[] args) throws IOException {
		new ParserPassTestGenerator().generate(args);
	}

	protected void printHeader(PrintWriter testFileWriter) {
		testFileWriter.println("package annotations;");
		testFileWriter.println();
		testFileWriter.println("import annotations.ast.Annotation;");
		testFileWriter.println();
		testFileWriter.println("public class AnnotationParserPassTests extends ParserPassTestBase {");
	}

	protected void printMethod(PrintWriter testFileWriter, String testName) {
		String methodName = "test_" + testName;
		String inFileName = "test/" + testName + ".in";
		String outFileName = "test/" + testName + ".out";
		testFileWriter.println();
		testFileWriter.println("	public void " + methodName + "() throws Exception {");
		testFileWriter.println("		AnnotationScanner scanner = getScanner(\"" + inFileName + "\");");
		testFileWriter.println("		AnnotationParser parser = new AnnotationParser();");
		testFileWriter.println("		Annotation actual = (Annotation) parser.parse(scanner);");
		testFileWriter.println("		Structure expected = parseStructure(\"" + outFileName + "\");");
		testFileWriter.println("		assertEquiv(actual, expected);");
		testFileWriter.println("	}");
	}

	protected void printFooter(PrintWriter testFileWriter) {
		testFileWriter.println("}");
		testFileWriter.println();
	}
}
