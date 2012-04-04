package natlab.tame.valueanalysis.value;

import natlab.tame.valueanalysis.aggrvalue.MatrixValue;

/**
 * Special Values are values that variables cannot actually take on.
 * Example
 * - colon ':' - used to pass colon to indexing operations
 * - error - used to signify that an error occured in a matlab operation
 * 
 * TODO
 * rather than
 *      value
 *     /     \
 *  values..  special value
 * 
 * there should be something above, like
 *     
 *     abstract parent
 *       /     \
 *  value      special value
 * 
 * TODO also for errors?
 * TODO implement methods throwing unsupported exceptions ...
 */
public abstract class SpecialValue<V extends Value<V>> implements Value<V> {

}
