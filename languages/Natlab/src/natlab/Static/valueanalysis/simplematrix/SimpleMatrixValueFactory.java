/**
 * 
 */
package natlab.Static.valueanalysis.simplematrix;

import natlab.Static.valueanalysis.constant.Constant;
import natlab.Static.valueanalysis.value.MatrixValueFactory;

public class SimpleMatrixValueFactory extends MatrixValueFactory<SimpleMatrixValue>{
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