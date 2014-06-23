package natlab.tame.classes;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import natlab.toolkits.path.FileEnvironment;
import natlab.toolkits.path.FunctionReference;

/**
 * reperesents a matlab class that is defined using the 'old' way,
 * i.e. via folders of overloaded methods.
 */

abstract public class OldMatlabClass implements MatlabClass{
	private Map<String,FunctionReference> methods;
	
	public OldMatlabClass(String className, FileEnvironment fileEnvironment){
        //TODO check the ref for constructor? - deal with doubly defined methods?
		this.methods = fileEnvironment.getOverloadedMethods(className).stream()
		    .collect(Collectors.toMap(f -> f.getName(), Function.identity()));
	}

	@Override
	public Map<String, FunctionReference> getMethods() {
		return Collections.unmodifiableMap(methods);
	}
}
