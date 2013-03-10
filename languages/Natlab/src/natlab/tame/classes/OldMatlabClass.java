package natlab.tame.classes;

import java.util.Collections;
import java.util.Map;

import natlab.toolkits.path.FileEnvironment;
import natlab.toolkits.path.FunctionReference;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

/**
 * reperesents a matlab class that is defined using the 'old' way,
 * i.e. via folders of overloaded methods.
 */

abstract public class OldMatlabClass implements MatlabClass{
	private Map<String,FunctionReference> methods;
	
	public OldMatlabClass(String className, FileEnvironment fileEnvironment){
		this.methods = Maps.uniqueIndex(fileEnvironment.getOverloadedMethods(className),
		    new Function<FunctionReference, String>() {
		  @Override public String apply(FunctionReference ref) {
	      //TODO check the ref for constructor? - deal with doubly defined methods?
		    return ref.getName();
		  }
		});
	}
	
	@Override
	public Map<String, FunctionReference> getMethods() {
		return Collections.unmodifiableMap(methods);
	}
}
