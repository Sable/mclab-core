// =========================================================================== //
//                                                                             //
// Copyright 2011 Anton Dubrau and McGill University.                          //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//  limitations under the License.                                             //
//                                                                             //
// =========================================================================== //

/**
 * 
 */
package natlab.Static.mc4;

import java.io.*;
import java.net.URL;
import java.util.HashSet;


import natlab.FlowAnalysisTestTool;
import natlab.Static.builtin.*;
import natlab.Static.callgraph.*;
import natlab.Static.classes.reference.*;
import natlab.Static.valueanalysis.*;
import natlab.Static.valueanalysis.simplematrix.SimpleMatrixValue;
import natlab.Static.valueanalysis.simplematrix.SimpleMatrixValueFactory;
import natlab.Static.valueanalysis.value.*;
import natlab.options.Options;
import natlab.toolkits.filehandling.genericFile.*;
import natlab.toolkits.path.*;


/**
 * Mc4 Main and entry point.
 * @author ant6n
 *
 */
public class Mc4 {    
    public static final String outDir = "natlab_out";
    public static boolean DEBUG = true;
    public static boolean PRINT_STACK_ON_ERROR = true;
    public static boolean EXIT_ON_ERROR = true;
    public static void debug(String message){
        if (DEBUG) System.out.println(message);
    }
    public static void error(String message){
        System.err.println("mc4 error: "+message);
        if (PRINT_STACK_ON_ERROR) Thread.dumpStack();
        if (EXIT_ON_ERROR) System.exit(1);
    }
    
    
    //TODO get rid of this
    public static void fiddle() throws Exception{
        URL location = Mc4.class.getProtectionDomain().getCodeSource().getLocation();
        System.out.println(location);
        System.out.println(location.getFile());
        
        System.out.println(natlab.Main.class.getResource("Main.class"));
        
        System.out.println(
                new ZippedFile("C:\\classes\\COMP 621.ZIP","ass2/ass2.pdf"));                
    }
    

	/**
	 * @param args
	 */
	public static void main(String[] args) {
        //for now just open a file if no option is given .. eclipse is a pain
		Options options = new Options();
		options.parse(args);
		
		
		/* play around with path
		//MatlabPath path = MatlabPath.getMatlabPath(C:\classes\mclab\Benchmarks\matlabBenchmarks\adpt\drv_adpt.m);
		CachedDirectory adir = DirectoryCache.get(new FileFile(
		        "C:\\classes\\mclab\\Benchmarks\\matlabBenchmarks\\adpt"));
		System.out.println(adir.listChildDirs()+"\n\n");
        System.out.println(adir.listChildFiles());
		
		System.exit(0);
		/* */
		
		
		
		//try to do all benchmarks
		String bFolderString = "C:\\classes\\mclab\\Benchmarks\\matlabBenchmarks\\";
		String[] subfolders = new String[]{
		    "aaatest",
		    "adpt",
		    "beul",
		    "capr",
		    "clos",
		    "crni",
		    "dich",
		    "diff",
		    "edit", //uses cell arrays
		    //"eigenval", //the benchmark is broken!
		    "fdtd",
		    "fft", 
		    "fiff",
		    //"fourier", //the benchmark is broken!
		    "hnormalise",
		    "lagrcheb",
		    "linear",
		    "mbrt",
		    "mils",
		    "nb1d",
		    "nb3d",
		    "nfrc", // uses &&
		    "nnet", //uses cell arrays
		    "play", //recursive
		    "rayt", //end-issue???
		    "sch2", //error in toeplitz - nargin
		    "schr", //uses lambda - toeplitz
		    //"sdku", //uses load
		    "spga", //end-issue???
		    "svd" //uses nargout - cannot be all inlined
		    };
		for (String subfolder : subfolders){
		    File dir = new File(bFolderString+subfolder);
		    File[] list = dir.listFiles(new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.matches(".*drv_.*m");
                }
            });
		    if (list == null || list.length == 0) continue;
		    String main = list[0].toString();
		    System.err.println("reading "+main);
		    args = new String[]{"-m",main};
		    options = new Options();
	        options.parse(args);
	        main(options);
	        System.exit(0);//only do first
		}
		System.out.println("\n---done---\n builtins: "+allRefs.size()+"\n\n");
		//for (FunctionReference ref : allRefs) System.out.println(ref);
		System.exit(0);
		/* */
		
		
		
		//if no files are given, we will use some internal test files
		if (options.getFiles().size() == 0){
			//try to get a file from the project folder
		    //where it is depends on whether we call from Project or natlab dir
		    File thisDir = new File(System.getProperty("user.dir"));
		    String main = (thisDir.getName().equals("McLab"))?
		            "languages/Natlab/src/natlab/Static/mc4/test/drv_mbrt.m"
		            :"src/natlab/Static/mc4/test/drv_mbrt.m";
            args = new String[]{main};
			options.parse(args);
		} 
		main(options);
	}

	static HashSet<FunctionReference> allRefs = new HashSet<FunctionReference>();
	
	/**
	 * compiles matlab files into fortran, using the Options as argument.
	 * This is basically the entry point for mc4.
	 * @param options A Natlab Options object defines the command line arguments to Mc4
	 */
	public static void main(Options options){
        //object that resolves function names to files      
	    long t = System.currentTimeMillis();
	    FilePathEnvironment functionFinder;
        functionFinder = new FilePathEnvironment(options, Builtin.getBuiltinQuery());
        //System.out.println("time "+(System.currentTimeMillis() - t));
        //System.exit(0);
        
	    //collect all needed matlab files
	    FunctionCollection functions = new FunctionCollection(functionFinder);
	    allRefs.addAll(functions.getAllFunctionBuiltinReferences());
	    
	    
	    
	    //test intraprocedural class analysis
	    try{
	    ValueAnalysis<SimpleMatrixValue> analysis = new ValueAnalysis<SimpleMatrixValue>(
	            functions,
	            Args.newInstance(new SimpleMatrixValue(PrimitiveClassReference.DOUBLE)),
	            new SimpleMatrixValueFactory());
        System.out.println(analysis);
        System.out.println(analysis.getPrettyPrinted());
	    } catch (StackOverflowError e){
	        System.err.println("stackoverflow in "+functions.getMain().name);
	        System.exit(0);
	    }
	    System.out.println("=> finished "+functions.getMain().name);
	    //FlowAnalysisTestTool test = new FlowAnalysisTestTool(classAnalysis);
	    //System.out.println(test.run(true,true));
	}
}




