package natlab.Static.valueanalysis.value;

import natlab.Static.classes.reference.PrimitiveClassReference;
import natlab.Static.valueanalysis.constant.*;

/**
 * represents a primitive value, i.e. a value that has a primitive type.
 * At this level this class stores a matlab class, and a constant.
 * @author adubra
 */


public class PrimitiveValue implements AbstractValue {
    PrimitiveClassReference classRef;
    Constant constant;
    
    @Override
    public PrimitiveClassReference getMatlabClass() {
        return classRef;
    }

    
    @Override
    public AbstractValue union(AbstractValue other) {
        if (!other.getClass().equals(classRef)) return null;
        return null; //TODO
    }
}
