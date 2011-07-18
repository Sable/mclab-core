package natlab.Static.valueanalysis.value;

import java.util.*;
import natlab.Static.classes.reference.*;
import natlab.toolkits.analysis.Mergable;


/**
 * A possible value a variable can take on. This can be
 * - a primitive datum (integer, float, logical, string)
 * - a function handle
 * - a composite value (struct, cell, object)
 * 
 * TODO: How to deal with abstract values that store different things 
 *       - probably more 'precise' abstract values are such because of more advanced 'primitive values'
 * TODO: should these be interned?
 * 
 * TODO: what operations should these support?
 * TODO: should this be iterned?
 * TODO: maybe we should have a special 'colon' value representing ':'
 * 
 * @author ant6n
 */

public interface Value<D extends MatrixValue<D>> extends Mergable<Value<D>>{
    public ClassReference getMatlabClass();
    
    /**
     * performs an indexing operation
     */
    public Value<D> subsref(List<Value<D>> indizes);
    
}




