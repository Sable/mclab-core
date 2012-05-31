package natlab.tame.valueanalysis.components.isComplex;

import java.util.List;

import natlab.tame.builtin.Builtin;
import natlab.tame.builtin.BuiltinVisitor;
import natlab.tame.builtin.isComplexInfoProp.HasisComplexPropagationInfo;
import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropTool;
import natlab.tame.builtin.shapeprop.HasShapePropagationInfo;
import natlab.tame.builtin.shapeprop.ShapePropTool;
import natlab.tame.valueanalysis.value.*;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.advancedMatrix.*;

public class isComplexInfoPropagator<V extends Value<V>> extends BuiltinVisitor<Args<V>, 
List<isComplexInfo<AggrValue<AdvancedMatrixValue>>>>{
	//this is a singleton class -- make it singleton, ignore all the generic stuff
    @SuppressWarnings("rawtypes")
	static isComplexInfoPropagator instance = null;
    @SuppressWarnings({ "rawtypes", "unchecked" })
	static public <V extends Value<V>> isComplexInfoPropagator<V> getInstance(){
        if (instance == null) instance = new isComplexInfoPropagator();
        return instance;
    }
    private isComplexInfoPropagator(){} //hidden private constructor

	
	@Override
	public List<isComplexInfo<AggrValue<AdvancedMatrixValue>>> 
	caseBuiltin(Builtin builtin, Args<V> arg) {
		// TODO base case -- use whatever the builtin's complex propagation info provides
		if(builtin instanceof HasisComplexPropagationInfo){
			//call shape prop tool
			return isComplexInfoPropTool.matchByValues(((HasisComplexPropagationInfo)builtin).getisComplexPropagationInfo(),arg);
		}
		else
		throw new UnsupportedOperationException();
	}

}
