package natlab.toolkits.path;
import natlab.toolkits.filehandling.genericFile.*;


/**
 * A function or script within a Matlab program can not be uniquely identified by a name alone
 * We need a soruce file as well
 * 
 * This may refer to a funciton or script or class inside a matlab file on the path,
 * or a builtin
 *
 * This class is immutable
 * 
 * TODO - this should maybe be an enum, or the parent class of a set of different
 * things.
 * TODO - this should maybe store what class it belongs to
 */


public class FunctionReference {
    GenericFile path;
    String name;
    boolean isBuiltin;
    boolean isPrivate; //TODO - denote whether private, sibling, nested etc.
    
    /**
     * creates a Function Reference referring to a function inside a matlab file
     * @param name the name of the function
     * @param path the path of the function (as an absolute File)
     */
    public FunctionReference(String name, GenericFile path){
        this.path = path;
        this.name = name;
        this.isBuiltin = false;
    }
    
    /**
     * creates a Function Reference referring to a function or script inside
     * a matlab file - it will refer to the funciton or script with the same name as the given
     * one
     */
    public FunctionReference(GenericFile path){
        this.path = path;
        //TODO this should be done nicer - what if there is no extension?
        this.name = path.getName().substring(0,path.getName().length()-path.getExtension().length()-1);
        this.isBuiltin = false;
    }
    
    
    /**
     * returns the name of the function
     */
    public String getname(){
        return name;
    }
    
    /**
     * returns the file where the Function which this FunctionReference refers
     * to resides, or null if this file doesn't exist (for builtins).
     */
    public GenericFile getFile(){
        return path;
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
    	if (isBuiltin) return name+"@builtin";
    	else return name+"@"+path;
    }
    
    @Override
    public int hashCode() {
    	return name.hashCode()+(isBuiltin?0:path.hashCode());
    }
}


