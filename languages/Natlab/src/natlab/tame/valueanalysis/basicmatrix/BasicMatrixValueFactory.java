package natlab.tame.valueanalysis.basicmatrix;

import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.valueanalysis.aggrvalue.*;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.components.mclass.ClassPropagator;
import natlab.tame.valueanalysis.components.shape.*;

/**
 * A factory of BasicMatrixValue,
 * which generate all kinds of BasicMatrixValue based on different input argument.
 */
public class BasicMatrixValueFactory extends AggrValueFactory<BasicMatrixValue> {
	
	@Override
	//factory method 1.
    public BasicMatrixValue newMatrixValue(Constant constant) {
        return new BasicMatrixValue(constant);
    }
    //factory method 2.
	public BasicMatrixValue newMatrixValueFromClassAndShape(
			PrimitiveClassReference aClass,
			Shape<AggrValue<BasicMatrixValue>> shape) {
		return new BasicMatrixValue(aClass, shape);
	}
	//factory method 3.
	public BasicMatrixValue newMatrixValueFromInputShape(
			PrimitiveClassReference aClass, String shapeInfo) {
		return new BasicMatrixValue(aClass, shapeInfo);
	}
	
    static AggrValuePropagator<BasicMatrixValue> propagator = 
    		new AggrValuePropagator<BasicMatrixValue>(new BasicMatrixValuePropagator());
    
	@SuppressWarnings("unchecked")
	static ClassPropagator<AggrValue<BasicMatrixValue>> classPropagator = ClassPropagator.getInstance();
	static ShapePropagator<AggrValue<BasicMatrixValue>> shapePropagator = ShapePropagator.getInstance();
    
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
		return new BasicMatrixValue(classPropagator.forRange(lower, upper, inc),shapePropagator.forRange(lower, upper, inc));
	}
}


