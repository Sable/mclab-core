package natlab.tame.valueanalysis.advancedMatrix;

import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.valueanalysis.aggrvalue.AggrValue;
import natlab.tame.valueanalysis.aggrvalue.AggrValueFactory;
import natlab.tame.valueanalysis.aggrvalue.AggrValuePropagator;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.components.isComplex.isComplexInfo;
import natlab.tame.valueanalysis.components.isComplex.isComplexInfoPropagator;
import natlab.tame.valueanalysis.components.mclass.ClassPropagator;
import natlab.tame.valueanalysis.components.rangeValue.RangeValue;
import natlab.tame.valueanalysis.components.rangeValue.RangeValuePropagator;
import natlab.tame.valueanalysis.components.shape.Shape;
import natlab.tame.valueanalysis.components.shape.ShapePropagator;

public class AdvancedMatrixValueFactory extends AggrValueFactory<AdvancedMatrixValue>{
    static boolean Debug = false;
	@Override
    public AdvancedMatrixValue newMatrixValue(String symbolic, Constant constant) {
        return new AdvancedMatrixValue(symbolic, constant);
    }
	
	public AdvancedMatrixValue newMatrixValueFromClassShapeRange(
			String symbolic,
			PrimitiveClassReference aClass,
			Shape<AggrValue<AdvancedMatrixValue>> shape,
			RangeValue<AggrValue<AdvancedMatrixValue>> rangeValue, 
			isComplexInfo<AggrValue<AdvancedMatrixValue>> complex) {
		return new AdvancedMatrixValue(symbolic, aClass, shape, rangeValue, complex);
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
	static RangeValuePropagator<AggrValue<AdvancedMatrixValue>> rangeValuePropagator = RangeValuePropagator.getInstance();
	static isComplexInfoPropagator<AggrValue<AdvancedMatrixValue>> iscomplexinfoPropagator=
			isComplexInfoPropagator.getInstance();
	
	@Override	
	public AggrValue<AdvancedMatrixValue> forRange(
			AggrValue<AdvancedMatrixValue> lower,
			AggrValue<AdvancedMatrixValue> upper, AggrValue<AdvancedMatrixValue> inc) { 
		return new AdvancedMatrixValue(null,
				classPropagator.forRange(lower, upper, inc),
				shapePropagator.forRange(lower, upper, inc),
				rangeValuePropagator.forRange(lower, upper, inc),
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
	