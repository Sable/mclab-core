package natlab.Static.valueanalysis.value;

import natlab.Static.classes.reference.*;


/**
 * A possible value a variable can take on. This can be
 * - a primitive datum (integer, float, logical, string)
 * - a function handle
 * - a composite value (struct, cell, object)
 * 
 * TODO: There should be an ExtendedAbstractValue with more info
 * TODO: How to deal with abstract values that store different things 
 *       - probably more 'precise' abstract values are such because of more advanced 'primitive values'
 * TODO: should these be interned?
 * 
 * TODO: what operations should these support?
 * TODO: should this be iterned?
 * 
 * @author ant6n
 */

public interface AbstractValue {
    public ClassReference getMatlabClass();
    
    /**
     * returns a new abstract value, which is the union of this and the other abstract value with the same class
     * returns null if the other value does not have the same class
     */
    public AbstractValue union(AbstractValue other);
}
