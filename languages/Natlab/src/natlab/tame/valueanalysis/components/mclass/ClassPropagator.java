package natlab.tame.valueanalysis.components.mclass;

import java.util.*;

import natlab.tame.builtin.*;
import natlab.tame.classes.reference.ClassReference;
import natlab.tame.valueanalysis.value.*;

public class ClassPropagator<V extends Value<V>> extends BuiltinVisitor<Args<V>,List<HashSet<ClassReference>>> {
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
	public List<HashSet<ClassReference>> caseBuiltin(Builtin builtin, Args<V> arg) {
		throw new UnsupportedOperationException();
	}

	
	
}
