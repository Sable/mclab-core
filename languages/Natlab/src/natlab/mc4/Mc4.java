/**
 * 
 */
package natlab.mc4;

import java.io.File;
import natlab.options.Options;


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
			//try to get a file from the project folder
			File file = new File("../Benchmarks/matlabBenchmarks/McFor/mcfor_test/mbrt/drv_mbrt.m");
			if (file.exists()){
				args = new String[]{file.getAbsolutePath()};
			} else {
				args = new String[]{"C:\\classes\\mclab\\Benchmarks\\matlabBenchmarks\\McFor\\mcfor_test\\mbrt\\drv_mbrt.m"};				
			}
			//if it doesn't exist, we're on anton's computer ...
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
