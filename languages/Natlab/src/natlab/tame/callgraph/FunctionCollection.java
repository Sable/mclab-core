package natlab.tame.callgraph;

import natlab.toolkits.path.FunctionReference;
/**
 * a collection of static functions, referenced by keys FunctionReference
 * used by interprocedural analyses
 */

public interface FunctionCollection {
	public StaticFunction get(Object functionReference);
	public FunctionReference getMain();
	//TODO - interface for adding functions - collect?
	//TODO - interface for inlining into one function?
	//TODO - interface for pretty printing?
}
