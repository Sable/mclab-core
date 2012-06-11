package natlab.tame.callgraph;

import natlab.toolkits.path.FunctionReference;
/**
 * a collection of static functions, referenced by keys FunctionReference
 * used by interprocedural analyses
 */

public interface FunctionCollection {
	public StaticFunction get(Object functionReference);
	public FunctionReference getMain();
	
	/**
	 * load the function into the collection. May also include other functions that
	 * are in the same file, and/or functions in other files that are being called by
	 * those functions
	 * This may also throw unsuppported operation exceptions if the function collection
	 * does not allow adding more functions.
	 */
	public boolean collect(FunctionReference ref);
	
	//TODO - interface for inlining into one function?
	//TODO - interface for pretty printing?
}

