package natlab.tame.valueanalysis.advancedMatrix;

import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.valueanalysis.aggrvalue.*;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.components.isComplex.*;
import natlab.tame.valueanalysis.components.mclass.ClassPropagator;
import natlab.tame.valueanalysis.components.shape.*;
import natlab.tame.valueanalysis.value.*;

public class AdvancedMatrixValueFactory extends AggrValueFactory<AdvancedMatrixValue>{
    static boolean Debug = false;
	@Override
    public AdvancedMatrixValue newMatrixValue(String symbolic, Constant constant) {
        return new AdvancedMatrixValue(symbolic, constant);
    }
    
    static AggrValuePropagator<AdvancedMatrixValue> propagator = 
    		new AggrValuePropagator<AdvancedMatrixValue>(new AdvancedMatrixValuePropagator());
    @Override
    public AggrValuePropagator<AdvancedMatrixValue> getValuePropagator() {
        return propagator;
    }
    
	@SuppressWarnings("unchecked")
	static ClassPropagator<AggrValue<AdvancedMatrixValue>> classPropagator = ClassPropagator.getInstance();
	static ShapePropagator<AggrValue<AdvancedMatrixValue>> shapePropagator = ShapePropagator.getInstance();
	static isComplexInfoPropagator<AggrValue<AdvancedMatrixValue>> iscomplexinfoPropagator=
			isComplexInfoPropagator.getInstance();
	
	@Override	
	public AggrValue<AdvancedMatrixValue> forRange(
			AggrValue<AdvancedMatrixValue> lower,
			AggrValue<AdvancedMatrixValue> upper, AggrValue<AdvancedMatrixValue> inc) { 
		return new AdvancedMatrixValue(null,
				classPropagator.forRange(lower, upper, inc),shapePropagator.forRange(lower, upper, inc),null,
				iscomplexinfoPropagator.forRange(lower, upper, inc));
	}
	

//	public AggrValue<AdvancedMatrixValue> forRange(
//			AggrValue<AdvancedMatrixValue> lower,
//			AggrValue<AdvancedMatrixValue> upper, AggrValue<AdvancedMatrixValue> inc) {  
//		System.out.println("Inside forRange");
//		//FIXME do something proper here
//		if (inc != null){//FIXME
//			
//			return new AdvancedMatrixValue((new AdvancedMatrixValue(
//					(PrimitiveClassReference)
//					(propagator.call("colon", Args.newInstance(lower,inc,upper))
//							.get(0).iterator().next().getMatlabClass()))),((HasisComplexInfo)(propagator.call("colon", Args.newInstance(lower,upper))
//									.get(0).iterator().next())).getisComplexInfo());
//		} else {
//			
//			return new AdvancedMatrixValue((new AdvancedMatrixValue(
//					(PrimitiveClassReference)
//					(propagator.call("colon", Args.newInstance(lower,upper))
//							.get(0).iterator().next().getMatlabClass()))),((HasisComplexInfo)(propagator.call("colon", Args.newInstance(lower,upper))
//									.get(0).iterator().next())).getisComplexInfo());
//
//		}
//	}
}
	