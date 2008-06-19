package matlab;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
//NB: only depends on stdlib

/**
 * Skeleton implementation for a test generator class.
 */
abstract class AbstractTestGenerator {
	private final String relativeFilename; //relative path to output file

	protected AbstractTestGenerator(String relativeFilename) {
		this.relativeFilename = relativeFilename;
	}

	/*
	 * To be called by main(String[] args)
	 * NB: calls System.exit
	 */
	protected void generate(String[] args) throws IOException {
		if(args.length != 2) {
			System.err.println("Usage: java " + getClass().getName() + " listFileName genDirName");
			System.exit(1);
		}

		String listFileName = args[0]; //name of file containing list of tests (to become methods)
		String genDirName = args[1];   //path to output directory (i.e. gen)

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

	/* Read a list of test names from the list file */
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

	/* True indicates that the line should be ignored */
	private boolean isCommentLine(String line) {
		return line.charAt(0) == '#';
	}

	/* print the beginning of the class */
	protected abstract void printHeader(PrintWriter testFileWriter);

	/* print a single test method */
	protected abstract void printMethod(PrintWriter testFileWriter, String testName);

	/* print the end of the class */
	protected abstract void printFooter(PrintWriter testFileWriter);
}
