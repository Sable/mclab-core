package natlab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import natlab.ast.Root;

public class Interpreter {
	private Interpreter() {}

	public static void main(String[] args) throws IOException {
		System.out.println("Welcome to the Lambda Calculus Interpreter!");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			System.out.print("> ");
			System.out.flush();

			String line = in.readLine();
			if(line == null) {
				break;
			}

			try {
				NatlabScanner scanner = new NatlabScanner(new StringReader(line));
				NatlabParser parser = new NatlabParser();
				Root original = (Root) parser.parse(scanner);

				System.out.println(original.getStructureString());
			} catch(Exception e) {
				System.out.println("Error:  " + e.getMessage());
			}
		}
		System.out.println("Goodbye!");
	}
}
