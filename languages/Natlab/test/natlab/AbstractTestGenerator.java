// =========================================================================== //
//                                                                             //
// Copyright 2011 Andrew Casey and McGill University.                          //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//  limitations under the License.                                             //
//                                                                             //
// =========================================================================== //

package natlab;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
//NB: only depends on stdlib

/**
 * Skeleton implementation for a test generator class.
 */
abstract class AbstractTestGenerator {
	protected final String relativeFilename; //relative path to output file

	protected AbstractTestGenerator(String relativeFilename) {
		this.relativeFilename = relativeFilename;
	}

        /**
         * Validates the given arguments. Ensures that there are exactly
         * two arguments. If there isn't, an error msg is printed and the
         * program is exited with code 1.
         */
        protected void validateArgs(String[] args)
        {
                if(args.length != 2) {
                    System.err.println("Usage: java " + getClass().getName() + " listFileName genDirName");
                    System.exit(1);
                }
        }
	/*
	 * To be called by main(String[] args)
	 * NB: calls System.exit
	 */
	protected void generate(String[] args) throws IOException {
		
                validateArgs(args);

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
	protected List<String> getTestNames(String listFileName) throws IOException {
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
	protected boolean isCommentLine(String line) {
		return line.charAt(0) == '#';
	}

	/* print the beginning of the class */
	protected abstract void printHeader(PrintWriter testFileWriter);

	/* print a single test method */
	protected abstract void printMethod(PrintWriter testFileWriter, String testName);

	/* print the end of the class */
	protected abstract void printFooter(PrintWriter testFileWriter);
}
