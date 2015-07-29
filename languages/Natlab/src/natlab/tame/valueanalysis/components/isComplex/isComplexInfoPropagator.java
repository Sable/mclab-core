package natlab.tame.valueanalysis.components.isComplex;

import java.util.ArrayList;
import java.util.List;

import natlab.tame.builtin.Builtin;
import natlab.tame.builtin.BuiltinVisitor;
import natlab.tame.builtin.isComplexInfoProp.HasisComplexPropagationInfo;
import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropTool;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class isComplexInfoPropagator<V extends Value<V>> extends BuiltinVisitor<Args<V>, 
List<isComplexInfo>>{
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
	public List<isComplexInfo> caseBuiltin(Builtin builtin, Args<V> arg) {
		// TODO base case -- use whatever the builtin's complex propagation info provides
		//num is the number of LHS arguments in the tame IR node
		if(builtin instanceof HasisComplexPropagationInfo){
			//call shape prop tool
			List<isComplexInfo> result =  isComplexInfoPropTool.matchByValues(((HasisComplexPropagationInfo)builtin).getisComplexPropagationInfo(),arg);
			 List<isComplexInfo> res= new ArrayList<isComplexInfo>();
			 for (isComplexInfo results : result)
			 {
				 res.add((isComplexInfo) results);
			 }
			 
			 return res;
		}
		else {
			 List<isComplexInfo> res= new ArrayList<isComplexInfo>();
			 res.add((new isComplexInfoFactory()).newisComplexInfoFromStr("REAL"));
			return res;
			 //			 return null ; //comment
	}
		

}
	
	
	public isComplexInfo forRange(V lower,	V upper, V inc){
		//FIXME do something proper here
		List<Integer> scalarShape = new ArrayList<Integer>(2);
		scalarShape.add(1);
		scalarShape.add(1);
		if (inc != null){
			return (new isComplexInfoFactory()).newisComplexInfoFromStr("REAL");
		} else {
			return (new isComplexInfoFactory()).newisComplexInfoFromStr("REAL");
		}
    }
}
