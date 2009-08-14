package natlab.toolkits.analysis;
/**
 * Adapted from soot.toolkits.scalar.AbstractFlowSet.java
 */


import java.util.*;

/**
 * Provides functional code for most of the methods. Subclasses are invited to
 * provide a more efficient version. Most often this will be done in the
 * following way:<br>
 * 
 * <pre>
 * public void yyy(FlowSet dest) {
 *   if (dest instanceof xxx) {
 *     blahblah;
 *   } else
 *     super.yyy(dest)
 * }
 * </pre>
 */
public abstract class AbstractFlowSet<D> implements FlowSet<D>
{
    public abstract AbstractFlowSet<D> clone();
    
    /**
     * implemented, but inefficient.
     */
    public AbstractFlowSet<D> emptySet() {
        AbstractFlowSet<D> t = clone();
        t.clear();
        return t;
    }
    
    public void copy(FlowSet<? super D> dest) {
        List<D> elements = toList();
        Iterator<D> it = elements.iterator();
        dest.clear();
        while (it.hasNext())
            dest.add(it.next());
    }
    
    /**
     * implemented, but *very* inefficient.
     */
    public void clear() {
        Iterator<D> it = toList().iterator();
        while (it.hasNext())
            remove(it.next());
    }
    
    public void union(FlowSet<? extends D> other) {
        union(other, this);
    }
    
    public void union(FlowSet<? extends D> other, FlowSet<? super D> dest) {
        if (dest != this && dest != other)
            dest.clear();
        
        if (dest != this) {
            Iterator<D> thisIt = toList().iterator();
            while (thisIt.hasNext())
                dest.add(thisIt.next());
        }
        
        if (dest != other) {
            Iterator otherIt = other.toList().iterator();
            while (otherIt.hasNext())
                dest.add((D)otherIt.next());
        }
    }
    
    public void intersection(FlowSet<? extends D> other) {
        intersection(other, this);
    }
    
    public void intersection(FlowSet<? extends D> other, FlowSet<? super D> dest) {
        if (dest == this && dest == other)
            return;
        List elements = null;
        FlowSet flowSet = null;
        if (dest == this) {
            /*
             * makes automaticly a copy of <code>this</code>, as it will be
             * cleared
             */
            elements = toList();
            flowSet = other;
        } else {
            /* makes a copy o <code>other</code>, as it might be cleared */
            elements = other.toList();
            flowSet = this;
        }
        dest.clear();
        Iterator it = elements.iterator();
        while (it.hasNext()) {
            Object o = it.next();
            if (flowSet.contains(o))
                dest.add((D)o);
        }
    }
    
    public void difference(FlowSet<? extends D> other) {
        difference(other, this);
    }
    
    public void difference(FlowSet<? extends D> other, FlowSet<? super D> dest) {
        if (dest == this && dest == other) {
            dest.clear();
            return;
        }
        
        Iterator<D> it = this.toList().iterator();
        FlowSet flowSet = (other == dest) ? other.clone() : other;
        dest.clear(); // now safe, since we have copies of this & other
        
        while (it.hasNext()) {
            D o = it.next();
            if (!flowSet.contains(o))
                dest.add(o);
        }
    }
    
    public abstract boolean isEmpty();
    
    public abstract int size();
    
    public abstract void add(D obj);
    
    public void add(D obj, FlowSet<? super D> dest) {
        if (dest != this)
            copy(dest);
        dest.add(obj);
    }
    
    public abstract boolean remove(D obj);
    
    public boolean remove(D obj, FlowSet<? super D> dest) {
        if (dest != this)
            copy(dest);
        return dest.remove(obj);
    }
    
    public abstract boolean contains(D obj);
    
    public Iterator<D> iterator() {
        return toList().iterator();
    }
    
    public abstract List<D> toList();
    
    public boolean equals(Object o) {
        if (!(o instanceof FlowSet))
            return false;
        FlowSet other = (FlowSet) o;
        if (size() != other.size())
            return false;
        for( D i : toList() )
            if( ! other.contains( i ) )
                return false;
        return true;
    }
    
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        for( D i : toList() )
            result = PRIME * result + i.hashCode();
        return result;
    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer("{");
        boolean first = true;
        for( D i : toList() ){
            if( first ){
                buffer.append( i );
                first = false;
            }
            else
                buffer.append(", " + i);
        }
        buffer.append("}");
        return buffer.toString();
    }
}
