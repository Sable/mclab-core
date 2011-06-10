package natlab.Static.valueanalysis.constant;

import natlab.Static.classes.reference.ClassReference;
import natlab.Static.classes.reference.PrimitiveClassReference;

/**
 * currently a scalar logical constant only.
 *
 * @author adubra
 */
public class LogicalConstant extends Constant {
    private boolean value;
    
    public LogicalConstant(boolean value) {
        this.value = value;
    }
    
    @Override
    public ClassReference getClassReference() {
        return PrimitiveClassReference.LOGICAL;
    }
    
    @Override
    public Boolean getValue() {
        return value;
    }
}
