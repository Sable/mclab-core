package natlab.tame.callgraph;

import java.util.List;

import natlab.tame.classes.ClassRepository;
import natlab.toolkits.path.FunctionReference;
/**
 * a collection of static functions, referenced by keys FunctionReference
 * used by interprocedural analyses
 */

public interface FunctionCollection {
	public StaticFunction get(Object functionReference);
	public FunctionReference getMain();
	
	/**
	 * every function collection should have a class repository associated with it.
	 * The function collection stores functions which are referenced by function references,
	 * the class repository stores matlab classes which are referenced by class references.
	 * This method returns the class repository.
	 */
	public ClassRepository getClassRepository();
	
	/**
	 * load the function into the collection. May also include other functions that
	 * are in the same file, and/or functions in other files that are being called by
	 * those functions
	 * This may also throw unsuppported operation exceptions if the function collection
	 * does not allow adding more functions.
	 */
	public boolean collect(FunctionReference ref);
	
	/**
	 * @author Amine Sahibi 
	 * @return List of all functions
	 */
	public List<StaticFunction> getAllFunctions();
	
	//TODO - interface for inlining into one function?
	//TODO - interface for pretty printing?
}

