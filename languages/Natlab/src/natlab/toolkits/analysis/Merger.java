package natlab.toolkits.analysis;

import java.util.*;


/**
 * @author Jesse Doherty
 */
public interface Merger<E>
{
    public E merge(E o1, E o2);
}
