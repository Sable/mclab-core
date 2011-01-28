package natlab.toolkits.filehandling;
/**
 * This is an interface that can be used by analyses (e.g.) to ask whether
 * a certain name exists as a function or script in the environment.
 *
 * This lookup is assumed to only do the lookup of
 * - builtins
 * - private functions
 * - class constructors
 * - overloaded methods
 * - functions in the current folder
 * - functions elsewhere on the path
 * 
 * The local lookup, i.e. within a given Program object has to be done
 * in another manner. These include
 * - recursive calls (the name of the function or script itself)
 * - nested functions
 * - sibling functions
 * 
 * @author ant6n
 */
public interface FunctionOrScriptQuery {
    /**
     * Returns true if a function or script exists in the file path environment
     * represented by this FunctionOrScriptQuery object.
     * 
     * @param name
     * @return true if such a function or script exists
     */
    boolean isFunctionOrScript(String name);
}
