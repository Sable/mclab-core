package natlab.Static.valueanalysis.value;

import natlab.Static.classes.MatlabClass;


/**
 * A possible value a variable can take on. This can be
 * - a primitive datum (integer, float, logical, string)
 * - a function handle
 * - a composite value (struct, cell, object)
 * 
 * TODO: There should be an ExtendedAbstractValue with more info
 * 
 * @author ant6n
 */

public interface AbstractValue {
    public MatlabClass getMatlabClass();
}
