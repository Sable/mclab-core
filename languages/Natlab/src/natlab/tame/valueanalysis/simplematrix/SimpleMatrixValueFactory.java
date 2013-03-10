package natlab.tame.valueanalysis.simplematrix;

import natlab.tame.valueanalysis.aggrvalue.*;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.components.mclass.ClassPropagator;

/**
 * 
 * @author ant6n
 *
 * extended by XU to support symbolic @ 8:38pm March 9th 2013.
 */
public class SimpleMatrixValueFactory extends AggrValueFactory<SimpleMatrixValue>{
    @Override
    public SimpleMatrixValue newMatrixValue(String symbolic, Constant constant) {
    	return new SimpleMatrixValue(symbolic, constant);
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
		return new SimpleMatrixValue(null, classPropagator.forRange(lower, upper, inc));
	}
}


