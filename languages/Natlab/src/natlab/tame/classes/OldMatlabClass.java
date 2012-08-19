package natlab.tame.classes;

import java.util.*;
import natlab.toolkits.path.*;

/**
 * reperesents a matlab class that is defined using the 'old' way,
 * i.e. via folders of overloaded methods.
 */

abstract public class OldMatlabClass implements MatlabClass{
	private Map<String,FunctionReference> methods;
	private String className;
	
	public OldMatlabClass(String className, FileEnvironment fileEnvironment){
		this.className = className;
		//System.out.println(className);
		methods = new HashMap<String,FunctionReference>();
		for (FunctionReference ref : fileEnvironment.getOverloadedMethods(className)){
			//TODO check the ref for constructor? - deal with doubly defined methods?
			methods.put(ref.getname(),ref);
		}
	}
	
	@Override
	public Map<String, FunctionReference> getMethods() {
		return Collections.unmodifiableMap(methods);
	}
	
}


