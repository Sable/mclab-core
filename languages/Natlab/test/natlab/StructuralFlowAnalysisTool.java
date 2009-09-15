package natlab;

import java.io.*;
import java.util.HashSet;
import java.util.Map;

import ast.ASTNode;
import ast.IfStmt;
import ast.Program;
import ast.Stmt;
import ast.SwitchStmt;
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
		    
		    FlowSet newDefSet =  defsAnalysis.getEmptySet();
		    FlowSet stopDefSet =  defsAnalysis.getEmptySet();
		    FlowSet preFlowset = null; 
		    
		    // Go through each code-node, check its before/after flow-sets
			for(ASTNode node: defsAnalysis.getNodeList()) {
				FlowSet afterSet = defsMap.get(node);
				FlowSet beforeSet = beforeMap.get(node);
				out.println("doAnalysis on: " + node.getNodeID()
				 		+" ["+ node.getDefBoxes()+"] ["+ node.getUseBoxes());	
				out.println("\t Before-Flow: " + beforeSet);
				out.println("\t After-Flow: " + afterSet);

				// Analyze the before/after flow sets, get the difference between them
				if(beforeSet!=null)
					preFlowset = beforeSet;
				else 
					preFlowset = defsAnalysis.getEmptySet();				
				// stopDefSet: stopped definitions
				preFlowset.difference(afterSet, stopDefSet);
				// newDefSet: new added definitions
				afterSet.difference(preFlowset, newDefSet);
				out.println("\t Stop-Defs: " + stopDefSet);
				out.print("\t New--Defs: " + newDefSet);
				printVariableNames(newDefSet, out);
				
				// Special treatment for IfStmt and SwitchStmt
				if(node instanceof IfStmt || node instanceof SwitchStmt) {
					// Using alternative result from Reaching-Def-Analysis
					// Where only return the flow set of 1st iteration
				    Map<Stmt, FlowSet> filterBeforeMap = defsAnalysis.getfilterBeforeFlow();
				    FlowSet fBSet = filterBeforeMap.get((Stmt)node);
				    Map<ASTNode, FlowSet> filterAfterMap = defsAnalysis.getfilterAfterFlow();
				    FlowSet fASet = filterAfterMap.get(node);
				    if(fBSet!=null && fASet!=null) {
					    FlowSet tmpSet =  defsAnalysis.getEmptySet();
					    fASet.difference(fBSet, tmpSet);
						out.print("\t #New#Defs: " + tmpSet);
						printVariableNames(tmpSet, out);
				    }
				}
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
	private static void printVariableNames(FlowSet newDefSet, PrintStream out) {
		// Print out names of new defined variables
		HashSet<String> varSet =  new HashSet<String>();
		for(Object defObj:newDefSet.toList()) {
			for(Object vb: ((ASTNode) defObj).getDefBoxes()) {
				varSet.add(((natlab.toolkits.ValueBox) vb).getValue());
			}
		}
		out.println("\t" + varSet);
		
	}
}

