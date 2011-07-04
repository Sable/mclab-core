package natlab.toolkits.analysis;

import java.util.*;


/**
 * @author Jesse Doherty
 */
public interface Mergable<E>
{
    public E merge(E o);
}
