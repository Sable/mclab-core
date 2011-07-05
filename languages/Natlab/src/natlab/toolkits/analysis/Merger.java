package natlab.toolkits.analysis;

import java.util.*;


/**
 * Represents an object that can perform the merge operation between
 * tow objects. A merger object should be immutable.
 *
 * @author Jesse Doherty
 */
public interface Merger<E>
{
    /**
     * Merges two objects and returns the result. This should have no
     * side effects. 
     */
    public E merge(E o1, E o2);
}
