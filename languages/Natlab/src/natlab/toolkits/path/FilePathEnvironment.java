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

package natlab.toolkits.path;

import java.io.FilenameFilter;
import java.util.Map;

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
 *
 */
public class FilePathEnvironment extends AbstractPathEnvironment {
	//private
    GenericFile main;
	GenericFile pwd;
	Options options;
	final FilenameFilter MATLAB_FILE_FILTER = new MatlabFileFilter();
	
	//the directory objects
	MatlabPath matlabPath;
	MatlabPath natlabPath;
	
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
        natlabPath = MatlabPath.getNatlabPath();
		matlabPath = MatlabPath.getMatlabPath();
		//DirectoryCache.touchAll();
		//DirectoryCache.printCache();
	}

	/**
     * creates file path environment from just a file
     * pwd is assumed to be the directory where the first file is.
     * 
     */
    public FilePathEnvironment(GenericFile file,BuiltinQuery builtin){
        super(builtin);
        this.options = new Options();
        main = file;
        pwd = main.getParent();
        natlabPath = MatlabPath.getNatlabPath();
        matlabPath = MatlabPath.getMatlabPath();
    }

	
	/**
	 * returns true if the given matlab function name matches the provided file
	 * (i.e. if the function name and .m file have the same name)
	 * TODO: should this be case sensitive?
	 */
	public boolean isMatch(String name,GenericFile file){
		String filename = file.getName(); //TODO there should be a cleaner way to do this
		return (filename.substring(
		        0, filename.length()-file.getExtension().length()-1).equalsIgnoreCase(name));
	}
	
	/**
	 * returns the main matlab file, i.e. the entry point.
	 * @return the main matlab file as an absolute File.
	 */
	@Override
	public FunctionReference getMain(){
		//TODO - so far only returns first
		return new FunctionReference(main);
	}

	/**
	 * given a matlab function name name, finds the file name it resides in
	 * returns the filename as a String, or null if it cannot be found
	 * or if it is a builtin
	 */
	public FunctionReference resolve(String name,GenericFile context){
	    
	    FunctionReference file = null;
		//private
		file = findInPrivate(name,context);
		if (file != null) return (file);
		
		//pwd
		file = findInPwd(name);
		if (file != null) return (file);
		
		//builtin?
		if (isBuiltin(name)){ return new FunctionReference(name); }
		
		//path
		return (findInPath(name));
	}
	
	@Override
	public FunctionReference resolve(String name, String className,
	        GenericFile context) {
	    throw new UnsupportedOperationException("resolve by classname");
	}
	
	
	@Override
	public Map<String, FunctionReference> resolveAll(String name, GenericFile context) {
        throw new UnsupportedOperationException("resolve by classname");
	}
	
	/**
	 * returns true if the given matlab function name refers to a builtin function,
	 * (even retuns false if it is a builtin
	 */
	public boolean isStrictlyBuiltin(String name,GenericFile context){
		if (findInPrivate(name,context) == null && findInPwd(name) == null){
			return isBuiltin(name);
		} else {
			return false;
		}
		
	}
	
	//private methods
	//there is a method for each possible lookup
	//it just takes the name
	private FunctionReference findInPrivate(String name,GenericFile context){
	    if (context == null) return null; //TODO
	    //create matlab path for current file's private dir
	    return new MatlabPath(context.getParent().getChild("private")).resolve(name, null);
	}
	private FunctionReference findInPwd(String name){
		return new MatlabPath(pwd).resolve(name, null);
	}
	private FunctionReference findInPath(String name){
		FunctionReference ref = natlabPath.resolve(name, null);
		if (ref == null) ref = matlabPath.resolve(name, null);
		return ref;
	}
	
	
	/*
	private FunctionReference findInDirectory(String name,GenericFile dir){
		Collection<GenericFile> list = 
		    dir.listChildren(GenericFileMatlabTools.MATLAB_FILE_FILTER); //find all files in dir
		if (list == null) return null; //not a dir
		for (GenericFile file : list){ //go through every file, check if it's match
			if (isMatch(name,file)){
				return new FunctionReference(file);
			}
		}
		return null;
	}
	*/
	
	
    @Override
    public Map<String, FunctionReference> getAllOverloaded(String className,
            GenericFile cotntext) {
        throw new UnsupportedOperationException("getAllOverloaded");
    }
	
}



