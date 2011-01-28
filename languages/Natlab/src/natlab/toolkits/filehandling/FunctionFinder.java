package natlab.toolkits.filehandling;

import java.io.File;
import java.io.FilenameFilter;

import natlab.options.Options;

/**
 * This class represents an object that allows finding matlab functions.
 * It does the look up resolution. Matlab functions and scripts are assumed
 * to have the following precedence (i.e. if a function call is encountered):
 * 
 * 1 nested functions
 * 2 subfunctions (functions in the same directory)
 * 3 private functions (functions in <directory of calling function>/private)
 * 4 functions in directory of current execution
 * 5 builtin functions
 * 6 path
 *
 * Since the existence of builtin does not depend on the semantics of the lookup,
 * it has to be done by separate class, which only exists as an interface.
 *
 */

public class FunctionFinder {
	/**
	 * an interface that allows finding whether a function is a builtin (#5)
	 */
	public interface BuiltinQuery{
		boolean isBuiltin(String functionname);		
	}

	//private
	File pwd;
	BuiltinQuery builtin;
	Options options;
	final FilenameFilter MATLAB_FILE_FILTER = new MatlabFileFilter();
	
	//methods
	/**
	 * constructor takes in an options object and a builtin query object.
	 * The Options object is used to find the path.
	 * pwd is assumed to be the directory where the first file is.
	 * 
	 */
	public FunctionFinder(Options options,BuiltinQuery builtin){
		this.options = options;
		File file = new File((String)options.getFiles().getFirst()).getAbsoluteFile();
		if (!file.isDirectory()){
            file = file.getParentFile();			
		}
		pwd = file;
		this.builtin = builtin;
	}

	/**
	 * returns true if the given matlab function name matches the provided file
	 * (i.e. if the function name and .m file have the same name)
	 * TODO: should this be case sensitive?
	 */
	public boolean isMatch(String name,File file){
		String filename = file.getName(); //TODO there should be a cleaner way to do this
		return (filename.substring(0, filename.length()-2).equalsIgnoreCase(name));
	}
	
	/**
	 * returns the main matlab file, i.e. the entry point.
	 * @return the main matlab file as an absolute File.
	 */
	public File getMain(){
		//TODO - so far only returns first
		return (new File((String)options.getFiles().getFirst())).getAbsoluteFile();
	}

	/**
	 * given a matlab function name name, finds the file name it resides in
	 * returns the filename as a String, or null if it cannot be found
	 * or if it is a builtin
	 */
	public File findName(String name){
		File file = null;
		//private
		file = findInPrivate(name);
		if (file != null) return file;
		
		//pwd
		file = findInPwd(name);
		if (file != null) return file;
		
		//builtin?
		if (builtin.isBuiltin(name)){ return null; }
		
		//path
		return findInPath(name);
	}
	
	/**
	 * returns true if the given matlab function name refers to a builtin function,
	 * (even retuns false if it is a builtin
	 */
	public boolean isBuiltin(String name){
		if (findInPrivate(name) == null && findInPwd(name) == null){
			return builtin.isBuiltin(name);
		} else {
			return false;
		}
		
	}
	
	//private methods
	//there is a method for each possible lookup
	//it just takes the name
	private File findInPrivate(String name){
		return null; //TODO implement this - has to be relative to current function
	}
	private File findInPwd(String name){
		return findInDirectory(name,pwd);
	}
	private File findInPath(String name){
		return null; //TODO implement this
	}
	private File findInDirectory(String name,File dir){
		String[] list = dir.list(MATLAB_FILE_FILTER); //find all files in dir
		if (list == null) return null; //not a dir
		for (String filename : list){ //go through every file, check if it's match
			File file = new File(dir,filename);
			if (isMatch(name,file)){
				return file.getAbsoluteFile();
			}
		}
		return null;
	}
	
	public FunctionOrScriptQuery getFunctionOrScriptQuery(){
	    return new FunctionOrScriptQuery(){
            public boolean isFunctionOrScript(String name) {
                return (findName(name) != null);
            }
	    };
	}
}



