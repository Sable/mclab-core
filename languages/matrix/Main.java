
// java Main Test.m >log.txt

import java.io.*;
import beaver.Symbol;
import beaver.Scanner;
import beaver.Parser;

import ast.*;

class Main
{
	private static PrintStream out = System.out;
	public static void main(String[] args) throws Exception
	{
		
		if (args.length == 2) {
			try {
				out = new PrintStream(new File("log.txt"));
			} catch (IOException exception) {
				out.println("Could not open " + "log.txt" + " for writing");
				return;
			}
		}
		
		out.println("------------------ Testing Scanner -----------------------------------------");
		MatrixScanner input = new MatrixScanner(new FileReader(args[0]));
			try
			{
				int i=1;
				Symbol token; 
				do{
					token = input.nextToken();
					out.println("Token #" + i+ ": " 
							+ MatrixParser.Terminals.NAMES[token.getId()]+ "\t" 
							+ token.getLine(token.getStart())+","+token.getColumn(token.getStart()) 
							+ " ~ "+ 
							+ token.getLine(token.getEnd())+","+token.getColumn(token.getEnd()) 
							+ "\t\t" 
							+ token.value);
					i++;
				} while (token.getId()!=MatrixParser.Terminals.EOF);
			}
			catch (Exception e)
			{
				System.err.println("Failed to read expression: " + e.getMessage());
			}

		out.println("------------------ Parsering  -----------------------------------------");

		input = new MatrixScanner(new FileReader(args[0]));
        MatrixParser parser = new MatrixParser();
        try {
        	Program prg = (Program) parser.parse(input);
			if(parser.hasError()) {
				System.out.println("**ERROR**");
				for(String error : parser.getErrors()) {
					System.out.println(error);
				}
			} else {
				out.println("-- Parsing success!");
	    		out.println("------------------ Testing Parser : Parser tree -------------------------");
	        	out.println(prg.dumpTree());
	        	// out.println(prg.dumpTreeNoRewrite());
	    		out.println("------------------ Testing : Structure String -------------------------");
	        	out.println(prg.getStructureString());
	        	
				// System.out.println(original.getStructureString());
			}
        } catch(Exception e) {
        	System.err.println("Failed to get parsed program object: " + e.getMessage());
        }
		out.println("------------------ The End -----------------------------------------");
	}
}
