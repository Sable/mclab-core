package natlab.toolkits.path;

import java.util.List;

import natlab.options.Options;
import natlab.tame.builtin.Builtin;
import natlab.toolkits.Context;
import natlab.toolkits.filehandling.FunctionOrScriptQuery;
import natlab.toolkits.filehandling.GenericFile;

import com.google.common.base.Joiner;
import com.google.common.base.StandardSystemProperty;
import com.google.common.collect.Lists;

/**
 * This class represents the file path environment information needed
 * to build look up objects (contexts) to find functions.
 * It stores
 * - the main file (entry point)
 * - the working directory (pwd, where the main file resides)
 * - a builtin query object
 * - a path (i.e. a list of files)
 * 
 * If no path is specified, it will use the matlab/natlab path stored
 * in the registry.
 * 
 * @author adubra
 */
public class FileEnvironment {
	BuiltinQuery query;
	GenericFile main,pwd;
	MatlabPath matlabPath, natlabPath;
	List<FolderHandler> folderHandlers;
	FolderHandler pwdHandler;
	
	/**
	 * builds a file environment from a path, a main file and a builtin query
	 */
	public FileEnvironment(GenericFile mainFile, MatlabPath path, BuiltinQuery query){
		this.main = mainFile;
		this.natlabPath = path;
		this.query = query;
		this.pwd = mainFile.getParent();
		this.folderHandlers = getFolderHandlers();
		this.pwdHandler = FolderHandler.getFolderHandler(this.pwd);
	}
	
	/**
	 * builds a file environment from just a main file
	 * uses default path and query
	 */
	public FileEnvironment(GenericFile mainFile){
		this.main = mainFile;
		this.natlabPath = MatlabPath.getNatlabPath();
		this.matlabPath = MatlabPath.getMatlabPath();
		this.query = Builtin.getBuiltinQuery();
		this.pwd = mainFile.getParent();
		this.folderHandlers = getFolderHandlers();
		this.pwdHandler = FolderHandler.getFolderHandler(this.pwd);
	}
	

	/**
	 * builds a file environment by reading the information from an options object.
	 * Will use the builtin query provided by the tame.builtin package.
	 * Throws an unsupported operations exception if the options are malformed (i.e. 
	 * no main file supplied)
	 */
	public FileEnvironment(Options options){
		this(options,Builtin.getBuiltinQuery());
	}
	
	/**
	 * builds a file environment by reading the information from an options object.
	 * Throws an unsupported operations exception if the options are malformed (i.e. 
	 * no main file supplied)
	 */
	public FileEnvironment(Options options,BuiltinQuery query){
		this.query = query;
		
		//parse main file
		String main = "";
		if (options.main() != null && options.main().length() > 0){
			main = options.main();
		} else {
			List<String> list = options.getFiles();
			if (list != null && list.size() > 0){
				main = list.get(0);
			}
		}
		if (main.length() == 0) throw new UnsupportedOperationException("no main file provided");
		this.main = GenericFile.create(main);
		if (this.main == null || !this.main.exists()){
			throw new UnsupportedOperationException("file "+main+" not found");
		}
		this.pwd = this.main.getParent();
		this.pwdHandler = FolderHandler.getFolderHandler(this.pwd);

		
		//get path
		@SuppressWarnings("unchecked")
		List<String> paths = (List<String>)options.lp();
		if (paths == null || paths.size() == 0){
          this.natlabPath = MatlabPath.getNatlabPath();
          this.matlabPath = MatlabPath.getMatlabPath();
		} else {
		  this.natlabPath =
		      new MatlabPath(Joiner.on(StandardSystemProperty.PATH_SEPARATOR.value()).join(paths));
		}
		this.folderHandlers = getFolderHandlers();
	}
	
	public GenericFile getMainFile(){
		return main;
	}
	
	public FunctionReference getMainFunctionReference(){
		return new FunctionReference(main);
	}
	
	public GenericFile getPwd(){
		return pwd;
	}
	
	/**
	 * returns the complete path as a list of path handlers
	 */
	private List<FolderHandler> getFolderHandlers(){
		//TODO - put this in the constructors, so that this list odesn't get recomputed
		List<FolderHandler> list = Lists.newArrayList();
		if (natlabPath != null){
			list.addAll(natlabPath.getAsFolderHandlerList());
		}
		if (matlabPath != null){
			list.addAll(matlabPath.getAsFolderHandlerList());
		}
		return list;
	}
	
	/**
	 * returns a context object for the given function/script and file where it resides
	 */
	public Context getContext(@SuppressWarnings("rawtypes") ast.ASTNode node,GenericFile file){
		return new Context(node, this.pwdHandler, 
				FolderHandler.getFolderHandler(file.getParent()),
				this.folderHandlers,
				this.query);
	}

	/**
	 * returns a functionOrScriptQuery relative to the given file
	 */
	public FunctionOrScriptQuery getFunctionOrScriptQuery(GenericFile programFile){
	  final FolderHandler fwd = FolderHandler.getFolderHandler(programFile.getParent());
	  final FolderHandler pwd = pwdHandler;
	  final List<FolderHandler> folders = this.folderHandlers;

	  return new FunctionOrScriptQuery() {
	    public boolean isPackage(final String name) {
	      return pwd.lookupPackage(name) != null
	          || folders.stream().anyMatch(f -> f.lookupPackage(name) != null);
	    }
	    public boolean isFunctionOrScript(final String name) {
	      return query.isBuiltin(name)
	          || pwd.lookupFunctions(name) != null
	          || pwd.lookupSpecializedAll(name).size() > 0
	          || fwd.lookupPrivateFunctions(name) != null
	          || folders.stream().anyMatch(f -> f.lookupFunctions(name) != null ||
	                                            f.lookupSpecializedAll(name).size() > 0);
	    }
	  };
	}
	
	/**
	 * returns all overload methods (via folders) in all the folders in the path 
	 * TODO - there may be builtin functions showing up as overloaded functions on the 
	 * matlab path, which may not actually contain code, but just help files -- although that's unlikely
	 */
	public List<FunctionReference> getOverloadedMethods(String classname){
		List<FunctionReference> list = Lists.newArrayList();
		List<FolderHandler> folders = Lists.newArrayList(getFolderHandlers());
		folders.add(pwdHandler);
		for (FolderHandler handler : folders){
			for (GenericFile f : handler.getAllSpecialized(classname)){
				list.add(new FunctionReference(f,FunctionReference.ReferenceType.OVERLOADED));
			}
			GenericFile constructor = handler.lookupClasses(classname);
			if (constructor != null){
				list.add(new FunctionReference(constructor,FunctionReference.ReferenceType.CLASS_CONSTRUCTOR));
			}
		}
		return list;
	}
}

