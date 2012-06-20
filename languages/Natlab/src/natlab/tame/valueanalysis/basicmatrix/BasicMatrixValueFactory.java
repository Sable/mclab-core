/**
 * 
 */
package natlab.tame.valueanalysis.basicmatrix;

import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.valueanalysis.aggrvalue.*;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.components.shape.*;
import natlab.tame.valueanalysis.value.*;

public class BasicMatrixValueFactory extends AggrValueFactory<BasicMatrixValue>{
    static boolean Debug = false;
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
			AggrValue<BasicMatrixValue> upper, AggrValue<BasicMatrixValue> inc) {  //XU try to add shape result into it
		//FIXME do something proper here
		System.out.println("Inside forRange");
		if (inc != null){
			if (Debug) System.out.println("inside forRange "+ ((HasShape)(propagator.call("colon", Args.newInstance(lower,upper))
					.get(0).iterator().next())).getShape());
			return new BasicMatrixValue((new BasicMatrixValue(
					(PrimitiveClassReference)
					(propagator.call("colon", Args.newInstance(lower,upper))
							.get(0).iterator().next().getMatlabClass()))),((HasShape)(propagator.call("colon", Args.newInstance(lower,upper))
									.get(0).iterator().next())).getShape());
		} else {
			if (Debug) System.out.println("inside forRange "+ ((HasShape)(propagator.call("colon", Args.newInstance(lower,upper))
					.get(0).iterator().next())).getShape());
			return new BasicMatrixValue((new BasicMatrixValue(
					(PrimitiveClassReference)
					(propagator.call("colon", Args.newInstance(lower,upper))
							.get(0).iterator().next().getMatlabClass()))),((HasShape)(propagator.call("colon", Args.newInstance(lower,upper))
									.get(0).iterator().next())).getShape());

		}
	}
}


