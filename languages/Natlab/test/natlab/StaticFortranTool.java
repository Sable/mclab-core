package natlab;

import java.io.*;

import java.util.*;

import natlab.ast.*;
import natlab.ast.List;

import beaver.Parser;

import natlab.toolkits.scalar.*;

import natlab.SymbolTableScope;
import natlab.SymbolTableEntry;
import annotations.ast.ArgTupleType;
import annotations.ast.MatrixType;
import annotations.ast.PrimitiveType;
import annotations.ast.Size;
import annotations.ast.Type;

/**
 * A utility for testing the MATLAB to Fortran code generator  
 * 
 *  Usage: 
 *  	-m2n [folder-name | filename]: translate .m to .n
 *  	filename
 *  	-d   folder-name: convert all .n files of the folder to F95
 *  			- pathname starting by '/' will be treated as 
 *  				absolution pathname, otherwise are seen as relative pathname 
 *  
 *  BTW: .n Errors
 *  1.	[3, 1]  unexpected token HELP_COMMENT
 *  	Cannot recover from the syntax error
 *  Solution: manual remove empty line after the function declaration
 *  2. Usage Example:
 *  (1) -m2n \languages\Fortran\benchmarks
 *  (2) -d .\languages\Fortran\benchmarks\adapt\
 */ 
public class StaticFortranTool {
	static boolean DEBUG = false; 	
	static boolean DEBUG_Renaming = false && DEBUG;	
	static boolean DEBUG_TreeFinal =  true && DEBUG; 	// the final tree before code gen 
	static boolean DEBUG_Default = false && DEBUG;
	static boolean DEBUG_Simplify= false && DEBUG;	
	static boolean DEBUG_Tree   =  true && DEBUG; 	// for anything about symbol table/entry 
	static boolean DEBUG_Symbol = true && DEBUG; 	// for anything about symbol table/entry
	static boolean DEBUG_Type  = true && DEBUG; 	// for  Symbol Entry TypeInferenceEngine 
	static boolean DEBUG_SymTbl = true && DEBUG;
	static boolean DEBUG_Flow  = false && DEBUG; 	// for [varRuleMap]
	static boolean DEBUG_Const = false && DEBUG;
	static boolean DEBUG_Func  = false && DEBUG ;
	static boolean DEBUG_Alloc = false && DEBUG ;
	static boolean DEBUG_Order = (true || DEBUG_Func) && DEBUG;
	static boolean DEBUG_Eval  = false && DEBUG ;	

	static boolean DEBUG_isFirmType = false; 
		
	final static String benchmarkFolderName = "\\languages\\Fortran\\benchmarks";
	final static String TRANSLATION = "TRANSLATION";
	final static String CONVERTION = "CONVERTION"; 
	public final static String TEMP_VAR_PREFIX = "tmpvar_";
	public static final String PhiFunctionName = "PHISSA";
	public final static String TEMP_M_FILENAME = "_temp_m2f.m";

	public static String PATH_STRING = "/";			// different in DOS and Linux 

	// Collect uses from declaration
	public static java.util.List<String> gExtraVarList = new ArrayList<String>();
	static String[] ExtraVarList={"pi", "eps", "timing_tic", "timing_toc","timing_clock",
			"III", "JJJ", "output_real_tmp", "int_tmpvar", "arg_buffer", };

	static PrintStream out = System.out; 
	
	// Flag, true: set the first .m function as main function (Program)
	//		false: translate the .m function into subroutine
    static boolean bMultipleFile = false;
    // extra flag used by convertParameterType() to indicate error ocurrance
    static boolean bReturnVariableConversion = true;
    
    // set inside the inferSymbolTableEntry(), to indicate that the new inferred type 
    // (matrix type) is bigger than pre-inferred, so need to adjust the previous 
    // define-node and use-node of same variable.
    static boolean bAdjustArrayAccess = false;
    // In main function, when a caller gets a variable's type from a function call's
    // return value, and the type has dynamic shape, then need to add allocation code.
    // this is the flag to indicate this situation.
    static boolean bAdjustDynamicShape = false;

    // Name of the source file
    static String fortranname="", basename="", pureName="";
	
    // The list of the sub trees
    static java.util.List<ASTNode> gTreeList = new ArrayList<ASTNode>();
    
    // During doing type-inference on a function, if encounters another user-defined function
    // then the type-inference should stop, and goes to infer that function, 
    // until get the return value type.  Current function should be put into a stack,
    // so we can re-do the type-inference when finishing user-defined function.
    static java.util.Stack<ASTNode> gReInferTreeStack = new Stack<ASTNode>(); 
    // Keep the list of user-defined functions need to be inferred.
    static java.util.List<String> gNextInferFuncNameList = new ArrayList<String>(); 
    
    // Global variable points to current sub-tree's code node list 
    static java.util.List<ASTNode> gCodeNodeList; 

    // A map between a AST tree node to its code-node list 
    static HashMap<ASTNode, java.util.List<ASTNode>> gTreeCodeNodeMap 
    		= new HashMap<ASTNode, java.util.List<ASTNode>>();
    
    // Link code node with a list of symbol table entry, keep for whole process
    // Because one assignment may have more than one return variables 
    // Also, more than one node can points to same symbol-table-entry
    static HashMap<ASTNode, ArrayList<SymbolTableEntry>> gCodeNodeEntryMap 
    		= new HashMap<ASTNode, ArrayList<SymbolTableEntry>>();

    // Save the scope for each sub-tree
	static HashMap<ASTNode, SymbolTableScope> gTreeSymbolTableMap = new HashMap<ASTNode, SymbolTableScope>();

	// Each function maps a list of function call
	public static HashMap<ArgTupleType, ArrayList<SymbolTableEntry>> gFuncCallVarMap
				= new HashMap<ArgTupleType, ArrayList<SymbolTableEntry>>();

	// For some symbol table entry (function call's), maps the scope it belongs to.
	public static HashMap<SymbolTableEntry, SymbolTableScope> gEntryScopeMap 
				= new HashMap<SymbolTableEntry, SymbolTableScope>() ;
	
	// List of return types, they are types of sub-function, NOT caller/converted types
	// Save it for further use. Indicate this function is type-inferred
	public static HashMap<String, java.util.List<Type>> gFuncReturnTypeListMap 
				= new HashMap<String, java.util.List<Type>>();

	// call node and its tree node
	public static HashMap<ASTNode, ASTNode> gFuncCallNodeMap 
				= new HashMap<ASTNode, ASTNode>();

	// Each function call node has its own calling signature, 
	// and it maps to two lists, ParameterList and ArgumentList
	public static HashMap<ArgTupleType, java.util.List<String>> gFuncCallParameterMap 
			= new HashMap<ArgTupleType, java.util.List<String>>();
	public static HashMap<ArgTupleType, java.util.List<String>> gFuncCallArgumentMap 
			= new HashMap<ArgTupleType, java.util.List<String>>();
	// A map between a function's Parameter's name and the caller site argument' name
	// Each function call will has one entry, even call to same function 
	

	public static void initialAll() {
	    
	    gFuncCallVarMap.clear();
	    gEntryScopeMap.clear();
	    gFuncReturnTypeListMap.clear();
	    gFuncCallNodeMap.clear();
	    gFuncCallParameterMap.clear();
	    gFuncCallArgumentMap.clear();

	    gTreeList.clear();
	    gReInferTreeStack.clear();
	    gNextInferFuncNameList.clear();
	    
	    gCodeNodeList = null;
	    
	    gTreeCodeNodeMap.clear();
	    gCodeNodeEntryMap.clear();
	    gTreeSymbolTableMap.clear();
	    
	    gExtraVarList.clear();
	    for(int i=0; i<ExtraVarList.length; ++i)
	    	gExtraVarList.add(ExtraVarList[i] );
	    
	    TypeInferenceEngine.initialAll();
	}
    //-------------------------------------------------------------------------
	// Main function
	public static String M2F(String[] args) {
		initialAll();
		TypeInferenceEngine.DEBUG = DEBUG_Type;
		if(args[0].equals("-m2n")) {	
        	// Translate .m => .n
        	if(args.length==1) {	// no extra argument, 
        		translateFolder(benchmarkFolderName);
        	} else {	// argument has the file name (with relative pathname) 
        		// i.e. -m2n \languages\Fortran\benchmark2_2.m
        		// i.e. -m2n \languages\Fortran\benchmarks\sdku\
        		// translateFolder() handles whether it's file/dir 
        		translateFolder(args[1]);        		
        	}

        } else if(args[0].equals("-d")) {	
        	// Convert a directory of .n files to Fortran
        	if(args.length==1) {	
        		// no extra argument, whole set of benchmarks
        		convertFolder(benchmarkFolderName);
        	} else {	// argument has the directory name 
        		// i.e. -d languages\Fortran\
        		convertFolder(args[1]);
        	}
        } else {
        	// For a single file
	        convert2Fortran(args[0]);
        }
        
        // Compile the file -- in Linux
        // systemCommand( "gfortran -O3 -Wall "+fortranname+" -o "+ basename);
        // systemCommand(basename);				
		System.out.println("-- Done --");
		return fortranname;
	}
	
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println("Usage: ");
			System.err.println("   java natlab.StaticFortranTool filename.n ");
			System.err.println("   java natlab.StaticFortranTool -d folder-name ");
			System.err.println("Note: Converting a specified file or all .n files of the folder to Fortran95.");
			System.err.println("      The pathname starting by '/' will be treated as absolution pathname,\n"
			                 + "      otherwise are relative pathname ");

			System.exit(1);
		}
		try{
			M2F(args);
		} catch(Exception e) {
			//In case some exception happened, make sure the program exit properly.
			e.printStackTrace();
			System.err.println(e.getMessage());
			System.exit(9);
		}
		
		
		System.exit(0);
	}
	
	//-------------------------------------------------------------------------
	// Following translate-... functions are used for translating .m to *.n files
	public static void translateFolder(String folderName) {
		translateFolder(folderName, TRANSLATION);
	}
	public static void convertFolder(String folderName) {
		translateFolder(folderName, CONVERTION);
	}
	// Find the list of .n files to translate. 
	// Merge them into a whole file, then call converting function.
	// Can handle one/two level directory 
	public static void translateFolder(String folderName, String task) {		
        int startIndex = System.getProperty("user.dir").lastIndexOf("/");
        if(startIndex<=0) {
        	PATH_STRING = "\\";
        } else {
        	PATH_STRING = "/";
        }
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
	    // Don't enter sub-folder, because '.svn' bothers the program
	    File[] subFolders = null;	//  mainFolder.listFiles(fileFilter);
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
	    		System.out.println("translate dir: "+dir+" ("+children.length+" files)");
		        for (int i=0; i<children.length; i++) {
		        	if(children[i].equals(TEMP_M_FILENAME))
		        		continue;
		        	String filename = dir.getAbsolutePath()+PATH_STRING+children[i];
		    		System.out.println("translateFile: "+filename);
		            translateFile(filename);
		        }
    		} else if (task.equals(CONVERTION)) {
	        	// Generate the Fortran files after parsing all files. 
    			// Need to read all files into the memory, and keep all AST, Symbol Table, ...
    			// in order to do forward type inference 
			    String[] children = dir.list(nfileFilter);
			    bMultipleFile = (children.length>1);
			    boolean bScriptFile = false;
			    String filename =  "";
			    if(children.length>0) {
		    		// create a temporary file name
			    	String tmpFilename = dir.getAbsolutePath()+PATH_STRING+TEMP_M_FILENAME;
			    	String mainFilename;	// first file name
			    	if(children.length>1) {
				    	// 1. Read the first file, 
					    //	(#) if it's a script, and there are more files,
					    //		then transform it into a function
					    filename =  mainFilename = dir.getAbsolutePath()+PATH_STRING+children[0];
					    ASTNode astTree = parseFile(filename, System.out);
					    if(astTree instanceof Script) {
					    	bScriptFile = true;
					    }
					    try {
					    	PrintStream outTemp = new PrintStream(tmpFilename);
					    	String str;
						    // 2. Merge all files into one memory file, 	
					        for (int i=0; i<children.length; i++) {
					        	if(children[i].substring(0, 4).equalsIgnoreCase("drv_")) {
						        	filename = dir.getAbsolutePath()+PATH_STRING+children[i];
						        	solveFilename(filename);
						        	appendFile(filename, outTemp, i, bScriptFile);
						        	break;
					        	}
					        }
					        for (int i=0; i<children.length; i++) {
					        	if(!(children[i].substring(0, 4).equalsIgnoreCase("drv_"))) {
						        	filename = dir.getAbsolutePath()+PATH_STRING+children[i];
						        	appendFile(filename, outTemp, i, bScriptFile);
					        	}
					        }					        
					        outTemp.close();
					        // If there is no file call drv_xxx, then use anyone
					        if(fortranname==null || fortranname.length()==0) {
					        	solveFilename(filename);
					        }
						} catch(IOException e) {
							e.printStackTrace();
							System.err.println(e.getMessage());
							System.exit(9);
						}
						// 3. Convert the whole file to Fortran
						String outFilename = fortranname;
			        	convert2Fortran(tmpFilename, outFilename);
			    	} else {
			        	convert2Fortran(filename);
			        }
			    } else {
			    	System.err.println("There is no source file in "+dir.getAbsolutePath()+" to be converted.");
			    }
    		}
    	}
	}
	public static void appendFile(String filename, PrintStream outTemp, int i, boolean bScriptFile) throws IOException {
		filename = getAbslouteName(filename);
    	System.out.println("MATLAB2Fortran-[found]: "+filename);

		BufferedReader inFile = new BufferedReader(new FileReader(filename));
		// Merge file together
		if (i==0 && bScriptFile) {
			// Add function header
			outTemp.println("function "+pureName);
		}
    	// save the file
		String str="";
        while ((str = inFile.readLine()) != null) {
            outTemp.println(str);
        }
		if (i==0 && bScriptFile) {
	    	// Add 'end'
    		outTemp.println();
			outTemp.println("end ");
		}
		outTemp.println();
		inFile.close();		
	}
	
	// Calling MATLAB to Natlab translator to translate .m to .n files
	public static void translateFile(String filename) {		
        filename = removeExtension(filename, ".m");
		System.out.println("Translating: "+filename);
		String[] files = {filename};
		matlab.Translator.main(files);		// TODO: Ant report error, wired! 
	}
	
	public static String removeExtension(String filename, String ext) {
		int len = filename.length() - ext.length();
        if(filename.substring(len).equals(ext)) {
        	filename = filename.substring(0,len);
        }
        return filename;
	}
	
	//-------------------------------------------------------------------------
	// executing the system command 
	public static int systemCommand(String cmd) 
	{ 
		try 
		{ 
			Process p=Runtime.getRuntime().exec(cmd); 
			p.waitFor(); 
			BufferedReader stdInput = new BufferedReader(new 
			        InputStreamReader(p.getInputStream()));
			
			BufferedReader stdError = new BufferedReader(new 
					InputStreamReader(p.getErrorStream()));
			// read the output from the command           
			System.out.println("Here is the standard output of the command:\n");
			String s = null;
			while ((s = stdInput.readLine()) != null) {
				System.out.println(s);
			}

			// read any errors from the attempted command
			System.out.println("Here is the standard error of the command (if any):\n");
			while ((s = stdError.readLine()) != null) {
				System.out.println(s);
			}
			System.out.println("Done - systemCommand - " + p.exitValue()); 
			return p.exitValue();
		} 
		catch(IOException e1) {
            System.err.println(e1.getMessage());
			e1.printStackTrace();
            return -99;
		} 
		catch(InterruptedException e2) {
            System.err.println(e2.getMessage());
			e2.printStackTrace();
            return -888;
		} 
	} 

	//-------------------------------------------------------------------------
	// Convert relative pathname to absolute pathname 
	public static String getAbslouteName(String name) {
		String absDirName = name;
		if(!name.subSequence(1, 2).equals(":")) {
			if(name.subSequence(0, 1).equals(PATH_STRING)) {
				absDirName = name;
			} else {
				absDirName = System.getProperty("user.dir")+ PATH_STRING + name;
			}
		}
		return absDirName;
	}
	// Solving the filename, initial some global variables
	private static void solveFilename(String filename) {
		filename = getAbslouteName(filename);
		basename = filename;
        if(basename.substring(basename.length()-2).equals(".m")
        		|| basename.substring(basename.length()-2).equals(".n")) {
        	basename = basename.substring(0,basename.length()-2);
        } 
        int startIndex = basename.lastIndexOf("/");
        if(startIndex<=0 || startIndex>=basename.length()) {
        	startIndex = basename.lastIndexOf(PATH_STRING);
        }
        if(startIndex<=0 || startIndex>=basename.length()) {
        	pureName = "";
        } else { 
        	pureName = basename.substring(startIndex+1);
        }        
	    fortranname = basename + ".f95";
	}
		
	//-------------------------------------------------------------------------
	// Merge a list of .m/.n files into a whole file, 
	// then parse it into a AST tree (FunctionList)
	public static ASTNode parseFile(String filename, PrintStream out) {
		filename = getAbslouteName(filename);
		ASTNode actual = null;
        try {
			BufferedReader in = new BufferedReader(new FileReader(filename));
			
			CommentBuffer commentBuffer = new CommentBuffer();
			NatlabScanner scanner = new NatlabScanner(in);
			scanner.setCommentBuffer(commentBuffer);
			NatlabParser parser = new NatlabParser();
			parser.setCommentBuffer(commentBuffer);
			// CompilationUnits cu = new CompilationUnits();	// Need this for rewrite 
			try {
				
				if(DEBUG_Default) out.println("---- Parsing Program --["+ filename +"]---------------------");
				// cu.addProgram((Program) parser.parse(scanner));
                actual = (Program) parser.parse(scanner);
                // cu.addProgram(actual);
				// actual = cu.getProgram(0);

                if(parser.hasError()) {
                    for(String error : parser.getErrors()) {
                    	out.println(error);
                    }
                } else {
    				// This is original output 
    				int startPos = actual.getStart();
    				int endPos = actual.getEnd();
    				if(DEBUG_Default) out.println(Program.getLine(startPos) + " " + Program.getColumn(startPos));
    				if(DEBUG_Default) out.println(Program.getLine(endPos) + " " + Program.getColumn(endPos));
                }
            } catch(Parser.Exception e) {
                for(String error : parser.getErrors()) {
                    out.println(error);
                }
                out.println(e.getMessage());
                System.err.println(e.getMessage());
    			e.printStackTrace();
    			System.exit(3);
            }
			
            if (actual==null) {
            	System.err.println("actual == null!");
            	System.exit(1);
            }
			in.close();
			// scanner.stop();
		} catch(IOException e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
			System.exit(2);
		}
        return actual;
        
    }
	//-------------------------------------------------------------------------
	// Main Function for converting .n file to Fortran code
	//-------------------------------------------------------------------------
	public static void convert2Fortran(String filename) {
        int startIndex = System.getProperty("user.dir").lastIndexOf("/");
        if(startIndex<=0) {
        	PATH_STRING = "\\";
        } else {
        	PATH_STRING = "/";
        }
    	solveFilename(filename);
    	convert2Fortran(filename, fortranname);
	}
	//-------------------------------------------------------------------------
	// This is the main coordinator, 
	//-------------------------------------------------------------------------
	public static void convert2Fortran(String filename, String fName) {
		// Printing the output of the program,  
		fortranname = fName;
		System.out.println("Converting 2 Fortran : "+filename);
		System.out.println("Fortran file : "+fortranname);

		// Working ....
		try {
			out = new PrintStream(fortranname);
			PrintStream  outSymTbl = new PrintStream(fortranname+".sym.ok");

			// Parsing the file
    		ASTNode actual = parseFile(filename, out);

			if(DEBUG_Default) out.println("---- Result File  ---------------------------------------------");
			if(DEBUG_Default) out.println(actual.getStructureString()); 
			if(DEBUG_Default) out.println();
			// if(DEBUG_Default) out.println("---- Original AST ---------------------------------------------");
			// if(DEBUG_Tree) out.print(actual.dumpTreeAll(false));
			if(DEBUG_Default) out.println("---------------------------------------------------------------");
			
    		// Handle each function one by one
			ASTNode prog = actual;
			// Initial the ASTNode's static variable ID to 0, so it can work 
			// properly while called by other program. 
	        actual.initNodeID();
	        // System.err.println("Init Node ID="+actual.getNodeID()+" next="+actual.getN);
	        boolean bMainFunc = true;
			FunctionList funcList;
            if(actual instanceof FunctionList) {
        		bMainFunc = true;
            	funcList = (FunctionList) prog;
            	for(Function func : funcList.getFunctionList()) {
            		// removeDeclNodes(func);  // TODO: not correct!!
            		func.mainFunc = bMainFunc;
            		gTreeList.add(func);
            		// TypeInferV2(func);	// try anothway
            		bMainFunc = false;
            	}
            } else {
            	// Script doesn't have any other function inside 
        		TypeInferV2(prog);
        		gTreeList.add(prog);
            }
            
            java.util.List<String> todoFuncList = new ArrayList<String>();
            // Type inference happen
            if(actual instanceof FunctionList) {
	            for(ASTNode tree: gTreeList) {
    				SymbolTableScope stScope0 = gTreeSymbolTableMap.get(tree);
    				if(stScope0==null) {
        				TypeInferV2(tree);		            					
    				} else {
    					// It's been already done.
    					// checkSymbolTable(stScope0, tree);
    				}
	            	
	            	// Doing next function
	            	while(gNextInferFuncNameList.size()>0) {
		            	todoFuncList.clear();
		            	todoFuncList.addAll(gNextInferFuncNameList);
		            	gNextInferFuncNameList.clear();
		            	for(String funcName: todoFuncList) {
		            		for(ASTNode funcTree: gTreeList) {
		            			if((funcTree instanceof Function)
		            					&& ((Function) funcTree).getName().equals(funcName))
		            			{
		            				SymbolTableScope stScope = gTreeSymbolTableMap.get(funcTree);
		            				if(DEBUG_Order) out.println("---------- [Order] Next Infer " + ((Function) funcTree).getName()+ stScope+" ----");
		            				if(stScope==null || gTreeCodeNodeMap.get(funcTree)==null) {
			            				TypeInferV2(funcTree);		            					
		            				} else {
		            					gCodeNodeList = gTreeCodeNodeMap.get(funcTree);		            					
		            					checkSymbolTable(stScope, funcTree);
		            				}
		            			}
		            		}
		            	}
	            	}
    				if(DEBUG_Order)out.println("---------- [Order] NextInfer DONE="+gNextInferFuncNameList.size()+", ReInfer Tree="+gReInferTreeStack.size());
    				// Infer those unfinished functions, 
	            	if(gReInferTreeStack.size()>0) {
	            		ASTNode redoTree = gReInferTreeStack.pop();  //redoTreeList.remove(0);
        				SymbolTableScope stScope = gTreeSymbolTableMap.get(redoTree);
        				if(DEBUG_Order)out.println("---------- [Order] ReInfer Tree  " + ((Function) redoTree).getName()+ stScope+" ----");
        				if(stScope==null || gTreeCodeNodeMap.get(redoTree)==null) {
            				TypeInferV2(redoTree);		            					
        				} else {
        					gCodeNodeList = gTreeCodeNodeMap.get(redoTree);		            					
        					checkSymbolTable(stScope, redoTree);
        				}
	            	}
	            }
            }
    		// After having all function signature, (which means we know all variables
            // that being assigned function return values), we need go to infer the variables
            // that use those variables (assigned return values).
            inferFuncCallVariable();
            
    		// [x] Transformation / Pre-compile phase: 
    		// 	- change the variable name with different case (case sensitive), to in-sensitive Fortran.
    		//	- adding bridge-variable for each parameter variable being changed in side function
    		transformBridgeVariable();

            // Add command line arguments in the main-function 
            if(actual instanceof FunctionList) {
	            for(ASTNode tree: gTreeList) {
	            	if(((Function) tree).mainFunc) {
	            		if(((Function) tree).getInputParamList().getNumChild()>0)
	            			((Function) tree).addCommandLineArgs();	            		
	            	}
	            }
            }
                    
            if(DEBUG) out.println("\n----- Static Fortran ------------------------------------------");

            // Generate Fortran together, because all symbol tables may have been changed 
            if(actual instanceof FunctionList) {
            	funcList = (FunctionList) actual;
        		java.util.List<Function> functionList = new ArrayList<Function>();
            	for(Function func : ((FunctionList) actual).getFunctionList()) {
            		functionList.add(func);
            	}
            	for(Function func : functionList ) {
    				if(DEBUG_Order) out.println("---------- [Order]doCodeGen "+ func.getName()+" ----");
            		doCodeGen(func, outSymTbl);
            	}
            } else {
            	doCodeGen(actual, outSymTbl);
            }

    		out.close();
    		outSymTbl.close();
		} catch(IOException e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
			System.exit(2);
		}
	} 
	public static void doCodeGen(ASTNode subtree) {
		doCodeGen(subtree, null);
	}
	public static void doCodeGen(ASTNode subtree, PrintStream  outSymTbl) {
		// Save the symbol table scope for that sub-tree
		SymbolTableScope stScope = gTreeSymbolTableMap.get(subtree);
		if(stScope==null) {
			System.err.println("Symbol Table Scope is null for ["+subtree+"]");
		}
		subtree.setSymbolTableScope(stScope);
		
		if(DEBUG_SymTbl)  dumpSymbolTable(stScope, out, subtree);
		// out.print(subtree.dumpTreeAll(true));

		HashSet<String> gLocalFuncList = new HashSet<String>();
		for(ArgTupleType func:gFuncCallParameterMap.keySet()) {
			gLocalFuncList.add(func.getName());
		}
		
		// Alex's Transformations to get clear Fortran code
		if(!DEBUG)
		{
			AlexFortranAnalyses.bSilence = true;
			AlexFortranAnalyses.gLocalFuncList = gLocalFuncList;
			AlexFortranAnalyses.Transformation1(subtree, stScope);
			AlexFortranAnalyses.Transformation4(subtree, stScope, gExtraVarList);
		}
		
		if(DEBUG_TreeFinal) out.println(subtree.dumpTreeAll(true));
		// dumpSymbolTable(stScope, System.out, subtree, true);		// TODO: DEBUG the isFirmType
		
		if(null!=outSymTbl) {
			dumpSymbolTable(stScope, outSymTbl, true, true);		
		}

		// Adding code for the dynamic shaped variables
        // After get all types by type-inference, and from the function calls
        addDynamicAllocation(subtree);

        // This is put behind the Transformation4 (eliminate unused definitions) 
        // is because it can avoid generate code for some temporary variables 
        // which are dynamically allocated but can be eliminated.

        // Transformation for each function call to subroutine call of whole file 
        transformAllFunctionCalls(stScope, subtree);
        
		out.println();
		out.print(subtree.getFortran());
		out.println();
	}
	// Handle Type inference and transformation
	//-------------------------------------------------------------------------
	// Type inference (v2.0 - flow-analysis)
	// 1.Disable all JastAdd rewrite rules defined in NameResolution.jrag
	// 			TypeInference.jrag, SymbolTable.jrag...
	// 2.Gather LHS by flow analysis 
	//-------------------------------------------------------------------------
	public static ASTNode TypeInferV2(ASTNode actual) {
        SymbolTableScope stScope2;
        
        if(DEBUG_Order || DEBUG_Default) out.println("----------  [Order] Type Inference v2.0 " + ((actual instanceof Function)?((Function) actual).getName():"Script") +" ----");
        
        // [x] Clear predefined data
        // gCodeNodeEntryMap keeps for whole process 
        gCodeNodeList = null; 

        // [x] Add some default statements, defined in Script, Function
        //if(!DEBUG)
        	actual.addDefaultStmt();	// TODO: should open it in release version!	

        actual.clearUseDefBoxes();
		actual.generateUseBoxesList();

		if(DEBUG_Default) out.println("------- Current Sub-Tree --------------------------------------");
		if(DEBUG_Simplify || DEBUG_Default) out.print(actual.dumpTreeAll(true)); 
		if(DEBUG_Default) out.println("---------------------------------------------------------------");

        //-----------------------------------------------------------------
		// [1] Simplify complex nodes
        actual.simplify(null, 2);
        
        if(DEBUG_Simplify) out.println("-------------- After Simplify --------------------------------");
		if(DEBUG_Simplify) out.println(actual.getStructureString()); 
        if(DEBUG_Tree || DEBUG_Simplify || DEBUG_SymTbl) out.print(actual.dumpTreeAll(true));
        // #Abandon  	[2] Covert into SSA form, and create symbol table
		// # 			// stScope2 = convert2SSA(actual);

		if(DEBUG_Default || DEBUG_SymTbl) out.println("---- Before Build Symbol Table ---------------------------------");
		if(DEBUG_Default || DEBUG_SymTbl) out.println(actual.getStructureString()); 
        
        // [2] Just create symbol Table, and map between def-node and symbol-entry
		stScope2 = buildSymbolTable(actual);
		
		for(int i=0; stScope2==null && i<50; ++i) {
			stScope2 = buildSymbolTable(actual);
		}
		if(stScope2==null) {
			System.err.println("Error: cannot create symbol table on the program");
		}

		if(DEBUG_Default || DEBUG_SymTbl) out.println("---- After Build Symbol Table ---------------------------------");
		if(DEBUG_Default || DEBUG_SymTbl) out.println(actual.getStructureString()); 
		
		// Save the symbol table scope for that sub-tree
		gTreeSymbolTableMap.put(actual, stScope2);

		actual.setSymbolTableScope(stScope2);
				
        // [3] Constant Propagation
		// Handle: n=2; m=2*n; t=2*10*4;...
		// Update the symbol table after doing each constant propagation
        // actual = ConstantPropagation(actual, stScope2 );
		// actual = ConstantPropagation.propagete(actual, stScope2);
        
		// [4] Generate use/def boxes list of each node of the tree after Constant Propagation
        // actual.clearUseDefBoxes();
		// actual.generateUseBoxesList();
        
		if(DEBUG_Const)   out.println("---- Code Build Symbol Table ---------------------------------");
		if(DEBUG_Const || DEBUG_Tree) out.println(actual.dumpTreeAll(true));
		// if(DEBUG_Const) dumpSymbolTable(stScope2, out, actual);	
		
		if(DEBUG_Const)   out.println("---- Code After Constant Propagation -------------------------");
		if(DEBUG_Default || DEBUG_SymTbl) out.println(actual.getStructureString()); 
		if(DEBUG_Default) out.println("---- Adding Decl Nodes ---------------------------------------");

		if(DEBUG_Default) out.println("---- Before checkSymbolTable() [V2] -----------------------------");
		if(DEBUG_SymTbl)  dumpSymbolTable(stScope2, out, actual);	// Debug purpose

		int entryCountBefore = stScope2.symTable.size();
        // [5] Inferring type for Decl nodes
        checkSymbolTable(stScope2, actual);   
        
		int entryCountAfter = stScope2.symTable.size();
		
		if(DEBUG) out.println("---- After checkSymbolTable() [V2] -----------------------------");
		if(DEBUG_SymTbl)  dumpSymbolTable(stScope2, out, actual);	// Debug purpose

		// System.out.println("[stScope2] before="+entryCountBefore+" after="+entryCountAfter);
		if(entryCountAfter>entryCountBefore) {
	        // [6] Reload the code node, because there are transformations happened 
			if(DEBUG) out.println("---- [6] Reload the code node [V2] -----------------------------");
	        // May not necessary
	        actual.clearUseDefBoxes();
			actual.generateUseBoxesList();
	        AbstractFlowAnalysis.setDebug(false, (PrintStream) out);
	        ReachingDefs defsAnalysis = new ReachingDefs(actual);
	
	        java.util.List<ASTNode> codeNodeList = defsAnalysis.getNodeList();
	        
	        // Save the result for further use (for convenience) 
	        // gCodeNodeEntryMap DON"T clear it, 
	        gCodeNodeList = codeNodeList;
	        gTreeCodeNodeMap.put(actual, codeNodeList);

	        // if(DEBUG) out.println(actual.dumpCodeTree());
	        if(DEBUG_Tree) out.println(actual.dumpTreeAll(true));
	        // Re-do type inference again, where most of them are easily skipped
	        checkSymbolTable(stScope2, actual);   
		}
		
		if(DEBUG_Default) out.println("---- After check SymbolTable &(Adding Decl Nodes )-------------");
		if(DEBUG_Tree) out.print(actual.dumpTreeAll(true));

		if(DEBUG_Default) out.println("---- After adding Decl Nodes [V2] -----------------------------");
		if(DEBUG_SymTbl)  dumpSymbolTable(stScope2, out, actual);	// Debug purpose

        if(DEBUG_Tree) out.println("--- Final m program (After Type Inference v2.0) ----------------");
        if(DEBUG_Tree || DEBUG_SymTbl) out.println(actual.getStructureString()); 

		// [7] Generate Static Fortran 
    	actual.FortranProgramName = pureName;
        
        //-----------------------------------------------------------------
/*        //???  Type inference (v2.0 - flow-analysis)
        try {
        	//TypeInfer
        } catch(Exception e) {
            //for(String error : parser.getErrors()) {
            //    out.println(error);
            //}
            out.println(e.getMessage());
            System.err.println(e.getMessage());
			System.exit(4);
        }
*/
 		// The flow-analysis results have been already changed during SSA
 		// conversion. 
		return actual;
	}
	
	// Infer variables used function call return values
	public static void inferFuncCallVariable() {
		for(ASTNode tree: gTreeList) {
			java.util.List<ASTNode> codeNodeList = gTreeCodeNodeMap.get(tree);
			SymbolTableScope stScope =  gTreeSymbolTableMap.get(tree); 
			
			java.util.List<ASTNode> callNodeList = new ArrayList<ASTNode>();
			java.util.HashSet<String> callVarSet = new HashSet<String>();
			// Get the list of call code node and call variable in this tree
		    for(ASTNode codeNode: gFuncCallNodeMap.keySet()) {
		    	if(gFuncCallNodeMap.get(codeNode)==tree) {
		    		callNodeList.add(codeNode);
		    		for(Object vb: (codeNode).getDefBoxes()) {
		    			callVarSet.add(((natlab.toolkits.ValueBox) vb).getValue());
		    		}
		    	}
		    }
		    // Searching all node use that variable (is return value from a function call)
		    if(codeNodeList!=null) {
			    for(ASTNode codeNode: codeNodeList) {
					if((! callNodeList.contains(codeNode)) 
						&& ((codeNode).getDefBoxes().size()>0) 
						&& (codeNode instanceof AssignStmt)) 
					{
						for(Object vb: (codeNode).getUseBoxes()) {						
							if (callVarSet.contains(((natlab.toolkits.ValueBox) vb).getValue())) {
								// infer the defined variable
								for(Object var: (codeNode).getDefBoxes()) {
						    		String varName = ((natlab.toolkits.ValueBox) var).getValue();
						    		SymbolTableEntry stEntry = stScope.getSymbolById(varName);
									if(DEBUG_Func) out.println("[inferFuncCallVar]"+varName+"["+(codeNode).getNodeID()+"]["+stEntry);
						    		inferSymbolEntry(stScope, stEntry, codeNode, true);
								}
							}
						}
					}
			    	
			    }
		    }
		}
	}

	// Transform all function calls in current sub-function
	public static void transformAllFunctionCalls(SymbolTableScope stScope, ASTNode subtree) {
	    for(ASTNode codeNode: gFuncCallNodeMap.keySet()) {
			// Transform the function calls to subroutine calls, for those user define
	        // For subroutine, translate into CALL func(p1,p2,...,r1,r2);
			// This transformation will postponed to just before code-gen, because
			// it creates new node and cause inconsistent to symbol table entry
	    	if(gFuncCallNodeMap.get(codeNode).equals(subtree))
	    		transformFunc2Subroutine(stScope, codeNode);
	    }
	}

	// Transformation / Pre-compile phase: transformation
	//	- adding bridge-variable for each parameter variable being changed in side function
	// i.g. func(b); b=2; =>func(b_tmp); b=btmp; b=2;
	// It only affect function declaration, 
	public static void transformBridgeVariable() {
		// Go through all sub-functions
		for(ASTNode tree: gTreeList) {
			if((tree instanceof Script) 
					|| ((tree instanceof Function) && (((Function) tree).mainFunc)))
			{
				// This is main function
				// Usually the first one is Program,
			} else { // tree is Function 
				// 1. gather the parameter variables 		
				java.util.List<String> paramVarList = new ArrayList<String>(); 
		    	for(Name varNode: ((Function)tree).getInputParamList()) {	// (FunctionDecl)
		    		paramVarList.add(varNode.getID());
		    	}
				
				// 2. go through code-node list, looking for conflict assignment
				SymbolTableScope stScope =  gTreeSymbolTableMap.get(tree); 
				java.util.List<ASTNode> codeNodeList = gTreeCodeNodeMap.get(tree);
				for(ASTNode codeNode: codeNodeList) {
					if(codeNode instanceof AssignStmt) {
						for(Object vb: (codeNode).getDefBoxes()) {						
				    		String varName = ((natlab.toolkits.ValueBox) vb).getValue();
							if (paramVarList.contains(varName)) {
								if(DEBUG_Func) out.println("[BridgeVariable]"+varName+"["+"]["+(codeNode).getNodeID());
								// 3. adding bridge-variable
					    		SymbolTableEntry stEntry = stScope.getSymbolById(varName);
					    		ASTNode varDeclNode = stEntry.getDeclLocation();
					    		
								// (1) Adding the new assignment, from variable to bridge variable,
					    		String tmpName = TypeInferenceEngine.getNewVarName(varName, varDeclNode); 
					    		AssignStmt asgStmt = new AssignStmt(new NameExpr(new Name(varName)), 
					    									new NameExpr(new Name(tmpName)) );
					    		asgStmt.setNodeID();
								// (2) Adding new declare node
					    		// Type pType = new PrimitiveType(.getName());
					    		// Type pType = new PrimitiveType("int");
					    		Type pType = ((VariableDecl)varDeclNode).getType();
						    	VariableDecl tmpDecl = new VariableDecl(tmpName, pType);
						    	tmpDecl.setNodeID();
						    	if(DEBUG_Func) System.err.println("[addNew Decl]"+tmpDecl.getStructureString());
						    	((Function)tree).addDeclStmt(tmpDecl);
						    	dumpTypeInfo(tmpDecl.getType(), "[BridgeVariable]"+tmpDecl.getFortranLessComments());

						    	// adding them into tree;
						    	ASTNode parent = varDeclNode.getParent();
					    		if(parent instanceof natlab.ast.List) {
					    			int i=0;
					    			for(; i < parent.getNumChild(); i++) {
					    				if(!(parent.getChild(i) instanceof VariableDecl)) {
					    					// this is the first non-declaration node
					    					break;
					    				}
					    			}
					    			parent.insertChild(asgStmt, i);
					    			// Wired, sometimes the type need to be set after insert into stmt-list
					    			// ((VariableDecl)parent.getChild(i)).setType(pType);
					    		}
					    		paramVarList.remove(varName);
					    		
								// (3) Adding symbol table entry
								SymbolTableEntry ste = new SymbolTableEntry(tmpName, tmpName, asgStmt); 
								ste.setDeclLocation(tmpDecl);
								stScope.addSymbol(ste);

								// (4) Renaming the parameter
						    	for(Name varNode: ((Function)tree).getInputParamList()) {	// (FunctionDecl)
						    		if(varName.equalsIgnoreCase(varNode.getID())) {
						    			varNode.setID(tmpName);
						    		}
						    	}
					    		
								// (5) Keep the original input-parameter declaration
						    	//for(VariableDecl decl: ((Function)tree).getParamDeclList()) {	// (FunctionDecl)
						    	//	if(varName.equalsIgnoreCase(decl.getID())) {
						    	//		decl.setID(tmpName);
						    	//	}
						    	//}
						    	

							} // if (paramVarList.contains(varName))
						}
						
					}
				}

			}	// if((tree is the Function .... 
		}
	}
	public static Expr getImageExpr(SymbolTableScope stScope, Expr expr) {
		Expr imgExpr = null;
		// Get the image sign 'i' , 'j' from i*y, j*y 
		if(expr instanceof MTimesExpr) {
			if(((MTimesExpr)expr).getLHS() instanceof NameExpr && 
				(((MTimesExpr)expr).getLHS().getVarName().equals("i")
				|| ((MTimesExpr)expr).getLHS().getVarName().equals("j")) ) {
				imgExpr = ((MTimesExpr)expr).getRHS();
			}
			if(imgExpr==null) {
				if(((MTimesExpr)expr).getRHS() instanceof NameExpr &&
					(((MTimesExpr)expr).getRHS().getVarName().equals("i")
					|| ((MTimesExpr)expr).getRHS().getVarName().equals("j")) ) {
					imgExpr = ((MTimesExpr)expr).getLHS();
				}
			}
		} else if(expr instanceof NameExpr || expr instanceof FPLiteralExpr || expr instanceof IntLiteralExpr ) {
			// Get the image sign 'i' , 'j' from 3i, 0.150j 
			String strImg = expr.getStructureString();
			if(isImaginaryPart(strImg)){
				imgExpr = new FPLiteralExpr(new natlab.FPNumericLiteralValue(strImg.substring(0,strImg.length()-1)));
			}			
		}
		return imgExpr;
	}
	// Transform a MATLAB complex expression x+i*y, x+j*y, x+2i, x+3.1j 
	// into Fortran format COMPLEX(x,y)
	// It replaces the original expression, so doesn't effect the code-node
	public static void transform2Complex(SymbolTableScope stScope, Expr expr) {
		Expr realExpr = null;
		Expr imgExpr = null;

		// x+i*y
		if(expr instanceof PlusExpr || expr instanceof MinusExpr) {
			realExpr = ((BinaryExpr) expr).getLHS();
			imgExpr = getImageExpr(stScope, ((BinaryExpr) expr).getRHS());
		} else {
			// i*y, 3.1j
			imgExpr = getImageExpr(stScope, expr);
		}
		if(imgExpr!=null) {
			ASTNode parent = expr.getParent();
			int loc = parent.getIndexOfChild(expr);
			
			natlab.ast.List<Expr> list = new natlab.ast.List<Expr>();
			if(realExpr!=null) {
				list.add(realExpr);
			} else {
				list.add(new FPLiteralExpr(new natlab.FPNumericLiteralValue("0.0")));
			}
			list.add(imgExpr);	
        	ParameterizedExpr cmplxParam = new ParameterizedExpr(new NameExpr(new Name("COMPLEX")), list);
			
			parent.setChild(cmplxParam, loc);
		}
	}
	
	// Adding dynamic allocation code for dynamic shape type
	// 1. in Program/main-function 
	// 2. For subroutine, only handle those variable whose dynamic variable are not from parameter
	//			which also not parameter variables
	// 3. For subroutine, those return parameters should not be DEALLOCATE(), 
	public static void addDynamicAllocation(ASTNode tree) {
		// [#] Check every function's variable (all of them) through symbol table , 
		// adding dynamic allocation code for dynamic shape variable
		{
			// Checking whether need to add allocation
			java.util.List<String> paramList = null;
			boolean bMainFunc = false;
			
			// Initial some variable
			if((tree instanceof Script) 
					|| ((tree instanceof Function) && (((Function) tree).mainFunc)))
			{	
				// (1) Main function, script
				bMainFunc = true;
			} else {
				// (2) variable that are not parameter variable
				bMainFunc = false;
				//	Getting the parameter variable list
				for(ArgTupleType func:gFuncCallParameterMap.keySet()) {
					if(((Function) tree).getName().equals(func.getName())) {
						paramList = gFuncCallParameterMap.get(func);
						if(DEBUG_Func) dumpList(paramList, "ParamList");
					}
				}
			}
			
			// Checking code-node one by one
			java.util.List<ASTNode> codeNodeList = gTreeCodeNodeMap.get(tree);
			if(codeNodeList!=null) {
				SymbolTableScope stScope =  gTreeSymbolTableMap.get(tree);
				TypeInferenceEngine.gstScope = stScope;
			    // for(ASTNode codeNode: codeNodeList) {
			    //	ArrayList<SymbolTableEntry> seList = gCodeNodeEntryMap.get(codeNode);
			    //	if(seList != null) {
				if(stScope != null) {
			    	// for(SymbolTableEntry stEntry: seList) {
					for(SymbolTableEntry stEntry: stScope.symTable.values()) {
				    	if(stEntry==null) { 
				    		continue;
				    	} else if(stEntry.getDeclLocation()==null) {
				    		System.err.println("[addDynamicAllocation] No decl node of entry "+stEntry.getSymbol());
				    		continue;
				    	}
				    	
			    		Type varType = ((VariableDecl) stEntry.getDeclLocation()).getType();
						ASTNode declNode = stEntry.getDeclLocation();

			    		// For those dynamic shape type
						if((varType instanceof MatrixType) 
								&& (((MatrixType) varType).getSize().getDims()==null)) {

							// For those need adjust
							if(DEBUG_Alloc) out.print("[addDynamicAllocation]-list=["+stEntry.getSymbol()+"]["+stEntry.getNodeLocation().getNodeID()+"] ");
							if(DEBUG_Alloc) out.println(((VariableDecl) declNode).bAdjustDynamic);
							if(DEBUG_Alloc) dumpTypeInfo(varType, "[addDynamicAllocation]-varType=");	

							if(declNode!=null && ((VariableDecl) declNode).bAdjustDynamic==false) {
								ASTNode varNode = stEntry.getNodeLocation();
								
								// Checking whether need to add allocation
								// (1) For sub-function, skip variables that are 
								//  input/output parameter variable - calling conversion
								if((!bMainFunc) && (paramList!=null) && (paramList.contains(stEntry.getSymbol()))){
									// for parameter variables, skip
									continue;
								} 
								// (2) Main function and sub-function, checking type
								// TODO: Should check the variable in dynamic extent are not parameter variables
								// (1) Find variable inside dynamic dimension
								java.util.List<String> strDimsNew = new ArrayList<String>();
								java.util.List<String> strDims=((MatrixType) varType).getSize().getDynamicDims();
								boolean bNeed = false;
								for(String strExtern: strDims) {
									if(!isInteger(strExtern)) {
										HashSet<String> varSet = getVariableListFromString(strExtern);
										if(DEBUG_Alloc) out.println("[DynamicAlloc]-check var-set="+varSet.toString());
										if(varSet!=null) {
											if(paramList==null) {
												bNeed = true;
												break;
											} else {
												varSet.removeAll(paramList);
												if(!(varSet.isEmpty())) {
													bNeed = true;
													break;
												}
											}
										}
									}
								}
								if(DEBUG_Alloc) out.println("[DynamicAlloc]-loc: "+varNode.getStructureString()+" ,need="+bNeed);
								if(!bNeed)
									continue;
								// otherwise, need to add allocate() 	

								// Add ALLOCATE(Array(n,m))
								java.util.List<Stmt> eslist = 
									createAllocStmtFromDecl((VariableDecl) declNode, (MatrixType) varType, varNode);
								Stmt allocExpr = eslist.get(0);
								Stmt deallocExpr = eslist.get(1);
								// get the StmtList	//getParentStmtListNode()
								// natlab.ast.List list 
						    	if(varNode.getParent() instanceof List) {
						    		List<Stmt> stmtList = (List<Stmt>)varNode.getParent();
						    		int loc = stmtList.getIndexOfChild(varNode);
						    		if(!(varNode instanceof AssignStmt))
						    			System.err.println("[DynamicAlloc]-loc="+loc+" of CALL: "+varNode.getStructureString()+" in "+stmtList.getNodeID());
						    		if(loc>=0) {
						    			stmtList.insertChild(allocExpr, loc);
						    		} else {
						    			// This may cause by transformation
						    			System.err.println("[DynamicAlloc]-loc="+loc+" of "+varNode.getStructureString()+" in "+stmtList.getNodeID());
						    		}
						    	} else {
						    		// Find the close List<Stmt> and location, insert into
						    		ASTNode parent = varNode.getParent(); 
						    		ASTNode child = varNode;
					    			while ((parent!=null) && !(parent instanceof natlab.ast.List)) {
					    	    		child  = parent;
					    	    		parent = child.getParent(); 	   
					    	    	}
					    			if(parent!=null) {
						    			int loc = parent.getIndexOfChild(child);
						    			parent.insertChild(allocExpr, loc);
					    			}
						    	}
								
					    		// 
					    		((VariableDecl) declNode).bAdjustDynamic=true;
					    		// find the end of list, add deallocate()
						    	if(declNode.getParent() instanceof List) {
						    		List<Stmt> declList = (List<Stmt>)declNode.getParent();
						    		declList.add(deallocExpr);
						    	}
					    		
							}
						}
			    	}	// for(...)

		    	} // if(stScope != null) 
			}
		}			
	}
/******	
	// In a matrix type, some extent of dimension has function (or complex expression ) in it,
	// need to create temporary variable for them, 
	// This case obey the Fortran calling conversion!
	public static void testingVariableDim(MatrixType varType, ASTNode curNode) {
		boolean bChanged = false;
     	java.util.List<String> strDim = new ArrayList<String>();
    	Size varSize = varType.getSize(); 
		for (String dim : varSize.getDynamicDims()) {
			// 1. Testing the function by try first '(' location
			System.err.println("[testingVariableDim]: Var:"+dim+ " "+dim.indexOf("(",1));
			if(dim.indexOf("(", 1)>1) {
    			ExprStmt dimStmt = parseString(dim);
    			if(dimStmt!=null) {
    				bChanged = true;
    				// 2. need to create temporary variable for them, 
    				NameExpr newVar = TypeInferenceEngine.addNewAssignment
    					(dimStmt.getExpr(), curNode, null, TypeInferenceEngine.gstScope);
    				strDim.add(newVar.getVarName());
    				System.err.println("[testingVariableDim]: New Var:"+newVar.getVarName());
    			} else {
    				strDim.add(dim);
    			}
			} else {
				strDim.add(dim);
			}
		}
		System.err.println("[testingVariableDim]: Var:"+strDim.toString());
		// 3. and update the matrix-type
		if(bChanged) 
			varSize.setDynamicDims(strDim);
	}
****/	
	
	// Create allocate() and deallocate() statement for the matrix-type variable
	public static java.util.List<Stmt> createAllocStmtFromDecl
			(VariableDecl declNode, MatrixType varType, ASTNode curNode) {

		java.util.List<Stmt> exprStmtList = new ArrayList<Stmt>();
     	natlab.ast.List<Expr> arglist = new natlab.ast.List<Expr>();
    	Size varSize = varType.getSize(); 
    	if(varSize != null) {
			if(varSize.getDims()!=null) {
				for(Integer dim: varSize.getDims()) {
					arglist.add(new NameExpr(new Name(dim.toString())));
				}
			} else if(varSize.getVariableDims()!=null) {
				// using variable for dynamic allocation
				for (String dim : varSize.getVariableDims()) {
					arglist.add(new NameExpr(new Name(dim)));
				}
			} else if(varSize.getDynamicDims()!=null) {
				// testingVariableDim(varType, curNode);
				for (String dim : varSize.getDynamicDims()) {
					arglist.add(StaticFortranTool.parseString(dim).getExpr());
					
				}
			}
		} else {
			arglist.add(new NameExpr(new Name(":")));	// new ColonExpr()
			if(DEBUG_Default) System.err.println("[Error]:[getExprFromDecl] size is empty!");
		}
		// Creating the phi-expression
		ParameterizedExpr varExpr = new ParameterizedExpr(
				new NameExpr(new Name(declNode.getID())), arglist);
				
		natlab.ast.List<Expr> listName = new natlab.ast.List<Expr>();
		listName.add(new NameExpr(new Name(declNode.getID())));
		ParameterizedExpr deallocExpr = new ParameterizedExpr(
				new NameExpr(new Name("DEALLOCATE")), listName);
		Stmt deallocStmt = new ExprStmt(deallocExpr);
		
     	natlab.ast.List<Expr> list = new natlab.ast.List<Expr>();
     	list.add(varExpr);
		ParameterizedExpr allocExpr = new ParameterizedExpr(
				new NameExpr(new Name("ALLOCATE")), list);		
		ParameterizedExpr checkExpr = new ParameterizedExpr(
				new NameExpr(new Name("ALLOCATED")), listName);
		
		natlab.ast.List<Stmt> stmtlist= new natlab.ast.List<Stmt>();
		stmtlist.add(new ExprStmt(allocExpr));

		IfStmt tmpIfStmt  = new IfStmt();
		IfBlock tmpIfBlock = new IfBlock(new NotExpr(checkExpr), stmtlist);
		ElseBlock tmpElseBlock = new ElseBlock(new List<Stmt>().add(new BreakStmt()));
		tmpIfStmt.setIfBlockList(new List<IfBlock>().add(tmpIfBlock));
		exprStmtList.add(tmpIfStmt);
		
		exprStmtList.add(deallocStmt);
		
		return exprStmtList;
	}
	
	// Infer the type of all symbol table entries
	public static void checkSymbolTable(SymbolTableScope stScope, ASTNode actual) 
	{
		// The order of variables being inferred is according to the symbol-entry order,
	    // Since HashMap does NOT guarantee that the order will remain constant over time. 
	    // But the AST tree has the right order. Some variable's type depended on 
	    // the previous variables'. Inferring by different order will cause incorrect result or failure!
	    // Solution: 
	    // 		Inferring by the order of the AST code-node.

	    // TypeInfer V2.0, 
		// [#1] For the subroutine,  
		// set types of its parameters, before doing type inference inside. 
		// First gathering the type information of each parameter from 
		// the caller's function call context.   
		ArgTupleType funcSignature = null; 		// ArrayList<SymbolTableEntry>>
		if((gFuncCallVarMap.size()>0) && (actual instanceof Function)) //  && !((Function)actual).mainFunc)
		{
			if(DEBUG_Func) out.println("[#1] Func "+((Function)actual).getName());
			
			for( ArgTupleType funcSign : gFuncCallVarMap.keySet())
			{
				if(DEBUG_Func) out.println("[#2] funcSign "+funcSign.getName()+" cur="+((Function)actual).getName());
				
				if(((Function)actual).getName().equalsIgnoreCase(funcSign.getName())) {
					funcSignature = funcSign;

					// Get the caller's symbol table scope, NOT use anymore
	    			//ArrayList<SymbolTableEntry> stEntryList = gFuncCallVarMap.get(funcSign);					
	    			// SymbolTableScope callerScope = gEntryScopeMap.get(stEntryList.get(0));
	    			
					int i = 0;
					int maxArg = funcSignature.getNumStaticArgType();
					// funcSignature.getStaticArgTypeList().getNumChildNoTransform();
					// ?? This cannot work sometime. maxArg =funcSignature.getNumStaticArgType();
					// ?? funcSignature.getStaticArgTypeList().getNumChildNoTransform();
					
					// Gather the parameter list and argument list
					java.util.List<String> ParameterList = new ArrayList<String>(); 
					java.util.List<String> ArgumentList = new ArrayList<String>();
					
					// (1st round), find the input parameter list, gather information 		
					// Since getParamDeclList() has Input+Output parameters, so need to take 
					// first few ones base on the number of input parameter 
			    	for(VariableDecl decl: ((Function)actual).getParamDeclList()) { // (FunctionDecl)
			    		String varName = decl.getID();
			    		SymbolTableEntry stEntry = stScope.getSymbolById(varName);
			    		if(DEBUG_Func) System.out.println("[#3] Variable ["+varName+"] symEnt="+stEntry+" i="+i);
			    		if(stEntry==null) { 
			    			System.err.println("[Error] Variable "+varName+" doesn't have an symbol entry");
			    		} else {
				    		// Set value of this variable from caller's scope
				    		// Using argument as the index B=func(n,A)=> REAL::A(n),B(n)
				    		// 		# if n is constant, should not use the constant value! 
				    		//			[caller] f(10,A) => A(10) is wrong
				    		// So set the parameter variable name as its value
				    		stEntry.setValue(new StringLiteralExpr(varName));
		        			if(DEBUG_Type) out.println("[Value]-set#2["+stEntry.getSymbol()+"]="+varName);
				    		
				    		if(i>=maxArg) {	// Another insurance
				    			break;
				    		} else {
					    		// Current in the sub-function, varName is the parameter (including return variable)
					    		ParameterList.add(varName);
				    			// put the corresponding formal parameter into the map. 
					    		ArgumentList.add(funcSignature.getStaticArgName(i));				    		
					    		i++;
				    		}
			    		}
			    	} // for(VariableDecl decl: ...
		    		gFuncCallParameterMap.put(funcSign, ParameterList);
		    		gFuncCallArgumentMap.put(funcSign, ArgumentList);

		    		// (2nd round), set the parameter's type
		    		i = 0;
			    	for(VariableDecl decl: ((Function)actual).getParamDeclList()) {	//(FunctionDecl)
			    		String varName = decl.getID();
			    		SymbolTableEntry stEntry = stScope.getSymbolById(varName);
			    		if(DEBUG_Func) out.println("[#5] Variable ["+varName+"] symEnt="+stEntry);
			    		if(stEntry==null) { 
			    			System.err.println("[Error] Variable "+varName+" doesn't have an symbol entry");
			    		} else {
				    		if(i>=maxArg) {	// Another insurance
				    			break;
				    		} else {
								// set type to those parameters
					    		Type varType = funcSignature.getStaticArgType(i);
					    		if(DEBUG_Func) dumpTypeInfo(varType, "Argument-type= ");
					    		if(TypeInferenceEngine.isCharacterType(varType)) {
					    			varType = new PrimitiveType("character(LEN=*)");
						    		((VariableDecl) stEntry.getDeclLocation()).setType(varType);
					    		} else {
						    		// Convert argument type to rely on parameter variable 
					    			// i.g. [caller] B1=func(n1,A1),A1(n1) ==> Function B=func(n,A),A(n)
					    			Type newType = convertParameterType(funcSignature, varType, false);
					    			varType = newType;
						    		((VariableDecl) stEntry.getDeclLocation()).setType(varType);
					    		}
					    		if(DEBUG_Func) out.println("[#4]-2 set SymbolTable Variable "+varName+" ("+maxArg+") i="+i);
					    		if(DEBUG_Func) dumpTypeInfo(varType, " new-type= ");
					    		
					    		i++;
				    		}
			    		}
			    	} // for(VariableDecl decl:
				}
			} // for( ArgTupleType
		} //if((gFunc...	
		
		// [#2] Inferring the type bases on the order of the code list, 
		if(gCodeNodeList!=null) {
			boolean stopInfer = false;
			for(ASTNode codeNode: gCodeNodeList) {
		    	// Get the symbol entry list of this code node 
				ArrayList<SymbolTableEntry> seList = gCodeNodeEntryMap.get(codeNode);

				if(seList == null) {
					seList = new ArrayList<SymbolTableEntry>();
		    		// TODO: there may have more than one variable
					for(Object vb: (codeNode).getDefBoxes()) {						
						SymbolTableEntry ste = stScope.getSymbolById(((natlab.toolkits.ValueBox) vb).getValue());
			    		if(ste!=null) {
			    			seList.add(ste);
			    			gCodeNodeEntryMap.put(codeNode, seList);
			    			break;
			    		}
					}
					// When this node is not an assignment, but a stand alone statement, 
					if(seList.isEmpty() && codeNode instanceof ExprStmt) {
						Expr expr = ((ExprStmt)codeNode).getExpr();
						if(expr instanceof NameExpr) {
							// then handle the case of command form function call. i.e. <2.2> tic, foo
							TypeInferenceEngine.inferType(stScope, (NameExpr)expr, codeNode); 
						} else if(expr instanceof ParameterizedExpr) {	
							// This is a function call, without receiving return value
							TypeInferenceEngine.SpecialTransform((NameExpr)((ParameterizedExpr)expr).getTarget());
							// TypeInferenceEngine.createFunctionSignature(stScope, expr, varNode);
							Type varType = TypeInferenceEngine.inferType(stScope, (ParameterizedExpr)expr, codeNode);						

							// If it's a user defined function, then save the caller info.
							// Since there is no return value, don't need to infer that function immediately. 
				    		if(varType instanceof ArgTupleType) {
				    			ArrayList<SymbolTableEntry> stEntryList = new ArrayList<SymbolTableEntry>();
				    			gFuncCallVarMap.put((ArgTupleType) varType, stEntryList);
	
				    			gFuncCallNodeMap.put(codeNode, actual);	//(ArgTupleType) varType
				    			
				    		}
						}
					}
				} 
	    		// if(DEBUG_Type) out.println("[checkSymbolTable]codeNode:"+codeNode.getNodeID()+" Symbol-Entry:("+seList.size()+")="+((seList.size()>0)?seList.get(0).getSymbol():"null")+stScope.getClass().getName());
				for(SymbolTableEntry stEntry: seList) {
			    	// For those assignment-statements, that's not the primary def-node
		    		if(DEBUG_Type) out.println("[checkSymbolTable]codeNode:"+codeNode.getNodeID()+" "+seList.size()+" "+codeNode.getVarName()+" Symbol-Entry:"+((stEntry!=null)?stEntry.getSymbol():"null") );
		    		
			    	if(stEntry!=null) {
			    		// Infer type and save into symbol table entry
			    		Type rhsType = inferSymbolEntry(stScope, stEntry, codeNode, true);
			    		
			    		if(stEntry.getDeclLocation()==null) {
				    		System.err.println("Type-inferType failed on "+codeNode.getStructureString());
				    		if(DEBUG_Type) out.println("Type-inferType failed on "+codeNode.getStructureString());
				    		dumpSymbolEntry(stEntry, true);  
				    		continue;
			    		}
			    		// For special case
			    		// If this is a function call, add into global data for further use.
			    		Type varType = ((VariableDecl) stEntry.getDeclLocation()).getType();		    		
			    		ASTNode varNode = stEntry.getNodeLocation();
			    		
			    		if(DEBUG_Type || DEBUG_Func) out.println("[#9] Infer "+ codeNode.getNodeID()+" has ="+varType+" rhs="+rhsType);
			    		if(DEBUG_Type || DEBUG_Func) out.println("[#9] Infer "+ codeNode.getStructureString()+" has ="+varType+" rhs="+rhsType);
			    		
			    		// When RHS is user-defined function, then use RHS type as type first, then merge them later
			    		if((rhsType!=null) && (rhsType instanceof ArgTupleType)) {
			    			varType = rhsType;
			    		}
			    		
			    		if(varType instanceof ArgTupleType) {
			    			ArrayList<SymbolTableEntry> stEntryList = gFuncCallVarMap.get((ArgTupleType) varType);
			    			if(stEntryList == null) {
			    				stEntryList = new ArrayList<SymbolTableEntry>();
			    			} 
			    			stEntryList.add(stEntry);
			    			gFuncCallVarMap.put((ArgTupleType) varType, stEntryList);
			    			gEntryScopeMap.put(stEntryList.get(0), stScope);
			    			// 
			    			// Extra transformation for each function call
			    			// Transform the function calls to subroutine calls, for those user define
			    	        // For subroutine, translate into CALL func(p1,p2,...,r1,r2);
			    			// This transformation will postponed to just before code-gen, because
			    			// it creates new node and cause inconsistent to symbol table entry
			    			// transformFunc2Subroutine(codeNode);
			    			gFuncCallNodeMap.put(codeNode, actual);	//(ArgTupleType) varType
			    			
			    			// Check whether the function has inferred / has return type list 
			    			java.util.List<Type> returnVarTypeList = gFuncReturnTypeListMap.get(((ArgTupleType) varType).getName());
			    			if(DEBUG_Func || DEBUG_Order) out.println("[##] varType-ArgTupleType "+gFuncCallVarMap.size()+" "+stEntry.getSymbol()+"="+returnVarTypeList);
			    			if(returnVarTypeList==null) {
				    			gReInferTreeStack.add(actual);
				    			gNextInferFuncNameList.add(((ArgTupleType) varType).getName());
				    			stopInfer = true;
				    			break;
			    			} else {
			    				// The sub-function has been inferred 
			    				// 1) convertParameterType base on a map, which only support one call
			    				// 		should based on symbol entry ..., tree node, ...
			    				// 2) current only support one return value, 
			    				// 		because 2 return values will cause 2 symbol entry
			    				//		add return value ID.!
			    				
			    				// (1) Find the  parameter list previous defined
			    				java.util.List<String> ParameterList = null;
			    				for(ArgTupleType callSign:gFuncCallParameterMap.keySet()) {
			    					if(callSign.getName().equals(((ArgTupleType) varType).getName())) {
			    						ParameterList = gFuncCallParameterMap.get(callSign);
			    					}
			    				}
			    				// (2) Create the parameter/argument map
			    				java.util.List<String> ArgumentList =  new ArrayList<String>();
								int numArg = ((ArgTupleType) varType).getStaticArgTypeList().getNumChildNoTransform();
								int i=0;
			    				for(i=0; i<numArg; i++) {
			    					ArgumentList.add(((ArgTupleType) varType).getStaticArgName(i));
			    				}
					    		gFuncCallParameterMap.put(((ArgTupleType) varType), ParameterList);
					    		gFuncCallArgumentMap.put(((ArgTupleType) varType), ArgumentList);

					    		// (3) Convert return type to current argument list 
	    						dumpTypeInfo(returnVarTypeList.get(0), "Before converting ");
					    		Type retType = convertParameterType((ArgTupleType) varType, returnVarTypeList.get(0), true);
	    						dumpTypeInfo(retType, "After converting of "+stEntry.getSymbol()+"=");
					    		
					    		// (4) Merge types
					    		Type orgType = ((VariableDecl) stEntry.getDeclLocation()).getType();
								// if(TypeInferenceEngine.isValidType(retType) )
								if(!(orgType instanceof ArgTupleType)) {
									Type resultType = TypeInferenceEngine.mergeType(retType, orgType);
		    						((VariableDecl) stEntry.getDeclLocation()).setType(resultType);
									dumpTypeInfo(resultType, "Merged Type of "+stEntry.getSymbol()+"=");
								} else {
		    						((VariableDecl) stEntry.getDeclLocation()).setType(retType);
								}
			    			}
			    		}
			    		
			    	} else {
			    		// should not happen
			    		System.err.println("Symbol-Entry is NULL! [checkSymbolTable]:"+codeNode.getNodeID());
			    	}
				} // for(SymbolTableEntry stEntry ...

				// Stop type inference, then return, skip all following process
    			if(stopInfer)
    				return;
    			
		    } // for(ASTNode codeNode: ...	

			// [#3] Based on the symbol table's type, adjust the array-access 
			if((bAdjustArrayAccess || TypeInferenceEngine.bAdjustArrayAccess)
					&& gCodeNodeList!=null) {
				
			    for(ASTNode codeNode: gCodeNodeList) {
			    	ArrayList<SymbolTableEntry> seList = gCodeNodeEntryMap.get(codeNode);
			    	if(seList != null) {
				    	for(SymbolTableEntry stEntry: seList) {
					    	if(stEntry!=null) {
					    		inferSymbolEntry(stScope, stEntry, codeNode, false);
					    	}
				    	}
			    	} 
			    }
			} // if((bAdjust...

		} // if(gCodeNodeList!=null)

		// [#4] If current sub-tree (actual) is a function, update caller's return variables' types
		// Caller's function calls saved in gFuncCallVarMap!
		// Gather the return variable type of this function, 
		// update the caller's variable's type (from gFuncCallVarMap). 
		// When return value has dynamic size, update the variable in the type to 
		// corresponding caller's variable, which may have different name!
		if(actual instanceof Function) {
			for( ArgTupleType funcSign : gFuncCallVarMap.keySet())
			{
				if(DEBUG_Func) out.println("[+0] funcSign "+funcSign.getName()+ " cur="+((Function)actual).getName());
				// If there are function calls call to current function  
				if(((Function)actual).getName().equalsIgnoreCase(funcSign.getName())) {
					funcSignature = funcSign;
					// A list of function call node's symbol table entry
					ArrayList<SymbolTableEntry> stEntryList = gFuncCallVarMap.get(funcSignature);
		
		    		if(DEBUG_Func) out.println("[+1] Found calls to "+ funcSignature.getName()+" has ="+stEntryList.size());
					
					// Create the list of the return variable type, match the order of LHS of that function call
					java.util.List<Type> returnVarTypeList = new ArrayList<Type>();
					java.util.List<Type> returnVarOrgTypeList = new ArrayList<Type>();
					for(Name param : ((Function) actual).getOutputParams()) {
						// Data type of the return variable inside this sub-function
			    		SymbolTableEntry stEntry = stScope.getSymbolById(param.getID());
			    		Type varType = ((VariableDecl) stEntry.getDeclLocation()).getType();
			    		Type newType = convertParameterType(funcSign, varType, true); 
			    		if(DEBUG_Func) out.println("[+2] Found "+ param.getID()+" Type="+varType.getName());
						if(DEBUG_Func) dumpTypeInfo(varType,"");
			    		returnVarTypeList.add(newType);
			    		returnVarOrgTypeList.add(varType);
					}
					// Save it for further use. Indicate this function is type-inferred
					// List of return types, they are types of sub-function, NOT caller/converted types
					gFuncReturnTypeListMap.put(((Function)actual).getName(), returnVarOrgTypeList);
					
					// Put those types into the return variable's symbol table entry
					for(SymbolTableEntry entry: stEntryList) {
						ArrayList<SymbolTableEntry> seList = gCodeNodeEntryMap.get(entry.getNodeLocation());
						if(seList == null) {
							continue;
						} 
						int i=0;
						// TODO: the order in gCodeNodeEntryMap is same or not???!!
						for(SymbolTableEntry stEntry: seList) {
							Type varType = returnVarTypeList.get(i);
							((VariableDecl) stEntry.getDeclLocation()).setType(varType);
							if(DEBUG_Func) out.println("[++] set("+i+") "+ stEntry.getSymbol()+" Type="+varType.getName());
							if(DEBUG_Func) dumpTypeInfo(varType,"");
							i++;
							if(i>=returnVarTypeList.size())	
								break;
						}
						
					} // for(SymbolTableEntry entry
				} else {   // if(equalsIgnoreCase(funcSign.getName()
					
					// If this function call's return value has been registered  
					// then assign it 
					// A list of function call node's symbol table entry
				
	    			java.util.List<Type> returnVarTypeList = gFuncReturnTypeListMap.get(funcSign.getName());
					ArrayList<SymbolTableEntry> stEntryList = gFuncCallVarMap.get(funcSign);
					if(DEBUG_Func) out.println("[+8] Looking for "+funcSign.getName()+" "+returnVarTypeList+" "+stEntryList );
	    			if(returnVarTypeList!=null) {
						if(DEBUG_Func) out.println("[####] Have return Type of ="+funcSign.getName());
	    				for(SymbolTableEntry entry: stEntryList) {
	    					ArrayList<SymbolTableEntry> seList = gCodeNodeEntryMap.get(entry.getNodeLocation());
	    					if(seList == null) {
	    						continue;
	    					} 
	    					int i=0;
	    					// TODO: the order in gCodeNodeEntryMap is same or not???!!
	    					for(SymbolTableEntry stEntry: seList) {
	    						Type varType = returnVarTypeList.get(i);
	    						Type curType = ((VariableDecl) stEntry.getDeclLocation()).getType() ; 
	    						if(curType instanceof ArgTupleType) {
						    		Type retType = convertParameterType((ArgTupleType) curType, varType, true); 
		    						((VariableDecl) stEntry.getDeclLocation()).setType(retType);
		    						if(DEBUG_Func) out.println("[++] set("+i+") "+ stEntry.getSymbol()+" Type="+retType.getName());
		    						if(DEBUG_Func) dumpTypeInfo(retType," new type");
	    						}
	    						i++;
	    						if(i>=returnVarTypeList.size())	
	    							break;
	    					}
	    					
	    				} // for(SymbolTableEntry entry
	    			}
					
				}
			}
		}	// if(actual instanceof Function) 
		// [#5] Transform intrinsic function from command form to function call
		// It doesn't affect other type infer / transformation
		TypeInferenceEngine.transformCmdFormFunction();
	}
	
	// Translate the variable inside type into variable of the caller side
	// i.g. [caller] B1=func(n1,A1),A1(n1) ==> Function B=func(n,A),A(n) B(n) ==>[caller] B1(n1)
	// i.g. [caller] B2=func(n2,A2),A2(n2) ==> Function B=func(n,A),A(n) B(n) ==>[caller] B2(n2)
	// # Return Variable Type needs to be converted according to the function call arguments. 
	// Dynamic type happens when variable used in Matrix dimensions
	// Put the correct type into return variables' type list
	// 
	// bReturnVar: true: varType is the sub-function type, convert to caller's type
	//			false: means convert argument's type to sub-function's type
	
	// StaticFortranTool.evaluateExpression
	
	// Evaluate and Compare two expressions by using testing value, 
	// Return:
	//	- -1: expL<expR
	//	- 0 : expL==expR
	//	- 1 : expL>expR
	//	Otherwise: error, they are not comparable
	public static final int COMPARE_LESS = -1; 
	public static final int COMPARE_EQUAL = 0; 
	public static final int COMPARE_GREATER = +1; 
	public static final int COMPARE_ERROR = -99;

	// Compare (i+1,i), (i-1,i), (i+1-1)
	public static String  selectLargerExpressions(String str0, String str1) {
		if (!(str0.equals(str1))) {
			int result = compareExpressions(str0, str1);
			if(result==COMPARE_EQUAL) { 
				// using shorter one
				if (str0.length() > str1.length()) {
					str0 = str1;
				}
			} else if(result==COMPARE_GREATER) {
				// using 'str0'
			} else if(result==COMPARE_LESS) {
				str0 = str1;
			} else {	// COMPARE_ERROR
				// When they are not comparable
				// Using the length to decide which is bigger
				if (str0.length() == str1.length()) {
					if (str0.compareTo(str1) < 0)
						str0 = str1;
				} else if (str0.length() < str1.length()) {
					str0 = str1;
				}
			}
		}
		return str0;		
	}
	
	public static int compareExpressions(String expL, String expR) {
		if(expL.equals(expR))
			return COMPARE_EQUAL;
		
		if(expL.equals(":")) {
			return COMPARE_LESS;
		} else if(expR.equals(":")) {
			return COMPARE_GREATER;
		}
		// 1. Parsing two expressions
		ExprStmt treeL = parseString(expL);
		ExprStmt treeR = parseString(expR);
		
		HashSet<String> varSetL = getVariableListFromString(treeL);
		HashSet<String> varSetR = getVariableListFromString(treeR);

		// 2. Have same variable sets
		if(varSetL==null || varSetR==null) {
			// there is parsing error 
			if(DEBUG_Eval) out.println("[compareExpressions]-NULL:["+expL+"]="+varSetL+";["+expR+"]="+varSetR);
			return COMPARE_ERROR;
		} 
		if(DEBUG_Eval) out.println("[compareExpressions]:["+expL+"]="+varSetL.toString()+"<"+treeL+">;["+expR+"]="+varSetR.toString()+"<"+treeR+">");
		
		if(varSetL.size()!=varSetR.size()) {
			// Two have different number of variables
			return COMPARE_ERROR;
		} else {
			// Check whether they have same variables
			HashSet<String> diffSet = new HashSet<String> ();
			diffSet.addAll(varSetL);
			diffSet.removeAll(varSetR);
			if(diffSet.size()>0)
				return COMPARE_ERROR;
		}
		// 3. Testing by test-value, check result value is same or not
		//	(1) one variable: using 999
		int[] testValue = {999,9};
		if(varSetL.size()==1) {
			for(String varName: varSetL) {
				treeL.constantPropagation(varName, new IntLiteralExpr(new DecIntNumericLiteralValue(""+testValue[0])));
				treeR.constantPropagation(varName, new IntLiteralExpr(new DecIntNumericLiteralValue(""+testValue[0])));
				int valueL = TypeInferenceEngine.getExprIntegerValue(treeL);
				int valueR = TypeInferenceEngine.getExprIntegerValue(treeR);
				// if(DEBUG_Eval) out.println("[compareExpressions]:["+treeL.dumpTreeAll(true)+"]="+treeL+";["+treeR.dumpTreeAll(true)+"]="+treeR);
				if(DEBUG_Eval) out.println("[compareExpressions]:["+expL+"]="+valueL+";["+expR+"]="+valueR);
				if(valueL==TypeInferenceEngine.ERROR_EXTENT
						|| valueL==TypeInferenceEngine.ERROR_EXTENT) {
					return COMPARE_ERROR;
				} else {
					if(valueL==valueR) {
						return COMPARE_EQUAL;
					} else if(valueL>valueR) {
						return COMPARE_GREATER; 
					} else {
						return COMPARE_LESS;
					}
				}
			}
		} else if(varSetL.size()==2) {
			//	(2) Two variable: testing twice (999,9), (9,999)
			// TODO: Need clone the tree, so can test twice
			// Or replace integer value ?
			
		}
		
		return COMPARE_ERROR;
	}

	public static Type convertParameterType(ArgTupleType funcCallSign, Type varType, boolean bReturnVar) {
		Type newType = varType;
		bReturnVariableConversion = true;
		if(DEBUG_Func) out.println("[convertParameterType]["+funcCallSign.getName()+" "+varType+" "+bReturnVar);

		if((varType instanceof MatrixType) 
				&& (((MatrixType) varType).getSize().getDims()==null)) {
			// (1) Find variable inside dynamic dimension
			java.util.List<String> strDimsNew = new ArrayList<String>();
			java.util.List<String> strDims=((MatrixType) varType).getSize().getDynamicDims();
			for(String strExtern: strDims) {
				if(isInteger(strExtern)) {
					strDimsNew.add(strExtern);
				} else if(strExtern.equals(":")){
					strDimsNew.add(strExtern);
				} else {
					ExprStmt tree = parseString(strExtern);					
					java.util.List<String> ParameterList = gFuncCallParameterMap.get(funcCallSign);
					java.util.List<String> ArgumentList = gFuncCallArgumentMap.get(funcCallSign);
					if(DEBUG_Func) out.println("[convertParameterType]["+ParameterList+"]["+ArgumentList);
					HashSet<String> varSet = getVariableListFromString(tree);
					if(varSet!=null) {
						// (2) Find the relation with function parameter; Assume it's true!
						// (3) Find the corresponding caller argument variable
						for(String pName: varSet) {
							String argName =null;
							if(bReturnVar) {
								// Search the value, caller side argument variable name 
								if(ParameterList!=null && ArgumentList!=null) {
									for(int i=0; i<ParameterList.size(); i++) {
										if(DEBUG_Func) out.print("[convertParameterType]["+funcCallSign.getName());
										if(DEBUG_Func) out.print("]("+i+") param="+ParameterList.get(i));
										if(DEBUG_Func) out.println("; arg="+ArgumentList.get(i)+";  var="+pName);
										if(ParameterList.get(i).equals(pName)) {
											argName = ArgumentList.get(i);
											break;
										}
									}
								}
							} else {
								//  convert argument's type to sub-function's type
								if(ParameterList!=null && ArgumentList!=null) {
									for(int i=0; i<ArgumentList.size(); i++) {
										if(DEBUG_Func) out.print("[convertParameterType-2]["+funcCallSign.getName());
										if(DEBUG_Func) out.print("]("+i+") param="+ParameterList.get(i));
										if(DEBUG_Func) out.println("; arg="+ArgumentList.get(i)+";  var="+pName);
										if(ArgumentList.get(i).equals(pName)) {
											argName = ParameterList.get(i);
											break;
										}
									}
								}
							}
				    		if(argName==null) {
				    			// This happens when there's no enough information about that sub-function
				    			System.err.println("[convertParameterType]["+funcCallSign.getName()+" Return="+bReturnVar);
				    			dumpTypeInfo(varType, "varType=",true, System.err);
				    			System.err.println("[Func-Error] the variable "+pName+" has no corresponding argument variable!");
				    			bReturnVariableConversion = false;
				    		} else {
				    			// (4) change the type with new argument variable
				    			// this replace still generate the dynamic type
				    			// later, need to generate ALLOCATABLE variable, for them...
				    			tree.constantPropagation(pName, new NameExpr(new Name(argName)));
				    			// Set flag to adjust later
				    			bAdjustDynamicShape= true;						    			
				    		}
						}
					}								
					strDimsNew.add(getExprStmtString(tree));
				} // if(isInteger(...						
			} // for(...
			newType = new MatrixType(new PrimitiveType(varType.getName()));
			Size newSize = new Size();
			newSize.setDynamicDims(strDimsNew);
			((MatrixType)newType).setSize(newSize); 
		} // if( MatrixType...
		return newType;
	}
	
	//-------------------------------------------------------------------------
	// Transform a function call to a sub-routine form, 
	// Transformation doesn't change LHS of this node, just add LHS variables 
	// to RHS function call expression (as arguments of parameterized expression)
	// This can handle multi-variable on LHS
	// Using AssignStmt.isCall as flag to indicate
	//    whether it has been transformed to subroutine or not
	
	public static void transformFunc2Subroutine(SymbolTableScope stScope, ASTNode varNode) {		
		// if(DEBUG_Func) System.out.println("[Func2Sub] var "+varNode.getStructureString() + " "+varNode);
		if(!(varNode instanceof AssignStmt) || ((AssignStmt)varNode).isCall)
				return; 
		
		ASTNode parentNode = ASTToolBox.getParentAssignStmtNode(varNode);
	    if(parentNode!=null && ((AssignStmt) parentNode).getRHS() instanceof ParameterizedExpr) {
	    	ParameterizedExpr rhs = (ParameterizedExpr)((AssignStmt) parentNode).getRHS();
	    	Expr lhs = ((AssignStmt) parentNode).getLHS();
	    	// For LHS is matrix, break down the LSH, i.g. [A,B]=func() 
	    	if(lhs instanceof MatrixExpr) {
	            // There should be only one row
	            for(Row row : ((MatrixExpr)lhs).getRows()) {
	    	        for(Expr element : row.getElements()) {
		                rhs.addArg(element);
	    	        }
	            }
	    	} else {	// For LHS has only one variable, 
	    		rhs.addArg(lhs);
	    	}
	    	((AssignStmt)varNode).isCall = true;
	    	
			// if(DEBUG_Func) System.out.println("[Func2Sub] parent "+parentNode.getStructureString()+ " "+rhs.getStructureString() + " "+parentNode.getParent());
	    	// put the new expression into statement list
	    	if(parentNode.getParent() instanceof List) {
	    		List<Stmt> stmtList = (List<Stmt>)parentNode.getParent();
	    		int loc = stmtList.getIndexOfChild(parentNode);
	    		ExprStmt callStmt = new ExprStmt(rhs);
	    		stmtList.setChild(callStmt, loc);

	    	/*	
	    		// If put this before addDynamicAllocation(), following are necessary!!
	    		// Update Symbol table entry, so that last
	    		// addDynamicAllocation() can find the right place to adding code
		    	HashSet<String> varSet = new HashSet<String>();
		    	if(lhs instanceof NameExpr) {
		    		varSet.add(((NameExpr)lhs).getVarName());
		    	} else if(lhs instanceof MatrixExpr) {
		            // There should be only one row
		            for(Row row : ((MatrixExpr)lhs).getRows()) {
		    	        for(Expr element : row.getElements()) {
		    	        	if(element instanceof NameExpr) 
		    	        		varSet.add(((NameExpr)element).getVarName());
		    	        }
		            }
		    	}
		    	for(String varName : varSet ) {
		    		SymbolTableEntry ste = stScope.getSymbolById(varName);
		    		if(ste!=null && ste.getNodeLocation().equals(varNode)) {
		    			ste.setNodeLocation(callStmt);
		    		}
		    		
		    	}
		        */
	    	} 
	    	
	    	
	    }
	}
	
	//-------------------------------------------------------------------------
	// The function will infer type of a symbol by its definition node (varNode)
	// 
	public static void inferSymbolEntry(SymbolTableScope stScope, SymbolTableEntry e)
	{
		inferSymbolEntry(stScope, e, e.getNodeLocation(), true);
	}
	// bNewInfer: need to infer type from the AST
	public static Type inferSymbolEntry(SymbolTableScope stScope, SymbolTableEntry e, 
			ASTNode varNode, boolean bNewInfer) 
	{
		TypeInferenceEngine.gstScope=stScope;
	    Type rhsType = null;  
	    VariableDecl varDecl = (VariableDecl) e.getDeclLocation();
	    if(varNode==null) {
	    	varNode = e.getNodeLocation();
	    }		
    	if ( varDecl == null) {
    		System.err.println("VariableDecl of ["+e.getSymbol()+"] should not be null!");
    		if(DEBUG_Type) out.println("VariableDecl of ["+e.getSymbol()+"] should not be null!");
    	} else {
    		// [#1] Infer type from RHS expression, 
    	    Type varType = null;  
    		Type orgType = varDecl.getType();
		    dumpTypeInfo(orgType, "[inferSymbolEntry]<"+varNode.getNodeID()+">["+e.getSymbol()+"] "+varNode.getStructureString()+" isFirm=["+e.isFirmType+"]Original Type:");
		    // Find the assignment statement of the variable, 
		    // varNode should be the LHS of it. 
		    AssignStmt parentNode = ASTToolBox.getParentAssignStmtNode(varNode);
    		Expr lhs = ((AssignStmt) parentNode).getLHS();
    		Expr rhs = ((AssignStmt) parentNode).getRHS();

		    if(parentNode!=null) {
		    	// (1) Perform the type inference in the first time when this function is called
		    	if(bNewInfer) {
				    varType = rhsType = rhs.collectType(stScope, varNode);

				    dumpTypeInfo(varType, "New Inferred Type of "+e.getSymbol()); 
				    
				    // When there is an user-defined function, then stop type inference
				    if(((rhsType!=null) && (rhsType instanceof ArgTupleType))) {
				    	return rhsType;
				    }
		    		// (#) Transform COMPLEX type structure
		    		if(TypeInferenceEngine.isComplexType(rhsType) && !(rhs instanceof ParameterizedExpr) 
		    				&& !(rhs instanceof NameExpr)) 
		    		{
		    			transform2Complex(stScope, rhs);
		    		}
				    
				    // (2) Merge new inferred type with the one already had
					if(TypeInferenceEngine.isValidType(varType) ) {
						if(TypeInferenceEngine.isValidType(orgType) ) {
						    if(e.isFirmType && orgType instanceof MatrixType) {
						    	if(DEBUG_Type) out.println("[inferType]-["+e.getSymbol()+"]e.isFirmType="+e.isFirmType+"; orgType="+(orgType instanceof MatrixType));
					    		// Only merge the compatible primitive data type
						    	String varTypeName=((MatrixType)orgType).getElementType().getName();
						    	PrimitiveType pType = (PrimitiveType)TypeInferenceEngine.mergePrimitiveType(varType,
					    			new PrimitiveType(varTypeName));
						    	if(!TypeInferenceEngine.isEqualType(((MatrixType)orgType).getElementType(), pType)) {
					    			((MatrixType)orgType).setElementType(pType);
					    			varType = orgType;
					    			varDecl.setType(varType);
					    		}
					    		if(DEBUG_Type) out.println("[inferType]-["+e.getSymbol()+"]pType.getName()="+pType.getName()+"; varTypeName="+varTypeName);
								if(DEBUG_Type) dumpTypeInfo(varType, "Merged Type of (FirmType) "+e.getSymbol()+"=");
							} else {
								// Merge the whole type
								Type resultType = TypeInferenceEngine.mergeType(varType, orgType);
								if(DEBUG_Type) dumpTypeInfo(resultType, "Merged Type of "+e.getSymbol()+"=");
				    		    varDecl.setType(resultType);
				    		    varType = resultType;
							}
			    		} else {	
			    			// Set the new type to be the variable type 
			    		    varDecl.setType(varType);
			    		}
					}
		    	} else {
		    		// This case used for doing type infer 2nd time, just for adjust the 
		    		// array-access expression 
		    		varType = orgType;
		    	}
	        	dumpTypeInfo(varType, "[inferType]-5["+e.getSymbol()+"]="); // dumpSymbolEntry(e);
if(DEBUG_isFirmType || true) {  
	        	// (3) Set the type to be firmed/unchangeable
	        	if((lhs instanceof NameExpr) && varType instanceof MatrixType) {
	        		if(rhs instanceof ParameterizedExpr) {
		        		String fname = ((NameExpr)((ParameterizedExpr) rhs).getTarget()).getName().getID();
			            if(fname.equalsIgnoreCase("zeros") 
			            		|| fname.equalsIgnoreCase("ones")
			            		|| fname.equalsIgnoreCase("randn") 
			            		|| fname.equalsIgnoreCase("rand")) 
			            {		    
			            	e.isFirmType = true;
			            	// For the case that initial the array for concatenation
			            	for(Expr arg: ((ParameterizedExpr) rhs).getArgs()) {
			            		if(TypeInferenceEngine.isIntLiteral(arg) &&
			            				TypeInferenceEngine.getIntLiteralValue(arg)==0) {
	    			            	e.isFirmType = false;
			            			break;
			            		}
			            	}
			            }
	        		} else if(rhs instanceof NameExpr) {
	        			// A=zeros(n,m); Data=A; 
	        			// A is firm, here transfer its firm to Data
        				String var = ((NameExpr)rhs).getVarName();
	        			SymbolTableEntry entry = stScope.getSymbolById(var);
	        			if(entry!=null && entry.isFirmType)
			            	e.isFirmType = true;

	        		} else if(rhs instanceof RangeExpr 
	        				|| rhs instanceof MatrixExpr) {
	        			// A=1:n , A=[1,2,3]
			            e.isFirmType = true;
	        			
	        		} else if(rhs instanceof BinaryExpr) {
	        			// Data=zeros(n,m)*0.12; =>temp=zeros(n,m);Data=temp*0.12
	        			// temp is firm, here transfer its firm to Data
	        			if(((BinaryExpr)rhs).getLHS() instanceof NameExpr) {
	        				String var = ((BinaryExpr)rhs).getLHS().getVarName();
		        			SymbolTableEntry entry = stScope.getSymbolById(var);
		        			if(entry!=null && entry.isFirmType)
				            	e.isFirmType = true;
	        			}
	        			if(((BinaryExpr)rhs).getRHS() instanceof NameExpr) {
	        				String var = ((BinaryExpr)rhs).getRHS().getVarName();
		        			SymbolTableEntry entry = stScope.getSymbolById(var);
		        			if(entry!=null && entry.isFirmType)
				            	e.isFirmType = true;
	        			}
	        		}
	        	}
}
	        	// (*1) Handle the use of array variables
        		if(rhs instanceof ParameterizedExpr) {
	        		String fname = ((NameExpr)((ParameterizedExpr) rhs).getTarget()).getName().getID();
	        		if(stScope.getSymbolById(fname)!=null) {
		            	// If it's an array variable, set its index as integer
			        	setIndex2Integer(stScope, (ParameterizedExpr) rhs);
	        		}
        		} else if((lhs instanceof NameExpr) && (rhs instanceof RangeExpr)) {
if(DEBUG_isFirmType) {        		
        			// (*3) Handle the case of loop index variable
        			if(parentNode.getParent() instanceof ForStmt) {
    					if(TypeInferenceEngine.isIntegerType(rhsType)) {
	        				e.isFirmType = true;
				    		if (!TypeInferenceEngine.isIntegerType(varType)) {
				    			((VariableDecl)e.getDeclLocation()).setType
				    				(new PrimitiveType(TypeInferenceEngine.TYPENAME_INTEGER));
				    		}
				    		// System.err.println("e.isFirmType = true;"+e.getSymbol()+"="+rhs.getStructureString());
    					}
        			}
}
        		}
	            if(rhs instanceof RangeExpr) {
	            	NameExpr varExpr = null;
	        		if(lhs instanceof NameExpr) {
	        			varExpr = (NameExpr) lhs;
	        		} else if(lhs instanceof ParameterizedExpr) {
	        			varExpr = (NameExpr)((ParameterizedExpr) lhs).getTarget();
	        		}
	        		if(varExpr!=null)
	        			TypeInferenceEngine.SpecialTransform(varExpr, "III"); 
	            }
	            


	        	// [#2] Get the value of variable, save in symbol table entry	        	
	        	if(varType instanceof PrimitiveType) {

		        	if(!e.isConstant() || e.getValue() == null) {
		        		// Set value for primitive type variable
		        		// Get value from the expression
		        		// (1) RHS is a variable : get value from that variable
		        		// (2) RHS is an parameterized expression: get the Fortran string
		        		//		- a function call:	OK
		        		//		- an array variable access:
		        		// (3)
		        		String strValue=null;
	        			
		        		if ((rhs instanceof RangeExpr)) {
		        			// Need decide which one is the last
		        			boolean bIncreased = true;
		        			if(((RangeExpr)rhs).hasIncr()) {
		        				Expr incExpr = ((RangeExpr)rhs).getIncr();
		        				if(TypeInferenceEngine.isIntLiteral(incExpr)) {
		        					bIncreased = (TypeInferenceEngine.getIntLiteralValue(incExpr)>0);
		        				} else if(TypeInferenceEngine.isFPLiteral(incExpr)) {
		        					bIncreased = (TypeInferenceEngine.getFPLiteralValue(incExpr)>0);
		        				}
		        			}
		        			String lowValue = null;
		        			if(bIncreased) {
		        				strValue = TypeInferenceEngine.getVariableValue(stScope, ((RangeExpr)rhs).getUpper());
		        				lowValue = TypeInferenceEngine.getVariableValue(stScope, ((RangeExpr)rhs).getLower());
		        			} else {
		        				strValue = TypeInferenceEngine.getVariableValue(stScope, ((RangeExpr)rhs).getLower());
		        				lowValue = TypeInferenceEngine.getVariableValue(stScope, ((RangeExpr)rhs).getUpper());
		        			}
		        			if(strValue!=null)
		        				e.setMaxValue(new StringLiteralExpr(strValue));
		        			if(lowValue!=null)
		        				e.setMinValue(new StringLiteralExpr(lowValue));		        	
		        					        		
		        		} else {
		        			strValue = null;	boolean bSet = false;
		        			if(TypeInferenceEngine.isIntegerType(varType) 
			        			 && (rhs instanceof ParameterizedExpr)) {
			        			// Don't need to calculate cases:
			        			// i.e.: int n=floor(x);
		        				// Value-JL

		        			} else {
		        				// TODO: Using RHS;  TypeInferException
		        				// here is trying to get the string from combining with  
		        				// the original variables. (retrieving to the first assignment) 
		        				try {
		        					strValue = TypeInferenceEngine.getVariableValue(stScope, rhs);
		        					
		        					// Handle the index=index+1 
		        					if(TypeInferenceEngine.isIntegerType(varType)) {
		        						bSet = getIndexVariableValue(stScope, e, rhs);
		        					}
		        					// public static Type inferSymbolEntry...(SymbolTableScope stScope, SymbolTableEntry e, 
		        					//		ASTNode varNode, boolean bNewInfer) 
		        					// {
		        					
		        				} catch(TypeInferException tie) {
		        					// Get the expression node that causes the exception
		        					String msg = tie.getMessage();
		        					Expr nodeExpr = rhs;
		        					if(tie.node!=null) 
		        						nodeExpr = (Expr) tie.node;
		        					if(DEBUG) System.err.println("[TypeInferException] solved variable:"+msg+" "+tie.node);
		        					// There are following cases:
		        					if(msg.equals("i") || msg.equals("j") || isImaginaryPart(msg)) {
		        						// (1) which means RSH is an complex number
		        						if(DEBUG) System.out.println("[TypeInferException] complex="+rhs.getStructureString());
		        						transform2Complex(stScope, rhs);
		        						varType = new PrimitiveType(TypeInferenceEngine.TYPENAME_COMPLEX);
		        						varDecl.setType(varType);
		        					} else {
		        						// (2) This must be a function in a command form
		        						// 		i.e.: timing = toc + 1, t1=clock, ...		        						
		        						// Don't need to handle it here, the inferType() handle it!
		        						if(TypeInferenceEngine.cmdFormFuncList.contains(tie.node)) {
		        							System.out.println("[TypeInferException] cmd-form function="+msg+" "+tie.node);
		        						} else {
			        						// (3) If the program is not syntactically correct,
			        						// there are un-defined variables
		        							System.err.println("[TypeInferException] undefined variable:"+msg+" "+tie.node);
		        						}
		        						
		        						// Ignore the value of the function
		        						strValue = null;
		        					}
		        				}
		        			}
		        			if(strValue==null) {
		        				// Using LHS, save the variable name as value
		        				strValue = ((AssignStmt) parentNode).getLHS().getVarName();
		        			}
		        			
	        				if(!bSet)
	        					e.setValue(new StringLiteralExpr(strValue));
		        			if(DEBUG_Type) out.println("[Value]-set #1["+e.getSymbol()+"]="+strValue);
		        		}
		        	} else{
		        		if(DEBUG_Type) out.println("[Value]-get ["+e.getSymbol()+"]="+e.getValue());
		        	}		        	
        		}
	        	
	        	// JL-
	        	if(varType instanceof MatrixType && lhs instanceof NameExpr) {
        			transform2Scalar(stScope, e, (MatrixType) varType, (NameExpr)lhs);
	        	}
	        	// The above function may cause tranformation, therefore, 
	        	// need to reset some variables.  
	        	lhs = ((AssignStmt) parentNode).getLHS();
	        	
				// [#3] LHS Range adjust,  
	        	// Check LHS, whether it's an array with correct indexing
		        if (lhs instanceof ParameterizedExpr) {
		        	boolean bExpand = false;
		        	ParameterizedExpr expr = (ParameterizedExpr) lhs; 
		        	Type mType;
		        	
		        	// (1) Merge type
		        	// For firm-type, LHS type is the original type 
				    if(e.isFirmType) {
				    	if(DEBUG_Type) out.println("[infer]-["+e.getSymbol()+"]e.isFirmType="+e.isFirmType+"; varType="+(varType instanceof MatrixType));
				    	mType = orgType;
				    } else {
				    	// For non-firm-type, need to 
			        	// merge current variable type (varType) with the expression's type
			        	MatrixType resultType = TypeInferenceEngine.adjustMatrixType(varType, expr);
			        	mType = resultType;
			        			        	
			        	if(TypeInferenceEngine.isValidType(mType)) {
			    		    varDecl.setType(mType);
			    		    e.setConstant(false);
			        	}
			        	
			        	dumpTypeInfo(resultType, "[infer]-adjustMatrixType["+e.getSymbol()+"]=");
				    }
            		// TODO: ... 
		        	// (*1) The array index variable must be integer type 
		        	setIndex2Integer(stScope, expr);

		        	// (2) Change LHS, 
		        	// When using linear indexing, which means index misses some dimensions
		        	// i.g.  A=Matrix(1*5),   expr:  A(2)=0.0  => A(1,2) 
		        	// i.g.  B=Matrix(5*1),   expr:  B(3)=0.0  => B(3,1)
		        	// i.g.  C=Matrix(2*10),  expr:  C(4)=0.0  => B(1,4)	// row major ??
		        	// 						  expr:  C(15)=0.0 => B(2,5)	// row major
		        	TypeInferenceEngine.adjustParameterizedExpr((MatrixType)mType, expr);
		        	
		        	if(TypeInferenceEngine.isValidType(varType)) {
			        	if((varType instanceof PrimitiveType)) {
			        		bExpand = true;	// mType.getSize().getDims().size()>0;
			        	} else {
			        		// TODO: After merge type, they should be same.
			            	java.util.List<Integer> vDims = ((MatrixType)varType).getSize().getDims();
			            	java.util.List<Integer> mDims = ((MatrixType)mType).getSize().getDims();
			        		
			        		if(vDims!=null && mDims!=null) { 
				        		bExpand = (((MatrixType)mType).getSize().getDims().size()> 
				        			((MatrixType)varType).getSize().getDims().size());
			        		} else if(vDims==null && mDims==null) { 
			        			java.util.List<String> vstrDims = ((MatrixType)varType).getSize().getDynamicDims();
			        			java.util.List<String> mstrDims = ((MatrixType)mType).getSize().getDynamicDims();
			        			if(vstrDims.size()!=mstrDims.size()) {
			        				bExpand = true;
			        			} else {
			        				bExpand = false;
			        				// ??? compare each dimension's string
			        				//for(int i=0;i<vstrDims.size();++i) {
			        				//	if(!(vstrDims.get(i).equals(mstrDims.get(i)))) {
			        				//		bExpand = true;
			        				//	}
			        				//}
			        			}		        			
			        		} else {
			        			bExpand = true;	
			        		}
			        	}
				    }
				    // (3) Expand the LHS or adjust RHS
		        	if(bExpand) {
		        		// Set flag to update all the previous define-node and use-node, 
		        		bAdjustArrayAccess = true;
			        	if(DEBUG_Type) out.println("[inferType]-expand["+e.getSymbol()+"]<"+varNode.getNodeID()+">");
		        	} else {		        	
			        	if(DEBUG_Type) out.println("[inferType]-adjustParamAssignment["+e.getSymbol()+"]<"+expr.getStructureString()+">");
			    		// TODO: [4] Special case,  
				        // 	U(1 : n, j1) = tmp;  U={n,m},  tmp={n,1}
		        	    // MATLAB legal, but Fortran needs RHS to be tmp(:,1);
			        	// right now, this part only handle this special case 
			        	if((mType instanceof MatrixType) && (rhsType instanceof MatrixType)  ) {
			        		TypeInferenceEngine.adjustParamAssignment(expr, (MatrixType) mType, rhs, rhsType);
			        	}
		        	}
			        
		        } else if (lhs instanceof NameExpr) {
		        	// (2) Change LHS, 
		        	// When LHS dimension doesn't match the RHS's, needs adjust the LHS's index 
		        	// It's usually the row/column vector case. 
		        	// i.g. A=Matrix(1*5), then A=1:5  should be A(1,:)=1:5
		        	// BUT: A=1 doesn't need to change!
		        	// Currently focus on handling the row/column vector case.
		        	if((orgType instanceof MatrixType)) {
		        		// Need to check RHS's matrix type, to do the matching 
		        		varType = ((AssignStmt) parentNode).getRHS().collectType(stScope, varNode);
			        		
			        	if(DEBUG_Type) dumpTypeInfo(varType, "[inferType]-adjustArrayIndex["+e.getSymbol());
			        	if(DEBUG_Type) dumpTypeInfo(orgType, "[inferType]-adjustArrayIndex["+e.getSymbol());
			        	// varType != orgType && 

			        	if((varType instanceof MatrixType)) {
			        		try {
				        		TypeInferenceEngine.adjustArrayIndex(
				        				(MatrixType)varType, (MatrixType)orgType, (NameExpr)lhs);
	        				} catch(TypeInferException tie) {
	        					// Get the expression node that causes the exception
	        					// 1. Renaming the variable, and it reaches nodes,
	        					String orgName = ((NameExpr)lhs).getVarName();
								String newName = TypeInferenceEngine.getNewVarName(orgName, lhs);
								if(DEBUG_Renaming) out.println("[Need to renaming variable by flow] <"+lhs.getParent().getNodeID()+"> "+orgName+" -> "+newName);
								RenamingRule newRule = new RenamingRule(orgName, lhs.getParent(), 
										newName, lhs.getParent());
								java.util.List<ASTNode> defNodeList = RenamingAllReached(gCodeNodeList, newRule);

								// 2. Create new symbol table entry for it
								TypeInferenceEngine.addDeclNodeSymTblEntry(newName, varType,
										lhs.getParent().getParent(), stScope, (Stmt)lhs.getParent());
								
								// 3. Modify gCodeNodeEntryMap, remove old relation node-symtbl-entry
								// 	create new one
								defNodeList.add(varNode);
								for(ASTNode def: defNodeList) {
									gCodeNodeEntryMap.remove(def);
									ArrayList<SymbolTableEntry> seList = new ArrayList<SymbolTableEntry>();
									seList.add(stScope.getSymbolById(newName));
									gCodeNodeEntryMap.put(def, seList);
								}
								if(DEBUG_SymTbl)  dumpSymbolTable(stScope, out, lhs);
	        				}
			        	} else {
			        		// This is the case, A=1, A=Matrix(n,m)
			        	}
		        	}
		        }
		        
			    // Disable "Refine" rewrite defined in TypeInference.jrag
		        // No use anymore, because code-gen has its own build target
			    // if(varType != null)
			    //	varDecl.setRefine(true);
		    }
    	}
    	return rhsType;
	}
	
	// Transform a 1x1 matrix into a scalar, 
	// create temporary variable for it, and update its symbol table	
	public static void transform2Scalar(SymbolTableScope stScope, SymbolTableEntry e, 
			MatrixType varType, NameExpr varNode) {
		if(!(varNode.getParent() instanceof AssignStmt))
			return;
		if((varNode.getVarName().contains(TEMP_VAR_PREFIX)))	
			return;
		
		// Check to make sure it's 1x1 matrix
		boolean onebyone = false;
		Size varSize = varType.getSize();
    	if(varSize != null) {
			if(varSize.getDims()!=null) 
				if(varSize.getDims().size()==2)
					for(Integer dim: varSize.getDims()) {
						if(dim!=1) {
							onebyone = false;
							break;
						} else {
							onebyone = true;
						}
					}
    	}
		if(!onebyone)
			return;
		
		AssignStmt asg = (AssignStmt)varNode.getParent();
		ASTNode parent = asg.getParent();
		int loc = parent.getIndexOfChild(asg);		
		
		if(DEBUG) out.println("[transform2Scalar]"+varNode.getVarName()+" "+asg.getStructureString()+" loc="+loc);
		
		// addNewAssignment
		// Create new assignment and add to AST
		String tmpName = TypeInferenceEngine.getTempVarName(asg);
		NameExpr lhs = new NameExpr(new Name(tmpName));
		
		AssignStmt newAssign = new AssignStmt();
		natlab.ast.List<Expr> list = new natlab.ast.List<Expr>();
		list.add(new IntLiteralExpr(new natlab.DecIntNumericLiteralValue("1")));
		list.add(new IntLiteralExpr(new natlab.DecIntNumericLiteralValue("1")));
		ParameterizedExpr arrayExpr = new ParameterizedExpr(new NameExpr(new Name(tmpName)), list);

		
		// #. because type inference based on the se-list saved in the gCodeNodeEntryMap.get(codeNode)
		// which is created by buildSymbolTable(): 
		// So it's important to change the current assignment to new one!!!
		newAssign.setLHS(lhs);
		newAssign.setRHS(asg.getRHS());
		newAssign.generateUseBoxesList();
		// Add the new assignment to location
		parent.insertChild(newAssign, loc);

		// update current assignment
		asg.setLHS((NameExpr)varNode);
		asg.setRHS(arrayExpr);
		asg.generateUseBoxesList();

		// Add new declaration node and symbol table entry
		MatrixType pType = TypeInferenceEngine.createMatrixType(varType);
		TypeInferenceEngine.addDeclNodeSymTblEntry(tmpName, pType, parent, stScope, asg);
		
		// SymbolTableEntry eorg = stScope.getSymbolById(varNode.getVarName());
		if(e!=null) {
			((VariableDecl )e.getDeclLocation()).setType(varType.getElementType());
			e.setNodeLocation(newAssign);
		}
	}	

	public static boolean getIndexVariableValue(SymbolTableScope stScope, SymbolTableEntry e,Expr rhs) {
		// 1. the statement format should be i=i+1
		AssignStmt asgStmt = (AssignStmt) rhs.getParent();
		Expr lhs = asgStmt.getLHS() ; 
		if(!(lhs instanceof NameExpr) || !(rhs instanceof PlusExpr))
			return false;

		boolean hasIntLiteral = false;
		int inc = 0;
		Expr rhsVarExpr = null;
		// RHS must have one Integer constant operand
		if( TypeInferenceEngine.isIntLiteral(((PlusExpr)rhs).getLHS()))  {
			inc = TypeInferenceEngine.getIntLiteralValue(((PlusExpr)rhs).getLHS());
			hasIntLiteral = true;
			rhsVarExpr = ((PlusExpr)rhs).getRHS();
		} else if( TypeInferenceEngine.isIntLiteral(((PlusExpr)rhs).getRHS()))  {
			hasIntLiteral = true;
			inc = TypeInferenceEngine.getIntLiteralValue(((PlusExpr)rhs).getRHS());
			rhsVarExpr = ((PlusExpr)rhs).getLHS();
		}
		if(!hasIntLiteral)
			return false;
//    	System.err.println("[getIndexVariableValue]"+inc+"["+rhsVarExpr.getStructureString()+"]"+
//   			TypeInferenceEngine.getLiteralString(e.getValue()));
		// RHS must have only one variable which equal to the lhs variable
		if((rhsVarExpr == null) || !(rhsVarExpr instanceof NameExpr) 
				|| !((NameExpr)rhsVarExpr).getVarName().equals(e.getSymbol()))
			return false;					
			
		// 2. statement inside a loop, (and the loop should have only one of this)
		if(!(asgStmt.getParent().getParent() instanceof ForStmt))
			return false;

		ForStmt forStmt = (ForStmt)asgStmt.getParent().getParent();
		String loopVar = forStmt.getAssignStmt().getLHS().getVarName();
    	SymbolTableEntry stEntry = stScope.getSymbolById(loopVar);
    	
		// 3. the min() and max() are the same, (implies only handle one of this)
    	if(e.getValue()!=null && !(TypeInferenceEngine.getLiteralString(e.getValue())
    			.equals(TypeInferenceEngine.getLiteralString(e.getValueMin()))))
    		return true;
    	String value = "";
    	if(e.getValue()==null)
    		value = "0";
    	else 
    		value = TypeInferenceEngine.getLiteralString(e.getValue());
    	String strMax = "("+TypeInferenceEngine.getLiteralString(stEntry.getValue())
    				+"-"+TypeInferenceEngine.getLiteralString(stEntry.getValueMin())+"+1)*"+inc+"+"+value+"";
    	String strMin = value;
    	
		e.setMaxValue(new StringLiteralExpr(strMax));
		e.setMinValue(new StringLiteralExpr(strMin));		        	

		if(DEBUG) System.err.println("[getIndexVariableValue]"+value+"["+strMax+"]["+strMin+"]");
		if(DEBUG) System.err.println(TypeInferenceEngine.getLiteralString(e.getValue()) +"]["+TypeInferenceEngine.getLiteralString(e.getValueMin())+"]");

		return true;
	}
	
	private static void setIndex2Integer(SymbolTableScope stScope, ParameterizedExpr expr) {
    	// The array index variable must be integer type
        for(Expr arg : expr.getArgs()) {
        	NameExpr varExpr = null;  
        	if(arg instanceof NameExpr) {
        		varExpr = (NameExpr) arg;
        	} else if(arg instanceof ParameterizedExpr) {
        		varExpr = (NameExpr) ((ParameterizedExpr) arg).getTarget();
        	}
        	if(varExpr != null) {
        		String idxVar = ((NameExpr) varExpr).getVarName();
		    	SymbolTableEntry stEntry = stScope.getSymbolById(idxVar);
		    	if(DEBUG) out.println("[setIndex2Integer]"+varExpr.getVarName()+" "+expr.getStructureString());
		    	if(stEntry!=null ) { // && !stEntry.isFirmType
		    		if(stEntry.getDeclLocation()!=null) {
			    		Type pType = ((VariableDecl)stEntry.getDeclLocation()).getType();
			        	dumpTypeInfo(pType, "[arry index]-["+idxVar+"]=");
			    		if (!TypeInferenceEngine.isIntegerType(pType)) {
			    			if(pType instanceof PrimitiveType) {
				    			((VariableDecl)stEntry.getDeclLocation()).setType
				    				(new PrimitiveType(TypeInferenceEngine.TYPENAME_INTEGER));
			    			} else {
			    				Type idxType = TypeInferenceEngine.createMatrixType
			    					(TypeInferenceEngine.TYPENAME_INTEGER, (MatrixType)pType);
			    				((VariableDecl)stEntry.getDeclLocation()).setType(idxType);
					        	dumpTypeInfo(idxType, "[arry index]-["+idxVar+"]=");
			    			}
			    		}
		    		}
if(DEBUG_isFirmType) {  
		    		stEntry.isFirmType = true;
}
		    	}
        	}
        }
	}
	
	// Remove those uncessary variable decl nodes
	public static void removeDeclNodes(ASTNode actual) {
		int limit = 0;
		if(actual instanceof FunctionDecl)
			limit = 3;
		// Cleanup all previous created decl-nodes
		for(int i=0; i<actual.getNumChild(); ++i) {
			if(i>=limit && actual.getChild(i) instanceof natlab.ast.List) {
				natlab.ast.List<ASTNode> list = (natlab.ast.List) actual.getChild(i);
				for(int j=list.getNumChild()-1; j>=0; --j) {
					if(list.getChild(j) instanceof VariableDecl)  {
						list.removeChild(j);
					}
				}
			}
		}
	}
	
	//-------------------------------------------------------------------------
	// TODO: generate symbol table by flow-analysis  
	// Precondition: there should no Decl-node exist
	//-------------------------------------------------------------------------
	public static SymbolTableScope buildSymbolTable(ASTNode actual) 
	{
		// Create new symbol table scope during the SSA conversion
		SymbolTableScope symtbl = new SymbolTableScope();
		
        //-----------------------------------------------------------------
        // Reaching definition analysis
		// [1] Generate use/def boxes list of each node of the tree 
        actual.clearUseDefBoxes();
		actual.generateUseBoxesList();
        
        //-----------------------------------------------------------------
		// Dump only the code-node -- for debug purpose
		// code-node: the node that we don't care about its subtree
		// Includes: simple-statement, assignment-statement, ... 
		if(DEBUG_Type) out.println();
		if(DEBUG_Type) out.println("--- Code Nodes [buildSymbolTable]----------------------------------");
		if(DEBUG_Type) out.println("--- [<ID>] Node.Class.Name [code][Use-Boxes][Def-Boxes]level--------");
		if(DEBUG_Type || DEBUG_Symbol) out.print(actual.dumpCodeTree());
		if(DEBUG_Flow) out.println("--- Reaching-Definition Analysis -----------------------------------");
					
		// [2] Calling Reaching defs directly  
		// Set the debug flag and out	
		AbstractFlowAnalysis.setDebug(DEBUG_Flow, (PrintStream) out);
		ReachingDefs defsAnalysis = new ReachingDefs(actual);

		// [3] Retrieve the result	
		// Sample code for outputting the result flow-set (after set)
	    Map<ASTNode, FlowSet> afterMap = defsAnalysis.getResult();
	    Map<ASTNode, FlowSet> beforeMap = defsAnalysis.getBeforeFlow();
	    java.util.List<ASTNode> codeNodeList = defsAnalysis.getNodeList(); 

	    FlowSet newDefSet =  defsAnalysis.getEmptySet();
	    FlowSet stopDefSet =  defsAnalysis.getEmptySet();
	    FlowSet preFlowset = null; 

		// Renaming rules is an ordered list, should be applied by order
		ArrayList<RenamingRule> PhiRules = new ArrayList<RenamingRule>();
		ArrayList<RenamingRule> RRules = new ArrayList<RenamingRule>();
		
		// buildSymbolTable() DIFFERENCE from convert2SSA(): Don't apply any rules!
		// TODO: So those code to create rule/ apply rule can be removed
		//---------------------------------------------------------
		// Flow-analysis type inference Algorithm
		//---------------------------------------------------------
		// [1] From the flow-sets, for new-defs, add them into symbol table
		// [2] For re-def-node, infer type of them, 
		//		choose one smallest compatible type (or generate union type) 
        //		for that variable, and update symbol table entry.
		//---------------------------------------------------------
		// Convert to SSA form Algorithm
		//---------------------------------------------------------
		// <1> Apply the existed renaming rules on the before/after set
		// <2> Checking before-set for nodes that define same variable
		//		Creating phi-function, new node, new def-box, and new 
        //		renaming rule. Applying the new renaming rule on phi-node.
		//		Updating before/after sets of current node by replacing
        //		duplicate def-nodes by new phi-node.
		// <3> Apply the existed renaming rules on current node!
		// <4> Checking stop-def-set/new-def-set, for new renaming rule
		// <5> Apply the new renaming rules on current nodes!
		// <#> While checking all code nodes one by one, all the rules 
		//		will be applied on the node, then SSA form generated!
		// 
		//---------------------------------------------------------
		// TODO: Assumption: one node only define one variable!
		// In the flow set, there are two cases need to be considered:
		// Case 1: new definition replaces the old one => Renaming
		// Case 2: two definition nodes merge into one set => Phi-function
		
	    // variable and its definition node for all the program, only keeps the first definition
	    HashMap<String, ASTNode> varDefMap = new HashMap<String, ASTNode>();	

	    // Save the result for further use (for convenience) 
	    // gCodeNodeEntryMap DON"T clear it, 
	    gCodeNodeList = codeNodeList;
	    gTreeCodeNodeMap.put(actual, codeNodeList);

		// TODO: [#] Adding the Function's input parameters into symbol table 
	    if(actual instanceof Function) {
	    	// ((Function)actual).initFlags();
		    ((Function)actual).addInputParam2SymbolTableScope(symtbl);
			dumpSymbolTable(symtbl, out, false);
	    }
	    
	    // [#] Go through the code node list
	    for(ASTNode codeNode: codeNodeList) {
	    	
			FlowSet afterSet = afterMap.get(codeNode);
			FlowSet beforeSet = beforeMap.get(codeNode);
			
	    	// DEBUG_Flow =(codeNode.getNodeID()==107); 
			if(DEBUG_Flow) out.println("doAnalysis on: " + codeNode.getNodeID() +" ["+ codeNode.getDefBoxes()+"] ["+ codeNode.getUseBoxes());	
			if(DEBUG_Flow) out.println("\t Before-Flow: " + beforeSet);
			if(DEBUG_Flow) out.println("\t After-Flow : " + afterSet);

			// Get the difference between two flow-sets
			if(beforeSet!=null)
				preFlowset = beforeSet;
			else 
				preFlowset = defsAnalysis.getEmptySet();
			
			// Analyze the before/after flow sets, to decide the renaming rules
			preFlowset.difference(afterSet, stopDefSet);
			afterSet.difference(preFlowset, newDefSet);

			if(DEBUG_Flow) out.println("\t Stop-Flow: " + stopDefSet);
			if(DEBUG_Flow) out.println("\t New--Flow: " + newDefSet);

			// <1> Applying phi-rules, 
			// Changes the flow-sets of current node

			// variable set of current flow-set
			HashSet<String> varSet = new HashSet<String>();
			// variable and it phi-rule of current flow-set
		    HashMap<String, RenamingRule> varRuleMap = new HashMap<String, RenamingRule>();

			// Because ForStmt, WhileStmt has the union of all def-nodes,
			// but they don't have any actual def-node.
			if((codeNode instanceof ForStmt)
					|| (codeNode instanceof WhileStmt)){
				continue;
			}

		    // <2>, create renaming rules, phi-nodes
		    //FlowSet workFlowSet;

		    // Finding new variable definition only need to check the after set
	    	// using the after set : workFlowSet = afterSet;
		    // (beforeSet==null) // This is the case for node "While/For/... "
			if(beforeSet!=null) {
		    	//workFlowSet = afterSet;	
		    	// (1) collecting variables definitions (into varDefMap),
			    //     and finding duplicated def-nodes (into varRuleMap)
		    	
		    	// Here we only need to create symbol table entry for current node 
		    	// if it's an assignment statement (has definitions).
		    	// There is no need to go through all the nodes in flow-set. 
		    	// Because when it's in a loop, flow-set contain union of all definition in the loop,
		    	// where is wrong.
				// for(Object defNode: workFlowSet.toList()) 	//-JL 02.23
		    	{
		    		ASTNode defNode = codeNode;
					// For each node, it will define (at least) one variable
					if(defNode==null || defNode.getDefBoxes()==null) {
						continue;
					}
					
					// Check if there are new variables defined
					for(Object vb: defNode.getDefBoxes()) {
						boolean isNewVar = true;
						
						if(DEBUG_Flow) out.println("[DefNode]<"+defNode.getNodeID()+">="+((natlab.toolkits.ValueBox) vb).getValue());
						
						for(String varName: varSet) {
							// Attached duplicated node to the rule, after 2nd def-node
							if (varName.equals(((natlab.toolkits.ValueBox) vb).getValue())) {
								if(DEBUG_Flow) out.println("[varName.equals]"+varName);
								RenamingRule rule = varRuleMap.get(varName);
								if(rule!=null) {
									rule.addDefNode(defNode);
								} else {
									// creating new rule, when 2nd def-node found
									ASTNode preDefNode = varDefMap.get(varName);
								 	// Create new phi-rule 
									RenamingRule newRule = new RenamingRule(varName, preDefNode,
											TypeInferenceEngine.getNewVarName(varName, codeNode), 
											preDefNode, defNode);
									varRuleMap.put(varName, newRule);
									rule = newRule;
								}
								isNewVar = false;								
								// break;
								if(DEBUG_Flow) out.println("[varName.rule]"+rule);
							}
						}
						// (2) Create symbol entry for new variables, and 
						// save result into varDefMap, gCodeNodeEntryMap
						if(isNewVar) {
							String newVarName = ((natlab.toolkits.ValueBox) vb).getValue(); 
							varSet.add(newVarName);
							if(null==varDefMap.get(newVarName)) {
								varDefMap.put(newVarName, defNode);
								if(DEBUG_Symbol) out.println("[varDefMap.put] "+newVarName+" <"+(defNode).getNodeID()+">:"+"["+defNode+"]:"+(defNode).getVarName());
							}
							// Create symbol entry for first time defined variables 
							// if(!((defNode).getVarName().isEmpty()) 
							//		&& symtbl.getSymbolById((defNode).getVarName())==null)
							
							if(symtbl.getSymbolById(newVarName)==null)
							{
								// Check variables with different case in symbol table
								boolean bCheckDiffCase = symtbl.varUpperCaseSet.contains(newVarName.toUpperCase());
								if(bCheckDiffCase) {
									// TODO: Renaming current variable
									// Rewrite LHS of the phiStmt to new name 
									String newName = TypeInferenceEngine.getNewVarName(newVarName, codeNode);
									if(DEBUG_Symbol) out.println("[Need to renaming variable] "+codeNode.getNodeID()+" "+newVarName+" -> "+newName);
									RenamingRule newRule = new RenamingRule(newVarName, codeNode, 
											newName, codeNode);
									RenamingAll(codeNodeList, newRule);
									clearCodeNodeEntryMap(codeNodeList);
									newVarName = newName;
									
									// Because there may have many defintion on same variable,
									// because renaming changes code-nodes,  
									// their def/use boxes are also changed, but this line of code
									// is inside the loop of using those def/use boxes.
									// So we have to force exit this function and redo building symbol table().
									// If we don't exit this function, then the program will encounter another
									// definition on the same variable and cause renaming again and lost 
									// its closest prior defintion.
									return null;	
								}
								// Add variable to symbol table 
								SymbolTableEntry stEntry = new SymbolTableEntry(newVarName, newVarName, defNode); 
								symtbl.addSymbol(stEntry);
                    			// Save a copy into global variable for further type inference
								ArrayList<SymbolTableEntry> seList = gCodeNodeEntryMap.get(defNode);
								if(seList == null) {
									seList = new ArrayList<SymbolTableEntry>();
								} 
								seList.add(stEntry);
								gCodeNodeEntryMap.put(defNode, seList);
				    			if(DEBUG_Symbol) out.println("[gCodeNodeEntryMap]2-put("+(defNode).getNodeID()+" "+stEntry.getSymbol());
                    			if(DEBUG_Symbol) out.println("[Call-addSymbolEntry]["+(defNode).getNodeID()+"]"+(defNode).getVarName()+"[add]codeNode:"+codeNode.getNodeID()+" - SymbolEntry:"+stEntry);
							}
						}
					}
				}
				// (3) Creating phi-node for the flow-set has more than two def-nodes
				// create corresponding RenamingRule.
				for(RenamingRule rule: varRuleMap.values()) {
					if(DEBUG_Flow) out.println("[varRuleMap]"+rule);
					// Gather all arguments of this phi-node
					natlab.ast.List<Expr> arglist = new natlab.ast.List<Expr>();
					for(ASTNode node: rule.nodeList) {
						// 1. AssignStmt/ParamExpr/NameExpr/ has getVarName()
						// 2. Node.getVarName() is updated name -- for renaming...
						// 	but the defBox() is old name -- used for this analysis 
						arglist.add(new NameExpr(new Name(node.getVarName())));
					}
					// Creating the phi-expression
					ParameterizedExpr phiExpr = new ParameterizedExpr(
							new NameExpr(new Name(PhiFunctionName)), arglist);
							
					// New phiStmt has def-box with the origin variable name 
					AssignStmt phiStmt = new AssignStmt(
							new NameExpr(new Name(rule.orgName)), phiExpr);
					phiStmt.setNodeID();
					phiStmt.generateUseBoxesList();	//collectUseBoxesList();
					// Rewrite LHS of the phiStmt to new name 
					RenamingRule newRule = new RenamingRule(rule.orgName, rule.orgDefNode, 
							rule.newName, phiStmt);
					RRules.add(newRule);

					// Complete the phi-rule
					rule.setPhiNode(phiStmt);						
				}
		    } // if(beforeSet!=null)

		    // <4> & <5>
			// Renaming happens when there is new def-node replaced old one
			// stop-def-set: could have >=1 node
			// new-def-set: only one node
			// Using a set is for compatibility, for the case [x,y]=f(n) 
		    varSet.clear();
		    varRuleMap.clear();
		    // Keep varDefMap: the original definition 
		    
		    // newDefSet: new variable defined here, refill the varSet.
			if(!stopDefSet.isEmpty() && !newDefSet.isEmpty()) {
				// 1. Get the variable name
				for(Object defNode: stopDefSet.toList()) {
					// For each node, it will define (at least) one variable
					for(Object vb: ((ASTNode) defNode).getDefBoxes()) {
						varSet.add(((natlab.toolkits.ValueBox) vb).getValue());
					}
				}
				// 2. For each variable, find out the new def-node in newDefSet ...
			    ASTNode varNewDefNode = null;
				for(String varName: varSet) {
					varNewDefNode = null;
					for(Object defNode: newDefSet.toList()) {
						// For each node, it will define (at least) one variable
						for(Object vb: ((ASTNode) defNode).getDefBoxes()) {
							if (varName.equals(((natlab.toolkits.ValueBox) vb).getValue())) {
								varNewDefNode = (ASTNode) defNode;
								break;
							}
						}
						if(varNewDefNode != null)
							break;
					}
					if(varNewDefNode == null)
						System.err.println("Cannot find the new def-node for "+varName);
					else {
					 	// 3. Save this renaming rule 
						if(DEBUG_Symbol) out.println("Found the new def-node for "+varName);
						ASTNode preDefNode = varDefMap.get(varName);
						RenamingRule newRule = new RenamingRule(varName, preDefNode,
								TypeInferenceEngine.getNewVarName(varName, varNewDefNode), varNewDefNode);
						RRules.add(newRule);
						// add it to code node -- symbol entry map
				    	SymbolTableEntry stEntry = symtbl.getSymbolById(varName);
				    	
				    	ArrayList<SymbolTableEntry> seList = gCodeNodeEntryMap.get(varNewDefNode);
				    	if(seList == null) {
				    		seList = new ArrayList<SymbolTableEntry>();
				    	} 
				    	seList.add(stEntry);
				    	gCodeNodeEntryMap.put(varNewDefNode, seList);
				    	
		    			if(DEBUG_Symbol) out.println("[gCodeNodeEntryMap]3-put<"+varNewDefNode.getNodeID()+">"+stEntry.getSymbol());            			
            			if(DEBUG_Symbol) out.println("[add]codeNode:"+varNewDefNode.getNodeID()+" - SymbolEntry:"+stEntry+" total=("+seList.size()+")");
					}
				}

			} // if(!stopDefSet.isEmpty() && newDefSet.isEmpty())

		}

	    if(DEBUG_SymTbl) dumpSymbolTable(symtbl, out, actual);	// Debug purpose

	    if(DEBUG_Flow) out.println("------------------- Updated flow sets () ------------------------------");
	    if(DEBUG_Flow) out.println(RRules);
	    if(DEBUG_Flow) out.println("------------------- Updated m program () ------------------------------");
	    if(DEBUG_Tree) out.print(actual.dumpTreeAll(false));
	    if(DEBUG_Flow) out.println(actual.getStructureString()); 
		
		// [#] Adding declaration for the Script, Function
		actual.updateSymbolTableScope(symtbl);		
		
		return symtbl;	// (SymbolTableScope) 
	}
	
	// This function works throught all the program 
	// renames a variable from old-name to new-name, 
	// regarding about flow-sets.
	public static void RenamingAll(java.util.List<ASTNode> codeNodeList, RenamingRule rule) 
	{
	    // Renaming the def-node 
	    if (rule.getDefNode() instanceof AssignStmt) {
	    	((AssignStmt)rule.getDefNode()).getLHS().renaming(rule.orgName, rule.newName);
	    } else {
	    	rule.getDefNode().renaming(rule.orgName, rule.newName);
	    }
		// Starting from the top, not current node
		// Because we cannot know how deep (nested loop/if) current node is in.
	    for(ASTNode node: codeNodeList) {
			node.renaming(rule.orgName, rule.newName);
			// Cannot change Use/Def boxes here because the line of code 
			// calling this function is inside a loop of using those Use/Def boxes. 
			// The iteration mechanism doesn't allow to do that.
			//	node.clearUseDefBoxes(); 
			//	node.generateUseBoxes();
	    }
	}
	// This should base on flow-sets
	// The goal is to renaming all the variables of same name 
	// in the rest of program (starting from 'startNode') to a new name.
	// Special case, please reference [Type Conflict Flow Analysis]	
	public static java.util.List<ASTNode>  RenamingAllReached(
			java.util.List<ASTNode> codeNodeList, RenamingRule rule) 
	{
	    if(DEBUG_Renaming) out.println("[RenamingAllReached]0:"+rule.getDefNode().getStructureString()+" "+rule.orgName+"->"+rule.newName+ " root="+codeNodeList.get(0));
	    
		ASTNode startNode = rule.getDefNode();
	    java.util.List<ASTNode> defNodeList = new ArrayList<ASTNode> ();
	    defNodeList.add(startNode);
	    // Renaming the def-node 
	    if (rule.getDefNode() instanceof AssignStmt) {
	    	((AssignStmt)rule.getDefNode()).getLHS().renaming(rule.orgName, rule.newName);
	    } else {
	    	rule.getDefNode().renaming(rule.orgName, rule.newName);
	    }
	    
		ReachingDefs defsAnalysis = new ReachingDefs(codeNodeList.get(0));

		// [3] Retrieve the result	
		// Sample code for outputting the result flow-set (after set)
	    Map<ASTNode, FlowSet> afterMap = defsAnalysis.getResult();
	    Map<ASTNode, FlowSet> beforeMap = defsAnalysis.getBeforeFlow();
	    // java.util.List<ASTNode> codeNodeList = defsAnalysis.getNodeList(); 
	    
	    FlowSet newDefSet =  defsAnalysis.getEmptySet();
	    FlowSet stopDefSet =  defsAnalysis.getEmptySet();
	    FlowSet preFlowset = null; 

		// Starting from the top, not current node
		// Because we cannot know how deep (nested loop/if) current node is in.
	    for(ASTNode node: codeNodeList) {
			FlowSet afterSet = afterMap.get(node);
			FlowSet beforeSet = beforeMap.get(node);

			// Get the difference between two flow-sets
			if(beforeSet!=null)
				preFlowset = beforeSet;
			else 
				preFlowset = defsAnalysis.getEmptySet();
			// Analyze the before/after flow sets, to decide the renaming rules
			preFlowset.difference(afterSet, stopDefSet);
			afterSet.difference(preFlowset, newDefSet);

			if(DEBUG_Flow) out.println("\t Stop-Flow: " + stopDefSet);
			if(DEBUG_Flow) out.println("\t New--Flow: " + newDefSet);

			if(beforeSet!=null && beforeSet.contains(startNode)) {
				node.renaming(rule.orgName, rule.newName);
				node.clearUseDefBoxes(); 
				node.generateUseBoxes();
				if(DEBUG_Renaming)  out.println("[Renaming-on]"+node.getStructureString());
			    // Check if there is new defition override the old one
				if(stopDefSet.contains(startNode)) {
					if(!newDefSet.isEmpty()) 
						startNode = (ASTNode)newDefSet.toList().get(0);
				    defNodeList.add(startNode);
					if(DEBUG_Renaming) out.println("[RenamingAllReached] Def-change to <"+startNode.getNodeID()+ "> -> "+startNode.getStructureString());
					
				}
			}
	    }
	    return defNodeList;
	}
	
	// Just remove all entries of this sub-function from map 
	public static void clearCodeNodeEntryMap(java.util.List<ASTNode> codeNodeList) {
	    for(ASTNode node: codeNodeList) {
	    	gCodeNodeEntryMap.remove(node);
	    }
		
	}
	
	
	//-------------------------------------------------------------------------
	public static boolean isVariableName(String test) {
		String strPattern = "[a-zA-Z]([_0-9a-zA-Z])*$";
		return isPatternMatch(strPattern, test);
	}
	public static boolean isInteger(String test) {
		String strPattern0 = "[-+]?0$";
		String strPattern = "[-+]?[1-9][0-9]*$";
		return isPatternMatch(strPattern0, test) || isPatternMatch(strPattern, test)  ;
	}
	public static boolean isFloat(String test) {
		int pos = test.indexOf('.');
		if(pos<0) {
			// Only have integer part
			return isInteger(test) ;
		} else {
			String intStr = test.substring(0, pos);
			String fractStr = test.substring(pos);
			String strPattern1 = ".([0-9])*$";	// .00 are legal,
			
			String strPattern = "[-+]?.([0-9])*$";	//+2.; +.00; .00 are legal, 
			
			return isPatternMatch(strPattern, test) || 
				(isInteger(intStr) && isPatternMatch(strPattern1, fractStr));
		}
			
	}
	public static boolean isImaginaryPart(String test) {		
		return 	(test.charAt(test.length()-1)=='i'  
					|| test.charAt(test.length()-1)=='j')
				&& isFloat(test.substring(0,test.length()-1));
	}
	public static boolean isPatternMatch(String strPattern, String test) {
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(strPattern);
		java.util.regex.Matcher matcher = pattern.matcher(test);
		return matcher.matches();
	}
	
	// Parsing a expression string, and return variables inside it
	// Return :
	//	- null: there is parsing error, i.g. it's empty tree, or other error  
	//  - 
	public static HashSet<String> getVariableListFromString(String content) {
		if(isVariableName(content)) { 
			HashSet<String> varSet = new HashSet<String>();
			varSet.add(content);
			return varSet;
		} else {
			return getVariableListFromString(parseString(content));
		}
	}
	public static HashSet<String> getVariableListFromString(ExprStmt expr) {
		HashSet<String> varSet = new HashSet<String>();
		if(expr==null) {
			return null;
		} else {
			expr.clearUseDefBoxes();
			expr.generateUseBoxesList();
			
			for(Object vb: expr.getUseBoxes()) {
				String variable = ((natlab.toolkits.ValueBox) vb).getValue();
				if(isVariableName(variable)) {
					varSet.add(variable);
				}
			}
			return varSet;
		}
	}
	
	// Get the string of the Expr from that simple tree
	public static String getExprStmtString(ExprStmt expr) {
		if(expr==null) {
			return "";
		} else {
			return expr.getStructureString();
		}
	}
	// Parsing a string, same as Interpreter.java, return Expr node
	// Return:
	//	- Null: cannot parse correctly
	public static ExprStmt parseString(String content) {
		String line = content;
		if(line == null) {
			return null;
		}
		// CommentBuffer commentBuffer = new CommentBuffer();
		NatlabParser parser = new NatlabParser();
		// parser.setCommentBuffer(commentBuffer);
		try {
			NatlabScanner scanner = new NatlabScanner(new StringReader(line));
			// scanner.setCommentBuffer(commentBuffer);
			Program original;
			original = (Program) parser.parse(scanner);
			if(parser.hasError()) {
				System.out.println("**ERROR**");
				for(String error : parser.getErrors()) {
					System.out.println(error);
				}				
			} else if(!original.errorCheck()) {
				// System.out.println(original.dumpTree());
				if(original instanceof Script) {
					// When parsing an expression, will get a Script  
					ASTNode exprStmt = ((Script) original).getStmtList().getChild(0);
					if(!(exprStmt instanceof ExprStmt)) {
						return null;
					} else {
						return (ExprStmt) exprStmt;
					}
				}		
			}
			// scanner.stop();
		} catch(Parser.Exception e) {
			System.out.println("**ERROR**");
			System.out.println(e.getMessage());
			for(String error : parser.getErrors()) {
				System.out.println(error);
			}
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return null;
	}

	//-------------------------------------------------------------------------
	// dump-serial function for debug purpose
	public static void dumpTypeInfo(Type varType, String str)	{
		dumpTypeInfo(varType, str, false, out);
	}
	public static void dumpTypeInfo(Type varType, String str, boolean bDebug, PrintStream out)
	{
		if(DEBUG_Type || DEBUG_Func || bDebug) 
		{
		    StringBuffer buf = new StringBuffer();
		    dumpTypeInfo(varType, str, buf);
		    if(DEBUG_Type || DEBUG_Func || bDebug) out.println(buf.toString());
		}
	}	
	public static void dumpTypeInfo(Type varType, String str, StringBuffer buf) {
		buf.append(" " +str+" {"+(varType==null? "null":(varType.getName()+"}"+varType.getClass().getName())));
		if(varType instanceof MatrixType) {
			buf.append(" [" +((MatrixType) varType).getSize().getStructureString()+"]");
			Size varSize = ((MatrixType) varType).getSize();
			if(varSize.getVariableDims()!=null) {
			// using variable for dynamic allocation
				buf.append("vardim=[" );
				for (String dim : varSize.getVariableDims()) {
					buf.append(dim+", ");
				}
				buf.append("]" );
			}
		}
	}
	// Static method for debug purpose, called from other class
	public static void dumpSymbolEntry(SymbolTableEntry e) {
		dumpSymbolEntry(e, false);
	}
	public static void dumpSymbolEntry(SymbolTableEntry e, boolean bOutput) {
	    StringBuffer buf = new StringBuffer();
	    buf.append("----[dump]");
	    dumpSymbolEntry(e, buf);
		if(DEBUG_SymTbl|| bOutput) out.println(buf.toString());
	}
	// Dump a symbol node and it's declaration node
	public static void dumpSymbolEntry(SymbolTableEntry e, StringBuffer buf) {
		dumpSymbolEntry(e, buf, false,false);
	}
	public static void dumpSymbolEntry(SymbolTableEntry e, StringBuffer buf, boolean firm) {
		buf.append("");
		if(e==null) {
		} else if(e.isFirmType){
			buf.append( e.getSymbol() + " <"+e.getNodeLocation().getNodeID()+">" );
			dumpSymbolDecl(((VariableDecl) e.getDeclLocation()), buf);
			buf.append(" const="+e.isConstant());
			buf.append(" isFirm="+e.isFirmType);
			buf.append(" value=["+TypeInferenceEngine.getLiteralString(e.getValue()));
			buf.append("]["+TypeInferenceEngine.getLiteralString(e.getValueMin())+"]");
			buf.append("\n");
		}
	}
	public static void dumpSymbolEntry(SymbolTableEntry e, StringBuffer buf, boolean firm, boolean limit) {
		if(e==null) {
			buf.append(" [null] ");
		} else {
			buf.append(e.getSymbol());
			buf.append("\t");
			if(!limit) 
				buf.append( " <"+e.getNodeLocation().getNodeID()+">" );
			dumpSymbolDecl(((VariableDecl) e.getDeclLocation()), buf, limit);
			buf.append(" \t const=<"+e.isConstant());
			buf.append("> isFirm="+e.isFirmType);
			buf.append(" value=["+TypeInferenceEngine.getLiteralString(e.getValue()));
			buf.append("]["+TypeInferenceEngine.getLiteralString(e.getValueMin())+"]");
		}
	}
	public static void dumpSymbolDecl(VariableDecl declNode, StringBuffer buf) {
		dumpSymbolDecl(declNode, buf, false);
	}
	public static void dumpSymbolDecl(VariableDecl declNode, StringBuffer buf, boolean limit) {
		if(declNode==null) {
			buf.append(" [null] ");
		} else {
			if(!limit)
				buf.append(" ["+ declNode.getNodeID()+"] "); 
		    dumpTypeInfo(declNode.getType(), "", buf);
			if(!limit)
				buf.append(" {"+ declNode.getStructureString() +"}");
		}
	}
	public static void dumpSymbolTable(SymbolTableScope stScope, PrintStream out, ASTNode actual) 
	{
		dumpSymbolTable(stScope, out, false);
	}
	public static void dumpSymbolTable(SymbolTableScope stScope, PrintStream out, boolean bOutput) 
	{
		dumpSymbolTable(stScope, out, bOutput, false);
	}
	public static void dumpSymbolTable(SymbolTableScope stScope, PrintStream out, boolean bOutput, boolean limit) 
	{
		StringBuffer buf = dumpSymbolTable(stScope, limit);
	    if(DEBUG_SymTbl||bOutput) out.println("---- Dump All entries of Symbol Table ---------------------------------");
	    if(DEBUG_SymTbl||bOutput) out.println(buf.toString());
	    if(DEBUG_SymTbl||bOutput) out.println("-----------------------------------------------------------------------");
	}
	public static StringBuffer dumpSymbolTable(SymbolTableScope stScope, boolean limit) 
	{
	    HashMap<String, SymbolTableEntry> table = stScope.symTable;
	    ArrayList<String> dataList = new ArrayList<String>();
	    for( SymbolTableEntry e : table.values() ){
		    StringBuffer buf = new StringBuffer();
	    	dumpSymbolEntry(e, buf, false, limit);
			// Dump the original node' declaration entry info.
			if((e.getOrgNodeLocation())!=null) {
				SymbolTableEntry varEntry = stScope.getSymbolById(e.getOrgNodeLocation().getVarName());
				dumpSymbolEntry(varEntry, buf);
			} else {
				buf.append(" [<null>] ");
			}
			// buf.append("\n");
			dataList.add(buf.toString());
	    }
	    if(limit) {
	    	Collections.sort(dataList);
	    }
	    StringBuffer bufAll = new StringBuffer();
	    for(String str: dataList) {
		    bufAll.append(str);
		    bufAll.append("\n");
	    }
	    return bufAll;
	}
	
	// Just for dump firmed type
	public static void dumpSymbolTable(SymbolTableScope stScope, PrintStream out, ASTNode actual, boolean bOutput) 
	{
	    HashMap<String, SymbolTableEntry> table = stScope.symTable;
	    
	    StringBuffer buf = new StringBuffer();

	    for( SymbolTableEntry e : table.values() ){
	    	dumpSymbolEntry(e, buf, true);
			// Dump the original node' declaration entry info.
			if((e.getOrgNodeLocation())!=null) {
				SymbolTableEntry varEntry = stScope.getSymbolById(e.getOrgNodeLocation().getVarName());
				dumpSymbolEntry(varEntry, buf);
			} else {
				buf.append(" ");
			}
			// buf.append("\n");
	    }
	    if(DEBUG_SymTbl||bOutput) out.println("---- Dump All entries of Symbol Table ---------------------------------");
	    if(DEBUG_SymTbl||bOutput) out.println(buf.toString());
	    if(DEBUG_SymTbl||bOutput) out.println("-----------------------------------------------------------------------");
	}
	private static void dumpList(java.util.List<String> strList, String info) {
		if(DEBUG_Default) 
		{
			out.print("[dumpList] "+info);
			for(String str: strList) {
				out.print(" "+str+",");
			}
			out.println();
		}
	}
	//-------------------------------------------------------------------------

}

//Each renaming rule associates with a def-node, it means that the variable 
//defined by the 'def-node' should be rename to a new-name.
//If the before-set has that def-node (means that definition reaches here), 
//then rename the variable used by current node.
class RenamingRule {
	String orgName;
	String newName;
	ArrayList<ASTNode> nodeList = new ArrayList<ASTNode>();
	ASTNode phiNode, orgDefNode;
	// Original variable name
	// New name for variable 
	// nodes: 
	//	=1 : current node == new definition node
	//	>1 : a list of def-nodes, used in phi-function
	public RenamingRule(String nameStr, ASTNode orgNode, String name2, ASTNode... nodes) 
	{
		this.orgName = nameStr;
		this.newName = name2;
		this.orgDefNode = orgNode;
		for (ASTNode node: nodes)
			this.nodeList.add(node);
	}
	public boolean isSingle() {
		return nodeList.size()==1;
	}
	public boolean isPhiRule() {
		return nodeList.size()>1;
	}
	public ASTNode getDefNode() {
		return nodeList.get(0);
	}
	public void addDefNode(ASTNode node) {
		nodeList.add(node);
	}
	
	public ASTNode getPhiNode() {
		return phiNode;
	}
	public void setPhiNode(ASTNode node) {
		phiNode = node;
	}
	
	public String toString() {
     StringBuffer buf = new StringBuffer();
     int orgID = (orgDefNode==null)?-1:orgDefNode.getNodeID();
     buf.append(orgName+ " <"+orgID+ "> -> "+ newName +" [" + nodeList.size()+"] ");
		for(ASTNode node: nodeList)
			buf.append(node.getNodeID()+",");
		return buf.toString();
	}
}
