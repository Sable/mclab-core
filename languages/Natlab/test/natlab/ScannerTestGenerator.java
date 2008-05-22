package natlab;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
//NB: only depends on stdlib

public class ScannerTestGenerator {
	private ScannerTestGenerator() {}

	public static void main(String[] args) throws IOException {
		if(args.length != 2) {
			System.err.println("Usage: java lambda.TestGenerator listFileName genDirName");
			System.exit(1);
		}

		String listFileName = args[0];
		String genDirName = args[1];

		List<String> testNames = getTestNames(listFileName);

		String testFileName = genDirName + "/natlab/NatlabScannerTests.java";
		PrintWriter testFileWriter = new PrintWriter(new FileWriter(testFileName));
		printHeader(testFileWriter);
		for(String testName : testNames) {
			printMethod(testFileWriter, testName);
		}
		printFooter(testFileWriter);
		testFileWriter.close();
	}

	private static List<String> getTestNames(String listFileName) throws IOException {
		List<String> testNames = new ArrayList<String>();
		BufferedReader listFileReader = new BufferedReader(new FileReader(listFileName));
		while(listFileReader.ready()) {
			String line = listFileReader.readLine().trim();
			if(!isCommentLine(line)) {
				testNames.add(line);
			}
		}
		listFileReader.close();
		return testNames;
	}

	private static boolean isCommentLine(String line) {
		return line.charAt(0) == '#';
	}

	private static void printHeader(PrintWriter testFileWriter) {
		testFileWriter.println("package natlab;");
		testFileWriter.println();
		testFileWriter.println("import java.util.ArrayList;");
		testFileWriter.println("import java.util.List;");
		testFileWriter.println();
		testFileWriter.println("import beaver.Scanner;");
		testFileWriter.println("import beaver.Symbol;");
		testFileWriter.println();
		testFileWriter.println("public class NatlabScannerTests extends ScannerTestBase {");
	}

	private static void printMethod(PrintWriter testFileWriter, String testName) {
		String methodName = "test_" + testName;
		String inFileName = "test/" + testName + ".in";
		String outFileName = "test/" + testName + ".out";
		testFileWriter.println();
		testFileWriter.println("	public void " + methodName + "() throws Exception {");
		testFileWriter.println("		Scanner scanner = getScanner(\"" + inFileName + "\");");
		testFileWriter.println("		List<Symbol> symbols = new ArrayList<Symbol>();");
		testFileWriter.println("		Scanner.Exception exception = parseSymbols(\"" + outFileName + "\", symbols);");
		testFileWriter.println("		checkScan(scanner, symbols, exception);");
		testFileWriter.println("	}");
	}

	private static void printFooter(PrintWriter testFileWriter) {
		testFileWriter.println("}");
		testFileWriter.println();
	}
}
