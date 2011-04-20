package natlab.toolkits.path;
import java.util.Collection;

import natlab.Static.callgraph.FunctionReference;
import natlab.toolkits.filehandling.FunctionOrScriptQuery;
import natlab.toolkits.filehandling.genericFile.GenericFile;

/**
 * Represents a matlab path environment.
 * Will find matlab files, given names and locations.
 * Will also find overloaded files.
 * 
 */

abstract public class AbstractPathEnvironment implements BuiltinQuery{
    private BuiltinQuery builtinQuery;
    public AbstractPathEnvironment(BuiltinQuery builtinQuery){
        this.builtinQuery = builtinQuery;
    }
    
    
    /**
     * returns the location of the main file
     * - the matlab file where execution starts
     */
    public abstract GenericFile getMain();
    

    @Override
    public boolean isBuiltin(String functionname) {
        return builtinQuery.isBuiltin(functionname);
    }
    
    
    /**
     * finds a function/script/class based on its name and context
     * @param name - the name
     * @param context - the location where this function is being called
     * @return
     */
    public abstract GenericFile resolve(String name, GenericFile context); 

    
    /**
     * finds functions/scripts/classes based on its name and context,
     * but does though for overloaded functions. Returns a map
     * type -> file
     * where the type is given as a String, an empty String refer to 
     * not overloaded functions 
     * resolveAll(a,b).get("") should be the same as resolve(a,b)
     * 
     * @param name - the name
     * @param context - the location where this function is being called
     * @return
     */    
    public abstract java.util.Map<String,GenericFile> resolveAll(String name,GenericFile context);    

    /**
     * resolves a function using resolve, and returns it as a FunctionReference
     * @param name
     * @param context
     * @return
     */
    public FunctionReference resolveToFunctionReference(String name, GenericFile context){
        GenericFile file = resolve(name,context);
        if (file != null){
            return new FunctionReference(name,file);
        } else if (isBuiltin(name)){
            return new FunctionReference(name);
        } else {
            return null;
        }
    }
    
    
    /**
     * finds a matlab file based on its name, and class type - i.e. finds
     * overloaded functions.
     * 
     * @param name
     * @param className
     * @param context
     * @return a function reference to the overloaded function, or null if it cannot be found
     */
    public abstract GenericFile resolve(String name, String className, GenericFile context);

    
    /**
     * returns all .m files that are overloaded for the given class name
     * 
     */
    public abstract Collection<GenericFile> getAllOverloaded(String className, GenericFile cotntext);
    
    
    /**
     * returns a  FunctionOrScriptQuery object for the given context
     * @param context
     * @return
     */
    public FunctionOrScriptQuery getFunctionOrScriptQuery(final GenericFile context){
        return new FunctionOrScriptQuery() {
            public boolean isFunctionOrScript(String name) {
                return (builtinQuery.isBuiltin(name)) || (resolve(name,context) != null);
            }
            public boolean isPackage(String name) {
                return false;
            }
        };
    }
    
}
