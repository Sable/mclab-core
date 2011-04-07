package natlab.Static.callgraph;

import java.io.File;

/**
 * A function within a Matlab program can not be uniquely identified by a name alone
 * We add the source file as well
 * 
 * This may refer to a funciton inside a matlab file on the path,
 * or a builtin
 *
 * This class is immutable
 */


public class FunctionReference {
    File path;
    String name;
    boolean isBuiltin;
    boolean isPrivate; //TODO - denote whether private, sibling, nested etc.
    
    /**
     * creates a Function Reference referring to a function inside a matlab file
     * @param name the name of the function
     * @param path the path of the function (as an absolute File)
     */
    public FunctionReference(String name, File path){
        this.path = path;
        this.name = name;
        this.isBuiltin = false;
    }
    
    
    /**
     * returns the name of the function
     */
    public String getname(){
        return name;
    }
    
    
    /**
     * creates a Functino Reference referring to a builtin matlab function
     * @param name the name of the builtin
     */
    public FunctionReference(String name){
    	this.name = name;
    	this.isBuiltin = true;
    }
    
    /**
     * returns whether this function refers to a builtin
     */
    public boolean isBuiltin(){ return isBuiltin; }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FunctionReference){
            FunctionReference ref = (FunctionReference)obj;
            if (isBuiltin){
            	return ref.isBuiltin && ref.name.equals(name);
            } else {            
            	return !ref.isBuiltin && ref.name.equals(name) && ref.path.equals(path);
            }
        } else return false;
    }
    
    @Override
    public String toString() {
    	if (isBuiltin) return "builtin "+name;
    	else return "function "+name+"@"+path.getAbsolutePath();
    }
    
    @Override
    public int hashCode() {
    	return name.hashCode()+(isBuiltin?0:path.hashCode());
    }
}


