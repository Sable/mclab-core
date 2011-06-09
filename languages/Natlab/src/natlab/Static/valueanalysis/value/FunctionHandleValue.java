package natlab.Static.valueanalysis.value;

import natlab.Static.classes.reference.FunctionHandleClassReference;

public class FunctionHandleValue implements AbstractValue {

    @Override
    public FunctionHandleClassReference getMatlabClass() {
        return new FunctionHandleClassReference();
    }

}
