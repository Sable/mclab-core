package natlab.toolkits.path;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;

import natlab.options.Options;
import natlab.toolkits.filehandling.*;
import natlab.toolkits.filehandling.genericFile.*;

/**
 * This class represents a PathEnvironment that allows finding matlab functions.
 * It does the look up resolution. Matlab functions, scripts and classes are assumed
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
public class FilePathEnvironment extends AbstractPathEnvironment {
	//private
    GenericFile main;
	GenericFile pwd;
	Options options;
	final FilenameFilter MATLAB_FILE_FILTER = new MatlabFileFilter();
	
	//methods
	/**
	 * constructor takes in an options object and a builtin query object.
	 * The Options object is used to find the path.
	 * pwd is assumed to be the directory where the first file is.
	 * 
	 */
	public FilePathEnvironment(Options options,BuiltinQuery builtin){
	    super(builtin);
		this.options = options;
		main = GenericFile.create((String)options.getFiles().getFirst());
		pwd = main.getParent();
	}

	/**
	 * returns true if the given matlab function name matches the provided file
	 * (i.e. if the function name and .m file have the same name)
	 * TODO: should this be case sensitive?
	 */
	public boolean isMatch(String name,GenericFile file){
		String filename = file.getName(); //TODO there should be a cleaner way to do this
		return (filename.substring(0, filename.length()-2).equalsIgnoreCase(name));
	}
	
	/**
	 * returns the main matlab file, i.e. the entry point.
	 * @return the main matlab file as an absolute File.
	 */
	@Override
	public GenericFile getMain(){
		//TODO - so far only returns first
		return main;
	}

	/**
	 * given a matlab function name name, finds the file name it resides in
	 * returns the filename as a String, or null if it cannot be found
	 * or if it is a builtin
	 */
	public GenericFile resolve(String name,GenericFile context){
	    
		GenericFile file = null;
		//private
		file = findInPrivate(name);
		if (file != null) return file;
		
		//pwd
		file = findInPwd(name);
		if (file != null) return file;
		
		//builtin?
		if (isBuiltin(name)){ return null; }
		
		//path
		return findInPath(name);
	}
	
	@Override
	public GenericFile resolve(String name, String className,
	        GenericFile context) {
	    throw new UnsupportedOperationException("rsolve by classname");
	}
	
	/**
	 * returns true if the given matlab function name refers to a builtin function,
	 * (even retuns false if it is a builtin
	 */
	public boolean isStrictlyBuiltin(String name){
		if (findInPrivate(name) == null && findInPwd(name) == null){
			return isBuiltin(name);
		} else {
			return false;
		}
		
	}
	
	//private methods
	//there is a method for each possible lookup
	//it just takes the name
	private GenericFile findInPrivate(String name){
		return null; //TODO implement this - has to be relative to current function
	}
	private GenericFile findInPwd(String name){
		return findInDirectory(name,pwd);
	}
	private GenericFile findInPath(String name){
		return null; //TODO implement this
	}
	private GenericFile findInDirectory(String name,GenericFile dir){
		Collection<GenericFile> list = 
		    dir.listChildren(GenericFileMatlabTools.MATLAB_FILE_FILTER); //find all files in dir
		if (list == null) return null; //not a dir
		for (GenericFile file : list){ //go through every file, check if it's match
			if (isMatch(name,file)){
				return file;
			}
		}
		return null;
	}
	
	
    @Override
    public Collection<GenericFile> getAllOverloaded(String className,
            GenericFile cotntext) {
        throw new UnsupportedOperationException("getAllOverloaded");
    }
	
}



