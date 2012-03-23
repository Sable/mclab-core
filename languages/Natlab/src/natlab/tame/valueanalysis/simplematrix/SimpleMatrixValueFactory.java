/**
 * 
 */
package natlab.tame.valueanalysis.simplematrix;

import natlab.tame.classes.reference.ClassReference;
import natlab.tame.valueanalysis.constant.Constant;
import natlab.tame.valueanalysis.value.ValueFactory;

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