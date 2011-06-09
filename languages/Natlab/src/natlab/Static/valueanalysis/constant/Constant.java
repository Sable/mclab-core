package natlab.Static.valueanalysis.constant;

import natlab.Static.classes.*;
import natlab.Static.classes.reference.ClassReference;

/**
 * represents an actual specific Matlab value.
 * Note that it may not be possible to represent all constant values. Even a specific
 * constant may not be able to represent all values - for example a constant for a specific
 * type may only be scalar, etc.
 * 
 * @author ant6n
 */

public abstract class Constant {

    /**
     * returns the underlying value of the constant
     */
    public abstract Object getValue();
    
    /**
     * returns the Matlab class of the constant
     */
    public abstract ClassReference getClassReference();
}
