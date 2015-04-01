package natlab.toolkits.analysis;

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
     * result. This should have no side effects. If it is given a
     * object that it cannot merge with, a {@code ClassCastException}
     * must be thrown
     *
     * @return the result of merging {@code this} and {@code o}
     * @throws ClassCastException If the input is not valid for merging
     */
    public E merge(E o);
}
