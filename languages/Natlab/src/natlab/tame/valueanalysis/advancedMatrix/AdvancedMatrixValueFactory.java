package natlab.tame.valueanalysis.advancedMatrix;

import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.valueanalysis.aggrvalue.*;
import natlab.tame.valueanalysis.components.constant.Constant;
import natlab.tame.valueanalysis.components.isComplex.HasisComplexInfo;
import natlab.tame.valueanalysis.components.shape.*;
import natlab.tame.valueanalysis.value.*;

public class AdvancedMatrixValueFactory extends AggrValueFactory<AdvancedMatrixValue>{
    static boolean Debug = false;
	@Override
    public AdvancedMatrixValue newMatrixValue(Constant constant) {
        return new AdvancedMatrixValue(constant);
    }
    
    static AggrValuePropagator<AdvancedMatrixValue> propagator = 
    		new AggrValuePropagator<AdvancedMatrixValue>(new AdvancedMatrixValuePropagator());
    @Override
    public AggrValuePropagator<AdvancedMatrixValue> getValuePropagator() {
        return propagator;
    }
    
	@SuppressWarnings("unchecked")
	@Override
	public AggrValue<AdvancedMatrixValue> forRange(
			AggrValue<AdvancedMatrixValue> lower,
			AggrValue<AdvancedMatrixValue> upper, AggrValue<AdvancedMatrixValue> inc) {  
		//FIXME do something proper here
		if (inc != null){//FIXME
			
			return new AdvancedMatrixValue((new AdvancedMatrixValue(
					(PrimitiveClassReference)
					(propagator.call("colon", Args.newInstance(lower,upper))
							.get(0).iterator().next().getMatlabClass()))),((HasisComplexInfo)(propagator.call("colon", Args.newInstance(lower,upper))
									.get(0).iterator().next())).getisComplexInfo());
		} else {
			
			return new AdvancedMatrixValue((new AdvancedMatrixValue(
					(PrimitiveClassReference)
					(propagator.call("colon", Args.newInstance(lower,upper))
							.get(0).iterator().next().getMatlabClass()))),((HasisComplexInfo)(propagator.call("colon", Args.newInstance(lower,upper))
									.get(0).iterator().next())).getisComplexInfo());

		}
	}
}
	