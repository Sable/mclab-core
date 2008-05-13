import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import ast.Node;
import ast.Root;

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
				LambdaScanner scanner = new LambdaScanner(new StringReader(line));
				LambdaParser parser = new LambdaParser();
				Root original = (Root) parser.parse(scanner);
				Node reduced = original.reduce();

				System.out.println("Before: " + original.getStructureString());
				System.out.println("After:  " + reduced.getStructureString());
			} catch(Exception e) {
				System.out.println("Error:  " + e.getMessage());
			}
		}
		System.out.println("Goodbye!");
	}
}
