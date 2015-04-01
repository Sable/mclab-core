package natlab.tame.classes;

import java.util.Map;

import natlab.toolkits.path.FunctionReference;
/**
 * an interface that describes matlab classes
 * 
 * there are multiple kinds of classes
 * - old classes (defined via folders named "@<classname>")
 *    - user defined old classes
 *    - builtin classes
 * - new classes (defined in files)
 *    - handle classes
 *    - non handle classes
 */

public interface MatlabClass {
	public Map<String,FunctionReference> getMethods();
	public boolean isSuperior(MatlabClass other);
	public boolean isInferior(MatlabClass other);
	//public Set<MatlabClass> getSuperiorClasses();
	//public Set<MatlabClass> getInferiorClasses();
	//implements dot operator?...
}
