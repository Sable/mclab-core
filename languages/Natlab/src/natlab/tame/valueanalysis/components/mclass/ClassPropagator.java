package natlab.tame.valueanalysis.components.mclass;

import java.util.*;

import natlab.tame.builtin.*;
import natlab.tame.builtin.classprop.ClassPropTool;
import natlab.tame.builtin.classprop.HasClassPropagationInfo;
import natlab.tame.classes.reference.ClassReference;
import natlab.tame.valueanalysis.value.*;

/**
 * given a builtin and values, returns the mclasses of the results. The results are returned as
 * a List<HashSet<ClassReference>>, where every entry in the lest corresponds to an output,
 * and the corresponding set contains all the possible mclasses for that output. These Sets
 * are never empty.
 */
public class ClassPropagator<V extends Value<V>> extends BuiltinVisitor<Args<V>,List<Set<ClassReference>>> {
	//this is a singleton class -- make it singleton, ignore all the generic stuff
    @SuppressWarnings("rawtypes")
	static ClassPropagator instance = null;
    @SuppressWarnings({ "rawtypes", "unchecked" })
	static public <V extends Value<V>> ClassPropagator<V> getInstance(){
        if (instance == null) instance = new ClassPropagator();
        return instance;
    }
    private ClassPropagator() {} //hidden private constructor
    
    @Override
	public List<Set<ClassReference>> caseBuiltin(Builtin builtin, Args<V> arg) {
        if (builtin instanceof HasClassPropagationInfo){
        	//call class prop tool
            return ClassPropTool.matchByValues(((HasClassPropagationInfo)builtin).getMatlabClassPropagationInfo(),arg);
        }
		throw new UnsupportedOperationException(
				"ClassPropgator cannot propgate classes for "+builtin);
	}	
}


