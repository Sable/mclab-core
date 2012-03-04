package natlab.toolkits.path;

import java.util.*;

import ast.Function;
import ast.FunctionList;
import ast.List;

import natlab.toolkits.filehandling.genericFile.FileFile;
import natlab.toolkits.filehandling.genericFile.GenericFile;
import natlab.toolkits.path.LookupResult.TYPE;

public class PathHandler {
    private LinkedHashSet<FolderHandler> handlers;
    private HashMap<String, Function> nestedFunctions;
    private HashMap<String, Function> siblingFunctions;
    private FunctionList currentFunctionList;
    private GenericFile fwd=null, pwd=null;
    public PathHandler(GenericFile[] path){
	handlers=new LinkedHashSet<FolderHandler>();
	for (GenericFile folder: path){
	    handlers.add(FolderHandler.getFolderHandler(folder));
	}
    }
    public void setPWD(GenericFile path){
	this.pwd=pwd;
    }
    public void setFunction(Function fun){
	if (fun.getParent().getParent() instanceof Function){
	    setFunction((Function)fun.getParent().getParent());
	    return;
	}
	FunctionList file=(FunctionList) fun.getParent().getParent();
	//fwd = file.getFile();
	if (currentFunctionList!=file){
	    currentFunctionList=file;
	    siblingFunctions=new HashMap<String, Function>();
	    ast.List<Function> l= (List<Function>) fun.getParent(); 
	    for (int i=0;i<l.getNumChild();i++)
				siblingFunctions.put(l.getChild(i).getName(), l.getChild(i));
	}
	nestedFunctions=new HashMap<String, Function>();
	for (Function f: fun.getNestedFunctionList()){
	    nestedFunctions.put(f.getName(), f);
		}
		
	}
	
	public void unsetFunction(){
		currentFunctionList=null;
		siblingFunctions=null;
		nestedFunctions=null;
		fwd=null;
	}
	
	public LookupResult staticQuery(String name){
		if (nestedFunctions.containsKey(name)){
			FunctionList f = (FunctionList) nestedFunctions.get(name).getParent().getParent().getParent().getParent();
			return new LookupResult(TYPE.FUNCTION, new FileFile(f.getName()));
		}
		if (siblingFunctions.containsKey(name)){
			FunctionList f = (FunctionList) siblingFunctions.get(name).getParent().getParent();
			return new LookupResult(TYPE.FUNCTION, new FileFile(f.getName()));
		}
		if (fwd!=null){
			GenericFile res = FolderHandler.getFolderHandler(fwd).lookupPrivateFunctions(name);
			if (res!=null)
				return new LookupResult(TYPE.FUNCTION, res);
		}
		for (FolderHandler f: handlers){
			if (f.lookupClasses(name)!=null)
				return new LookupResult(TYPE.CLASS, f.lookupClasses(name));
		}
		for (FolderHandler f: handlers)
			if (f.lookupFunctions(name)!=null){
				return new LookupResult(TYPE.FUNCTION, f.lookupFunctions(name));
		}
		return null;		
	}
}
