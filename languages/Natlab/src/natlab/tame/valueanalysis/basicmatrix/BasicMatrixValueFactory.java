package natlab.tame.valueanalysis.basicmatrix;

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

/**
 * A factory of BasicMatrixValue,
 * which generate all kinds of BasicMatrixValue based on different input argument.
 */
public class BasicMatrixValueFactory extends AggrValueFactory<BasicMatrixValue> {
	
	@Override
	//factory method 1.
    public BasicMatrixValue newMatrixValue(String symbolic, Constant constant) {
        return new BasicMatrixValue(symbolic, constant);
    }
    //factory method 2.
	public BasicMatrixValue newMatrixValueFromClassShapeRange(
			String symbolic,
			PrimitiveClassReference aClass,
			Shape<AggrValue<BasicMatrixValue>> shape,
			RangeValue<AggrValue<BasicMatrixValue>> rangeValue, 
			isComplexInfo<AggrValue<BasicMatrixValue>> complex) {
		return new BasicMatrixValue(symbolic, aClass, shape, rangeValue, complex);
	}
	//factory method 3.
	public BasicMatrixValue newMatrixValueFromInputShape(
			String symbolic, PrimitiveClassReference aClass, String shapeInfo, String complexInfo) {
		return new BasicMatrixValue(symbolic, aClass, shapeInfo, complexInfo);
	}
	
    static AggrValuePropagator<BasicMatrixValue> propagator = 
    		new AggrValuePropagator<BasicMatrixValue>(new BasicMatrixValuePropagator());
    
	@SuppressWarnings("unchecked")
	static ClassPropagator<AggrValue<BasicMatrixValue>> classPropagator = ClassPropagator.getInstance();
	static ShapePropagator<AggrValue<BasicMatrixValue>> shapePropagator = ShapePropagator.getInstance();
	static RangeValuePropagator<AggrValue<BasicMatrixValue>> rangeValuePropagator = RangeValuePropagator.getInstance();
	static isComplexInfoPropagator<AggrValue<BasicMatrixValue>> iscomplexinfoPropagator=
			isComplexInfoPropagator.getInstance();
	
    @Override
    public AggrValuePropagator<BasicMatrixValue> getValuePropagator() {
        return propagator;
    }
	
	@Override
	public AggrValue<BasicMatrixValue> forRange(
			AggrValue<BasicMatrixValue> lower,
			AggrValue<BasicMatrixValue> upper,
			AggrValue<BasicMatrixValue> inc) {
		//FIXME do something proper here
		return new BasicMatrixValue(null
				, classPropagator.forRange(lower, upper, inc)
				,shapePropagator.forRange(lower, upper, inc)
				, rangeValuePropagator.forRange(lower, upper, inc)
				, iscomplexinfoPropagator.forRange(lower, upper, inc));
	}
}


