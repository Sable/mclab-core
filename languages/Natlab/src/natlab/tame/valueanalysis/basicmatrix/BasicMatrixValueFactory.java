/**
 * 
 */
package natlab.tame.valueanalysis.basicmatrix;

import natlab.tame.valueanalysis.aggrvalue.*;
import natlab.tame.valueanalysis.components.constant.Constant;

public class BasicMatrixValueFactory extends AggrValueFactory<BasicMatrixValue>{
    @Override
    public BasicMatrixValue newMatrixValue(Constant constant) {
        return new BasicMatrixValue(constant);
    }
    

    static BasicMatrixValuePropagator propagator = new BasicMatrixValuePropagator();
    @Override
    public BasicMatrixValuePropagator getValuePropagator() {
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


