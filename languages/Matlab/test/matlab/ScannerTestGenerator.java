package matlab;

import java.io.IOException;
import java.io.PrintWriter;

/** Generates matlab.ExtractionScannerTests from the provided list file. */
public class ScannerTestGenerator extends AbstractTestGenerator {
	private ScannerTestGenerator() {
		super("/matlab/ExtractionScannerTests.java");
	}

	public static void main(String[] args) throws IOException {
		new ScannerTestGenerator().generate(args);
	}

	protected void printHeader(PrintWriter testFileWriter) {
		testFileWriter.println("package matlab;");
		testFileWriter.println();
		testFileWriter.println("import java.util.ArrayList;");
		testFileWriter.println("import java.util.List;");
		testFileWriter.println();
		testFileWriter.println("import beaver.Scanner;");
		testFileWriter.println("import beaver.Symbol;");
		testFileWriter.println();
		testFileWriter.println("public class ExtractionScannerTests extends ScannerTestBase {");
	}

	protected void printMethod(PrintWriter testFileWriter, String testName) {
		String methodName = "test_" + testName;
		String inFileName = "test/" + testName + ".in";
		String outFileName = "test/" + testName + ".out";
		testFileWriter.println();
		testFileWriter.println("	public void " + methodName + "() throws Exception {");
		testFileWriter.println("		ExtractionScanner scanner = getScanner(\"" + inFileName + "\");");
		testFileWriter.println("		List<Symbol> symbols = new ArrayList<Symbol>();");
		testFileWriter.println("		Scanner.Exception exception = parseSymbols(\"" + outFileName + "\", symbols);");
		testFileWriter.println("		checkScan(scanner, symbols, exception);");
		testFileWriter.println("	}");
	}

	protected void printFooter(PrintWriter testFileWriter) {
		testFileWriter.println("}");
		testFileWriter.println();
	}
}
