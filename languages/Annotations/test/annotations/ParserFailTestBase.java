package annotations;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/** 
 * Parent class of the generated AnnotationParserFailTests class.
 * Provides helper methods to keep AnnotationParserFailTests short.
 */
class ParserFailTestBase extends TestCase {

	/* Construct a scanner that will read from the specified file. */
	static AnnotationScanner getScanner(String filename) throws FileNotFoundException {
		return new AnnotationScanner(new BufferedReader(new FileReader(filename)));
	}

	/* Read a list of errors from the .out file - one per line. */
	static List<String> readErrors(String filename) throws IOException {
		List<String> errors = new ArrayList<String>();
		BufferedReader in = new BufferedReader(new FileReader(filename));
		while(in.ready()) {
			errors.add(in.readLine());
		}
		in.close();
		return errors;
	}
}
