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
package natlab.tame.mc4;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;

import natlab.options.Options;
import natlab.tame.builtin.Builtin;
import natlab.tame.callgraph.SimpleFunctionCollection;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.interproceduralAnalysis.InterproceduralAnalysisNode;
import natlab.tame.valueanalysis.IntraproceduralValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysis;
import natlab.tame.valueanalysis.ValueAnalysisPrinter;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.simplematrix.SimpleMatrixValue;
import natlab.tame.valueanalysis.simplematrix.SimpleMatrixValueFactory;
import natlab.tame.valueanalysis.value.Args;
import natlab.toolkits.path.FileEnvironment;
import natlab.toolkits.path.FunctionReference;


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
		String bFolderString;// = "C:\\classes\\mclab\\Benchmarks\\matlabBenchmarks\\";
		bFolderString = "../Benchmarks/matlabBenchmarks/";
		String[] subfolders = new String[]{
		    "aaatest",
		    "adpt",
		    //"beul", //handle
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
		    //"sch2", //handle error only? - error in toeplitz - nargin
		    //"schr", //uses lambda - toeplitz
		    //"sdku", //uses load
		    "spga", //end-issue???
		    "svd" //uses nargout - cannot be all inlined
		    };
		/*for (String subfolder : subfolders){
		    File dir = new File(bFolderString+subfolder);
		    System.out.println(dir.getAbsolutePath());
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
	        //System.exit(0);//only do first
		}*/
		
		String mainf = "/home/adubra/mclab/papers/oopsla12tame/main.m";
	    System.err.println("reading "+mainf);
	    args = new String[]{"-m",mainf};
	    options = new Options();
        options.parse(args);
        main(options);
		
		System.out.println("\n---done---\n builtins: "+allRefs.size()+"\n\n");
		//for (FunctionReference ref : allRefs) System.out.println(ref);
		System.out.println(allRefs);
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
        //System.out.println("time "+(System.currentTimeMillis() - t));
        //System.exit(0);
        
	    //collect all needed matlab files
	    SimpleFunctionCollection functions = new SimpleFunctionCollection(new FileEnvironment(options));
	    allRefs.addAll(functions.getAllFunctionBuiltinReferences());
	    //System.out.println(functions);
	    
	    
	    //test intraprocedural class analysis
	    try{
	    ValueAnalysis<AggrValue<SimpleMatrixValue>> analysis = new ValueAnalysis<AggrValue<SimpleMatrixValue>>(
	            functions,
	            Args.<AggrValue<SimpleMatrixValue>>newInstance(new SimpleMatrixValue(null, PrimitiveClassReference.DOUBLE)),
	            new SimpleMatrixValueFactory());
        System.out.println(analysis);
        //System.out.println(analysis.getPrettyPrinted());
        //print code
        for (int i = 0; i < analysis.getNodeList().size(); i++){
        	System.out.println(analysis.getNodeList().get(i).getAnalysis().getTree().getPrettyPrinted());        	
        }
        System.out.println("**********************************************************************");
        for (int i = 0; i < analysis.getNodeList().size(); i++){
        	System.out.println(ValueAnalysisPrinter.prettyPrint(
        			analysis.getNodeList().get(i).getAnalysis()));        	
        }

        
        //System.out.println(ValueAnalysisPrinter.prettyPrint(analysis.getMainNode().getAnalysis()));
	    //System.exit(0);
	    } catch (StackOverflowError e){
	        System.err.println("stackoverflow in "+functions.getMain().name);
	        e.printStackTrace();
	        System.exit(0);
	    }
	    System.out.println("=> finished "+functions.getMain().name);
	    
	    
	    //FlowAnalysisTestTool test = new FlowAnalysisTestTool(classAnalysis);
	    //System.out.println(test.run(true,true));
	}

}




