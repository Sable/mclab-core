package natlab;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import beaver.Scanner;

class ParserFailTestBase extends TestCase {
	static Scanner getScanner(String filename) throws FileNotFoundException {
		return new NatlabScanner(new BufferedReader(new FileReader(filename)));
	}

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
