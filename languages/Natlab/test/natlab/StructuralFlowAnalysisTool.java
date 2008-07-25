package natlab;

import java.io.*;
import java.util.Map;

import natlab.ast.ASTNode;
import natlab.ast.Program;
import beaver.Parser;

import natlab.toolkits.scalar.*;

/**
 * A utility for testing the structural flow analysis,  
 * 
 * This tool prints out the parse tree of the program of the input file,
 * and currently, it also prints out the structured string of the program.
 * The output file is named as: basename + ".tree" 
 */
public class StructuralFlowAnalysisTool {
	public static void main(String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: java natlab.StructuralFlowAnalysisTool {basename}");
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
			Program actual = (Program) parser.parse(scanner);
			PrintStream out = new PrintStream(basename + ".tree");
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

			// Using the Reaching-Def analysis need 3 steps: labeled [1],[2],[3]
			// [1] Generate use/def boxes list of each node of the tree 
			actual.generateUseBoxesList();
			
			// Dump all of nodes by internal structure -- for debug purpose
			out.println();
			out.print(actual.dumpTreeAll());
			
			// Dump only the code-node -- for debug purpose
			// code-node: the node that we don't care about its subtree
			// Includes: simple-statement, assignment-stmt, ... 
			out.println();
			out.println("--- Code Nodes -----------------------------------------------");
			out.println("--- [<ID>] Node.Class.Name [code][Use-Boxes][Def-Boxes]-------");
			out.print(actual.dumpCodeTree());
						
			out.println("--- Reaching-Definition Analysis ----------------------------");
			// Set the debug flag and out
			AbstractFlowAnalysis.setDebug(true, (PrintStream) out);
			
			// [2] Calling Reaching Defs directly  
			ReachingDefs defsAnalysis = new ReachingDefs(actual);

			// [3] Retrieve the result	
			// Sample code for outputing the result flow-set (after set)
		    Map<ASTNode, FlowSet> defsMap = defsAnalysis.getResult(); 
			for(ASTNode node: defsAnalysis.getNodeList()) {
				FlowSet flowset = defsMap.get(node);
				out.println("doAnalysis on: " + node.getNodeID());	// node.dumpCode());
				out.println("\t After-Flow: " + flowset);
			}

			out.close();
			in.close();
			System.exit(0);
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(2);
		} catch (Parser.Exception e) {
			e.printStackTrace();
			System.exit(3);
		}
	}
}

