/**
 * 
 */
package natlab.tame.valueanalysis.basicmatrix;

import natlab.tame.valueanalysis.aggrvalue.*;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.value.ValueFactory;

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
	@Override
	public AggrValue<BasicMatrixValue> forRange(
			AggrValue<BasicMatrixValue> lower,
			AggrValue<BasicMatrixValue> upper, AggrValue<BasicMatrixValue> inc) {
		//FIXME do something proper here
		throw new UnsupportedOperationException();
	}
}


