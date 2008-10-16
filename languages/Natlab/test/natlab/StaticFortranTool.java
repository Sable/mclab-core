package natlab;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

import natlab.ast.*;

import beaver.Parser;

import natlab.toolkits.scalar.*;

import natlab.SymbolTableScope;
import natlab.SymbolTableEntry;
import annotations.ast.MatrixType;
import annotations.ast.Type;

/**
 * A utility for testing the structural flow analysis,  
 * 
 * This tool prints out the parse tree of the program of the input file,
 * and currently, it also prints out the structured string of the program.
 * The output file is named as: basename + ".tree"
 * 
 *  Extent the tool to support more functionalities
 *  	-m2n : translate .m to .n
 *  	-d   : convert all .n files of a folder to F95
 *  
 *  BTW: .n Errors
 *  1.	[3, 1]  unexpected token HELP_COMMENT
 *  	Cannot recover from the syntax error
 *  Sol: manual remove empty line after the function declaration
 */
public class StaticFortranTool {
	final static String benchmarkFolderName = "\\languages\\Fortran\\benchmarks";
	final static String TRANSLATION = "TRANSLATION";
	final static String CONVERTION = "CONVERTION";
	
	public static void translateFolder(String folderName) {
		translateFolder(folderName, TRANSLATION);
	}
	public static void convertFolder(String folderName) {
		translateFolder(folderName, CONVERTION);
	}
	public static String getAbslouteName(String name) {
		String absDirName = name;
		if(!name.subSequence(1, 2).equals(":")) {
			if(name.subSequence(0, 1).equals("\\")) {
				absDirName = System.getProperty("user.dir") + name;
			} else {
				absDirName = System.getProperty("user.dir") + name;
			}
		}
		return absDirName;
	}
	// Can handle one/two level directory 
	public static void translateFolder(String folderName, String task) {		
		// Absolute directory name = Current working folder + ...
		String absDirName = getAbslouteName(folderName);
	    File mainFolder = new File(absDirName);
	    System.out.println("Folder/file name: "+absDirName);
	    // Check whether it's a file, 
	    if(!mainFolder.isDirectory()) {
	    	if(task.equals(TRANSLATION)) {
	    		translateFile(absDirName);
	    	}
	    	return;
	    }
	    // Filtering the list of returned files.
        // This filter only returns .m files
    	FilenameFilter mfileFilter = new FilenameFilter() {
            public boolean accept(File file, String name) {
                return name.startsWith(".m", name.length()-2);
            }
        };
    	FilenameFilter nfileFilter = new FilenameFilter() {
            public boolean accept(File file, String name) {
                return name.startsWith(".n", name.length()-2);
            }
        };
	    // This filter only returns directories
	    FileFilter fileFilter = new FileFilter() {
	        public boolean accept(File file) {
	            return file.isDirectory();
	        }
	    };
	    File[] subFolders = mainFolder.listFiles(fileFilter);
	    if (subFolders == null || subFolders.length==0) {
	    	// System.err.println("Either dir does not exist or is not a directory.");
	    	// There is only one level of directory
	    	subFolders = new File[1];
	    	subFolders[0] = mainFolder;
	    } 
    	// Traverse all sub-folders
    	for(File dir : subFolders) {
    		if(task.equals(TRANSLATION)) {
			    String[] children = dir.list(mfileFilter);
	    		System.out.println("translateFile: "+dir);
		        for (int i=0; i<children.length; i++) {
		        	String filename = dir.getAbsolutePath()+"\\"+children[i];
		    		System.out.println("translateFile: "+filename);
		            translateFile(filename);
		        }
    		} else if (task.equals(CONVERTION)) {
			    String[] children = dir.list(nfileFilter);
		        for (int i=0; i<children.length; i++) {
		        	String filename = dir.getAbsolutePath()+"\\"+children[i];
		    		System.out.println("convert2Fortran: "+filename);
		        	convert2Fortran(filename);
		        }
    		}
    	}
	}
	public static void translateFile(String filename) {
        filename = removeExtension(filename, ".m");
		String[] files = {filename};
		System.out.println("Translating: "+filename);
		// matlab.Translator.main(files);
	}
	public static String removeExtension(String filename, String ext) {
		int len = filename.length() - ext.length();
        if(filename.substring(len).equals(ext)) {
        	filename = filename.substring(0,len);
        }
        return filename;
	}
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage: java natlab.StaticFortranTool {basename}");
			System.exit(1);
		}
		String basename = args[0];
		if(basename.equals("-m2n")) {	
        	// Translate .m => .n
        	if(args.length==1) {	// no extra argument, 
        		translateFolder(benchmarkFolderName);
        	} else {	// argument has the file name (with relative pathname) 
        		// i.e. -m2n \languages\Fortran\benchmark2_2.m
        		// i.e. -m2n \languages\Fortran\benchmarks\sdku\
        		// translateFolder() handles whether it's file/dir 
        		translateFolder(args[1]);        		
        	}
        	return;
        } else if(basename.equals("-d")) {	
        	// Convert a directory of .n files to Fortran
        	if(args.length==1) {	
        		// no extra argument, whole set of benchmarks
        		convertFolder(benchmarkFolderName);
        	} else {	// argument has the directory name 
        		// i.e. -d languages\Fortran\
        		convertFolder(args[1]);
        	}
        	return;
        }
        convert2Fortran(args[0]);
	}
	// Function for converting .n file into static Fortran
	public static void convert2Fortran(String filename) {
		filename = getAbslouteName(filename);
		System.out.println("Converting 2 Fortran : "+filename);
		String basename = filename;
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
			CompilationUnits cu = new CompilationUnits();
			PrintStream out = new PrintStream(basename + ".f95");
			Program actual = null;
			try {
				
				out.println("----addProgram--------------------------------------------");
				// cu.addProgram((Program) parser.parse(scanner));
                actual = (Program) parser.parse(scanner);
                cu.addProgram(actual);
	
                out.println("----getProgram  " + cu.getNumProgram());
				actual = cu.getProgram(0);
				out.println("---- starting ---------------------------------------------");
                if(parser.hasError()) {
                    for(String error : parser.getErrors()) {
                        out.println(error);
                    }
                } else {
    				// This is original output Matlab code
    				int startPos = actual.getStart();
    				int endPos = actual.getEnd();
    				out.println(Program.getLine(startPos) + " " + Program.getColumn(startPos));
    				out.println(Program.getLine(endPos) + " " + Program.getColumn(endPos));
    				out.print(actual.getStructureString()); //true to get transformed ast, false for raw
    				out.println();
    				out.println("---- Original AST ---------------------------------------------");
    				// out.print(actual.dumpTreeAll(false));
    				out.println("---------------------------------------------------------------");
                }
            } catch(Parser.Exception e) {
                for(String error : parser.getErrors()) {
                    out.println(error);
                }
                out.println(e.getMessage());
                System.err.println(e.getMessage());
    			System.exit(3);
            }
			
            if (actual==null) {
            	System.err.println("actual == null!");
            	System.exit(1);
            }
            
            //-----------------------------------------------------------------
            // Using SymbolTable
            
            SymbolTableScope stScope = actual.getSymbolTableScope();
            dumpSymbolTable(stScope, out, actual);
            // findVariable("run", stScope, out);
            // findVariable("a", stScope, out);

            
            //-----------------------------------------------------------------
            
			// Generate Fortron need 3 steps: labeled [1],[2],[3]
			// [1] Generate use/def boxes list of each node of the tree, basic step for all analysis
			actual.generateUseBoxesList();
			// TODO: should rename the variable's name, on different LHS 

			// Dump all of nodes by internal structure -- for debug purpose
			out.println();
			out.println("---- Dump Tree with All Info ---------------------------------");
			// out.print(actual.dumpTreeAll());
			out.println();
			out.println("---- Before Simplify -----------------------------------------");
			// out.print(actual.dumpTreeAll(false));

			// [2] Simplify complex nodes
			actual.simplify(null, 2);
			((Script) actual).addDefaultStmt();
			
			// Dump all of nodes by internal structure -- for debug purpose
			out.println();
			out.println("---- After Simplify  -----------------------------------------");
			// out.print(actual.dumpTreeAll(true));

            
			out.println("---- Adding Decl Nodes ---------------------------------------");
            //-----------------------------------------------------------------
            // <1> Add Decl node for new Variables after simplification 
            //     Happened when create new SymbolTableScope
            SymbolTableScope stScope2 = actual.getSymbolTableScope();

            // out.print(actual.dumpTreeAll(true));
            dumpSymbolTable(stScope2, out, actual);	// Debug purpose
            
            // <2> Inferring type for Decl nodes
            // checkSymbolTable(stScope2, actual);  // TODO: wait after Constant Propagation   
            
            out.println("---- After adding Decl Nodes ---------------------------------------");
            dumpSymbolTable(stScope2, out, actual);	// Debug purpose

            // checkVariableDeclNodeList(actual);	// Just for debug use
            out.print(actual.dumpTreeAll(true));
            
            //-----------------------------------------------------------------
			// Dump only the code-node -- for debug purpose
			// code-node: the node that we don't care about its subtree
			// Includes: simple-statement, assignment-stmt, ... 
			out.println();
			out.println("--- Code Nodes -----------------------------------------------");
			out.println("--- [<ID>] Node.Class.Name [code][Use-Boxes][Def-Boxes]-------");
			out.print(actual.dumpCodeTree());
			out.println("--- Simplified & with new Decl Nodes -------------------------");
			out.print(actual.getStructureString());
			
            dumpSymbolTable(stScope2, out, actual);	// Debug purpose

            //-----------------------------------------------------------------
            // Reaching definition analysis
			// [1] Generate use/def boxes list of each node of the tree 
            actual.clearUseDefBoxes();
			actual.generateUseBoxesList();
            
            out.println("--- Reaching-Definition Analysis ----------------------------");
			// Set the debug flag and out
			// AbstractFlowAnalysis.setDebug(true, (PrintStream) out);
			
			// [2] Calling Reaching Defs directly  
			ReachingDefs defsAnalysis = new ReachingDefs(actual);

			// [3] Retrieve the result	
			// Sample code for outputting the result flow-set (after set)
		    Map<ASTNode, FlowSet> defsMap = defsAnalysis.getResult(); 
			for(ASTNode node: defsAnalysis.getNodeList()) {
				FlowSet flowset = defsMap.get(node);
				// out.println("doAnalysis on: " + node.getNodeID()
				// 		+" ["+ node.getUseBoxes());	// node.dumpCode());
				// out.println("\t After-Flow: " + flowset);
			}
            //-----------------------------------------------------------------
			// Constant Propagation
			// TODO: 
			// <1> Check Symbol table, find constant variables
		    // System.err.println("Constant Propagation");
			int defID;
			String varName;
			LiteralExpr lExpr;
		    for( SymbolTableEntry e : (stScope2.symTable).values() ){
		    	if(e.isConstant())
		    			// (((VariableDecl) e.getDeclLocation()).getType()!=null)
			    		//  ((VariableDecl) e.getDeclLocation()).getType() 
			    		//		instanceof annotations.ast.PrimitiveType)
		    	{
		    		lExpr = e.getValue();
		    		varName = e.getSymbol();
					// <2> find the Def-node ID, which is AssignStmt node
	    		    ASTNode varNode = e.getNodeLocation();
	    		    ASTNode parentNode = e.getNodeLocation().getParent();
	    		    // e.getNodeLocation() is NameExpr, it may directly belongs to
    		    	// AssignStmt, or ParameterizedExpr.
	    		    // When it's parent is ParameterizedExpr, we cannot handle right now
	    		    // TODO: q(1)=1 =>
		    		if(!(parentNode instanceof AssignStmt)) {
		    				continue;
		    		}
		    		//
		    		//while (!(parentNode instanceof AssignStmt)) {
    		    	//	varNode = parentNode;
    		    	//	parentNode = parentNode.getParent(); 	    		    		
    		    	//}
    		    	defID = parentNode.getNodeID();

	    		    // System.err.println("Constant ["+ e.getSymbol()+"]="
	    		    // 		+" Value:" + lExpr.getStructureString()+" node:"+defID);
    		    	// <3> Check the U/D chain, get reachable code-node
    				for(ASTNode node: defsAnalysis.getNodeList()) {
    					FlowSet flowset = defsMap.get(node);
						// System.err.println("doAnalysis on: " + node.getNodeID() +
						//		"\t After-Flow: " + flowset);
    					if(node.getNodeID()!=defID
    							&& ((ArraySparseSet)flowset).contains(defID)) {
    						// System.err.println("doAnalysis on: " + node.getNodeID()
    						//		+" ["+ node.getUseBoxes());	// node.dumpCode());
    						// out.println("\t After-Flow: " + flowset);
	        				// <4> Propagating on that node
	    					node.constantPropagation(varName, lExpr);
    					}
    				}

		    		
		    	}
		    }
			out.println("--- Final m program -------------------------------------------");
			out.print(actual.getStructureString()); 
			
			// DO type infer AGAIN: before generate code
            //-----------------------------------------------------------------
            // <1> Add Decl node for new Variables after simplification 
            //     Happened when create new SymbolTableScope
            SymbolTableScope stScope3 = actual.getSymbolTableScope();

            // <2> Inferring type for Decl nodes
            checkSymbolTable(stScope3, actual);   
            
            out.println("---- After adding Decl Nodes [3] ------------------------------");
            dumpSymbolTable(stScope3, out, actual);	// Debug purpose

			// [2] Generate Static Fortran 
			out.println("--- Static Fortran --------------------------------------------");
			out.print(actual.getFortran()); //true to get transformed ast, false for raw
			out.println();
            // dumpSymbolTable(stScope2, out, actual);	// Debug purpose

			out.close();
			in.close();
			return;
		} catch(IOException e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
			System.exit(2);
		}
	}
	public static void findVariable(String name, SymbolTableScope stScope, PrintStream out) 
	{
        SymbolTableEntry stEntry = stScope.getSymbolById(name);
        out.println(stEntry);
        out.println("stEntry.getDeclLocation() - ");
        out.println(stEntry.getDeclLocation());	// this is null!
	}
	public static void dumpSymbolTable(SymbolTableScope stScope, PrintStream out,
			Program prog) 
	{
	    HashMap<String, SymbolTableEntry> table = stScope.symTable;
	    
	    StringBuffer buf = new StringBuffer();

	    for( SymbolTableEntry e : table.values() ){
	    	if(e.getDeclLocation()==null) {
	    		buf.append( e.getSymbol() +" [null]"); 
	    	} else {
	    		buf.append( e.getSymbol() +" "+ e.getDeclLocation().getNodeID() 
	        		+" "+ e.getNodeLocation().getNodeID() 
	        		+" ["+ (((VariableDecl) e.getDeclLocation()).getType()==null?"":
	        			((VariableDecl) e.getDeclLocation()).getType().getName())
	        		+"] {"+ (((VariableDecl) e.getDeclLocation()).getType()==null?"":
	        			((VariableDecl) e.getDeclLocation()).getType().getClass().getName())
	        		+"} "+ ((VariableDecl) e.getDeclLocation()).getStructureString()
	        		+"\n");
	    	}

	        // String getSymbol()
	        // ASTNode getDeclLocation() // natlab.ast.VariableDecl
	        // prog.localGetType(e.getSymbol()); //Ask for Name type
	    }
		out.println("---- Dump All entries of Symbol Table ---------------------------------");
		out.println(buf.toString());
		out.println("---- ---------------------------------");
	}
	public static void printVariableDeclNode(VariableDecl node) {
        System.out.println( node.getNodeID() 
        		+" "+ node 
        		+" "+ (node.getType()==null?"":node.getType().getName())
        		+" "+ ((VariableDecl) node).getStructureString()
        		);		
	}
	
	// This function used to verify the VariableDecl node in the AST 
	// are the save ones catched in Script.getDeclList()
	public static void checkVariableDeclNodeList(Program prog) 
	{	
		System.out.println( "checkVariableDeclNodeList") ;
		for(int i = 0; i < prog.getNumChild(); i++) {
            if (prog.getChild(i) != null) {
            	if(prog.getChild(i) instanceof natlab.ast.List) {
            		natlab.ast.List list = (natlab.ast.List)prog.getChild(i);
                    for(int j = 0; j < list.getNumChild(); j++) {
                    	if(list.getChild(j) instanceof natlab.ast.VariableDecl) {
                    		printVariableDeclNode((VariableDecl) list.getChild(j));
                    	}
                    }
            	} else if(prog.getChild(i) instanceof natlab.ast.VariableDecl) {
                	printVariableDeclNode((VariableDecl) prog.getChild(i));
                }                
            }
        }
		
		java.util.List<VariableDecl> declList = ((Script) prog).getDeclList();
       	System.out.println( "from saved VariableDeclNodeList"+declList.size()) ;
		for(VariableDecl decl : declList) {
	    	System.out.println("checkSymbolTable:"+decl.getID()+" "+ decl
	    			+" "+(decl.getType()==null?"":decl.getType().getName())
	    			+" "+decl.getNodeID());
		}
	}
	// Purpose: check the symbol-table, , looking for untyped entry
	// 		Calling "Type Inference" visitor, to do...
	public static void checkSymbolTable(SymbolTableScope stScope, Program prog) 
	{
		natlab.TypeInferenceEngine TIEngine = new TypeInferenceEngine();
		// prog.generateUseBoxesList();
		prog.setTypeInferenceEngine(TIEngine);
		
	    HashMap<String, SymbolTableEntry> table = stScope.symTable;
	    VariableDecl varDecl;
	    for( SymbolTableEntry e : table.values() ){
	    	varDecl = (VariableDecl) e.getDeclLocation();
	    	if ( varDecl == null) {
	    		System.err.println("VariableDecl of ["+e.getSymbol()+"] should not be null!");
	    	} else {
    	    	//System.out.println("checkSymbolTable:"+e.getSymbol()
    	    	//		+" "+(varDecl.getType()==null? "null":varDecl.getType().getName()));
	    		// for those untyped entry
	    		// TODO: for some type missing dimension,still need to re-infer 
	    		if(varDecl.getType()==null 
	    				|| varDecl.getType() instanceof annotations.ast.UnknownType) {
	    			
	    		    //System.out.println("checkSymbolTable:"+e.getSymbol()
	    	    	//		+" "+e.getDeclLocation().getNodeID()
	    	    	//		+" "+e.getNodeLocation().getNodeID()
	    	    	//		+" "+e.getNodeLocation()
	    	    	//		+" "+e.getNodeLocation().getParent()
	    	    	//		+" "+e.getNodeLocation().getParent().getNodeID());
	    		    ASTNode varNode = e.getNodeLocation();
	    		    ASTNode parentNode = e.getNodeLocation().getParent();
	    		    annotations.ast.Type varType = null;
	    		    // e.getNodeLocation() is NameExpr, it may directly belongs to
    		    	// AssignStmt, or ParameterizedExpr.
    		    	while (!(parentNode instanceof AssignStmt)) {
    		    		varNode = parentNode;
    		    		parentNode = parentNode.getParent(); 	    		    		
    		    	} ;
	    		    varType = ((AssignStmt) parentNode).getRHS().collectType(stScope, varNode);
	    		    varDecl.setType(varType);
	    		    // Disable "Refine" rewrite defined in TypeInference.jrag 
	    		    if(varType != null)
	    		    	varDecl.setRefine(true);
	    		    
	    		    //System.out.println("<3\t ["+ e.getSymbol()+"]="
	    		    //		+(varType==null? "null":varType.getName())
	    		    //		+" DeclNode:" + varDecl.getNodeID());
	    		    		// +(varType instanceof MatrixType?"":((MatrixType) varType).getSize()));
	    		}
	    	}
	    } 
	}
}
