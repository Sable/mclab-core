/**
 * 
 */
package natlab.tame.valueanalysis.simplematrix;

import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.valueanalysis.aggrvalue.*;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.value.*;

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
	@Override
	public AggrValue<SimpleMatrixValue> forRange(
			AggrValue<SimpleMatrixValue> lower,
			AggrValue<SimpleMatrixValue> upper, AggrValue<SimpleMatrixValue> inc) {
		//FIXME do something proper here
		if (inc != null){
			return new SimpleMatrixValue(
					(PrimitiveClassReference)
					(propagator.call("colon", Args.newInstance(lower,inc,upper))
							.get(0).iterator().next().getMatlabClass()));
		} else {
			return new SimpleMatrixValue(
					(PrimitiveClassReference)
					(propagator.call("colon", Args.newInstance(lower,upper))
							.get(0).iterator().next().getMatlabClass()));

		}
	}
}


