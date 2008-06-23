package natlab;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import natlab.ast.Program;
import beaver.Parser;

public class Interpreter {
	private Interpreter() {}

	public static void main(String[] args) throws IOException {
		System.out.println("Welcome to the Natlab Interpreter!");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			System.out.print("> ");
			System.out.flush();

			String line = in.readLine();
			if(line == null) {
				break;
			}

			CommentBuffer commentBuffer = new CommentBuffer();
			NatlabParser parser = new NatlabParser();
			parser.setCommentBuffer(commentBuffer);
			try {
				// Temporarily changed to accept a file name - JL 2008.05.27 
				NatlabScanner scanner = new NatlabScanner(new FileReader(line));
				scanner.setCommentBuffer(commentBuffer);
				//NatlabScanner scanner = new NatlabScanner(new StringReader(line));
				Program original = (Program) parser.parse(scanner);
				if(parser.hasError()) {
					System.out.println("**ERROR**");
					for(String error : parser.getErrors()) {
						System.out.println(error);
					}
				} else {
					java.util.List<String> weedingErrors = original.getWeedingErrors();
					if(!weedingErrors.isEmpty()) {
						System.out.println("Weeding Error(s):");
						for(String error : weedingErrors) {
							System.out.println(error);
						}
					} else
					// Using Pretty print
					//System.out.println(original.getPrettyPrinted());
					System.out.println(original.getStructureString());
					// System.out.println(original.dumpTree());
				}
			} catch(Parser.Exception e) {
				System.out.println("**ERROR**");
				System.out.println(e.getMessage());
				for(String error : parser.getErrors()) {
					System.out.println(error);
				}
			}
		}
		System.out.println("Goodbye!");
	}
}
