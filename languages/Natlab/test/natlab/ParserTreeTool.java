package natlab;

import java.io.*;

import ast.Program;
import beaver.Parser;

/**
 * A utility for producing the output file corresponding to a given input file.
 * Note that the output should be checked manually before using it as a test.
 * This tool prints out the parse tree of the program of the input file,
 * and currently, it also prints out the structured string of the program.
 * The output file is named as: basename + ".tree" 
 */
public class ParserTreeTool {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java natlab.ParserTreeTool {basename}");
            System.exit(1);
        }
        String basename = args[0];
        try {
            BufferedReader in = new BufferedReader(new FileReader(basename + ".in"));

            CommentBuffer commentBuffer = new CommentBuffer();
            NatlabScanner scanner = new NatlabScanner(in);
            scanner.setCommentBuffer(commentBuffer);
            NatlabParser parser = new NatlabParser();
            parser.setCommentBuffer(commentBuffer);
            PrintWriter out = new PrintWriter(new FileWriter(basename + ".tree"));
            try {
                Program actual = (Program)((Program) parser.parse(scanner));//.rewriteTo();
                if(parser.hasError()) {
                    for(String error : parser.getErrors()) {
                        out.println(error);
                    }
                } else {
                    int startPos = actual.getStart();
                    int endPos = actual.getEnd();
                    out.println(Program.getLine(startPos) + " " + Program.getColumn(startPos));
                    out.println(Program.getLine(endPos) + " " + Program.getColumn(endPos));
                    out.print(actual.getStructureString());
                    out.println();
                    out.print(actual.dumpTree());
                }
            } catch(Parser.Exception e) {
                for(String error : parser.getErrors()) {
                    out.println(error);
                }
                out.println(e.getMessage());
            }
            out.close();
            in.close();
            System.exit(0);
        } catch(IOException e) {
            e.printStackTrace();
            System.exit(2);
        }
    }
}
