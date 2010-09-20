/**
 * 
 */
package natlab.mc4;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;

import natlab.CompilationProblem;
import natlab.FlowAnalysisTestTool;
import natlab.ProgramEntry;
import natlab.options.Options;
import natlab.toolkits.analysis.Analysis;
import natlab.toolkits.analysis.FlowSet;
import natlab.toolkits.analysis.StructuralAnalysis;
import natlab.toolkits.analysis.varorfun.VFStructuralForwardAnalysis;

import ast.Function;
import ast.FunctionList;
import ast.Program;

/**
 * Mc4 Main and entry point.
 * @author ant6n
 *
 */
public class Mc4 {
    public static boolean DEBUG = true;
    public static boolean PRINT_STACK_ON_ERROR = true;
    public static boolean EXIT_ON_ERROR = true;
    public static void debug(String message){
        if (DEBUG) System.out.println(message);
    }
    public static void error(String message){
        System.err.println(message);
        if (PRINT_STACK_ON_ERROR) Thread.dumpStack();
        if (EXIT_ON_ERROR) System.exit(1);
    }
    

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//for now just open a file if no option is given .. eclipse is a pain
		Options options = new Options();
		if (args.length == 0){
			args = new String[]{"-m","C:\\classes\\mclab\\Benchmarks\\matlabBenchmarks\\McFor\\mcfor_test\\mbrt\\drv_mbrt.m"};
			options.parse(args);
		} else {
			options.parse(args);
		}
		main(options);
	}

	/**
	 * compiles matlab files into fortran, using the Options as argument.
	 * This is basically the entry point for mc4.
	 * @param options A Natlab Options object defines the command line arguments to Mc4
	 */
	public static void main(Options options){	    
	    //collect all need matlab files
	    FunctionCollection functions = new FunctionCollection(options);
	    	    
	    //inline all
	    functions.inlineAll();
	    
	    //print result for now
	    System.out.println(functions.get(functions.getMain()));
	}
}
