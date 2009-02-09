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
		String filename = StaticFortranTool.getAbslouteName(basename);
		System.out.println("Converting 2 Fortran : "+filename);
		basename = filename;
        if(basename.substring(basename.length()-2).equals(".m")
        		|| basename.substring(basename.length()-2).equals(".n")) {
        	basename = basename.substring(0,basename.length()-2);
        } 
		try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			
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
				// out.print(actual.dumpTree());
			}

			//-----------------------------------------------------------------
			// Reaching Definitions Analysis example:
			//-----------------------------------------------------------------
			// Using the Reaching-Def analysis need 3 steps: labeled [1],[2],[3]
			// [1] Generate use/def boxes each node of the tree, 
			//		here actual is the root. 
			actual.generateUseBoxesList();
			
			// Dump all of nodes by internal structure -- for debug purpose
			// out.println();
			// out.print(actual.dumpTreeAll());
			
			// Dump only the code-node -- for debug purpose
			// code-node: the node that we don't care about its subtree
			// Includes: simple-statement, assignment-stmt, ... 
			out.println();
			out.println("--- Code Nodes -----------------------------------------------");
			out.println("--- [<ID>] Node.Class.Name [code][Use-Boxes][Def-Boxes]-------");
			out.println(actual.dumpCodeTree());
						
			// out.println("--- Reaching-Definition Analysis ----------------------------");
			// Set the debug flag and out stream, which will show analysis details 
			// AbstractFlowAnalysis.setDebug(true, (PrintStream) out);
			
			// [2] Calling Reaching Defs directly  
			ReachingDefs defsAnalysis = new ReachingDefs(actual);

			// [3] Retrieve the result	
			// Sample code for outputting the result flow-set (after set)
			out.println("---- Example of result flowsets ------------------------");
			// Retrieve after flow-sets
		    Map<ASTNode, FlowSet> defsMap = defsAnalysis.getResult(); 
			// Retrieve before flow-sets
		    Map<ASTNode, FlowSet> beforeMap = defsAnalysis.getBeforeFlow();
		    // Go through each code-node, check its before/after flow-sets
			for(ASTNode node: defsAnalysis.getNodeList()) {
				FlowSet flowset = defsMap.get(node);
				FlowSet beforeSet = beforeMap.get(node);
				out.println("doAnalysis on: " + node.getNodeID()
				 		+" ["+ node.getDefBoxes()+"] ["+ node.getUseBoxes());	
				out.println("\t Before-Flow: " + beforeSet);
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

