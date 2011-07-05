package natlab.toolkits.analysis;

import java.util.*;


/**
 * Represents mergable objects. It is understood that if a class
 * {@code C} implements this interface, then it must implement {@code
 * Mergable<C>}.
 *
 * @author Jesse Doherty
 */
public interface Mergable<E>
{
    /**
     * Merges the given object with {@code this} returning the
     * result. This should have no side effects.
     *
     * @return the result of merging {@code this} and {@code o}
     */
    public E merge(E o);
}
