/**
 * 
 */
package natlab.Static.valueanalysis.simplematrix;

import natlab.Static.classes.reference.ClassReference;
import natlab.Static.valueanalysis.constant.Constant;
import natlab.Static.valueanalysis.value.ValueFactory;

public class SimpleMatrixValueFactory extends ValueFactory<SimpleMatrixValue>{
    @Override
    public SimpleMatrixValue newMatrixValue(Constant constant) {
        return new SimpleMatrixValue(constant);
    }
    

    static SimpleMatrixValuePropagator propagator = new SimpleMatrixValuePropagator();
    @Override
    public SimpleMatrixValuePropagator getValuePropagator() {
        return propagator;
    }
}