package natlab.Static.valueanalysis.constant;

import natlab.Static.classes.*;
import natlab.Static.classes.reference.ClassReference;

/**
 * represents an actual specific Matlab value.
 * Note that it may not be possible to represent all constant values. Even a specific
 * constant may not be able to represent all values - for example a constant for a specific
 * type may only be scalar, etc.
 * 
 * This is also a factory for constants, via the 'get' methods
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
    
    /**
     * returns the union for this and the other constant
     * - i.e. it returns the same constant if this and the other are the same, null otherwise
     */
    public Constant union(Constant other){
        if (this.equals(other)) return this;
        return null;
    }
    
    /* factory method */
    public static Constant get(boolean value){ return new LogicalConstant(value); }
    public static Constant get(double  value){ return new DoubleConstant(value); }
    public static Constant get(String  value){ return new CharConstant(value); }
    
    
}
