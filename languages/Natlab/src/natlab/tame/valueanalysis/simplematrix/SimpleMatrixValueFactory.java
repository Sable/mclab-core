/**
 * 
 */
package natlab.tame.valueanalysis.simplematrix;

import natlab.tame.valueanalysis.aggrvalue.*;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.components.mclass.ClassPropagator;

public class SimpleMatrixValueFactory extends AggrValueFactory<SimpleMatrixValue>{
    @Override
    public SimpleMatrixValue newMatrixValue(Constant constant) {
    	return new SimpleMatrixValue(constant);
    }
    

    static AggrValuePropagator<SimpleMatrixValue> propagator = 
    		new AggrValuePropagator<SimpleMatrixValue>(new SimpleMatrixValuePropagator());
    @Override
    public AggrValuePropagator<SimpleMatrixValue> getValuePropagator() {
        return propagator;
    }
    
	@SuppressWarnings("unchecked")
	static ClassPropagator<AggrValue<SimpleMatrixValue>> classPropagator = ClassPropagator.getInstance();
	@Override
	public AggrValue<SimpleMatrixValue> forRange(
			AggrValue<SimpleMatrixValue> lower,
			AggrValue<SimpleMatrixValue> upper, AggrValue<SimpleMatrixValue> inc) {
		return new SimpleMatrixValue(classPropagator.forRange(lower, upper, inc));
	}
}


