package natlab.tame.valueanalysis.components.isComplex;

import java.util.ArrayList;
import java.util.List;
import natlab.tame.builtin.isComplexInfoProp.*;
import natlab.tame.builtin.isComplexInfoProp.ast.*;
import natlab.tame.builtin.*;
import natlab.tame.builtin.isComplexInfoProp.HasisComplexPropagationInfo;
import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropTool;
import natlab.tame.valueanalysis.value.*;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.components.isComplex.isComplexInfo;
import natlab.tame.valueanalysis.components.shape.Shape;
import natlab.tame.valueanalysis.components.shape.ShapeFactory;
import natlab.tame.valueanalysis.value.*;

public class isComplexInfoPropagator<V extends Value<V>> extends BuiltinVisitor<Args<V>, 
List<isComplexInfo<V>>>{
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
//	public List<isComplexInfo<AggrValue<AdvancedMatrixValue>>> 
//	public List<V extends Value<V>> 
	public List<isComplexInfo<V>> caseBuiltin(Builtin builtin, Args<V> arg) {
		// TODO base case -- use whatever the builtin's complex propagation info provides
		//num is the number of LHS arguments in the tame IR node
		if(builtin instanceof HasisComplexPropagationInfo){
			//call shape prop tool
			List<isComplexInfo<?>> result =  isComplexInfoPropTool.matchByValues(((HasisComplexPropagationInfo)builtin).getisComplexPropagationInfo(),arg);
			 List<isComplexInfo<V>> res= new ArrayList<isComplexInfo<V>>();
			 for (isComplexInfo<?>results : result)
			 {
				 res.add((isComplexInfo<V>) results);
			 }
			 
			 return res;
		}
		else {
		
			return null ; //comment
	}
		

}
	
	
	public isComplexInfo<V> forRange(V lower,	V upper, V inc){
		//FIXME do something proper here
		List<Integer> scalarShape = new ArrayList<Integer>(2);
		scalarShape.add(1);
		scalarShape.add(1);
		if (inc != null){
			return (new isComplexInfoFactory<V>()).newisComplexInfoFromStr("REAL");
		} else {
			return (new isComplexInfoFactory<V>()).newisComplexInfoFromStr("REAL");
		}
    }
}
