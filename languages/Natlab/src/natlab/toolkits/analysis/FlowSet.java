package natlab.toolkits.analysis;

import java.util.*;

public interface FlowSet<D> {


    /**
     * Clones the current FlowSet.
     */
    public FlowSet<D> clone();

    /** 
     * returns an empty set, most often more efficient than:
     * <code>((FlowSet)clone()).clear()</code>
     */
    public FlowSet<D> emptySet();
    
    /**
     * Copies the current FlowSet into dest.
     */
    public void copy(FlowSet<? super D> dest);
    
    /** 
     * Sets this FlowSet to the empty set (more generally, the bottom element
     * of the lattice.) */
    public void clear();
    
    /**
     * Returns the union (join) of this FlowSet and <code>other</code>, putting
     * result into <code>this</code>. */
    public void union(FlowSet<? extends D> other);
    
    /** 
     * Returns the union (join) of this FlowSet and <code>other</code>, putting
     * result into <code>dest</code>. <code>dest</code>, <code>other</code> and
     * <code>this</code> could be the same object.
     */
    public void union(FlowSet<? extends D> other, FlowSet<? super D> dest);
    
    /**
     * Returns the intersection (meet) of this FlowSet and <code>other</code>,
     * putting result into <code>this</code>.
     */
    public void intersection(FlowSet<? extends D> other);
    
    /**
     * Returns the intersection (meet) of this FlowSet and <code>other</code>,
     * putting result into <code>dest</code>. <code>dest</code>,
     * <code>other</code> and <code>this</code> could be the same object.
     */
    public void intersection(FlowSet<? extends D> other, FlowSet<? super D> dest);
    
    /** 
     * Returns the set difference (this intersect ~other) of this FlowSet and
     * <code>other</code>, putting result into <code>this</code>.
     */
    public void difference(FlowSet<? extends D> other);
    
    /**
     * Returns the set difference (this intersect ~other) of this FlowSet and 
     * <code>other</code>, putting result into <code>dest</code>.
     * <code>dest</code>, <code>other</code> and <code>this</code> could be the
     * same object.
     */
    public void difference(FlowSet<? extends D> other, FlowSet<? super D> dest);
    
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
     * puts <code>this</code> union <code>obj</code> into <code>dest</code>.
     */
    public void add(D obj, FlowSet<? super D> dest);
    
    /**
     * Removes <code>obj</code> from <code>this</code>.
     *
     * @return success or failure of removal
     */
    public boolean remove(D obj);
    
    /**
     * Puts <code>this</code> minus <code>obj</code> into <code>dest</code>.
     *
     * @return success or failure of removal
     */
    public boolean remove(D obj, FlowSet<? super D> dest);
    
    /**
     * Returns true if this FlowSet contains <code>obj</code>.
     */
    public boolean contains(D obj);
    
    /**
     * returns an iterator over the elements of the flowSet. Note that the
     * iterator might be backed, and hence be faster in the creation, than doing
     * <code>toList().iterator()</code>.
     */
    public Iterator<D> iterator();
    
    /**
     * Returns an unbacked list of contained objects for this FlowSet.
     */
    public List<D> toList();
    
}