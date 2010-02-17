package natlab;

import java.io.*;

import java.util.*;

import org.antlr.runtime.ANTLRReaderStream;

import matlab.CompositePositionMap;
import matlab.FunctionEndScanner;
import matlab.MatlabParser;
import matlab.OffsetTracker;
import matlab.PositionMap;
import matlab.TextPosition;
import matlab.TranslationProblem;
import ast.*;
import ast.List;

import beaver.Parser;

import natlab.toolkits.scalar.*;

import natlab.SymbolTableScope;
import natlab.SymbolTableEntry;
import annotations.ast.ArgTupleType;
import annotations.ast.MatrixType;
import annotations.ast.PrimitiveType;
import annotations.ast.Size;
import annotations.ast.Type;
import annotations.ast.UnknownType;

import matlab.*;
import matlab.FunctionEndScanner.NoChangeResult;
import matlab.FunctionEndScanner.ProblemResult;
import matlab.FunctionEndScanner.TranslationResult;

import org.antlr.runtime.ANTLRReaderStream;

/**
 * A  for translate MATLAB program to Fortran code
 * 
 *  Usage: 
 *  	filename1 filename2
 *  	-d   folder-name: convert all .m files of the folder to Fortran95
 *  			- pathname starting by '/' will be treated as 
 *  				absolution pathname, otherwise are seen as relative pathname 
 *  
 *  BTW: .n Errors
 *  1.	[3, 1]  unexpected token HELP_COMMENT
 *  	Cannot recover from the syntax error
 *  Solution: manual remove empty line after the function declaration
 *  2. Usage Example:
 *  (1) .\languages\Fortran\benchmarks\fft\drv_fft.m 
 *  (2) -d .\languages\Fortran\benchmarks\adapt\
 */ 
public class McFor {
	static boolean DEBUG = false; 
	static int cnt = 0;
	
	final static String benchmarkFolderName = ".\\languages\\Fortran\\benchmarks";
	final static String TRANSLATION = "TRANSLATION";
	final static String CONVERTION = "CONVERTION"; 
	final static String PHI_FUNC_NAME = "PHISSA";
	final static String ALPHA_FUNC_NAME = "_Alpha_";
	final static String BETA_FUNC_NAME = "_Beta_";
	final static String LAMBDA_FUNC_NAME = "_Lambda_";
	final static String TEMP_M_FILENAME = "_temp_m2f.m";
	
	
	static String PATH_STRING = "/";			// different in DOS and Linux 

	// Collect uses from declaration
	static java.util.List<String> gExtraVarList = new ArrayList<String>();
	static String[] ExtraVarList={"pi", "eps", "timing_tic", "timing_toc","timing_clock",
			"III", "JJJ", "output_real_tmp", "int_tmpvar", "arg_buffer", "ZERO", "ONE"};

	static PrintStream out = System.out; 
	static PrintStream outSymTbl = null; 
	
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
    static String fortranname="", basename="", pureName="", temp_filename="";
	
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
    
    // Link code node with a list of symbol table entry, keep for whole process		--OK
    // Because one assignment may have more than one return variables 
    // Also, more than one node can points to same symbol-table-entry
    // It is filled when creating symbol table.
    static HashMap<ASTNode, ArrayList<SymbolTableEntry>> gCodeNodeEntryMap 
    		= new HashMap<ASTNode, ArrayList<SymbolTableEntry>>();

    
    // TODO: Each function should have one, 
    // Map between variable-name and a list of its definition nodes
    // During the type inference process, a new temporary variable can be created
    // by type inference engine, but save no record in this map. 
    // Therefore, when encounter this case, the program manually add a new def-node-list to this map.   
    static HashMap<String, ArrayList<ASTNode>> gVariableDefNodeMap 
    		= new HashMap<String, ArrayList<ASTNode>>();

    
    // Save the scope for each sub-tree
	static HashMap<ASTNode, SymbolTableScope> gTreeSymbolTableMap = new HashMap<ASTNode, SymbolTableScope>();
	// Whether there are changes in symbol table through the type inference
	static HashMap<SymbolTableScope, Integer> gSymbolTableUpdateMap = new HashMap<SymbolTableScope, Integer>();

	// call node and its tree node
	// Extra transformation for each function call
	// Transform the function calls to subroutine calls, for those user define
    // For subroutine, translate into CALL func(p1,p2,...,r1,r2);
	// This transformation will postponed to just before code-gen, because
	// it creates new node and cause inconsistent to symbol table entry
	// transformFunc2Subroutine(codeNode);
	public static HashMap<ASTNode, ASTNode> gFuncCallNodeMap 
				= new HashMap<ASTNode, ASTNode>();

	// Link node with its function call signature
	public static HashMap<ASTNode, ArgTupleType> gFuncCallNodeSignMap = 
		new HashMap<ASTNode, ArgTupleType>();
	
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

	// Each function call node has its own calling signature, 
	// and it maps to two lists, ParameterList and ArgumentList
	public static HashMap<ArgTupleType, java.util.List<String>> gFuncCallParameterMap 
			= new HashMap<ArgTupleType, java.util.List<String>>();
	public static HashMap<ArgTupleType, java.util.List<String>> gFuncCallArgumentMap 
			= new HashMap<ArgTupleType, java.util.List<String>>();
	
	public static HashMap<String, java.util.List<String>> gFuncParameterMap 
				= new HashMap<String, java.util.List<String>>();
	public static HashMap<String, java.util.List<String>> gFuncReturnVarMap 
				= new HashMap<String, java.util.List<String>>();	

	// A map between a function's Parameter's name and the caller site argument' name
	// Each function call will has one entry, even call to same function 
		
	// TODO: Current database
	// 1. Take a list of files, where the first file is the main program
	// 2. taking .m files instead of .n files in a folder to translate

	// TODO: if it doesn't use anywhere else, maybe it's better to be a local variable 
    // A list of sub-tree, each represents a M-file
	// This is only used in the preparing phase - inline script M-files 
	static HashMap<String, ASTNode> gsubTreeMap = new HashMap<String, ASTNode>();
	
	// Save the map of the phi-function expression and a list of pointers 
	// that each points to a definition of this variable 
	static HashMap<Expr, ArrayList<ASTNode>> gPhiFunctionMap = new HashMap<Expr, ArrayList<ASTNode>>();
	
	// Saves all the assignment nodes, whose LHS are indexed array accesses,  
	// that need array bounds check	
	// TODO: can it work on multiple functions? adding checking functions?
	static HashMap<SymbolTableEntry, HashSet<ASTNode>> gArrayVarCheckNodeMap
			= new HashMap<SymbolTableEntry, HashSet<ASTNode>>();
	static HashSet<String> gArrayVarCheckList = new HashSet<String>();

    //-------------------------------------------------------------------------
	/**
	 * Main entry, arguments are described in displayUsage() 
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length < 1) {
			displayUsage();
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
	public static void displayUsage() {
		System.err.println("Usage: ");
		System.err.println("   java natlab.McFor filename.m [filename2.m] ...");
		System.err.println("Note: Converting a list of specified files to Fortran-95.");
		System.err.println("      The first file in the list should be the main program.");
		System.err.println();
		System.err.println("   java natlab.McFor -d folder-name ");
		System.err.println("Note: Converting all .m files of the folder to Fortran-95.");
		System.err.println("      The file whose name is starting with 'drv_' should be the main program.");
		System.err.println("      The pathname starting by '/' will be treated as a absolution pathname,\n"
		                 + "      otherwise it is a relative pathname ");
		System.err.println("Support options: -fblas -fbounds-check");
	}
	
	/**
	 * M2F() : The core function for translating MATLAB program into Fortran
	 * 			Calling this method can get full functionality of this class. 
	 * @param args : same arguments from command line
	 * @return : the file name of the translated Fortran program 
	 */
	public static boolean FLAG_BLAS = false;
	public static boolean FLAG_BOUNDS_CHECK = false;
	public static String M2F(String[] args) {
		initialAll();
		TypeInferenceEngine.DEBUG = DEBUG;
		
		ArrayList<String> filelist = new ArrayList<String>();
		ArrayList<String> filelist1 = null;
		// Parsing the arguments
    	// (1) Translate a directory/list of .m files to .n files, get the .n filename list  
		for(int j=0; j<args.length; ++j) {
			if(args[j].equals("-fblas")) {
				FLAG_BLAS = true;
				System.err.println("-fblas FLAG_BLAS = true;");
			} else if(args[j].equals("-fbounds-check")) {
				System.err.println("-fblas FLAG_BOUNDS_CHECK = true;");
				FLAG_BOUNDS_CHECK = true;
			} else {
				if(args[j].equals("-d")) {	
		        	if(args.length==1) {	
		        		displayUsage();
		        		System.exit(1);
		        	} else {	// argument has the directory name, e.g., 
		        				// -d ..\Benchmarks\matlabBenchmarks\test\
		        		filelist = translateFolder(args[j+1]);
		        		break;
		        	}
		        } else {
		        	// For a list of file(s), then translate them one by one
		        	for(int i=j; i<args.length; i++) {
		        		if(args[i]!=null) {
			        		filelist1 = translateFolder(args[i]);
			        		if(i==j)
			        			solveFilename(args[i]);
			        		filelist.addAll(filelist1);
		        		}
		        	}
		        	break;
		        }
			}
		}
		
    	// (2) Translate a list of .m files to Fortran
		convert2Fortran(filelist);
        
        // (3) Compile the file and run it -- in Linux
        // systemCommand( "gfortran -O3 -Wall "+fortranname+" -o "+ basename);
        // systemCommand(basename);	
		
		System.out.println("-- Done --");
		return fortranname;
	}
	
	/**
	 * Initialize all global variables
	 */
	public static void initialAll() {
		// Decide the path separator for Linux/Windows 
        int startIndex = System.getProperty("user.dir").lastIndexOf("/");
        if(startIndex<=0) {
        	PATH_STRING = "\\";
        } else {
        	PATH_STRING = "/";
        }
		
        // New Database
        gsubTreeMap.clear();
        gPhiFunctionMap.clear();
        gArrayVarCheckNodeMap.clear();
        gArrayVarCheckList.clear();
        gSymbolTableUpdateMap.clear();
        
        
        // Old code code
	    gFuncCallVarMap.clear();
	    gEntryScopeMap.clear();
	    gFuncReturnTypeListMap.clear();
	    gFuncCallNodeMap.clear();
	    gFuncCallNodeSignMap.clear();
	    
	    gFuncCallParameterMap.clear();
	    gFuncReturnVarMap.clear();
	    gFuncCallArgumentMap.clear();
	    gFuncParameterMap.clear();
	    
	    gTreeList.clear();
	    gReInferTreeStack.clear();
	    gNextInferFuncNameList.clear();
	    
	    gCodeNodeList = null;
	    
	    gTreeCodeNodeMap.clear();
	    gCodeNodeEntryMap.clear();
	    gTreeSymbolTableMap.clear();
	    
	    gExtraVarList.clear();
	    for(int i=0; i<ExtraVarList.length; ++i) {
	    	gExtraVarList.add(ExtraVarList[i] );
	    }
	    
	    ASTNode.VarCnt = 0;
	    TypeInferenceEngine.initialAll();
	}
	
	/**
	 * Translate all .m files inside the folder into .n files.  
	 * @param folderName : same arguments from command line
	 * @return : the list of the translated .n filenames 
	 */
	public static ArrayList<String> translateFolder(String folderName) {
		ArrayList<String> filelist = new ArrayList<String>();
		if(folderName==null)
			return filelist;		

		// Working on absolute pathname 
		String absDirName = getAbsoluteName(folderName);
	    File mainFolder = new File(absDirName);
	    // Check existence of the folder/file 
	    if(!mainFolder.exists()) {
		    System.err.println("Error: Folder/file name doesn't exist! ("+absDirName +")");
		    System.exit(2);
	    }
	    System.out.println("Folder/file name: "+absDirName);
	    // Check whether it's a file, 
	    if(!mainFolder.isDirectory()) {
            String nfilename = translateFile(absDirName);
    	    filelist.add(nfilename);
	    	return filelist;
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
	    // Don't enter sub-folder, because '.svn' bothers the program

	    // Don't enter sub-folder, because '.svn' bothers the program
	    File[] subFolders = null;	//  mainFolder.listFiles(fileFilter);
	    if (subFolders == null || subFolders.length==0) {
	    	// There is only one level of directory
	    	subFolders = new File[1];
	    	subFolders[0] = mainFolder;
	    } 
    	// Traverse all sub-folders
    	for(File dir : subFolders) {
		    String[] children = dir.list(mfileFilter);
	        for (int i=0; i<children.length; i++) {
	        	if(children[i].equals(TEMP_M_FILENAME))
	        		continue;
	        	String filename = dir.getAbsolutePath()+PATH_STRING+children[i];
	            String nfilename = translateFile(filename);
	            filelist.add(nfilename);
	        }

	        // If there is no .m file, checking .n files
	        if(children.length==0 || filelist.size()==0) {
	        	children = dir.list(nfileFilter);
		        for (int i=0; i<children.length; i++) {
		        	if(children[i].equals(TEMP_M_FILENAME))
		        		continue;
		        	String filename = dir.getAbsolutePath()+PATH_STRING+children[i];
		            String nfilename = translateFile(filename);
		            filelist.add(nfilename);
		        }
    		}

    		System.out.println("translate dir: "+dir+" ("+filelist.size()+" files)");
	        
	        // Because for files in a folder, we assume 'drv_' is the main program, thus,
	        // we rearrange the order of the files, make the first one to be the main program
	        ArrayList<String> list = new ArrayList<String>();
	        list.addAll(filelist);
	        filelist.clear();
	        for (int i=0; i<list.size(); i++) {
	        	String filename = getPureFilename(list.get(i));
	        	if(filename.substring(0, 4).equalsIgnoreCase("drv_")) {
		        	solveFilename(list.get(i));
		        	filelist.add(list.get(i)); 
		        	list.remove(i);
		        	break;
	        	}
	        }
	        filelist.addAll(list);	        
		}
		
		return filelist;
	}

	/**
	 * Calling MATLAB-to-Natlab translator to translate .m to .n files
	 * @param filename
	 * @return the translated .n filename
	 */
	public static String translateFile(String filename) {
		// Don't translate .n file
		if(hasExtension(filename, ".n"))
			return filename;
		System.out.println("Translating: "+filename);
        filename = removeExtension(filename, ".m");
		String[] files = {filename};
		// matlab.Translator.main(files);
		MATLAB2Natlab(filename);
		return filename +".n"; 
	}
	// This is taken from main() of matlab.Translator
	public static void MATLAB2Natlab(String filename) {
        String basename = filename;
        try {
            BufferedReader in = new BufferedReader(new FileReader(basename + ".m"));

            FunctionEndScanner prescanner = new FunctionEndScanner(in);
            FunctionEndScanner.Result result = prescanner.translate();
            in.close();

            if(result instanceof NoChangeResult) {
                in = new BufferedReader(new FileReader(basename + ".m")); //just re-open original file
            } else if(result instanceof ProblemResult) {
                for(TranslationProblem prob : ((ProblemResult) result).getProblems()) {
                    System.err.println(prob);
                }
                System.exit(0); //terminate early since extraction parser can't work without balanced 'end's
            } else if(result instanceof TranslationResult) {
                TranslationResult transResult = (TranslationResult) result;
                in = new BufferedReader(new StringReader(transResult.getText()));
            }

            OffsetTracker offsetTracker = new OffsetTracker(new TextPosition(1, 1));
            ArrayList<TranslationProblem> problems = new ArrayList<TranslationProblem>();
            String destText = MatlabParser.translate(new ANTLRReaderStream(in), 1, 1, offsetTracker, problems);
            
            PrintWriter out = new PrintWriter(new FileWriter(basename + ".n"));
            if(problems.isEmpty()) {
                out.print(destText);
            } else {
                for(TranslationProblem prob : problems) {
                    System.err.println("~" + prob);
                }
            }
            out.close();
            in.close();
            // System.exit(0);
        } catch(IOException e) {
            e.printStackTrace();
            // System.exit(2);
        }
	}

	
	/**
	 * Executes system command in Linux, print out the outputs
	 * @param cmd
	 * @return
	 */
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
	// Some utility functions
	public static String removeExtension(String filename, String ext) {
		int len = filename.length() - ext.length();
        if(filename.substring(len).equals(ext)) {
        	filename = filename.substring(0,len);
        }
        return filename;
	}
	public static boolean hasExtension(String filename, String ext) {
		int len = filename.length() - ext.length();
        return (filename.substring(len).equals(ext));
	}
	
	// Discover relative pathname and convert it to absolute pathname
	public static String getAbsoluteName(String name) {
		String absDirName = name;
		// For Windows pathname, absolute pathname includes a driver name, e.g., C:\
		if(!name.subSequence(1, 2).equals(":")) {
			// Otherwise, Checking the first letter 
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
		filename = getAbsoluteName(filename);
		basename = filename;
        if(basename.substring(basename.length()-2).equals(".m")
        		|| basename.substring(basename.length()-2).equals(".n")) {
        	basename = basename.substring(0,basename.length()-2);
        } 
        pureName = getPureFilename(basename);
	    fortranname = basename + ".f95";
		int loc = basename.lastIndexOf(pureName);
    	temp_filename = basename.substring(0,loc)+TEMP_M_FILENAME;	    
	}
	// Return the filename without pathname
	public static String getPureFilename(String filename) {
		// Because Java accepts mixed Linux and Windows path separators, 
		// we need check both.
		String[] path_string = {"\\","/"};
		String name = filename;
		for(String str:path_string) {
			int loc = name.lastIndexOf(str);
			if(loc>0)
				name = name.substring(loc+1);
		}
		return name;
		// int loc = filename.lastIndexOf(PATH_STRING);;
		// return filename.substring(loc+1);
	}
		
	//-------------------------------------------------------------------------
	
	public static boolean isSymbolTableChanged(SymbolTableScope scope) {
		Integer val = gSymbolTableUpdateMap.get(scope);
		return val!=null && val>0;
	}
	public static void recordSymbolTableChange(SymbolTableScope scope) {
		Integer val = gSymbolTableUpdateMap.get(scope);
		if(val==null)
			gSymbolTableUpdateMap.put(scope, new Integer(1));
		else
			gSymbolTableUpdateMap.put(scope, val+1);
	}
	public static void clearSymbolTableChange(SymbolTableScope scope) {
		gSymbolTableUpdateMap.remove(scope);
	}
	
	/**
	 * Parsing a file to a AST, using Natlab parser
	 * For file has multiple functions, it will return a FunctionList  
	 * @param filename: the file to be parsed
	 * @param out: the PrintStream stores result
	 * @return ASTNode: root node of the AST
	 */
	public static ASTNode parseFile(String filename, PrintStream out) {
		filename = getAbsoluteName(filename);
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
            	System.err.println("Parsing result is null!");
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
	/**
	 * Main Function for translating a list of .n files to Fortran code
	 * It does:
	 * 	- inline the script call
	 *  - create a temporary M-file
	 * @param filelist: list of filenames
	 */
	public static void convert2Fortran(ArrayList<String> filelist) {
		// main function file name
		String mainfile = "", tmpFilename = "";
		boolean first = true;
		// sub-function and sub-script file names
		ArrayList<String> funcList = new ArrayList<String>();
		ArrayList<String> scriptList = new ArrayList<String>();
		
		// 1. Parsing all the files into a list of sub-tree
		for(String filename: filelist) {
		    ASTNode astTree = parseFile(filename, System.out);
		    String treename = "";
		    if(astTree instanceof Function) {
		    	treename = ((Function) astTree).getName();
			    if(!first)
			    	funcList.add(treename);
		    } else if(astTree instanceof FunctionList) {
		    	for(Function func:((FunctionList)astTree).getFunctionList()) {
			    	treename = ((Function) func).getName();
				    if(!first)
				    	funcList.add(treename);
		    	}
		    } else if(astTree instanceof Script) {
			    treename = removeExtension(getPureFilename(filename),".n");
			    if(!first)
			    	scriptList.add(treename);
		    } else {
		    	System.err.println("Unsupport sub-tree's type: "+astTree);
		    }
		    gsubTreeMap.put(treename, astTree);
		    if(first) {
		    	mainfile = treename;
		    	first = false;
		    }
		}
		// 2. Traverse the each function, inline script calls
		if(filelist.size()>1) {
			// (1) check every script file,
			//		- inline calls to scripts
			//			thus it don't have any script calls, we don't 
			//			need to check the script after it is inlined into others
			ASTNode subtree = null;
			for(String treename: gsubTreeMap.keySet()) {
				if(!treename.equals(mainfile)) {
					subtree = gsubTreeMap.get(treename);
					if(subtree instanceof Script) {
						inlineScript(subtree, scriptList, treename);
					}
				}
			}
			
			// (2) check main file
			//		- inline script calls
			subtree = gsubTreeMap.get(mainfile);
			inlineScript(subtree, scriptList, mainfile);
	
			// (3) check every function, 
			//		- inline script calls
			for(String treename: gsubTreeMap.keySet()) {
				if(!treename.equals(mainfile)) {
					subtree = gsubTreeMap.get(treename);
					if(subtree instanceof Function) {
						inlineScript(subtree, scriptList, treename);
					}
				}
			}
			// 3. Merge them into a temporary file, then call converting function.
			tmpFilename = temp_filename;
			buildMFile(mainfile, tmpFilename);
		} else {
			tmpFilename = filelist.get(0);
		}
		
		// 4. Convert the whole file to Fortran
		String outFilename = fortranname;
    	convert2Fortran(tmpFilename, outFilename);
	}
	
	// Building the temporary .m file, from a list of AST sub-tree, includes only functions
	public static void buildMFile(String mainfile, String tmpFilename) 
	{
		PrintStream outTemp = null; 
	    try {
	    	outTemp = new PrintStream(tmpFilename);
	    	
			// 		- add main to temporary file
			ASTNode subtree = gsubTreeMap.get(mainfile);
			append2MFile(subtree, outTemp, mainfile);
			
			// 		- add other functions to temporary file
			for(String treename: gsubTreeMap.keySet()) {
				if(!treename.equals(mainfile)) {
					subtree = gsubTreeMap.get(treename);
					if(subtree instanceof Function || subtree instanceof FunctionList) {
						append2MFile(subtree, outTemp, treename);
					}
				}
			}			
			outTemp.close();
		} catch(IOException e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
			System.exit(9);
		}
	}
	public static void append2MFile(ASTNode subtree, PrintStream outTemp, String name) 
	{
		if(subtree instanceof Script) {  // Add function header
			outTemp.println("function "+name);
		}
		outTemp.println(subtree.getPrettyPrinted());
		if(subtree instanceof Script) { // Add 'end'
    		outTemp.println();
			outTemp.println("end ");
		}
	}	
	
	// For calls to script M-files, inline the script by replacing call node by script's AST
	public static void inlineScript(ASTNode actual, ArrayList<String> scriptList, String name) 
	{
		if(actual==null) {
			System.err.println("Error: inline-script receives a null pointer");
			return;
		}

        // Using reaching definition analysis to get the list of code nodes
        actual.clearUseDefBoxes();
		actual.generateUseBoxesList();
		ReachingDefs defsAnalysis = new ReachingDefs(actual);

	    java.util.List<ASTNode> codeNodeList = defsAnalysis.getNodeList();
	    for(ASTNode codeNode : codeNodeList) {
	    	if(codeNode instanceof ExprStmt) {
	    		Expr expr = ((ExprStmt)codeNode).getExpr();
	    		String exprName = null;
	    		// Command form function/script call
	    		if(expr instanceof NameExpr) {
		    		exprName = ((NameExpr) expr).getName().getID();		    		
	    		} else if(expr instanceof ParameterizedExpr) {
		    		Expr target = ((ParameterizedExpr) expr).getTarget();
		    		if(target instanceof NameExpr) {
		    			exprName = ((NameExpr) target).getName().getID();
		    		}
	    		}

	    		if(exprName!=null && scriptList.contains(exprName)) {
	    			// Inline script call
	    			if(true||DEBUG) System.out.println("["+name+"] Inline script call: "+exprName);

	    			ASTNode subtree = gsubTreeMap.get(exprName);
	    			if(subtree!=null) {
		    			ASTNode parent = codeNode.getParent();
		    			int loc = parent.getIndexOfChild(codeNode);
		    			// Save the comments attached to this node, 
		    			// by replacing it with an empty statement
		    			Stmt emptyStmt = new EmptyStmt();
		    			emptyStmt.setComments(codeNode.getComments());
		    			parent.setChild(emptyStmt, loc);
		    			// Adding reset of statments from the inlined script
		    			int i=1;
		    			for(Stmt stmt: ((Script)subtree).getStmtList()) {
	    					parent.insertChild(stmt, loc+i);
	    					i++;
		    			}
	    			}
	    		}
	    	}
	    }
	}
	
	
	public static ASTNode preprocess(ASTNode actual) {
        // Add extra output statement for output parameters in the main-function 
		if(actual instanceof FunctionList) {
	    	boolean bMainFunc = true;
	    	for(Function func:((FunctionList)actual).getFunctionList()) {
	    		func.mainFunc = bMainFunc;
				if(bMainFunc && ((Function) func).getOutputParamList().getNumChild()>0) {
					List<Stmt> stmtlist = ((Function) func).getStmtList();	            	
		    		for(Name outparam :((Function) func).getOutputParamList()) {
		    	    	ast.List<Expr> list = new ast.List<Expr>();
		    			list.add(new NameExpr(outparam));
		    	    	ParameterizedExpr dispExpr = new ParameterizedExpr(new NameExpr(new Name("disp")), list);
		    	    	ExprStmt dispStmt = new ExprStmt(dispExpr);
		    	    	dispStmt.setOutputSuppressed(true);
		    	    	stmtlist.add(dispStmt);	            	
		    		}
				}
				bMainFunc = false;
	    	}
	    }
		
		// actual.renaming(CellArrayExpr, MatrixExpr);
		// Temporary solution for Changing Cell array to Matrix ...
		if(actual instanceof FunctionList) {
	    	for(Function func:((FunctionList)actual).getFunctionList()) {
	    		changeCell2Matrix(((Function) func).getStmtList());
	    	}
	    } else if(actual instanceof Script) {
    		changeCell2Matrix(((Script) actual).getStmtList());
	    }
		return actual;
	}
	public static void changeCell2Matrix( ast.List<Stmt> stmtList) {
		for(Stmt stmt : stmtList) {
			if(stmt instanceof AssignStmt) {
				Expr rhs = ((AssignStmt) stmt).getRHS();
				if(rhs instanceof CellArrayExpr) {
					MatrixExpr matrixExpr = new MatrixExpr(((CellArrayExpr)rhs).getRows());
					((AssignStmt) stmt).setRHS(matrixExpr);
				}
			}
		}
	}
	
	//-------------------------------------------------------------------------
	// This is the main coordinator
	// It will recursively traverse the AST tree, when encountering 
	//	an user-defined function call, it will go into it to get inference result 
	//-------------------------------------------------------------------------
	public static void convert2Fortran(String filename, String fName) {
		// Printing the output of the program,  
		fortranname = fName;
		System.out.println("Converting to Fortran: "+filename);
		System.out.println("Fortran file: "+fortranname);

		try {
			out = new PrintStream(fortranname);	
			// outSymTbl = new PrintStream(fortranname+".sym");	// DEBUG

			// Parsing the file
    		ASTNode actual = parseFile(filename, out);

    		// McFor pre-process, changing sth. 
    		actual = preprocess(actual);
    		
			// Initial the ASTNode's static variable ID to 0, so it can work 
			// properly while called by other program. 
	        actual.initNodeID();

    		// Handle each function one by one
	        // The AST has two possible types: FunctionList and Script  
            if(actual instanceof FunctionList) {
            	boolean bMainFunc = true;
        		FunctionList funcList = (FunctionList) actual;
            	for(Function func : funcList.getFunctionList()) {
            		func.mainFunc = bMainFunc;
            		gTreeList.add(func);
        			bMainFunc = false;

        			// Renaming user-defined function that overload intrinsic functions
                	// If a user-defined function is overloaded an intrinsic function
                	// Then renaming all the place use it. 
                	String orgName = func.getName(); 
            		if(TypeInferenceEngine.isIntrinsicFunction(orgName)) {
            			String newName = TypeInferenceEngine.getNewFuncName(orgName);
            			actual.renaming(orgName, newName);      
            			func.setName(newName);
            		}
            	
            		// Add to TypeInferenceEngine
            		TypeInferenceEngine.userFuncList.add(func.getName());
            	}
            } else {
            	// Script doesn't have any other function inside 
        		TypeInferV2(actual);
        		gTreeList.add(actual);
            }
            /**if(actual instanceof FunctionList) {
            	boolean bMainFunc = true;
        		FunctionList funcList = (FunctionList) actual;
            	for(Function func : funcList.getFunctionList()) {
            		
            		// DEBUG purpose >> 
            		// just try first function
        			TypeInferV2(func);        			
            		break;
                	// DEBUG purpose << 
            		
            	}
            }
**/
            
			// Following are the handle the order of performing type inference 
			// when encountering calls to user-defined functions
            java.util.List<String> todoFuncList = new ArrayList<String>();
            // Type inference happen on the list of functions
            if(actual instanceof FunctionList) {
            	
	            for(ASTNode tree: gTreeList) {
    				SymbolTableScope stScope0 = gTreeSymbolTableMap.get(tree);
    				if(stScope0==null) {
        				TypeInferV2(tree);		            					
    				} else {
    					// It's been already done.
    					// TypeInferProcess(stScope0, tree);
    				}
	            	while(!gNextInferFuncNameList.isEmpty() || !gReInferTreeStack.empty()) {
		            	// Inferring user-defined functions after encountering function calls
	    				// next-function only have one, because the type inference stops when encourt one uninferred function, 
		            	while(!gNextInferFuncNameList.isEmpty()) {
			            	todoFuncList.clear();
			            	todoFuncList.addAll(gNextInferFuncNameList);
			            	gNextInferFuncNameList.clear();
			            	for(String funcName: todoFuncList) {
			            		// whether the next inference function is in the user-defined function list
			            		boolean bFound = false;		
			            		for(ASTNode funcTree: gTreeList) {
			            			if((funcTree instanceof Function)
			            					&& ((Function) funcTree).getName().equals(funcName))
			            			{
			            				bFound = true;
			            				SymbolTableScope stScope = gTreeSymbolTableMap.get(funcTree);
			            				if(stScope==null || gTreeCodeNodeMap.get(funcTree)==null) {
				            				TypeInferV2(funcTree);		            					
			            				} else {
			            					gCodeNodeList = gTreeCodeNodeMap.get(funcTree);		            					
			            					TypeInferProcess(stScope, funcTree);
			            				}
			            			}
			            		}
			            		if(!bFound) {
			            			System.err.println("Error: Function '"+funcName+"' cannot be found in the source file list!");
			            			System.exit(99);
			            		}
			            	}
		            	}
	    				// Inferring those unfinished functions that were interrupted by function call to user-defined functions
	    				// Every time after infer a user-defined function, pop() one node from stack, 
		            	if(!gReInferTreeStack.empty()) {
		            		ASTNode redoTree = gReInferTreeStack.pop();  
	        				SymbolTableScope stScope = gTreeSymbolTableMap.get(redoTree);
	        				if(stScope==null || gTreeCodeNodeMap.get(redoTree)==null) {
	        					System.err.println(" [Order] ReInfer Tree  ---- should not happened");
	            				TypeInferV2(redoTree);		            					
	        				} else {
	        					gCodeNodeList = gTreeCodeNodeMap.get(redoTree);		            					
	        					TypeInferProcess(stScope, redoTree);
	        				}
		            	}
	            	}
	            }
            }
                        
   
          
    		// Here add extra transformations and the code generation
    		// After having all function signature, (which means we know all variables
            // that being assigned function return values), we need go to infer the variables
            // that use those variables (assigned return values).
            inferFuncCallVariable();
            
    		// Transformation / Pre-compile phase: 
    		// 	- change the variable name with different case (case sensitive), to in-sensitive Fortran.
    		//	- adding bridge-variable for each parameter variable being changed in side function
    		// addBridgeVariable();	//[JL-R3]: move to after building symbol-table


            // Add command line arguments in the main-function 
            if(actual instanceof FunctionList) {
	            for(ASTNode tree: gTreeList) {
	            	if(((Function) tree).mainFunc) {
	            		if(((Function) tree).getInputParamList().getNumChild()>0) {
	            			((Function) tree).addCommandLineArgs();	            	
	            		}
	            	}
	            }
            }


            // Set the flag for code generator
            actual.FLAG_BLAS = FLAG_BLAS;
            actual.FLAG_BOUNDS_CHECK = FLAG_BOUNDS_CHECK;
            
            // Generate Fortran together, because all symbol tables may have been changed 
            if(actual instanceof FunctionList) {
            	FunctionList funcList = (FunctionList) actual;
        		java.util.List<Function> functionList = new ArrayList<Function>();
        		String progName = "";
            	for(Function func : ((FunctionList) actual).getFunctionList()) {
            		functionList.add(func);
            		if(func.mainFunc)
            			progName = (func.getName()).trim();
            	}

            	// Add module header
                out.println("MODULE Mod_"+progName);
                out.println("CONTAINS");
                
            	for(Function func : functionList ) {
            		// (1) First generate module containing all subroutines
            		if(!func.mainFunc)
            			doCodeGen(func);
            	}
            	// Add module end
                out.println("END MODULE Mod_"+progName);
            	for(Function func : functionList ) {
            		// (2) Then generate the program part
            		if(func.mainFunc)
            			doCodeGen(func);
            	}
            	
            } else {
            	doCodeGen(actual);
            }

    		out.close();
    		// outSymTbl.close();
            
		} catch(IOException e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
			System.exit(2);
		}
	} 
	
	// Adding array bounds checking code for all places
	public static void addArrayBoundsCheck(ASTNode func) {
		// 
		HashMap<ASTNode, Integer> gTreeCheckMap = new HashMap<ASTNode, Integer>();
		for(ASTNode tree: gTreeList) {
			gTreeCheckMap.put(tree, 0);
		}
		
		// (1) Decide which function need to add initialization code
		for(SymbolTableEntry e:	gArrayVarCheckNodeMap.keySet()) {
			HashSet<ASTNode> nodeSet = gArrayVarCheckNodeMap.get(e);
			for(ASTNode node: nodeSet) {
				ASTNode root = ASTToolBox.getSubtreeRoot(node);
				if(root!=null) {
					Integer check = gTreeCheckMap.get(root);
					if(check!=null && check==0) {
						addArrayBoundsInit(root, node, e);
						gTreeCheckMap.put(root, 1);
					}
				}
				addArrayBoundsCheckCode(node, e);				
			}
		}
	}
	public static void addArrayBoundsInit(ASTNode tree, ASTNode node, SymbolTableEntry e) {
		SymbolTableScope stScope = gTreeSymbolTableMap.get(tree);
		if(stScope == null) {
			System.err.println("This is not a sub-tree root node!");
			return;
		}
		if(!(node instanceof AssignStmt) || 
				!(((AssignStmt)node).getLHS() instanceof ParameterizedExpr)) { 
			System.err.println("This is not an indexed assignment node!");
			return ;
		}
		ParameterizedExpr lhs = (ParameterizedExpr)((AssignStmt)node).getLHS();
		String varName = ((NameExpr)lhs.getTarget()).getVarName();
		
		Type varType = ((VariableDecl) e.getDeclLocation()).getType();
		if(!(varType instanceof MatrixType)) {
			System.err.println("Variable "+varName+" is not a matrix type!");
			return ;
		}
		java.util.List<String> strDims=((MatrixType) varType).getSize().getDynamicDims();
		if(strDims.size()==1 || strDims.size()==2) {
			// IF (ALLOCATED(SRmat)) THEN 
			// SRmat_d1 = SIZE(SRmat,1);
			// SRmat_d2 = SIZE(SRmat,2);
			//END IF
			// Creating the expression
			// If Statement list ...
			ast.List<Stmt> stmtlist= new ast.List<Stmt>();
			// If condition
	     	ast.List<Expr> list0 = new ast.List<Expr>();
	     	list0.add(new NameExpr(new Name(varName)));
			ParameterizedExpr checkExpr = new ParameterizedExpr(
					new NameExpr(new Name("ALLOCATED")), list0);
			
			// assignments 
	     	ast.List<Expr> list1 = new ast.List<Expr>();
	     	list1.add(new NameExpr(new Name(varName)));
	     	list1.add(new IntLiteralExpr(new natlab.DecIntNumericLiteralValue("1")));
			ParameterizedExpr sizeExpr1 = new ParameterizedExpr(
					new NameExpr(new Name("SIZE")), list1);
			AssignStmt asg1 = new AssignStmt(
					new NameExpr(new Name(varName+"_d1")), sizeExpr1);
			stmtlist.add(asg1);

			AssignStmt asg2 = null;
			if(strDims.size()==2) {
		     	ast.List<Expr> list2 = new ast.List<Expr>();
		     	list2.add(new NameExpr(new Name(varName)));
		     	list2.add(new IntLiteralExpr(new natlab.DecIntNumericLiteralValue("2")));
				ParameterizedExpr sizeExpr2 = new ParameterizedExpr(
						new NameExpr(new Name("SIZE")), list2);
				asg2 = new AssignStmt(
						new NameExpr(new Name(varName+"_d2")), sizeExpr2);
				stmtlist.add(asg2);
			}
			
			IfStmt tmpIfStmt  = new IfStmt();
			IfBlock tmpIfBlock = new IfBlock(checkExpr, stmtlist);
			ElseBlock tmpElseBlock = new ElseBlock(new List<Stmt>().add(new BreakStmt()));
			tmpIfStmt.setIfBlockList(new List<IfBlock>().add(tmpIfBlock));

			// Add to the location
			List<Stmt> parent = null;
			if(tree instanceof Function) {
				parent = ((Function) tree).getStmtList();
			} else if(tree instanceof Script) {
				parent = ((Script) tree).getStmtList();
			} else {
				System.err.println("This is not a script or function root node!");
				return;
			}
			int i=0;
			for(Stmt stmt:parent) {
				if(!(stmt instanceof VariableDecl))
					break;
				i++;
			}
			parent.insertChild(tmpIfStmt, i);

			// Create variables declarations 
			TypeInferenceEngine.addDeclNodeSymTblEntry(varName+"_d1",
					new PrimitiveType(TypeInferenceEngine.TYPENAME_INTEGER),
					parent, stScope, asg1);
			TypeInferenceEngine.addDeclNodeSymTblEntry(varName+"_d1max",
					new PrimitiveType(TypeInferenceEngine.TYPENAME_INTEGER),
					parent, stScope, asg1);
			if(strDims.size()==2) {
				TypeInferenceEngine.addDeclNodeSymTblEntry(varName+"_d2",
					new PrimitiveType(TypeInferenceEngine.TYPENAME_INTEGER),
					parent, stScope, asg2);
				TypeInferenceEngine.addDeclNodeSymTblEntry(varName+"_d2max",
						new PrimitiveType(TypeInferenceEngine.TYPENAME_INTEGER),
						parent, stScope, asg2);
			}
			TypeInferenceEngine.addDeclNodeSymTblEntry(varName+"_tmp",
					varType, parent, stScope, asg1);
			
		} else {
			System.err.println("Currently we don't supput dimension over 2! ["+varName+"]="+strDims.size());
			return ;
		}
	}
	public static void addArrayBoundsCheckCode(ASTNode node, SymbolTableEntry e) {
		if(!(node instanceof AssignStmt) || 
				!(((AssignStmt)node).getLHS() instanceof ParameterizedExpr)) { 
			System.err.println("This is not an indexed assignment node!");
			return ;
		}
		ParameterizedExpr lhs = (ParameterizedExpr)((AssignStmt)node).getLHS();
		String varName = ((NameExpr)lhs.getTarget()).getVarName();
		
		Type varType = ((VariableDecl) e.getDeclLocation()).getType();
		if(!(varType instanceof MatrixType)) {
			System.err.println("Variable "+varName+" is not a matrix type!");
			return ;
		}
		java.util.List<String> strDims=((MatrixType) varType).getSize().getDynamicDims();
		if(strDims.size()==1) {
			
		} else if(strDims.size()==2) {
			String lhsArg0 = "";
			if(lhs.getArg(0) instanceof ColonExpr) {
				lhsArg0 = varName+"_d1";
			} else if(lhs.getArg(0) instanceof RangeExpr) {
				lhsArg0 = ((RangeExpr)lhs.getArg(0)).getUpper().getFortran();
			} else {
				lhsArg0 = lhs.getArg(0).getFortran();
			}
			String lhsArg1 = "";
			if(lhs.getArg(1) instanceof ColonExpr) {
				lhsArg1 = varName+"_d2";
			} else if(lhs.getArg(1) instanceof RangeExpr) {
				lhsArg1 = ((RangeExpr)lhs.getArg(1)).getUpper().getFortran();
			} else {
				lhsArg1 = lhs.getArg(1).getFortran();
			}
			
			// Creating the expression
			// If Statement list ...
			ast.List<Stmt> stmtlist= new ast.List<Stmt>();
			// 1.	        DEALLOCATE(SRmat_tmp)
			Stmt deallocTemp = createDeallocateStmt(varName+"_tmp");
			stmtlist.add(deallocTemp);

		    // 2. ALLOCATE(SRmat_tmp(SRmat_d1, SRmat_d2))
	     	ast.List<Expr> list1 = new ast.List<Expr>();
	     	list1.add(new NameExpr(new Name(varName+"_d1")));
	     	list1.add(new NameExpr(new Name(varName+"_d2")));
			ParameterizedExpr tempArray = new ParameterizedExpr(
					new NameExpr(new Name(varName+"_tmp")), list1);		
	     	ast.List<Expr> list2 = new ast.List<Expr>();
	     	list2.add(tempArray);
			ParameterizedExpr allocTemp = new ParameterizedExpr(
					new NameExpr(new Name("ALLOCATE")), list2);		
			stmtlist.add(new ExprStmt(allocTemp));
			
		    // 3.  SRmat_tmp = SRmat
			AssignStmt backupStmt = new AssignStmt(new NameExpr(new Name(varName+"_tmp")),
					new NameExpr(new Name(varName)));
			stmtlist.add(backupStmt);
			// 4.  DEALLOCATE(SRmat)
			Stmt deallocStmt = createDeallocateStmt(varName);
			stmtlist.add(deallocStmt);
			
			// 5. SRmat_d1max=MAX(SRmat_d1, p); SRmat_d2max=MAX(SRmat_d2, 5);
	     	ast.List<Expr> listD1 = new ast.List<Expr>();
	     	listD1.add(new NameExpr(new Name(varName+"_d1")));
	     	listD1.add(new NameExpr(new Name(lhsArg0)));
			ParameterizedExpr maxD1 = new ParameterizedExpr(
					new NameExpr(new Name("MAX")), listD1);		
			AssignStmt maxD1Stmt = new AssignStmt(
					new NameExpr(new Name(varName+"_d1max")),maxD1);
			stmtlist.add(maxD1Stmt);
				
	     	ast.List<Expr> listD2 = new ast.List<Expr>();
	     	listD2.add(new NameExpr(new Name(varName+"_d2")));
	     	listD2.add(new NameExpr(new Name(lhsArg1)));
			ParameterizedExpr maxD2 = new ParameterizedExpr(
					new NameExpr(new Name("MAX")), listD2);		
			AssignStmt maxD2Stmt = new AssignStmt(
					new NameExpr(new Name(varName+"_d2max")),maxD2);
			stmtlist.add(maxD2Stmt);
			
			// 6. 	      ALLOCATE(SRmat(SRmat_d1max, SRmat_d2max))
	     	ast.List<Expr> list5 = new ast.List<Expr>();
	     	list5.add(new NameExpr(new Name(varName+"_d1max")));
	     	list5.add(new NameExpr(new Name(varName+"_d2max")));
			ParameterizedExpr array2 = new ParameterizedExpr(
					new NameExpr(new Name(varName)), list5);		
	     	ast.List<Expr> list6 = new ast.List<Expr>();
	     	list6.add(array2);
			ParameterizedExpr reallocExpr = new ParameterizedExpr(
					new NameExpr(new Name("ALLOCATE")), list6);		
			stmtlist.add(new ExprStmt(reallocExpr));
				     	
			// 7. SRmat(1:SRmat_d1,1:SRmat_d2) = SRmat_tmp(1:SRmat_d1,1:SRmat_d2)
	     	ast.List<Expr> listC1 = new ast.List<Expr>();
	     	listC1.add(new RangeExpr(
	     			new IntLiteralExpr(new natlab.DecIntNumericLiteralValue("1")),
	     			new Opt<Expr>(),
	     			new NameExpr(new Name(varName+"_d1"))));
	     	listC1.add(new RangeExpr(
	     			new IntLiteralExpr(new natlab.DecIntNumericLiteralValue("1")),
	     			new Opt<Expr>(),
	     			new NameExpr(new Name(varName+"_d2"))));
			ParameterizedExpr lhsC1 = new ParameterizedExpr(
					new NameExpr(new Name(varName)), listC1);		
			
	     	ast.List<Expr> listC2 = new ast.List<Expr>();
	     	listC2.add(new RangeExpr(
	     			new IntLiteralExpr(new natlab.DecIntNumericLiteralValue("1")),
	     			new Opt<Expr>(),
	     			new NameExpr(new Name(varName+"_d1"))));
	     	listC2.add(new RangeExpr(
	     			new IntLiteralExpr(new natlab.DecIntNumericLiteralValue("1")),
	     			new Opt<Expr>(),
	     			new NameExpr(new Name(varName+"_d2"))));
			ParameterizedExpr lhsC2 = new ParameterizedExpr(
					new NameExpr(new Name(varName+"_tmp")), listC2);		
			
			AssignStmt copyStmt = new AssignStmt(lhsC1, lhsC2);
			stmtlist.add(copyStmt);
			
			// 7. SRmat_d1=SRmat_d1max; SRmat_d2=SRmat_d2max;
			AssignStmt d1Stmt = new AssignStmt(new NameExpr(new Name(varName+"_d1")),
					new NameExpr(new Name(varName+"_d1max")));
			stmtlist.add(d1Stmt);
			AssignStmt d2Stmt = new AssignStmt(new NameExpr(new Name(varName+"_d2")),
					new NameExpr(new Name(varName+"_d2max")));
			stmtlist.add(d2Stmt);

			
			// If condition expression
			GTExpr gt1 = new GTExpr(new NameExpr(new Name(lhsArg0)), new NameExpr(new Name(varName+"_d1")));
			GTExpr gt2 = new GTExpr(new NameExpr(new Name(lhsArg1)), new NameExpr(new Name(varName+"_d2")));
			
			IfStmt tmpIfStmt  = new IfStmt();
			IfBlock tmpIfBlock = new IfBlock(new OrExpr(gt1, gt2), stmtlist);
			ElseBlock tmpElseBlock = new ElseBlock(new List<Stmt>().add(new BreakStmt()));
			tmpIfStmt.setIfBlockList(new List<IfBlock>().add(tmpIfBlock));
			
			// Add to the location
			ASTNode parent = node.getParent();
			int loc = parent.getIndexOfChild(node);
			parent.insertChild(tmpIfStmt, loc);

		} else {
			System.err.println("Currently we don't supput dimension over 2! ["+varName+"]="+strDims.size());
			return ;
		}
	}

	/**
	 * Generate the Fortran code, one function/script a time.
	 * Dynamic allocation and function transformations are here.
	 * @param subtree: the function/script sub-tree
	 */
	public static void doCodeGen(ASTNode subtree) {

		// Save the symbol table scope for that sub-tree
		SymbolTableScope stScope = gTreeSymbolTableMap.get(subtree);
		if(stScope==null) {
			System.err.println("Symbol Table Scope is null for ["+subtree+"]");
			return;
		}
		subtree.setSymbolTableScope(stScope);
		

		HashSet<String> gLocalFuncList = new HashSet<String>();
		for(ArgTupleType func:gFuncCallParameterMap.keySet()) {
			gLocalFuncList.add(func.getName());
		}
		
		// Adding array bounds check
		if(FLAG_BOUNDS_CHECK) {
			addArrayBoundsCheck(subtree);
		}
		
		// Alex's Transformations to get clear Fortran code
		if(!DEBUG)
		{
			AlexFortranAnalyses.bSilence = true;
			AlexFortranAnalyses.gLocalFuncList = gLocalFuncList;
			AlexFortranAnalyses.Transformation1(subtree, stScope);
			AlexFortranAnalyses.Transformation4(subtree, stScope, gExtraVarList);
		}
		
				
		if(FLAG_BLAS) {
			addBLASSupport(subtree);
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
	
	// Type inference main function
	public static ASTNode TypeInferV2(ASTNode actual) {
        SymbolTableScope stScope2;
        
        
        // [x] Clear predefined data
        // gCodeNodeEntryMap keeps for whole process 
        gCodeNodeList = null; 

        // [x] Add some default statements, defined in Script, Function
        if(!DEBUG)
        	actual.addDefaultStmt();	// TODO: should open it in release version!	

        actual.clearUseDefBoxes();
		actual.generateUseBoxesList();


        //-----------------------------------------------------------------
		// [1] Simplify complex nodes
        actual.simplify(null, 2);
        
        // #Abandon  	[2] Covert into SSA form, and create symbol table
		// # 			// stScope2 = convert2SSA(actual);

        
        // [2] Just create symbol Table, and map between def-node and symbol-entry
		// If some variables need to be renamed, then it will return null; so we need re-do it.
		int i=0;
		do {
			stScope2 = buildSymbolTable(actual);
			i++;
		} while(stScope2==null && i<10);
		if(stScope2==null) {
			System.err.println("Error: cannot create symbol table on the program!");
			System.exit(8);
		}

		
		// Save the symbol table scope for that sub-tree
		gTreeSymbolTableMap.put(actual, stScope2);

		actual.setSymbolTableScope(stScope2);
				
		
		// [JL-R3], this affect the type inference
		addBridgeVariable(actual);
		
        // [3] Constant Propagation
		// Handle: n=2; m=2*n; t=2*10*4;...
		// Update the symbol table after doing each constant propagation
        // actual = ConstantPropagation(actual, stScope2 );
		// actual = ConstantPropagation.propagate(actual, stScope2);
        
		// [4] Generate use/def boxes list of each node of the tree after Constant Propagation
        // actual.clearUseDefBoxes();
		// actual.generateUseBoxesList();

		// (5.1) prepare 

        // [5] Inferring type
		actual = TypeInferProcess(stScope2, actual);
		

		// [7] Generate Static Fortran 
    	actual.FortranProgramName = pureName;

		return actual;
	}
	
	public static ASTNode TypeInferProcess(SymbolTableScope stScope2, ASTNode actual){
		int entryCountBefore = stScope2.symTable.size();
		int entryCountAfter = -1;
		int counter=0;
		// Iterative process, until not changes occurred in the symbol table 		
		do {
			if(entryCountAfter!=entryCountBefore) {
				entryCountBefore = entryCountAfter;
				// [6] Reload the code node, because there are transformations happened 
				// That cause problem that type-conflict-functions are in the new code-node-list. 
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
	
			}
	        // Re-do type inference again, where most of them are easily skipped

			clearSymbolTableChange(stScope2);
			checkSymbolTable(stScope2, actual);   
	        entryCountAfter = stScope2.symTable.size();
        			

			
			counter++;
			if(counter>6)	{
				System.err.println("---- [TypeInferProcess] too many times -- ["+counter+"]");
				out.println("[stScope2] before="+entryCountBefore+" after="+entryCountAfter+" change="+isSymbolTableChanged(stScope2));
				break;		// DEBUG
			}
			// When the type inference is stoped by a unknown user-defined function, then stop it.
			if(!gNextInferFuncNameList.isEmpty()) {
				break;
			}
		} while(entryCountAfter>entryCountBefore || isSymbolTableChanged(stScope2)) ;
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
	// e.g. func(b); b=2; =>func(b_tmp); b=btmp; b=2;
	// It only affect function declaration, 
	public static void addBridgeVariable() {
		// Go through all sub-functions
		for(ASTNode tree: gTreeList) {
			if((tree instanceof Script) 
					|| ((tree instanceof Function) && (((Function) tree).mainFunc)))
			{
				// This is main function
				// Usually the first one is Program,
			} else { // tree is Function
				addBridgeVariable(tree);
			}
		}
	}
	public static void addBridgeVariable(ASTNode tree) {
		if(tree instanceof Function) {
			// 1. gather the parameter variables 		
			java.util.List<String> paramVarList = new ArrayList<String>(); 
	    	for(Name varNode: ((Function)tree).getInputParamList()) {	// (FunctionDecl)
	    		paramVarList.add(varNode.getID());
	    	}
			
			// 2. go through code-node list, looking for conflict assignment
			SymbolTableScope stScope =  gTreeSymbolTableMap.get(tree); 
			java.util.List<ASTNode> codeNodeList = gTreeCodeNodeMap.get(tree);
			if(codeNodeList==null) {
				tree.clearUseDefBoxes();
				tree.generateUseBoxesList();			        
				ReachingDefs defsAnalysis = new ReachingDefs(tree);
			    codeNodeList = defsAnalysis.getNodeList(); 					
			}
			for(ASTNode codeNode: codeNodeList) {
				if(codeNode instanceof AssignStmt) {
					for(Object vb: (codeNode).getDefBoxes()) {						
			    		String varName = ((natlab.toolkits.ValueBox) vb).getValue();
						if (paramVarList.contains(varName)) {
							// 3. adding bridge-variable
				    		SymbolTableEntry stEntry = stScope.getSymbolById(varName);
				    		ASTNode varDeclNode = stEntry.getDeclLocation();
				    		
							// (1) Adding the new assignment, from variable to bridge variable,
				    		String tmpName = TypeInferenceEngine.getNewVarName(varName, stScope); 
				    		AssignStmt asgStmt = new AssignStmt(new NameExpr(new Name(varName)), 
				    									new NameExpr(new Name(tmpName)) );
				    		asgStmt.setNodeID();
							// (2) Adding new declare node
				    		// Type pType = new PrimitiveType(.getName());
				    		// Type pType = new PrimitiveType("int");
				    		Type pType = ((VariableDecl)varDeclNode).getType();
					    	VariableDecl tmpDecl = new VariableDecl(tmpName, pType);
					    	tmpDecl.setNodeID();
					    	((Function)tree).addDeclStmt(tmpDecl);

					    	// adding them into tree;
					    	ASTNode parent = varDeclNode.getParent();
				    		if(parent instanceof ast.List) {
				    			int i=0, j=0;
				    			for(; i < parent.getNumChild(); i++) {
				    				if(!(parent.getChild(i) instanceof VariableDecl)) {
				    					// this is the first non-declaration node
				    					break;
				    				}
				    			}
				    			// Stop until reach the first definition
				    			boolean bFound = false;
				    			for(j=i; j < parent.getNumChild(); j++) {
				    				if((parent.getChild(j) instanceof AssignStmt)) {
				    					// this is the first non-declaration node
				    					if(parent.getChild(j).getDefBoxes()!=null) {
					    					for(Object vb2: parent.getChild(j).getDefBoxes()) {
					    						if(varName.equals(((natlab.toolkits.ValueBox) vb2).getValue())) {
					    							bFound = true;
							    					break;
					    						}
			    							}
				    					}
				    				} else {
				    					// Those definition may inside the for, while, if, switch,...
				    					// But this is not an accurate solution. 
				    					if((parent.getChild(j) instanceof ForStmt) 
					    					|| (parent.getChild(j) instanceof WhileStmt)
				    						|| (parent.getChild(j) instanceof IfStmt)
				    						|| (parent.getChild(j) instanceof SwitchStmt)
				    						) {
				    						break;
				    					} else {
				    						continue;
				    					}
				    				}
				    				if(bFound) {
				    					break;
				    				} else {
					    				// Renaming all the uses before that definition
					    				parent.getChild(j).renaming(varName, tmpName);
				    				}
				    			}
				    			parent.insertChild(asgStmt, j);
				    			// Wired, sometimes the type need to be set after insert into stmt-list
				    			// ((VariableDecl)parent.getChild(i)).setType(pType);
				    		}
				    		paramVarList.remove(varName);
				    		
							// (3*) Change the original input-parameter declaration, it's local variable now
					    	ASTNode nodeLoc = stEntry.getNodeLocation();
					    	stEntry.setNodeLocation(asgStmt);

					    	// (3) Adding symbol table entry
							SymbolTableEntry ste = new SymbolTableEntry(tmpName, tmpName, nodeLoc); 
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
	public static Expr getImageExpr(SymbolTableScope stScope, Expr expr) {
		Expr imgExpr = null;
		// Get the image sign 'i' , 'j' from i*y, j*y, y*i, y*j
		if(expr instanceof MTimesExpr) {
			if(((MTimesExpr)expr).getLHS() instanceof NameExpr && 
					isImaginaryUnit(stScope, ((MTimesExpr)expr).getLHS().getVarName())  ) {
				imgExpr = ((MTimesExpr)expr).getRHS();
			} else if(((MTimesExpr)expr).getRHS() instanceof NameExpr &&
					isImaginaryUnit(stScope, ((MTimesExpr)expr).getRHS().getVarName()) ) {
				imgExpr = ((MTimesExpr)expr).getLHS();				
			}
		} else if(expr instanceof NameExpr) {
			if(isImaginaryUnit(stScope, ((NameExpr)expr).getName().getID())) {
				imgExpr = new FPLiteralExpr(new natlab.FPNumericLiteralValue("1.0"));
			}
		} else if(expr instanceof FPLiteralExpr || expr instanceof IntLiteralExpr) { 
			// the image sign 'i' , 'j' from 3i, 0.150j, is interpreted by lexer 
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

		ASTNode parent = expr;
		int loc = -1;
		
		// x+i*y; i*y+x
		if(expr instanceof PlusExpr || expr instanceof MinusExpr) {
			imgExpr = getImageExpr(stScope, ((BinaryExpr) expr).getRHS());
			if(imgExpr!=null) {
				realExpr = ((BinaryExpr) expr).getLHS();
				loc = expr.getIndexOfChild(((BinaryExpr) expr).getRHS());
			} else {
				imgExpr = getImageExpr(stScope, ((BinaryExpr) expr).getLHS());
				if(imgExpr!=null) {
					realExpr = ((BinaryExpr) expr).getRHS();
					loc = expr.getIndexOfChild(((BinaryExpr) expr).getLHS());
				}
			}
		} else {
			// i*y, 3.1j, i
			imgExpr = getImageExpr(stScope, expr);
		}
		if(imgExpr!=null) {
			Expr cmplxParam = null;
			// Check the realExpr is a complex variable or not
			if(realExpr!=null && realExpr instanceof NameExpr) {
	    		Type varType = getVariableType(stScope, ((NameExpr)realExpr).getVarName());
	    		if(TypeInferenceEngine.isComplexType(varType)) {
	    			// Make sure that 'i'/'j' are not defined, they are still the imaginary units
	    			cmplxParam = createComplexExpr(null, imgExpr);
	    			parent.setChild(cmplxParam, loc);
	    			cmplxParam = expr;
				}
			}
			if(cmplxParam==null) {
    			cmplxParam = createComplexExpr(realExpr, imgExpr);
			}
			parent = expr.getParent();
			loc = parent.getIndexOfChild(expr);
			parent.setChild(cmplxParam, loc);
		}
	}
	private static Expr createComplexExpr(Expr realExpr, Expr imgExpr) {
		ast.List<Expr> list = new ast.List<Expr>();
		if(realExpr!=null) {
			list.add(realExpr);
		} else {
			list.add(new FPLiteralExpr(new natlab.FPNumericLiteralValue("0.0")));
		}
		list.add(imgExpr);	
    	return new ParameterizedExpr(new NameExpr(new Name("COMPLEX")), list);
	}
	
	// Adding dynamic allocation code for dynamic shape type
	// 1. in Program/main-function 
	// 2. For subroutine, only handle those variable whose dynamic variable are not from parameter
	//			which also not parameter variables
	// 3. For subroutine, those input and return parameters should not be DEALLOCATE(), 
	public static void addDynamicAllocation(ASTNode tree) {
		// [#] Check every function's variable (all of them) through symbol table , 
		// adding dynamic allocation code for dynamic shape variable
		{
			// Checking whether need to add allocation
			java.util.List<String> paramList = null;		// Input parameters
			java.util.List<String> paramOutList = null;		// Output parameters
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
				paramList = new ArrayList<String>();
				paramOutList = new ArrayList<String>();
				for(Name name:((Function) tree).getInputParamList()) {
					paramList.add(name.getID());
				}
				for(Name name:((Function) tree).getOutputParamList()) {
					paramOutList.add(name.getID());
				}
/*				
				//	Getting the parameter variable list
				for(ArgTupleType func:gFuncCallParameterMap.keySet()) {
					if(((Function) tree).getName().equals(func.getName())) {
						paramList = gFuncCallParameterMap.get(func);
					}
				}
*/				
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

							if(declNode!=null && ((VariableDecl) declNode).bAdjustDynamic==false) {
								ASTNode varNode = stEntry.getNodeLocation();
								String varName = stEntry.getSymbol();

								// Checking whether need to add allocation	[JL-R3]
								// (#) using allocatable array - calling conversion
								// For sub-function, do not skip variables that are input/output parameter variable
								// They are could be allocatable arrays
								// (2) Main function and sub-function, checking type
								// TODO: Should check the variable in dynamic extent are not parameter variables
								// (1) Find variable inside dynamic dimension
								java.util.List<String> strDimsNew = new ArrayList<String>();
								java.util.List<String> strDims=((MatrixType) varType).getSize().getDynamicDims();
								boolean bNeed = false;
								HashSet<String> varSet = null;
								HashSet<String> varSetAll = new HashSet<String>();
								// Collecting all the variables for deciding allocations position 
								for(String strExtern: strDims) {
									if(!isInteger(strExtern)) {
										HashSet<String> tmpSet = getVariableListFromString(strExtern);
										if(tmpSet!=null) {
											varSetAll.addAll(tmpSet);
										}
									}
								}
								for(String strExtern: strDims) {
									if(!isInteger(strExtern)) {
										// [JL-R3]
										// Check whether its dimension has been passed into subroutine
										// varSet = new HashSet<String>();
										// varSet.add(strExtern);
										
										// This is handle when the string contains operators, e.g., n+1 
										// currently, we treat those cases as need to allocate locally 
										varSet = getVariableListFromString(strExtern);
										
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
										// All variables that not parameters need to be allocated [JL-R3]
										//if(!paramList.contains(varName) || !paramOutList.contains(varName)) {
										//	bNeed = true;
										//	break;
											
									}
								}
								if(paramList!=null)
									varSetAll.removeAll(paramList);
								
								// [JL-R3]
								// If needs array bounds checking, then this need to be allocated
								if(gArrayVarCheckList.contains(varName))
									bNeed = true;
								// When the variable is input parameter, should not create allocation for it 
								// if(paramList!=null)
								//	bNeed = !(paramList.contains(varName));
								
								if(!bNeed)
									continue;

								// otherwise, need to add allocate() 	
								// Create allocate() and deallocate() statements
								java.util.List<Stmt> eslist = 
									createAllocStmtFromDecl((VariableDecl) declNode, (MatrixType) varType, varNode);								
								Stmt allocExpr = eslist.get(0);
								Stmt deallocExpr = eslist.get(1);
								
								// Add statement: e.g., ALLOCATE(Array(n,m))
								// Only if this is an assignment that initializes the array, 
								// It must be an intrinsic function, not a user-defined function
								// First, check the statement
								/*
								// Allocation-initialization [JL-R3]
						    	if(varNode instanceof AssignStmt) {
						    		Expr rhs = ((AssignStmt) varNode).getRHS();
						    		if(rhs instanceof ParameterizedExpr) {
						    			bNeed = TypeInferenceEngine.isIntrinsicFunction(((ParameterizedExpr)rhs).getTarget().getVarName());
						    		}
						    	} */
						    	// Add allocate() only if it's needed
								if(bNeed) {
									// get the StmtList	
							    	if(varNode.getParent() instanceof List) {
							    		List<Stmt> stmtList = (List<Stmt>)varNode.getParent();
							    		int loc = stmtList.getIndexOfChild(varNode);
							    		// Check the dimension variable's location, 
							    		// the allocation statements should below them
							    		int locMax = loc;
							    		boolean bUndefined = false; 
							    		for(String var : varSetAll) {
						                	SymbolTableEntry ste = stScope.getSymbolById(var);
						                	// If it's not a parameter, then it is defined inside function
						                	if(ste==null) {
						                		bUndefined = true;
						                		// This usually is because the return variable's dimension contains variables
						                		// that are local variables of that user-defined function
						                	}
						                	if(ste!=null) {
						                		try {
									    			int loc2= stmtList.getIndexOfChild(ste.getNodeLocation());
									    			if(loc2>locMax)
									    				locMax = loc2;
						                		} catch (Exception e) {
						                			// TODO, wired error, why it's array-out-of-bound??
							                		// System.err.println("[DynamicAlloc]-var="+var+" of ["+varName+"] sym-entry "+ste.getNodeLocation());						                		
							                	}
						                	}
							    		}
							    		if(bUndefined) {	// [JL-R3]
					                		// System.err.println("[DynamicAlloc]-=["+varName+"] should not allocated!"+locMax+" "+loc);
							    		} else {
								    		if(locMax>loc) {
									    		// Move the array definition below them
								    			stmtList.insertChild((Stmt)varNode, locMax+1);
								    			stmtList.insertChild(allocExpr, locMax+1);
								    			stmtList.removeChild(loc);
								    		} else {
									    		if(loc>=0) {
									    			stmtList.insertChild(allocExpr, loc);
									    		} else {
									    			// This may cause by transformation
									    			// System.err.println("[DynamicAlloc]-loc="+loc+" of "+varNode.getStructureString()+" in "+stmtList.getNodeID());
									    		}
								    		}
							    		}
							    	} else {
							    		// Find the close List<Stmt> and location, insert into
							    		ASTNode parent = varNode.getParent(); 
							    		ASTNode child = varNode;
						    			while ((parent!=null) && !(parent instanceof ast.List)) {
						    	    		child  = parent;
						    	    		parent = child.getParent(); 	   
						    	    	}
						    			if(parent!=null) {
							    			int loc = parent.getIndexOfChild(child);
							    			parent.insertChild(allocExpr, loc);
						    			}
							    	}
								}								
					    		// 
					    		((VariableDecl) declNode).bAdjustDynamic=true;
					    		
					    		// find the end of list, add deallocate()
								if((paramList!=null && paramList.contains(varName))
									|| (paramOutList!=null && paramOutList.contains(varName))) {
										// Should not add deallocate() for input / output parameters
								} else {						    		
							    	if(declNode.getParent() instanceof List) {
							    		List<Stmt> declList = (List<Stmt>)declNode.getParent();
							    		declList.add(deallocExpr);
							    	}
								}					    		
							}
						}
			    	}	// for(...)

		    	} // if(stScope != null) 
			}
		}			
	}

	// Add some code and variables for supporting blas 
	private static void addBLASSupport(ASTNode tree) {
		SymbolTableScope stScope = gTreeSymbolTableMap.get(tree);
		boolean done = false;
		String[] varList = {"ZERO", "ONE"};
		if(stScope == null) {
			return;
		}
		java.util.List<ASTNode> codeNodeList = gTreeCodeNodeMap.get(tree);
		for(ASTNode codeNode : codeNodeList) {
			if((codeNode instanceof AssignStmt)) {
				Expr lhs = ((AssignStmt)codeNode).getLHS();
				Expr rhs = ((AssignStmt)codeNode).getRHS();
				if(lhs instanceof NameExpr && rhs instanceof MTimesExpr
						&& ((MTimesExpr)rhs).getLHS() instanceof NameExpr 
						&& ((MTimesExpr)rhs).getRHS() instanceof NameExpr) {
					NameExpr op1 =  (NameExpr)((MTimesExpr)rhs).getLHS() ;
					NameExpr op2 =  (NameExpr)((MTimesExpr)rhs).getLHS() ;					
					SymbolTableEntry ste1 = stScope.getSymbolById(op1.getVarName());
					SymbolTableEntry ste2 = stScope.getSymbolById(op2.getVarName());
					if(ste1==null || ste2==null) {
						continue;
					}
					Type type1 = ((VariableDecl) ste1.getDeclLocation()).getType();
					Type type2 = ((VariableDecl) ste1.getDeclLocation()).getType();
					if(!(type1 instanceof MatrixType) 
							|| !(type2 instanceof MatrixType) ) {
						continue;
					}
					java.util.List<String> strDims1=((MatrixType) type1).getSize().getDynamicDims();
					java.util.List<String> strDims2=((MatrixType) type1).getSize().getDynamicDims();
					
					// SymbolTableEntry stLHS = stScope.getSymbolById(((NameExpr)lhs).getVarName());
					
					ast.List<Expr> arglist = new ast.List<Expr>();	
					arglist.add(new StringLiteralExpr("N"));
					arglist.add(new StringLiteralExpr("N"));
					arglist.add(new NameExpr(new Name(strDims1.get(0))));
					arglist.add(new NameExpr(new Name(strDims2.get(1))));
					arglist.add(new NameExpr(new Name(strDims1.get(1))));
					arglist.add(new NameExpr(new Name("ONE")));
					arglist.add(new NameExpr(new Name(op1.getVarName())));
					arglist.add(new NameExpr(new Name(strDims1.get(0))));
					arglist.add(new NameExpr(new Name(op2.getVarName())));
					arglist.add(new NameExpr(new Name(strDims2.get(0))));
					arglist.add(new NameExpr(new Name("ZERO")));
					arglist.add(new NameExpr(new Name(((NameExpr)lhs).getVarName())));
					arglist.add(new NameExpr(new Name(strDims1.get(0))));

					ParameterizedExpr blasExpr = new ParameterizedExpr(
							new NameExpr(new Name("dgemm")), arglist);
					
					((AssignStmt)codeNode).setRHS(blasExpr);
					((AssignStmt)codeNode).isCall = true;
					
					if(!done) {
						for(int i=0; i<varList.length; ++i) {
							SymbolTableEntry stEntry = stScope.getSymbolById(varList[i]);
							if(stEntry == null)
								continue;
							ASTNode varNode = stEntry.getNodeLocation();
							String varName = stEntry.getSymbol();
				    		List<Stmt> stmtList = (List<Stmt>)varNode.getParent();
				    		int loc = stmtList.getIndexOfChild(varNode);
				    		AssignStmt copyAsg = new AssignStmt(new NameExpr(new Name(varName)), 
				    				new NameExpr(new Name(varName)));
			    			stmtList.insertChild(copyAsg, loc+1);
						}		
						done = true;
					}
				}				
			}
		}
		// a22[m,k] *b22[k,n] = c22[m,n]
		// call dgemm('N', 'N', m, n, k, ONE, a22, m, b22, k, ZERO,  c22, m)
		//	call dgemm('N', 'N', N, N, N, ONE, B, N, B, N, ZERO,  B1, N)				
	}
	
	// Create allocate() and deallocate() statement for the matrix-type variable
	public static java.util.List<Stmt> createAllocStmtFromDecl
			(VariableDecl declNode, MatrixType varType, ASTNode curNode) {

		java.util.List<Stmt> exprStmtList = new ArrayList<Stmt>();
     	ast.List<Expr> arglist = new ast.List<Expr>();
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
					arglist.add(McFor.parseString(dim).getExpr());					
				}
			}
		} else {
			arglist.add(new NameExpr(new Name(":")));	// new ColonExpr()
		}
		// Creating the expression
		ParameterizedExpr varExpr = new ParameterizedExpr(
				new NameExpr(new Name(declNode.getID())), arglist);
		
		ast.List<Expr> listName = new ast.List<Expr>();
		listName.add(new NameExpr(new Name(declNode.getID())));
		//ParameterizedExpr deallocExpr = new ParameterizedExpr(
		//		new NameExpr(new Name("DEALLOCATE")), listName);
		//Stmt deallocStmt = new ExprStmt(deallocExpr);
		Stmt deallocStmt = createDeallocateStmt(declNode.getID());
		
     	ast.List<Expr> list = new ast.List<Expr>();
     	list.add(varExpr);
		ParameterizedExpr allocExpr = new ParameterizedExpr(
				new NameExpr(new Name("ALLOCATE")), list);		
		ParameterizedExpr checkExpr = new ParameterizedExpr(
				new NameExpr(new Name("ALLOCATED")), listName);
		
		ast.List<Stmt> stmtlist= new ast.List<Stmt>();
		stmtlist.add(new ExprStmt(allocExpr));

		IfStmt tmpIfStmt  = new IfStmt();
		IfBlock tmpIfBlock = new IfBlock(new NotExpr(checkExpr), stmtlist);
		ElseBlock tmpElseBlock = new ElseBlock(new List<Stmt>().add(new BreakStmt()));
		tmpIfStmt.setIfBlockList(new List<IfBlock>().add(tmpIfBlock));
		exprStmtList.add(tmpIfStmt);
		
		exprStmtList.add(deallocStmt);
		
		return exprStmtList;
	}
	private static Stmt createDeallocateStmt(String varName) {
		// Deallocate statement
		ast.List<Expr> listName = new ast.List<Expr>();
		listName.add(new NameExpr(new Name(varName)));
		ParameterizedExpr deallocExpr = new ParameterizedExpr(
				new NameExpr(new Name("DEALLOCATE")), listName);

		// Checking expression
		ParameterizedExpr checkExpr = new ParameterizedExpr(
				new NameExpr(new Name("ALLOCATED")), listName);
		
		ast.List<Stmt> stmtlist= new ast.List<Stmt>();
		stmtlist.add(new ExprStmt(deallocExpr));

		// If Statement
		IfStmt tmpIfStmt  = new IfStmt();
		IfBlock tmpIfBlock = new IfBlock(checkExpr, stmtlist);
		ElseBlock tmpElseBlock = new ElseBlock(new List<Stmt>().add(new BreakStmt()));
		tmpIfStmt.setIfBlockList(new List<IfBlock>().add(tmpIfBlock));
		
		return tmpIfStmt;
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

		// [#1] For the subroutine,  //#######################
		// 		set types of its parameters, before doing type inference inside. 
		// First gathering the type information of each parameter from 
		// the function call context.   
		ArgTupleType funcSignature = null;
		// Set the type of main function's parameters as double
		if((actual instanceof Function) && ((Function)actual).mainFunc) {
			java.util.List<String> ParameterList = new ArrayList<String>(); 			
	    	for(Name inputName: ((Function)actual).getInputParamList()) { 
	    		String varName = inputName.getID();
	    		SymbolTableEntry stEntry = stScope.getSymbolById(varName);
	    		if(stEntry==null) { 
	    			System.err.println("[Error] Parameter "+varName+" doesn't have an symbol entry");
	    		} else {
		    		// Set value of this variable from as itself
		    		stEntry.setValue(new StringLiteralExpr(varName));
		    		ParameterList.add(varName);
		    		((VariableDecl) stEntry.getDeclLocation()).setType(new PrimitiveType(TypeInferenceEngine.TYPENAME_INTEGER));
		    		stEntry.setConstant(true);
	    		}
	    	}
		} 
		// For those function calls
		if((gFuncCallVarMap.size()>0) && (actual instanceof Function)) {
		
			
			for( ArgTupleType funcSign : gFuncCallVarMap.keySet())
			{
				
				if(((Function)actual).getName().equalsIgnoreCase(funcSign.getName())) {
					funcSignature = funcSign;

					int i = 0;
					int maxArg = 100;
					try {
						maxArg = funcSignature.getNumStaticArgType();
					} catch (Exception e) {
						System.err.println("[#22] funcSign ["+funcSign.getName()+"] current="+((Function)actual).getName());
						System.err.println("[#23] funcSign ["+funcSign.getStructureString());
					}
					// funcSignature.getStaticArgTypeList().getNumChildNoTransform();
					// ?? This cannot work sometime. maxArg =funcSignature.getNumStaticArgType();
					// ?? funcSignature.getStaticArgTypeList().getNumChildNoTransform();
					
					// Gather the parameter list and argument list
					java.util.List<String> ParameterList = new ArrayList<String>(); 
					java.util.List<String> ReturnVarList = new ArrayList<String>(); 
					java.util.List<String> ArgumentList = new ArrayList<String>();
					
					// (1st round), find the input parameter list, gather information 		
					// Since getParamDeclList() has Input+Output parameters, so need to take 
					// first few ones base on the number of input parameter 
			    	for(VariableDecl decl: ((Function)actual).getParamDeclList()) { 
			    		String varName = decl.getID();
			    		SymbolTableEntry stEntry = stScope.getSymbolById(varName);
			    		if(stEntry==null) { 
			    			System.err.println("[Error] Parameter "+varName+" doesn't have an symbol entry");
			    		} else {
				    		// Set value of this variable from caller's scope
				    		// Using argument as the index B=func(n,A)=> REAL::A(n),B(n)
				    		// 		# if n is constant, should not use the constant value! 
				    		//			[caller] f(10,A) => A(10) is wrong
				    		// So set the parameter variable name as its value
				    		stEntry.setValue(new StringLiteralExpr(varName));
				    		
				    		if(i>=maxArg) {	// Another insurance
				    			ReturnVarList.add(varName);
				    			// break;
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
		    		gFuncParameterMap.put(funcSign.getName(), ParameterList);
		    		gFuncReturnVarMap.put(funcSign.getName(), ReturnVarList);

		    		// (2nd round), set the parameter's type
		    		i = 0;
			    	for(VariableDecl decl: ((Function)actual).getParamDeclList()) {	//(FunctionDecl)
			    		String varName = decl.getID();
			    		SymbolTableEntry stEntry = stScope.getSymbolById(varName);
			    		if(stEntry==null) { 
			    			System.err.println("[Error] Parameter "+varName+" doesn't have an symbol entry");
			    		} else {
				    		if(i>=maxArg) {	// Another insurance
				    			break;
				    		} else {
								// set type to those parameters
					    		Type varType = funcSignature.getStaticArgType(i);
					    		if(TypeInferenceEngine.isCharacterType(varType)) {
					    			varType = new PrimitiveType("character(LEN=*)");
						    		((VariableDecl) stEntry.getDeclLocation()).setType(varType);
					    		} else {
						    		// Convert argument type to rely on parameter variable 
					    			// e.g. [caller] B1=func(n1,A1),A1(n1) ==> Function B=func(n,A),A(n)
					    			Type newType = convertParameterType(funcSignature, varType, false);
					    			if(TypeInferenceEngine.isValidType(newType)) {
					    				// Merge the type from calling context 
					    				// and the type currently inferred (inside the function)
						    			varType = newType;
					    				Type curType = ((VariableDecl) stEntry.getDeclLocation()).getType();
					    				if(TypeInferenceEngine.isValidType(curType)) {
					    					Type tmpType = TypeInferenceEngine.mergeType(newType, curType);
						    				if(!TypeInferenceEngine.isValidType(tmpType)) {
						    					// if cannot merge the use the new type
						    					varType = newType;
						    				} else if(TypeInferenceEngine.isEqualType(newType, curType)) {
						    					// If type same, the don't update
						    					varType = null;
						    				} 
					    				}
					    				if(TypeInferenceEngine.isValidType(varType)) {
								    		recordSymbolTableChange(stScope);
								    		((VariableDecl) stEntry.getDeclLocation()).setType(varType);
					    				}
					    			} else {
					    				System.err.println("[setType] newType is not a valid type!");
					    			}
					    		}
					    		// Parameters are constant expressions
					    		stEntry.setConstant(true);
					    		stEntry.setConstantSize(true);
					    		
					    		i++;
				    		}
			    		}
			    	} // for(VariableDecl decl:
				}
			} // for( ArgTupleType
		} //if((gFunc...	//#######################

		
		// [#2] Type inference process follows the order of the code list, 
		if(gCodeNodeList!=null) {
			boolean stopInfer = false;
			for(ASTNode codeNode: gCodeNodeList) {
		    	// Get the symbol entry list of this code node. 
				// Symbol entry list is the list of all variables this assignment node created.  
				ArrayList<SymbolTableEntry> seList = gCodeNodeEntryMap.get(codeNode);

				// Case 1: When this node doesn't link to a symbol table entry, implies it is not an assignment
				if(seList == null) {
					seList = new ArrayList<SymbolTableEntry>();		    		
					for(Object vb: (codeNode).getDefBoxes()) {						
						SymbolTableEntry ste = stScope.getSymbolById(((natlab.toolkits.ValueBox) vb).getValue());
			    		if(ste!=null) {
			    			seList.add(ste);
			    			gCodeNodeEntryMap.put(codeNode, seList);
			    			// break;	// ?: there may have more than one variable
			    		}
					}
					// When this node is a stand alone statement, 
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
							// Since there is no return value, we don't need to infer that function immediately. 
				    		if(varType instanceof ArgTupleType) {
				    			updateFunctionCallSignature(codeNode, actual, (ArgTupleType) varType, null);
				    		}
						}
					}
				}

				// Case 2: When this node is an assignment, then handles all variables this assignment created. 
				for(SymbolTableEntry stEntry: seList) {
			    	// For those assignment-statements, that are not the primary def-node
		    		
		    		// Infer type and save into symbol table entry
			    	if(stEntry!=null) {
			    		// Perform type inference, includes: 
			    		// 	- Infer RHS's type and save into symbol table entry
			    		// 	- For normal case, the merge has been done 
			    		Type rhsType = inferSymbolEntry(stScope, stEntry, codeNode, true);
			    		
			    		if(stEntry.getDeclLocation()==null) {
				    		System.err.println("Type-inferType failed on "+codeNode.getStructureString());
				    		continue;
			    		}
			    	
			    		//#######################
			    		// Get the variable's original type from symbol table.
			    		Type varType = ((VariableDecl) stEntry.getDeclLocation()).getType();		    		

			    		
			    		// Handle special case, RSH is a function: 
			    		// If RHS is a function call, then add it into global data for future use.
			    		if((rhsType!=null) && (rhsType instanceof ArgTupleType)) {
			    			ArrayList<SymbolTableEntry> stEntryList = getFunctionCallEntryList(codeNode);//(ArgTupleType) rhsType);
			    			if(stEntryList == null) {
			    				stEntryList = new ArrayList<SymbolTableEntry>();
			    			} 
			    			stEntryList.add(stEntry);
			    			// (ii) If the calling arguments has different type, 
			    			boolean bUpdated = updateFunctionCallSignature(codeNode, actual, (ArgTupleType) rhsType, stEntryList);
			    			gEntryScopeMap.put(stEntryList.get(0), stScope);
			    			
			    			// Check whether the function has inferred / has return type list 
			    			java.util.List<Type> returnVarTypeList = gFuncReturnTypeListMap.get(((ArgTupleType) rhsType).getName());
			    						    			
			    			// (i) If the user-defined function hasn't been inferred, then stop and go into it
			    			if(returnVarTypeList==null || bUpdated) {
			    				String fname = ((ArgTupleType) rhsType).getName();
			    				if(returnVarTypeList==null) {
			    					gFuncReturnTypeListMap.remove(fname);
			    				}
			    				// Add it to next inferred function
			    				// but avoid to keep duplicated copy of the same un-inferred user-defined function 
			    				if(gNextInferFuncNameList.isEmpty()
			    						|| !gNextInferFuncNameList.get(gNextInferFuncNameList.size()-1).equals(fname)) {
					    			gNextInferFuncNameList.add(fname);
					    			gReInferTreeStack.add(actual);
			    				}
			    				// Stop current type inference and return, (skip all following process)
				    			stopInfer = true;
				    			return ; // break;
			    			} else {
			    				// (iii) The sub-function has been inferred
			    				// (1) Find the  parameter list previous defined
			    				java.util.List<String> ParameterList = null;
			    				for(ArgTupleType callSign:gFuncCallParameterMap.keySet()) {
			    					if(callSign.getName().equals(((ArgTupleType) rhsType).getName())) {
			    						ParameterList = gFuncCallParameterMap.get(callSign);
			    					}
			    				}
			    				// (2) Create the parameter/argument map
			    				java.util.List<String> ArgumentList =  new ArrayList<String>();
								int numArg = ((ArgTupleType) rhsType).getStaticArgTypeList().getNumChildNoTransform();
								int i=0, pos=0;
			    				for(i=0; i<numArg; ++i) {
			    					ArgumentList.add(((ArgTupleType) rhsType).getStaticArgName(i));
			    				}
			    				java.util.List<String> ReturnVarList = gFuncReturnVarMap.get(((ArgTupleType) rhsType).getName());
			    				if(ReturnVarList!=null) {
			    					for(i=0; i<ReturnVarList.size(); ++i) {
				    					if(stEntry.getSymbol().equals(ReturnVarList.get(i))){
				    						pos = i;
				    					}
			    					}
			    				}
			    				
					    		gFuncCallParameterMap.put(((ArgTupleType) rhsType), ParameterList);
					    		gFuncCallArgumentMap.put(((ArgTupleType) rhsType), ArgumentList);

					    		// (3) Convert return type to current argument list 
					    		Type retType = convertParameterType((ArgTupleType) rhsType, returnVarTypeList.get(pos), true);
					    		
					    		// (4) Merge types
					    		Type orgType = ((VariableDecl) stEntry.getDeclLocation()).getType();
								
								if(!(orgType instanceof ArgTupleType)) {
									Type resultType = TypeInferenceEngine.mergeType(retType, orgType);
									if(TypeInferenceEngine.isValidType(resultType) &&
											!TypeInferenceEngine.isEqualType(orgType, resultType)) {
										((VariableDecl) stEntry.getDeclLocation()).setType(resultType);
							    		recordSymbolTableChange(stScope);
									} 
									if(resultType==null) {
									}
								} else {
									if(TypeInferenceEngine.isValidType(retType) &&
											!TypeInferenceEngine.isEqualType(orgType, retType)) {
										((VariableDecl) stEntry.getDeclLocation()).setType(retType);
							    		recordSymbolTableChange(stScope);
									}
								}
			    			}
			    		}
			    		//#######################
			    		
			    	} else {
			    		// should not happen
			    		System.err.println("Symbol-Entry is NULL! [checkSymbolTable]:"+codeNode.getNodeID());
			    	}
				} // for(SymbolTableEntry stEntry ...
				
    			// if(stopInfer) return;
    			
		    } // for(ASTNode codeNode: ...	
/**			// TODO: This may not useful .... ...
			// [#3] Based on the symbol table's type, adjust the array-access 
			//	It happens inside the inferSymbolEntry()
			if((bAdjustArrayAccess || TypeInferenceEngine.bAdjustArrayAccess)
					&& gCodeNodeList!=null) 
			{			
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
**/
		} // if(gCodeNodeList!=null)

		//#######################
		// [#4] If current sub-tree (actual) is a function, update caller's return variable types
		// Caller's function calls saved in gFuncCallVarMap!
		// Gather the return variable type of this function, 
		// update the caller's variable's type (from gFuncCallVarMap). 
		// When return value has dynamic size, update the variable in the type to 
		// corresponding caller's variable, which may have different name!
		if(actual instanceof Function) {
			for( ArgTupleType funcSign : gFuncCallVarMap.keySet())
			{
				// If there are function calls call to current function  
				if(((Function)actual).getName().equalsIgnoreCase(funcSign.getName())) {
					funcSignature = funcSign;
					// A list of function call node's symbol table entry
					ArrayList<SymbolTableEntry> stEntryList = getFunctionCallEntryList(funcSignature);
		
					
					// Create the list of the return variable type, match the order of LHS of that function call
					java.util.List<Type> returnVarTypeList = new ArrayList<Type>();
					java.util.List<Type> returnVarOrgTypeList = new ArrayList<Type>();
					for(Name param : ((Function) actual).getOutputParams()) {
						// Data type of the return variable inside this sub-function
			    		SymbolTableEntry stEntry = stScope.getSymbolById(param.getID());
			    		Type varType = ((VariableDecl) stEntry.getDeclLocation()).getType();
			    		Type newType = convertParameterType(funcSign, varType, true); 
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
						// TODO: the order in gCodeNodeEntryMap is same !!
						for(SymbolTableEntry stEntry: seList) {
							Type varType = returnVarTypeList.get(i);
							Type orgType = ((VariableDecl) stEntry.getDeclLocation()).getType();
							if(TypeInferenceEngine.isValidType(varType)){
								if(!TypeInferenceEngine.isEqualType(orgType, varType))	// TODO: should allow update return type [JL-R3]
								{									
									((VariableDecl) stEntry.getDeclLocation()).setType(varType);
						    		recordSymbolTableChange(stScope);
								}
							} else {
								System.err.println("Function "+((Function)actual).getName()+" return type #"+i+" is null");
							}
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
					ArrayList<SymbolTableEntry> stEntryList = getFunctionCallEntryList(funcSign);
	    			if(returnVarTypeList!=null) {
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
						    		if(TypeInferenceEngine.isValidType(retType)) {
						    			((VariableDecl) stEntry.getDeclLocation()).setType(retType);
						    		}
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
		//#######################
		
		// [#5] Transform intrinsic function from command form to function call
		// It doesn't affect other type infer / transformation
		TypeInferenceEngine.transformCmdFormFunction();
	}
	
	// Put function call signature as a whole
	public static boolean updateFunctionCallSignature(ASTNode codeNode, ASTNode treenode, 
			ArgTupleType funcSign, ArrayList<SymbolTableEntry> stEntryList) {
		boolean bUpdate = false;
		if(gFuncCallNodeMap.get(codeNode)!=null) {
			ArgTupleType funcSignOld= gFuncCallNodeSignMap.get(codeNode);
			try{
				bUpdate = !TypeInferenceEngine.isEqualFuncSignature(funcSignOld, funcSign);
			} catch (Exception e) {
				return false;
			}
			if(stEntryList!=null)	{			
			}
			if(bUpdate) {
				gFuncCallVarMap.remove(funcSignOld);
			}
		}
		if(gFuncCallNodeMap.get(codeNode)==null || bUpdate) {
			if(stEntryList==null)
				stEntryList = new ArrayList<SymbolTableEntry>();
			gFuncCallVarMap.put(funcSign, stEntryList);
			gFuncCallNodeMap.put(codeNode, treenode);
			gFuncCallNodeSignMap.put(codeNode, funcSign);
			bUpdate = true;
		}
		
		return bUpdate;
	}
	
	// Put function call signature as a whole
	public static ArrayList<SymbolTableEntry> getFunctionCallEntryList(ASTNode codeNode) {
		ArrayList<SymbolTableEntry> stEntryList = new ArrayList<SymbolTableEntry>(); 
		ArgTupleType funcSign = gFuncCallNodeSignMap.get(codeNode);
		if(funcSign!=null) {
			stEntryList = gFuncCallVarMap.get(funcSign);
		}
		return stEntryList;
	}
	public static ArrayList<SymbolTableEntry> getFunctionCallEntryList(ArgTupleType funcSign) {
		ArrayList<SymbolTableEntry> stEntryList = gFuncCallVarMap.get(funcSign);
		if(stEntryList==null)
			stEntryList = new ArrayList<SymbolTableEntry>();
		return stEntryList;
	}
	

	
	// Translate the variable inside type into variable of the caller side
	// e.g. [caller] B1=func(n1,A1),A1(n1) ==> Function B=func(n,A),A(n) B(n) ==>[caller] B1(n1)
	// e.g. [caller] B2=func(n2,A2),A2(n2) ==> Function B=func(n,A),A(n) B(n) ==>[caller] B2(n2)
	// # Return Variable Type needs to be converted according to the function call arguments. 
	// Dynamic type happens when variable used in Matrix dimensions
	// Put the correct type into return variables' type list
	// 
	// bReturnVar: true: varType is the sub-function type, convert to caller's type
	//			false: means convert argument's type to sub-function's type
	
	// McFor.evaluateExpression
	
	// Evaluate and Compare two expressions by using testing value, 
	// Return:
	//	- -1: expL<expR
	//	- 0 : expL==expR
	//	- 1 : expL>expR
	//	Otherwise: error, they are not comparable
	public final static int COMPARE_LESS = -1; 
	public final static int COMPARE_EQUAL = 0; 
	public final static int COMPARE_GREATER = +1; 
	public final static int COMPARE_ERROR = -99;

	// Compare two expression, (i+1,i), (i-1,i), (i+1-1)
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
				return null;
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
		// 0. If both are integers, compare their integer values
		// 0. If one is a integer, another is an variable, then variable is bigger
		if(isInteger(expL) && isInteger(expR)) {
			int valL = new Integer(expL);
			int valR = new Integer(expR);
			if(valL>valR) {
				return COMPARE_GREATER;
			} else if(valL==valR) {
				return COMPARE_EQUAL;
			} else {
				return COMPARE_LESS;
			}
		} else if (!isInteger(expL) && isInteger(expR)) {
			return COMPARE_GREATER;
		
		} else if (isInteger(expL) && !isInteger(expR)) {
			return COMPARE_LESS;
		}
		// 1. Parsing two expressions
		ExprStmt treeL = parseString(expL);
		ExprStmt treeR = parseString(expR);
		
		HashSet<String> varSetL = getVariableListFromString(treeL);
		HashSet<String> varSetR = getVariableListFromString(treeR);

		// 2. Have same variable sets
		if(varSetL==null || varSetR==null) {
			// there is parsing error 
			return COMPARE_ERROR;
		} 
		
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
		// We generately assume its in the form: 2*n+3
		// There is not a Big-O, one must be bigger in all cases.
		// But for these: "2*n+3" < "3*n-1", when n>4. They should be comparable or not?
		// So in McFor, we believe that n is biger enough, so we try one value.  
		// TODO: BTW, If wants to test twice (x,y)=(999,9), or=(9,999)
		// Need clone the tree, so can test twice
		//	Testing once: using 99
		int[] testValue = {99,9};
		{
			for(String varName: varSetL) {
				treeL.constantPropagation(varName, new IntLiteralExpr(new DecIntNumericLiteralValue(""+testValue[0])));
				treeR.constantPropagation(varName, new IntLiteralExpr(new DecIntNumericLiteralValue(""+testValue[0])));
			}
			int valueL = TypeInferenceEngine.getExprIntegerValue(treeL);
			int valueR = TypeInferenceEngine.getExprIntegerValue(treeR);
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
	}

	public static Type convertParameterType(ArgTupleType funcCallSign, Type varType, boolean bReturnVar) {
		Type newType = varType;
		bReturnVariableConversion = true;

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
										if(ArgumentList.get(i).equals(pName)) {
											argName = ParameterList.get(i);
											break;
										}
									}
								}
							}
				    		if(argName==null) {
				    			// This happens when there's no enough information about that sub-function
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
		if(!(varNode instanceof AssignStmt) || ((AssignStmt)varNode).isCall)
				return; 
		
		ASTNode parentNode = ASTToolBox.getParentAssignStmtNode(varNode);
	    if(parentNode!=null && ((AssignStmt) parentNode).getRHS() instanceof ParameterizedExpr) {
	    	ParameterizedExpr rhs = (ParameterizedExpr)((AssignStmt) parentNode).getRHS();
	    	Expr lhs = ((AssignStmt) parentNode).getLHS();
	    	// For LHS is matrix, break down the LSH, e.g. [A,B]=func() 
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

	
	
	private static boolean isPhiFunction(Expr expr) {
		boolean bResult = false;
	
		if (expr instanceof ParameterizedExpr) {
            String fname = ((NameExpr)((ParameterizedExpr)expr).getTarget()).getName().getID();
            if(fname.equals(ALPHA_FUNC_NAME) || fname.equals(BETA_FUNC_NAME)
            		|| fname.equals(LAMBDA_FUNC_NAME)) {
            	bResult = true;
            }
		}
		return bResult;
	}
	/**
	 * Three steps
	 * 		[#1] Infer type from RHS expression, and merge it
	 * 		[#2] Value-propagation: get the value of variable, save in symbol table entry
	 * 		[#3] LHS Range adjust, for indexed array access 
	 * @param stScope
	 * @param e
	 * @param varNode
	 * @param bNewInfer: true: first time, need to infer type from the AST
	 * @return
	 */
	public static Type inferSymbolEntry(SymbolTableScope stScope, SymbolTableEntry e, 
			ASTNode varNode, boolean bNewInfer) 
	{
    	TypeInferenceEngine.gstScope=stScope;
	    Type rhsType = null;  
	    Type varType = null;  
	    VariableDecl varDecl = (VariableDecl) e.getDeclLocation();
	    if(varNode==null) {
	    	varNode = e.getNodeLocation();
	    }		
    	if (varDecl == null) {
    		System.err.println("VariableDecl of ["+e.getSymbol()+"] should not be null!");
    	} else {
    		
    		// [#1] Infer type from RHS expression, and merge it 
    		Type orgType = varDecl.getType();
		    // Find the assignment statement of the variable, 
		    // varNode should be the LHS of it. 
		    AssignStmt parentNode = ASTToolBox.getParentAssignStmtNode(varNode);
    		Expr lhs = ((AssignStmt) parentNode).getLHS();
    		Expr rhs = ((AssignStmt) parentNode).getRHS();

		    if(parentNode!=null) {
		    	// (1) Perform the type inference in the first time when this function is called
		    	if(bNewInfer) {
		    		// TODO:Handle phi-functions first 
		    		if (isPhiFunction(rhs)) {
		        		ASTNode parent = parentNode.getParent();
		        		int loc = parent.getIndexOfChild(parentNode);		
		        		if(loc>=0) parent.setChild(new EmptyStmt(), loc);
		        		return null;
		    		}
		    		
				    varType = rhsType = rhs.collectType(stScope, varNode);

				    
				    // When there is an user-defined function, then stop type inference
				    if(((rhsType!=null) && (rhsType instanceof ArgTupleType))) {
				    	return rhsType;
				    }
		    		// (#) Transform COMPLEX type structure
		    		if(TypeInferenceEngine.isComplexType(rhsType) 
		    				&& !(rhs instanceof ParameterizedExpr) 
		    				// && !(rhs instanceof NameExpr)
		    				) 		    			
		    		{
		    			transform2Complex(stScope, rhs);
		    		}
				    
				    // (2) Merge new inferred type with the old type
					if(TypeInferenceEngine.isValidType(varType) ) {
						if(TypeInferenceEngine.isValidType(orgType) ) {
						    if(e.isFirmType && orgType instanceof MatrixType) {
					    		// Only merge the compatible primitive data type
						    	String varTypeName=((MatrixType)orgType).getElementType().getName();
						    	PrimitiveType pType = (PrimitiveType)TypeInferenceEngine.mergePrimitiveType(varType,
					    			new PrimitiveType(varTypeName));
						    	if(!TypeInferenceEngine.isEqualType(((MatrixType)orgType).getElementType(), pType)) {
					    			((MatrixType)orgType).setElementType(pType);
					    			varType = orgType;
					    		}
							} else {
								// Merge the whole type
								Type resultType = TypeInferenceEngine.mergeType(varType, orgType);
								if(resultType==null) {
								}
				    		    varType = resultType;
				    		    transformTypeConversion(stScope, varType, rhsType, lhs, rhs);
							}
			    		} else {	
			    			// Set the new type to be the variable type 
			    		    // varDecl.setType(varType);
			    		}
						// Save the new type and record the type change in the type inference process
						if(TypeInferenceEngine.isValidType(varType) &&
								!TypeInferenceEngine.isEqualType(orgType, varType)) {
				    		
				    		recordSymbolTableChange(stScope);
							varDecl.setType(varType);
						}
					}
		        	// Decide the matrix index is constant or not	// TODO: JL-09.06
		        	// Check it only when the type changed
		    		//System.out.println("[]-["+e.getSymbol()+"]equal type="+TypeInferenceEngine.isEqualType(varType, orgType));
		        	if(TypeInferenceEngine.isMatrixType(varType)
		        			&& !TypeInferenceEngine.isEqualType(varType, orgType)) {
		        		determineMatrixSize(stScope, e, (MatrixType) varType);
		        	}

		    	} else {
		    		// This case used for doing type infer 2nd time, just for adjust the 
		    		// array-access expression 
		    		varType = orgType;
		    	}
/**	        	
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

	        	// (*1) Handle the use of array variables
        		if(rhs instanceof ParameterizedExpr) {
	        		String fname = ((NameExpr)((ParameterizedExpr) rhs).getTarget()).getName().getID();
	        		if(stScope.getSymbolById(fname)!=null) {
		            	// If it's an array variable, set its index as integer
			        	setIndex2Integer(stScope, (ParameterizedExpr) rhs);
	        		}
        		} else if((lhs instanceof NameExpr) && (rhs instanceof RangeExpr)) {
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
}  **/      			
        		
        		// Extra transformations - for Aggregation Transformation
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
	            
	        	// TODO: [#2] Value-Propagation: 
	        	// TODO: get the value of variable, save in symbol table entry
	        	// TODO: New approach:
	            // 
	            // Only handle integer/double scalar variables 
	        	if(varType instanceof PrimitiveType && 
	        			(TypeInferenceEngine.isIntegerType(varType)
	        			|| TypeInferenceEngine.isDoubleType(varType)) ) 
	        	{
			    	ArrayList<ASTNode> nodeList = gVariableDefNodeMap.get(e.getSymbol());

		            // Here handles the case that 
		            // this temporary variable is created by type inference engine
			    	if(nodeList == null) {
			    		nodeList = new ArrayList<ASTNode>();
			    	} 
			    	if(!nodeList.contains(parentNode))
			    		nodeList.add(parentNode);
			    	gVariableDefNodeMap.put(e.getSymbol(), nodeList);

		        	//// if(!e.isConstant() || e.getValue() == null) 
		        	{
		        		// Set value for primitive type variable
		        		String strValue=null, strLow = null;
		        		boolean bSet = false;

		        		// Handle case: x=1;for i=1:n, x=x+1; end;
		        		boolean isIndexVar = false;
		        		if(nodeList.size()==2) {
		        			// Second one is inside the loop
		        			// First one is out side the loop
		        			ForStmt forStmt2 = ASTToolBox.getParentForStmtNode(nodeList.get(1));
		        			if(forStmt2!=null) {
		        				ASTNode parent1 = nodeList.get(0).getParent();
		        				ASTNode parent2 = forStmt2.getParent();
		        				isIndexVar = (parent1.equals(parent2));
		        			}
		        		}

		        		// Estimate constant-variable's value 
		        		if(nodeList.size()==1 || isIndexVar) {
	        				e.setConstant(true);
		        			if(isConstantExpr(stScope, rhs)) {
		        				// Calculate value of constant-expression 
			        			strValue = null;
		        				// Get value of RHS; null means cannot be calculated. 
		        				try {
		        					strValue = TypeInferenceEngine.getVariableValue(stScope, rhs);
		        					if(rhs instanceof RangeExpr)
		        						strLow = TypeInferenceEngine.getVariableValue(stScope, ((RangeExpr)rhs).getLower());
		        					
		        					// Handle the index=index+1 
		        					if(isIndexVar && TypeInferenceEngine.isIntegerType(varType)) {
		        						bSet = getIndexVariableValue(stScope, e, rhs);
		        					}
		        					
		        				} catch(TypeInferException tie) {
		        					// Get the expression node that causes the exception
		        					// Here handles complex number and command form function calls
		        					String msg = tie.getMessage();
		        					Expr nodeExpr = rhs;
		        					if(tie.node!=null) 
		        						nodeExpr = (Expr) tie.node;
		        					// There are following cases:
		        					if(isImaginaryUnit(msg) || isImaginaryPart(msg)) {
		        						// (1) which means RSH is an complex number with imaginary unit.
		        						transform2Complex(stScope, rhs);
		        						varType = new PrimitiveType(TypeInferenceEngine.TYPENAME_COMPLEX);
		        						if(!TypeInferenceEngine.isEqualType(varDecl.getType(), varType)) {
		        				    		recordSymbolTableChange(stScope);
		        							varDecl.setType(varType);
		        						}
		        					} else {
		        						// (2) This must be a function call in a command form
		        						// 		i.e.: timing = toc + 1, t1=clock, ...		        						
		        						// Don't need to handle it here, the inferType() handle it!
		        						if(TypeInferenceEngine.cmdFormFuncList.contains(tie.node)) {
		        						} else {
			        						// (3) If the program is not syntactically correct,
			        						// there are un-defined variables
		        							System.err.println("[TypeInferException] undefined variable:"+msg+" "+tie.node);
		        						}		        						
		        						// Ignore the value of the function
		        						strValue = null;
		        					}
		        				}				        			
		        			} else {
		        				// RHS is not a constant expression
		        				// If we still use the value from the range expression needs value, 
		        				// then this varaible is not a constant. 
				        		if ((rhs instanceof RangeExpr)) {
		        					strValue = TypeInferenceEngine.getVariableValue(stScope, rhs);
	        						strLow = TypeInferenceEngine.getVariableValue(stScope, ((RangeExpr)rhs).getLower());
			        				if(strValue != null)
				        				e.setConstant(false);
				        		}
		        			}
		        		}
	        			if(strValue==null) {
	        				// Use LHS variable name as value
	        				strValue = ((AssignStmt) parentNode).getLHS().getVarName();
	        			}
	        			
        				if(!bSet) {
        					e.setValue(new StringLiteralExpr(strValue));
        					if ((rhs instanceof RangeExpr) && strLow != null) {
        						e.setMinValue(new StringLiteralExpr(strLow));
        					}
        				}
		        	}		        	
        		}


	            // Extra tranformation: tranform 1x1 matrix to a scalar 
	        	if(varType instanceof MatrixType && lhs instanceof NameExpr) {
        			transform2Scalar(stScope, e, (MatrixType) varType, (NameExpr)lhs);
	        	}
	        	// The above function may cause tranformation, therefore, 
	        	// need to reset some variables.  
	        	lhs = ((AssignStmt) parentNode).getLHS();
	        	
				// [#3] LHS Range adjust, for indexed array access 
	        	// Check LHS, whether it's an array with correct indexed access
		        if (lhs instanceof ParameterizedExpr) {
		        	e.setConstant(false);	// matrix variables are not constant
//		        	boolean bExpand = false;
		        	ParameterizedExpr expr = (ParameterizedExpr) lhs; 
		        	String varName = expr.getTarget().getVarName();
		        	Type mType=null;
		        	
		        	// (1) Merge types of two matrice, only when their sizes are comparable: 
		        	// 1. size of symbol table type is null, and current is not
		        	// 2. size of symbol table type and current type are constant values
		        	boolean constantIndex = true;
		        	for(Expr arg: expr.getArgs()) {
			        	if(!isConstantExpr(stScope, arg)) {
			        		constantIndex = false;
			        		break;
			        	}
		        	}
		        	
		        	if(TypeInferenceEngine.isPrimitiveType(varType)
		        			|| (constantIndex && e.isConstantSize()) ) 
		        	{
			        	// merge current variable type (varType) with the expression's type
		        		// If they are un-comparable, then this node needs array bounds check 
		        		if(!TypeInferenceEngine.isValidType(varType)) {
		        			varType = varDecl.getType();
		        		}
		        		
		        		boolean insideIf = (ASTToolBox.getParentIfStmtNode(parentNode)!=null);

	        			// Skip those cases that under if statement, they are not predictable! e.g.,clos
			        	mType = TypeInferenceEngine.adjustMatrixType(varType, expr);
			        	
			        	if(TypeInferenceEngine.isValidType(mType)){ 
		        			e.setConstantSize(constantIndex);
		        			if(insideIf) {
		        				mType = varType;
		        			} else {
				        		if (!TypeInferenceEngine.isEqualType(varDecl.getType(), mType)) {
						    		recordSymbolTableChange(stScope);
					    		    varDecl.setType(mType);
				        		}
		        			}
			        	} else {
			        		// The merge is failed, 
		        			// Add for futhur array bounds checking
			        		addArrayBoundsCheckPoint(e, parentNode, varName);
			        	}

		        	} else {
	        			// Add for futhur array bounds checking
		        		addArrayBoundsCheckPoint(e, parentNode, varName);
		        		// Don't changes the current variable's constant status
		        		// Two possible case
		        		//		old is constant -- new is not constant
		        		//  	old is not constant -- new is constant
		        	}

		        	
		        	// (1) The array index variable must be integer type	// TODO: no need anymore???  
		       //  	setIndex2Integer(stScope, expr);	// This causes problem, in 2nd iteration, int->double->int

		        	// (2) Change LHS, for linear indexing. e.g., from A(2) to A(1,2)
		        	// For linear indexing, which means indexed-access misses some dimensions
		        	// e.g.  A=Matrix(1*5),   expr:  A(2)=0.0  => A(1,2) 
		        	// e.g.  B=Matrix(5*1),   expr:  B(3)=0.0  => B(3,1)
		        	// e.g.  C=Matrix(2*10),  expr:  C(4)=0.0  => B(1,4)	// row major ??
		        	// 						  expr:  C(15)=0.0 => B(2,5)	// row major
		        	if(TypeInferenceEngine.isValidType(mType)) {
		        		TypeInferenceEngine.adjustParameterizedExpr((MatrixType)mType, expr);

		        		// Save the new type and record the type change in the type inference process
		        		if(TypeInferenceEngine.isValidType(mType) &&
		        				!TypeInferenceEngine.isEqualType(mType, varType)) {
				    		recordSymbolTableChange(stScope);
							varDecl.setType(mType);
						}
		        	} else if(TypeInferenceEngine.isMatrixType(varType)) {
		        		TypeInferenceEngine.adjustParameterizedExpr((MatrixType)varType, expr);
		        	}
		       /**	
		        	// I don't think this is useful anymore // TODO: JL-09.06
		        	// Right now, type inference is a iterative process, 
		        	// That means, everytime a type changes, we need do type-inference again, 
		        	// so that the change can be correctly affect on the variables used it.
		        	 	
		        	if(TypeInferenceEngine.isValidType(mType)
		        			&& TypeInferenceEngine.isValidType(varType)) {
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
		        	} else {
		        	
				**/
		        		
			        	// TODO: ???????????????
			    		// [4] Special case,  Transformation.
				        // 	U(1 : n, j1) = tmp;  U={n,m},  tmp={n,1}
		        	    // MATLAB legal, but Fortran needs RHS to be tmp(:,1);
			        	// right now, this part only handle this special case 
			        	if((mType instanceof MatrixType) && (rhsType instanceof MatrixType)  ) {
			        		TypeInferenceEngine.adjustParamAssignment(expr, (MatrixType) mType, rhs, rhsType);
			        	}
		        	// }
			        
		        } else if (lhs instanceof NameExpr) {
		        	// (2) Change LHS, 
		        	// When LHS dimension doesn't match the RHS's, needs adjust the LHS's index 
		        	// It's usually the row/column vector case. 
		        	// e.g. A=Matrix(1*5), then A=1:5  should be A(1,:)=1:5
		        	// BUT, A=1 doesn't need to change!
		        	// Currently focus on handling the row/column vector case.
		        	
		    		// Reload orgType since current type in symbol table may have been changed.
		        	orgType = varDecl.getType();
		        	if((orgType instanceof MatrixType)) {
		        		// Need to check RHS's matrix type, to do the matching 
		        		// varType = rhsType; 	// ((AssignStmt) parentNode).getRHS().collectType(stScope, varNode);
			        		
			        	// varType != orgType && 

			        	if((rhsType instanceof MatrixType)) {
			        		try {
				        		TypeInferenceEngine.adjustArrayIndex(
				        				(MatrixType)rhsType, (MatrixType)orgType, (NameExpr)lhs);
	        				} catch(TypeInferException tie) {
	        					// Get the expression node that causes the exception
	        					// 1. Renaming the variable, and it reaches nodes,
	        					String orgName = ((NameExpr)lhs).getVarName();
								String newName = TypeInferenceEngine.getNewVarName(orgName, stScope);
								java.util.List<ASTNode> defNodeList = RenamingAllReached(gCodeNodeList, 
										orgName, newName, lhs.getParent());

								// 2. Create new symbol table entry for it
								TypeInferenceEngine.addDeclNodeSymTblEntry(newName, rhsType,
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
    	// TODO: varType, which one is correct 
    	return rhsType;
	}
	
	// 
	// This is not the best way of doing it, but it's ok.
	public static void determineMatrixSize(SymbolTableScope stScope, 
			SymbolTableEntry e, MatrixType  mType) {
		// Check the size of matrix, to decide whether it's constant expression or not
		// Constant expression
		// 1. only contain literals, 
		// 2. only constains constant variables
		boolean bConstant = false;
		Size size = mType.getSize();
		java.util.List<Integer> intDim = size.getDims();
		java.util.List<String> strDim = size.getDynamicDims();
    	if(intDim!=null) {
    		bConstant = true;
    	} else if(strDim!=null) {
    		bConstant = true;
    		for(String str: strDim) {
    			if(!isConstantExpr(stScope, str)) {
    				bConstant = false;
    				break;
    			}
    		}
    	} 
		e.setConstantSize(bConstant);
	}

	// Add to database for futhur array bounds checking
	public static void addArrayBoundsCheckPoint(SymbolTableEntry e, ASTNode node, String varName) {
		HashSet<ASTNode> nodeSet = gArrayVarCheckNodeMap.get(e);
		if(nodeSet==null) {
			nodeSet = new HashSet<ASTNode>(); 
		}
		nodeSet.add(node);
		gArrayVarCheckNodeMap.put(e, nodeSet);
		if(FLAG_BOUNDS_CHECK) {
			gArrayVarCheckList.add(varName);
			gArrayVarCheckList.add(varName+"_tmp");
		}
	}
	
	// Transform a 1x1 matrix into a scalar, 
	// create temporary variable for it, and update its symbol table	
	public static void transform2Scalar(SymbolTableScope stScope, SymbolTableEntry e, 
			MatrixType varType, NameExpr varNode) {
		if(!(varNode.getParent() instanceof AssignStmt))
			return;
		if((varNode.getVarName().contains(TypeInferenceEngine.TEMP_VAR_PREFIX)))	
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
		
		
		// addNewAssignment
		// Create new assignment and add to AST
		String tmpName = TypeInferenceEngine.getTempVarName(asg);
		NameExpr lhs = new NameExpr(new Name(tmpName));
		
		AssignStmt newAssign = new AssignStmt();
		ast.List<Expr> list = new ast.List<Expr>();
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

	// Solving the case, when the assignment is happened in a loop,
	// then adding the loop iterations onto the expression
	// e.g., i=0; for j=1:n, i=i+1; end
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


		return true;
	}
	
	private static void setIndex2Integer(SymbolTableScope stScope, ParameterizedExpr expr) {
/**		
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
		    	if(stEntry!=null ) { // && !stEntry.isFirmType
		    		if(stEntry.getDeclLocation()!=null) {
			    		Type pType = ((VariableDecl)stEntry.getDeclLocation()).getType();
			    		if (!TypeInferenceEngine.isIntegerType(pType)) {
			    			if(pType instanceof PrimitiveType) {
				    			((VariableDecl)stEntry.getDeclLocation()).setType
				    				(new PrimitiveType(TypeInferenceEngine.TYPENAME_INTEGER));
			    			} else {
			    				Type idxType = TypeInferenceEngine.createMatrixType
			    					(TypeInferenceEngine.TYPENAME_INTEGER, (MatrixType)pType);
			    				Type orgType = ((VariableDecl)stEntry.getDeclLocation()).getType();
			    				if(TypeInferenceEngine.isValidType(idxType) && 
			    						!TypeInferenceEngine.isEqualType(orgType, idxType)) {
			    					((VariableDecl)stEntry.getDeclLocation()).setType(idxType);
			    				}
			    			}
			    		}
		    		}
			    		stEntry.isFirmType = true;
}
		    	}
        	}
        }
**/        
	}
	
	// Remove those uncessary variable decl nodes
	public static void removeDeclNodes(ASTNode actual) {
		int limit = 0;
		if(actual instanceof FunctionDecl)
			limit = 3;
		// Cleanup all previous created decl-nodes
		for(int i=0; i<actual.getNumChild(); ++i) {
			if(i>=limit && actual.getChild(i) instanceof ast.List) {
				ast.List<ASTNode> list = (ast.List) actual.getChild(i);
				for(int j=list.getNumChild()-1; j>=0; --j) {
					if(list.getChild(j) instanceof VariableDecl)  {
						list.removeChild(j);
					}
				}
			}
		}
	}
	
	//-------------------------------------------------------------------------
	// Transformation functions  
	//-------------------------------------------------------------------------
	// Transform for implicit type conversions
	// Here implements one of the cases: B=B>0;  double=boolean; 
	public static void transformTypeConversion(SymbolTableScope stScope, 
			Type lhsType, Type rhsType, Expr lhs, Expr rhs) {
    	
		if(TypeInferenceEngine.isDoubleMatrixType(lhsType) 
				|| TypeInferenceEngine.isDoubleType(lhsType)) {
			
			if(TypeInferenceEngine.isLogicalMatrixType(rhsType) 
					|| TypeInferenceEngine.isLogicalType(rhsType)) {
				
				// (1). remember current location
				ASTNode parent = rhs.getParent();
				int loc = parent.getIndexOfChild(rhs);
			
				// (2) Add more assignment when necessary
				//		Using parent, because the expr is lost, becomes part of new assignment
				Type imType = new PrimitiveType(TypeInferenceEngine.TYPENAME_INTEGER);
				if(TypeInferenceEngine.isMatrixType(lhsType)) {
					imType = TypeInferenceEngine.createMatrixType(TypeInferenceEngine.TYPENAME_INTEGER, (MatrixType)lhsType);
				}
				NameExpr newVar = TypeInferenceEngine.addNewAssignment(rhs, rhs, null, stScope, imType);
			
		    	// (3) replace by temporary assignment 
		    	parent.setChild(newVar, loc);
			}
		}
	}
	
	//-------------------------------------------------------------------------
	// generate symbol table by flow-analysis  
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
		// DEBUG: Dump only the code-node -- for debug purpose
		// code-node: the node that we don't care about its subtree
		// Includes: simple-statement, assignment-statement, ... 
					
		// [2] Calling Reaching defs directly  
		// Set the debug flag and out	
		AbstractFlowAnalysis.setDebug(DEBUG, (PrintStream) out);
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
//		ArrayList<RenamingRule> PhiRules = new ArrayList<RenamingRule>();
//		ArrayList<RenamingRule> RRules = new ArrayList<RenamingRule>();
	    // variable and its definition node for all the program, only keeps the first definition
//	    HashMap<String, ASTNode> varDefMap = new HashMap<String, ASTNode>();	

	    // Save the result for further use (for convenience) 
	    // gCodeNodeEntryMap DON'T clear it, 
	    gCodeNodeList = codeNodeList;
	    gTreeCodeNodeMap.put(actual, codeNodeList);
	    // Initialize all the global variables used in this function
	    clearCodeNodeEntryMap(codeNodeList);
	    	    
	    // [##] gather all the variable names used in the function
	    // This is used to identify the new created name has never been used by the function.	    
	    symtbl.nameSet = new HashSet<String>();
	    for(ASTNode codeNode: codeNodeList) {
			ASTNode defNode = codeNode;
			// For each node, it will define (at least) one variable
			if(defNode==null || defNode.getDefBoxes()==null) {
				continue;
			}
			// Check if there are new variables defined
			for(Object vb: defNode.getDefBoxes()) {
				symtbl.nameSet.add(((natlab.toolkits.ValueBox) vb).getValue());
			}
	    }
		// [#-Renaming] Renaming Function's input parameters 
	    // that have the same names as output parameters 	
	    if(actual instanceof Function) {
	    	HashSet<String> paramSet = new HashSet<String>();
			for(Name param: ((Function)actual).getOutputParamList()) {
				paramSet.add(param.getID());
			}
			boolean bRenaming = false;
			for(Name param: ((Function)actual).getInputParamList()) {
				String pName = param.getID();
				if(paramSet.contains(pName)) {
					String newName = TypeInferenceEngine.getNewVarName(pName, symtbl);
					param.setID(newName);
					AssignStmt paramStmt = new AssignStmt(new NameExpr(new Name(pName)), 
								new NameExpr(new Name(newName)));
					((Function)actual).getStmtList().insertChild(paramStmt, 0);
					bRenaming = true;
				}
			}
			if(bRenaming)
				return null;
	    }
	    if(actual instanceof Function) {
			for(Name param: ((Function)actual).getInputParamList()) {
				symtbl.nameSet.add(param.getID());
			}
			for(Name param: ((Function)actual).getOutputParamList()) {
				symtbl.nameSet.add(param.getID());
			}
	    }
	    
	    
	    // Starting building symbol table
		// [#1] Adding the Function's input parameters into symbol table 
	    if(actual instanceof Function) {
		    ((Function)actual).addInputParam2SymbolTableScope(symtbl);
	    }
	    
	    // [#2] Go through the code node list
	    for(ASTNode codeNode: codeNodeList) {
	    	
			FlowSet afterSet = afterMap.get(codeNode);
			FlowSet beforeSet = beforeMap.get(codeNode);
			
			// Analyze the before/after flow sets, get the difference between them
			if(beforeSet!=null)
				preFlowset = beforeSet;
			else 
				preFlowset = defsAnalysis.getEmptySet();
			// stopDefSet: stopped definitions
			preFlowset.difference(afterSet, stopDefSet);
			// newDefSet: new added definitions
			afterSet.difference(preFlowset, newDefSet);

	    	// DEBUG_Flow =(codeNode.getNodeID()==107);

			// <1> Applying phi-rules, 
			// Changes the flow-sets of current node

			// variable set of current flow-set
			HashSet<String> varSet = new HashSet<String>();
			// variable and it phi-rule of current flow-set
		    // HashMap<String, RenamingRule> varRuleMap = new HashMap<String, RenamingRule>();

			// Skip ForStmt, because it has a assignment statement, 
		    // current flowset hasn't flow-throught that assignment,
		    // so it hasn't removed previous definition of the loop variable.
		    // However, WhileStmt doesn't have this problem.  
			if((codeNode instanceof ForStmt)){
				continue;
			}

		    // <2>, create renaming rules, phi-nodes

		    // Finding new variable definition only need to check the after set
	    	// using the after set : workFlowSet = afterSet;
		    // (beforeSet==null) // This is the case for node "While/For/... "
			// if(beforeSet!=null) // no need to do that
			{
		    	// (1) collecting variables definitions (into varDefMap),
			    //     and finding duplicated def-nodes (into varRuleMap)
		    	// Here we only need to create symbol table entry for current node, 
		    	// if it's an assignment statement (has definitions).

				ASTNode defNode = codeNode;
				// For each node, it will define (at least) one variable
				if(defNode==null || defNode.getDefBoxes()==null) {
					continue;
				}
				
				// Check if there are new variables defined
				for(Object vb: defNode.getDefBoxes()) {
					
					String newVarName = ((natlab.toolkits.ValueBox) vb).getValue(); 
					/*
					if(null==varDefMap.get(newVarName)) {
						varDefMap.put(newVarName, defNode);
					} */

					// Add it to variable---Def-Node map; for future use
					ArrayList<ASTNode> nodeList = gVariableDefNodeMap.get(newVarName);
			    	if(nodeList == null) {
			    		nodeList = new ArrayList<ASTNode>();
			    	} 
			    	if(!nodeList.contains(defNode))
			    		nodeList.add(defNode);
			    	gVariableDefNodeMap.put(newVarName, nodeList);

					// (2) Rename the variable when necessary,  
			    	// 	   Or create symbol entry for new variables, and 
					// 		save result into varDefMap, gCodeNodeEntryMap
					if(symtbl.getSymbolById(newVarName)==null)
					{
						// Check variables with case-insensitive form in symbol table
						boolean bCheckDiffCase = symtbl.varUpperCaseSet.contains(newVarName.toUpperCase());
						if(bCheckDiffCase) {
							// Renaming-[#1] Renaming this variable in the whole program
							String newName = TypeInferenceEngine.getNewVarName(newVarName, symtbl);
							RenamingAll(codeNodeList, codeNode, newVarName, newName);
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

					} else {
						// Renaming cases: when this is not the first definition
						if((codeNode instanceof AssignStmt) && (nodeList.size()>1)) {
							if(codeNode.getParent() instanceof ForStmt) {
								// Renaming-[#3] Loop variable renaming
								renameForVariable(codeNode, newVarName, symtbl);
								// Code node changes, thus return 
								clearCodeNodeEntryMap(codeNodeList);
								return null;
							} else {
								// Renaming-[#2] Loop variable renaming inside loop
								if(renameRedefineForVariable(codeNode, newVarName, symtbl)) {
									// Code node changes, thus return 
									clearCodeNodeEntryMap(codeNodeList);
									return null;
								} else {
									// Renaming-[#4] Check previous def-nodes, if it is a loop variable, then rename it
									for(ASTNode node: nodeList) {
										if(!node.equals(codeNode) &&
												(node instanceof AssignStmt) 
												&& (node.getParent() instanceof ForStmt)) {
											renameForVariable(node, newVarName, symtbl);
											// Code node changes, thus return 
											clearCodeNodeEntryMap(codeNodeList);
											return null;
										}
									}
									// transform matrix Concatenation to Indexed-access
									if((codeNode instanceof AssignStmt) 
											&& (((AssignStmt)codeNode).getRHS() instanceof MatrixExpr)) {
										boolean result=TypeInferenceEngine.transformConcatenation2Index(symtbl, (MatrixExpr) ((AssignStmt)codeNode).getRHS());
										if(result) {
											clearCodeNodeEntryMap(codeNodeList);
											return null;
										}
									}
								}
							}
						}
					}	// End of else... 
					
				} //End of  for(Object vb: defNode.getDefBoxes())
				
		    } // End of if(beforeSet!=null)
/**
		    // I Don't think they are useful! - JL-2009.06.16
		    // <4> & <5>
			// Renaming happens when there is new def-node replaced old one
			// stop-def-set: could have >=1 node
			// new-def-set: only one node
			// Using a set is for compatibility, for the case [x,y]=f(n) 
		    varSet.clear();
		    varRuleMap.clear();
		    // Keep varDefMap: the original definition 
		    
		    // Find out new definitions, add them to node-symbol-entry map 
		    // newDefSet: new variable defined here, refill the varSet.
			if(!stopDefSet.isEmpty() && !newDefSet.isEmpty()) {
				// 1. Get the variable name from stop-def-set
				for(Object defNode: stopDefSet.toList()) {
					// For each node, it will define (at least) one variable
					for(Object vb: ((ASTNode) defNode).getDefBoxes()) {
						varSet.add(((natlab.toolkits.ValueBox) vb).getValue());
					}
				}
				// 2. For each variable, find out the new def-node in newDefSet
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
					 	// 3. Save this, and 

						// add it to code node-symbol-entry map
				    	ArrayList<SymbolTableEntry> seList = gCodeNodeEntryMap.get(varNewDefNode);
				    	if(seList == null) {
				    		seList = new ArrayList<SymbolTableEntry>();
				    	} 
				    	SymbolTableEntry stEntry = symtbl.getSymbolById(varName);
				    	seList.add(stEntry);
				    	gCodeNodeEntryMap.put(varNewDefNode, seList);
				    	
					}
				}

			} // if(!stopDefSet.isEmpty() && newDefSet.isEmpty())
**/			

		}
		// (3) Creating phi-function for the flow-set has more than two def-nodes
	    // 	After renaming finished, then go through the code node list again
	    for(ASTNode codeNode: codeNodeList) {
	    	
			FlowSet afterSet = afterMap.get(codeNode);
			FlowSet beforeSet = beforeMap.get(codeNode);
			
			// Get the difference between two flow-sets
			if(beforeSet!=null)
				preFlowset = beforeSet;
			else 
				preFlowset = defsAnalysis.getEmptySet();
			
			// Analyze the before/after flow sets, to decide the renaming rules
			// stopDefSet: stoped definitions
			preFlowset.difference(afterSet, stopDefSet);
			// newDefSet: new added definitions
			afterSet.difference(preFlowset, newDefSet);

			// Skip ForStmt, because it has a assignment statement, 
		    // current flowset hasn't flow-throught that assignment,
		    // so it hasn't removed previous definition of the loop variable.
		    // However, WhileStmt doesn't have this problem.  
			if((codeNode instanceof ForStmt)){
				continue;
			}

			{
				ASTNode defNode = codeNode;
				
				// For each node, it will define (at least) one variable
				if(defNode==null || defNode.getDefBoxes()==null) {
					continue;
				}
				// (#) Creating phi-function for the flow-set has more than two def-nodes
				// Three locations: 
				//	[1] Inside the loop, 1st place in the loop-statement-list, after loop-assignment
				//	[2] After end statement of a loop
				if(codeNode instanceof AssignStmt && codeNode.getParent() instanceof ForStmt) {
					// Get the definitions happened inside the loop
					FlowSet forBeforeSet = beforeMap.get(codeNode.getParent());
					FlowSet loopNewDefSet = defsAnalysis.getEmptySet();
					afterSet.difference(forBeforeSet, loopNewDefSet);
					// Find the related definition nodes passed in.
					FlowSet loopSet = getConflictFlowSet(loopNewDefSet, afterSet);
					addPhiFunction(codeNode, loopSet);
					
				} else if(codeNode instanceof WhileStmt) {
					// Get the definitions happened inside the loop
					FlowSet loopNewDefSet = defsAnalysis.getEmptySet();
					afterSet.difference(beforeSet, loopNewDefSet);
					// Find the related definition nodes passed in.
					FlowSet loopSet = getConflictFlowSet(loopNewDefSet, afterSet);
					addPhiFunction(codeNode, loopSet);
				}
				//	[3] After the end statement of a branch statement
				if(codeNode instanceof IfStmt || codeNode instanceof SwitchStmt) {
					// Using alternative result from Reaching-Def-Analysis
					// Where only return the flow set of 1st iteration
				    Map<Stmt, FlowSet> filterBeforeMap = defsAnalysis.getfilterBeforeFlow();
				    FlowSet fBSet = filterBeforeMap.get((Stmt)codeNode);
				    Map<ASTNode, FlowSet> filterAfterMap = defsAnalysis.getfilterAfterFlow();
				    FlowSet fASet = filterAfterMap.get(codeNode);
				    if(fBSet!=null && fASet!=null) {
					    FlowSet tmpSet =  defsAnalysis.getEmptySet();
					    fASet.difference(fBSet, tmpSet);
				    	addPhiFunction(codeNode, tmpSet);
				    } else {
				    	addPhiFunction(codeNode, newDefSet);
				    }
				}
			}
	    }
				

		// [#] Adding declaration for the Script, Function
		actual.updateSymbolTableScope(symtbl);		

		
		return symtbl;	// (SymbolTableScope) 
	}
	// @return: true: rename happened; false: not the case
	private static boolean renameRedefineForVariable(ASTNode codeNode, String newVarName, SymbolTableScope symtbl) {
		// Renaming-[#3] Loop variable renaming inside loop
		ForStmt forStmt = ASTToolBox.getParentForStmtNode(codeNode);
		if(forStmt!=null && forStmt.getAssignStmt().getLHS().getVarName().equals(newVarName)) {

			String newName = TypeInferenceEngine.getNewVarName(newVarName, symtbl);
			// Renaming the whole loop
			(forStmt.getStmtList()).renaming(newVarName, newName);
			// Add new assignment to restore the value of original variable
			AssignStmt newAssign = new AssignStmt(new NameExpr(new Name(newName)),new NameExpr(new Name(newVarName)));
			forStmt.getStmtList().insertChild(newAssign, 0);
			return true;
		}
		return false;		
	}
	
	private static void renameForVariable(ASTNode codeNode, String newVarName, SymbolTableScope symtbl) {
		String newName = TypeInferenceEngine.getNewVarName(newVarName, symtbl);
		// Renaming the whole loop
		(codeNode.getParent()).renaming(newVarName, newName);

		// Add new assignment to restore the value of original variable
		AssignStmt newAssign = new AssignStmt(new NameExpr(new Name(newVarName)),new NameExpr(new Name(newName)));
		Stmt stmt = (Stmt)codeNode.getParent();
		int loc = stmt.getParent().getIndexOfChild(stmt);
		if(loc>=0) {
			stmt.getParent().insertChild(newAssign, loc+1);
		}
	}

	// For each definition, find previous definitions from preSet
	// then merge with newSet into the new set
	private static FlowSet getConflictFlowSet(FlowSet newSet, FlowSet preSet) {
		HashSet<ASTNode> nodeSet = new HashSet<ASTNode>();
		
		for(Object node: newSet.toList()) {
			for(Object vb: ((ASTNode)node).getDefBoxes()) {
				String varName = ((natlab.toolkits.ValueBox) vb).getValue();
				for(Object preNode: preSet.toList()) {
					for(Object prevb: ((ASTNode)preNode).getDefBoxes()) {
						if(varName.equals(((natlab.toolkits.ValueBox) prevb).getValue())) {
							nodeSet.add((ASTNode)preNode);
							break;
						}
					}
				}
			}
		}
		for(ASTNode node:nodeSet) {
			newSet.add(node);
		}
		return newSet;
	}
	
	// Convert the flow-set (a set of ASTNodes) into a map
	// from variable-name to a list of ASTNodes.
	private static HashMap<String, ArrayList<ASTNode>>  getVarDefListMap(FlowSet flowSet) {
		HashMap<String, ArrayList<ASTNode>> varDefListMap = new HashMap<String, ArrayList<ASTNode>>();
		
		for(Object node: flowSet.toList()) {
			for(Object vb: ((ASTNode)node).getDefBoxes()) {
				String varName = ((natlab.toolkits.ValueBox) vb).getValue();
				ArrayList<ASTNode> defList = varDefListMap.get(varName);
				if(defList==null) 
					defList = new ArrayList<ASTNode>();
				defList.add((ASTNode)node);
				varDefListMap.put(varName, defList);
			}
		}
		return varDefListMap;
	}
	
	private static AssignStmt createPhiFunction(String name, ArrayList<ASTNode> varDefList, String funcName) {
		if(varDefList!=null && varDefList.size()>1) {
			// Gather all arguments of this phi-node, (currently, using the node-id instead)
			ast.List<Expr> arglist = new ast.List<Expr>();
			for(ASTNode node: varDefList) {
				arglist.add(new IntLiteralExpr(new natlab.DecIntNumericLiteralValue(""+node.getNodeID())));
			}
			// Creating the phi-expression
			ParameterizedExpr phiExpr = new ParameterizedExpr(
					new NameExpr(new Name(funcName)), arglist);
			AssignStmt phiStmt = new AssignStmt(
					new NameExpr(new Name(name)), phiExpr);
			phiStmt.setNodeID();
			phiStmt.generateUseBoxesList();
			// Save it into global database
			gPhiFunctionMap.put(phiExpr, varDefList);
			return phiStmt;
		} else {
			return null;
		}
	}
	// Add type conflict (alpha/beta/lamdba) functions for joined definitions 
	// in the flow-set 
	private static void addPhiFunction(ASTNode codeNode, FlowSet flowSet) 
	{

		// Convert the flow-set (a set of ASTNodes) into a map
		// from variable-name to a list of ASTNodes.
		HashMap<String, ArrayList<ASTNode>> varDefListMap = getVarDefListMap(flowSet);

		for(String name: varDefListMap.keySet()) {
			ArrayList<ASTNode> varDefList = varDefListMap.get(name);
			if(varDefList!=null && varDefList.size()>1) {
				if(codeNode instanceof IfStmt || codeNode instanceof SwitchStmt) {
					//	(3) After end statement of a branches
					AssignStmt alphaStmt = createPhiFunction(name, varDefList, ALPHA_FUNC_NAME);
					Stmt stmt = (Stmt)codeNode;
					ASTNode parent = stmt.getParent();
					int loc = parent.getIndexOfChild(stmt);
					parent.insertChild(alphaStmt, loc+1);
					
				} else {
					Stmt forstmt = null;
					List<Stmt> stmtlist = null;
					if(codeNode instanceof AssignStmt && codeNode.getParent() instanceof ForStmt) {
						forstmt = (ForStmt)codeNode.getParent();
						stmtlist = ((ForStmt)codeNode.getParent()).getStmtList();
					} else if(codeNode instanceof WhileStmt) {
						forstmt = (WhileStmt)codeNode;
						stmtlist = ((WhileStmt)codeNode).getStmtList();
					} else {
						return;
					}

					AssignStmt betaStmt = createPhiFunction(name, varDefList, BETA_FUNC_NAME);
					stmtlist.insertChild(betaStmt, 0);
					
					AssignStmt lambdaStmt = createPhiFunction(name, varDefList, LAMBDA_FUNC_NAME);
					ASTNode parent = forstmt.getParent();
					int loc = parent.getIndexOfChild(forstmt);
					parent.insertChild(lambdaStmt, loc+1);
						    				
				}  
			}
		}		
	}
	
	// This function works throught all the program 
	// renames a variable from old-name to new-name, 
	// regarding about flow-sets.
	public static void RenamingAll(java.util.List<ASTNode> codeNodeList, 
			ASTNode defNode, String orgName, String newName)// RenamingRule rule) 
	{
	    // Renaming the def-node 
	    if (defNode instanceof AssignStmt) {
	    	((AssignStmt)defNode).getLHS().renaming(orgName, newName);
	    } else {
	    	defNode.renaming(orgName, newName);
	    }
		// Starting from the top, not current node
		// Because we cannot know how deep (nested loop/if) current node is in.
	    for(ASTNode node: codeNodeList) {
			node.renaming(orgName, newName);
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
			java.util.List<ASTNode> codeNodeList,
			String orgName, String newName, ASTNode defNode)			
	{
	    
		ASTNode startNode = defNode;
	    java.util.List<ASTNode> defNodeList = new ArrayList<ASTNode> ();
	    defNodeList.add(startNode);
	    // Renaming the def-node 
	    if (defNode instanceof AssignStmt) {
	    	((AssignStmt)defNode).getLHS().renaming(orgName, newName);
	    } else {
	    	defNode.renaming(orgName, newName);
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

			if(afterSet==null) {
				// Those new node of phi-functions
				continue;
			}
			// Get the difference between two flow-sets
			if(beforeSet!=null)
				preFlowset = beforeSet;
			else 
				preFlowset = defsAnalysis.getEmptySet();
			// Analyze the before/after flow sets, to decide the renaming rules
			preFlowset.difference(afterSet, stopDefSet);
			afterSet.difference(preFlowset, newDefSet);


			if(beforeSet!=null && beforeSet.contains(startNode)) {
				node.renaming(orgName, newName);
				node.clearUseDefBoxes(); 
				node.generateUseBoxes();
			    // Check if there is new defition override the old one
				if(stopDefSet.contains(startNode)) {
					if(!newDefSet.isEmpty()) 
						startNode = (ASTNode)newDefSet.toList().get(0);
				    defNodeList.add(startNode);
					
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
    	gVariableDefNodeMap.clear();
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
	public static boolean isImaginaryUnit(String test) {		
		return 	(test.equals("i") || test.equals("j"));
	}
	public static boolean isImaginaryUnit(SymbolTableScope stScope, String test) {
		if(test.equals("i")) {
			return !TypeInferenceEngine.isValidType(getVariableType(stScope, "i"));
		} else if(test.equals("j")) {
			return !TypeInferenceEngine.isValidType(getVariableType(stScope, "j"));
		} else {
			return false;
		}		
	}
	public static Type getVariableType(SymbolTableScope stScope, String var) {
		Type varType = null;
		SymbolTableEntry e = stScope.getSymbolById(var);
		if(e!=null && e.getDeclLocation()!=null) {
    		varType = ((VariableDecl) e.getDeclLocation()).getType();
		}
		return varType;
	}
	
	public static boolean isPatternMatch(String strPattern, String test) {
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(strPattern);
		java.util.regex.Matcher matcher = pattern.matcher(test);
		return matcher.matches();
	}
	
	
	// 1. Checking whether an expression involves any non-constant variables
	//	- if ok, then calculate value
	//	- if not ok, then value= its name
	public static boolean isConstantExpr(SymbolTableScope stScope, String exprStr) {
		ExprStmt stmt = parseString(exprStr);
		if(stmt!=null) {
			return isConstantExpr(stScope, stmt.getExpr());
		}
		return false;
	}
	public static boolean isConstantExpr(SymbolTableScope stScope, Expr expr) {
		HashSet<String> varSet = getVariableListFromString(expr);
		for(String varName : varSet) {
	        SymbolTableEntry stEntry = stScope.getSymbolById(varName);
	        if(stEntry!=null && !stEntry.isConstant()) {
				return false;
	        } 
		}		
		return true;
	}
	
	// Parsing a expression string, and return variables inside it
	// Return :
	//	- null: there is parsing error, e.g. it's empty tree, or other error  
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
		if(expr==null) {
			return null;
		} else {
			return getVariableListFromString(expr.getExpr());
		}		
	}
	public static HashSet<String> getVariableListFromString(Expr expr) {
		
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
				if(original instanceof Script) {
					// When parsing an expression, will get a Script  
					ASTNode exprStmt = ((Script) original).getStmtList().getChild(0);
					if(!(exprStmt instanceof ExprStmt)) {
						// scanner.stop();
						return null;
					} else {
						// scanner.stop();
						return (ExprStmt) exprStmt;
					}
				}		
			}
			//  scanner.stop();
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
}
