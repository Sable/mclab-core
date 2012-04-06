package natlab.tame.valueanalysis.components.complex;

import java.util.List;

import natlab.tame.builtin.Builtin;
import natlab.tame.builtin.BuiltinVisitor;
import natlab.tame.valueanalysis.value.*;

public class ComplexInfoPropagator<V extends Value<V>> extends BuiltinVisitor<Args<V>, List<ComplexInfo>>{
	//this is a singleton class -- make it singleton, ignore all the generic stuff
    @SuppressWarnings("rawtypes")
	static ComplexInfoPropagator instance = null;
    @SuppressWarnings({ "rawtypes", "unchecked" })
	static public <V extends Value<V>> ComplexInfoPropagator<V> getInstance(){
        if (instance == null) instance = new ComplexInfoPropagator();
        return instance;
    }
    private ComplexInfoPropagator(){} //hidden private constructor

	
	@Override
	public List<ComplexInfo> caseBuiltin(Builtin builtin, Args<V> arg) {
		// TODO base case -- use whatever the builtin's complex propagation info provides
		return null;
	}

}
