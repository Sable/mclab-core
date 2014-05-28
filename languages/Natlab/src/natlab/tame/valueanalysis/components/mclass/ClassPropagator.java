package natlab.tame.valueanalysis.components.mclass;

import java.util.List;
import java.util.Set;

import natlab.tame.builtin.Builtin;
import natlab.tame.builtin.BuiltinVisitor;
import natlab.tame.builtin.classprop.ClassPropTool;
import natlab.tame.builtin.classprop.HasClassPropagationInfo;
import natlab.tame.classes.reference.ClassReference;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

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
            
    /**
     * returns a classref of i in 
     *   i = lower:inc:upper 
     * when used as the range expression in a for loop. The classref for i has to be a valid
     * representation for all iterations.
     * 
     * inc is optional, and may be null.
     * lower, upper, inc should be values with matrix mclasses.
     */
    @SuppressWarnings("unchecked")
	public PrimitiveClassReference forRange(V lower,V upper, V inc){
		//FIXME do something proper here
		if (inc != null){
			return (PrimitiveClassReference)
					(this.caseBuiltin(Builtin.Colon.getInstance(), Args.newInstance(lower,inc,upper))
							.get(0).iterator().next());
		} else {
			return (PrimitiveClassReference)
					(this.caseBuiltin(Builtin.Colon.getInstance(), Args.newInstance(lower,upper))
							.get(0).iterator().next());
		}
    }
    
}


