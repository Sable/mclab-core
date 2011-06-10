package natlab.Static.valueanalysis.constant;

import natlab.Static.classes.reference.ClassReference;
import natlab.Static.classes.reference.PrimitiveClassReference;

/**
 * currently a scalar double only.
 * 
 * TODO - should this be a matrix?
 * @author adubra
 */

public class DoubleConstant extends Constant {
    double value;
    
    /**
     * creates a scalar double constant with the given value
     */
    public DoubleConstant(double value){
        this.value = value;
    }
    
    @Override
    public Double getValue() {
        return value;
    }
    
    @Override
    public ClassReference getClassReference() {
        return PrimitiveClassReference.DOUBLE;
    }
    
    
}
