package natlab.tame.callgraph;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

import natlab.CompilationProblem;
import natlab.toolkits.path.FileEnvironment;
import natlab.toolkits.path.FunctionReference;

/**
 * this is similar to the simple function collection, but rather than
 * attempting to load every file involved in the program, the files are loaded
 * only as they are requested. Every time a file is actually loaded, all 
 * files that it refers to are placed in a load cache.
 */
public class IncrementalFunctionCollection extends SimpleFunctionCollection{
	private static final long serialVersionUID = 1L;
	public ArrayList<CompilationProblem> errors = new ArrayList<CompilationProblem>();
	private HashSet<FunctionReference> loadCache;
	
	public IncrementalFunctionCollection(FileEnvironment env) {
		super(env);
	}
	
	@Override
	public boolean collect(FunctionReference ref,
			ArrayList<CompilationProblem> errList) {
        if (super.containsKey(ref)) return true;
        if (ref.isBuiltin) return true;
        if (loadCache == null) loadCache = new HashSet<FunctionReference>();
        loadCache.add(ref);
        return true;
	}
	
	@Override
	public boolean containsKey(Object key) {
		return super.containsKey(key) || loadCache.contains(key);
	}
	
	@Override
	public StaticFunction get(Object key) {
		//load if necessary
		if (!containsKey(key)){
			collect((FunctionReference)key,errors);
		}
		//actually load if necessary
		if (loadCache.contains(key)){
			//make sure function is not actually loaded, so we won't load it a second time
			if (super.containsKey(key)){
				loadCache.remove(key);
			} else {		
				super.collect((FunctionReference)key, errors);
				//remove whatever was loaded
				Iterator<FunctionReference> iterator = loadCache.iterator();
				while (iterator.hasNext()){
					if (super.containsKey(iterator.next())) iterator.remove();
				}
			}
		}
		//then return
		return super.get(key);
	}

}
