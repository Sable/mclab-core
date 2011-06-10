package natlab.Static.valueanalysis.value;

import natlab.Static.classes.reference.FunctionHandleClassReference;

/**
 * This represents value which refers to a function handle.
 * Stores along with it the possible set of function hanldes that this can refer to.
 * 
 * @author adubra
 *
 */

public class FunctionHandleValue implements AbstractValue {

    @Override
    public FunctionHandleClassReference getMatlabClass() {
        return new FunctionHandleClassReference();
    }
    
    @Override
    public AbstractValue union(AbstractValue other) {
        // TODO Auto-generated method stub
        return null;
    }

}
