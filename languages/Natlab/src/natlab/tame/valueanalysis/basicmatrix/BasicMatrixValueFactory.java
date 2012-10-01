/**
 * 
 */
package natlab.tame.valueanalysis.basicmatrix;

import java.util.ArrayList;
import java.util.List;

import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.valueanalysis.aggrvalue.*;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.components.mclass.ClassPropagator;
import natlab.tame.valueanalysis.components.shape.*;
import natlab.tame.valueanalysis.value.*;

public class BasicMatrixValueFactory extends AggrValueFactory<BasicMatrixValue>{
	@Override
    public BasicMatrixValue newMatrixValue(Constant constant) {
        return new BasicMatrixValue(constant);
    }
    
    static AggrValuePropagator<BasicMatrixValue> propagator = 
    		new AggrValuePropagator<BasicMatrixValue>(new BasicMatrixValuePropagator());
    @Override
    public AggrValuePropagator<BasicMatrixValue> getValuePropagator() {
        return propagator;
    }
    
	@SuppressWarnings("unchecked")
	static ClassPropagator<AggrValue<BasicMatrixValue>> classPropagator = ClassPropagator.getInstance();
	static ShapePropagator<AggrValue<BasicMatrixValue>> shapePropagator = ShapePropagator.getInstance();
	
	@Override
	public AggrValue<BasicMatrixValue> forRange(
			AggrValue<BasicMatrixValue> lower,
			AggrValue<BasicMatrixValue> upper, AggrValue<BasicMatrixValue> inc) {
		//FIXME do something proper here
		return new BasicMatrixValue(classPropagator.forRange(lower, upper, inc),shapePropagator.forRange(lower, upper, inc));
	}
}


