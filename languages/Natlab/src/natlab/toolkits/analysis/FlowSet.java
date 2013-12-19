package natlab.toolkits.analysis;

import java.util.Iterator;

/**
 * A representation of flow data that is naturally represented as a
 * set. 
 *
 * @author Jesse Doherty
 */
@Deprecated
public interface FlowSet<D> extends Iterable<D>
{
    /**
     * Copies the flow set and returns the new copy.
     * This method must return the same type as the flow set being
     * copied. E.g. if a class A implements FlowSet, it's copy method
     * must return something of type A.
     */
    public FlowSet<D> copy();
    
    /**
     * Copies the current FlowSet into dest.
     */
    public void copy(FlowSet<? super D> dest);
    
    /** 
     * Sets this FlowSet to the empty set (more generally, the bottom element
     * of the lattice.) */
    public void clear();
    
    /**
     * Returns true if this FlowSet is the empty set.
     */
    public boolean isEmpty();
    
    /* The following methods force the FlowSet to be a powerset. */
    
    /**
     * Returns the size of the current FlowSet.
     */
    public int size();
    
    /**
     * Adds <code>obj</code> to <code>this</code>.
     */
    public void add(D obj);
    
    /**
     * Removes <code>obj</code> from <code>this</code>.
     *
     * @return success or failure of removal
     */
    public boolean remove(Object obj);
    
    /**
     * Returns true if this FlowSet contains <code>obj</code>.
     */
    public boolean contains(Object obj);
    
    /**
     * Returns an iterator over the elements of the flowSet. Iterator
     * must implement the <code>remove</code> method.
     */
    public Iterator<D> iterator();
}