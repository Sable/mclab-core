package natlab;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
//NB: only depends on stdlib

public abstract class TestGenerator {
	private final String relativeFilename;

	protected TestGenerator(String relativeFilename) {
		this.relativeFilename = relativeFilename;
	}

	protected void generate(String[] args) throws IOException {
		if(args.length != 2) {
			System.err.println("Usage: java " + getClass().getName() + " listFileName genDirName");
			System.exit(1);
		}

		String listFileName = args[0];
		String genDirName = args[1];

		List<String> testNames = getTestNames(listFileName);

		String testFileName = genDirName + relativeFilename;
		PrintWriter testFileWriter = new PrintWriter(new FileWriter(testFileName));
		printHeader(testFileWriter);
		for(String testName : testNames) {
			printMethod(testFileWriter, testName);
		}
		printFooter(testFileWriter);
		testFileWriter.close();
		System.exit(0);
	}

	private List<String> getTestNames(String listFileName) throws IOException {
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

	private boolean isCommentLine(String line) {
		return line.charAt(0) == '#';
	}

	protected abstract void printHeader(PrintWriter testFileWriter);

	protected abstract void printMethod(PrintWriter testFileWriter, String testName);

	protected abstract void printFooter(PrintWriter testFileWriter);
}
