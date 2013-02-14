package natlab.toolkits.analysis;

import java.util.Iterator;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

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
    @Override public AbstractFlowSet<D> copy()
    {
        AbstractFlowSet<D> theCopy = emptySet();
        copy( theCopy );
        return theCopy;
    }
    
    abstract public AbstractFlowSet<D> emptySet();
    
    public void copy(FlowSet<? super D> dest) {
    	if (this == dest) return;
        dest.clear();
        for (D element : this){
        	dest.add(element);
        }
    }
    
    /**
     * implemented, but *very* inefficient.
     */
    public void clear() {
        List<D> data = Lists.newArrayListWithCapacity(size());
        for( D i : this )
            data.add(i);
        for( D i : data )
            remove( i );
    }
    
    public void union(FlowSet<? extends D> other) {
        union(other, this);
    }

    /**
     * @param other    set being unioned with this
     * @param dest     set where the result is put
     * @throws NullPointerException   when any argument is null
     */    
    public void union(FlowSet<? extends D> other, FlowSet<? super D> dest) {
        Preconditions.checkNotNull(other);
        Preconditions.checkNotNull(dest);

        if (dest != this && dest != other)
            dest.clear();
        
        if (dest != this) {
            Iterator<D> thisIt = iterator();
            while (thisIt.hasNext())
                dest.add(thisIt.next());
        }
        
        if (dest != other) {
            Iterator<? extends D> otherIt = other.iterator();
            while (otherIt.hasNext()){
                dest.add((D) otherIt.next());
            }
        }
    }
    
    public void intersection(FlowSet<? extends D> other) {
        intersection(other, this);
    }
    /**
     * @param other    set being intersected with this
     * @param dest     set where the result is put
     * @throws NullPointerException   when any argument is null
     */
    public void intersection(FlowSet<? extends D> other, FlowSet<? super D> dest) {
        Preconditions.checkNotNull(other);
        Preconditions.checkNotNull(dest);

        if (dest == this && dest == other)
            return;
        else if( other == this ){
            dest.clear();
            for( D i : this )
                dest.add( i );
        }
        else if( dest == this ){
            List<D> toRemove = Lists.newLinkedList();
            for( D i : this )
                if( !other.contains( i ) )
                    toRemove.add(i);
            for( D i : toRemove )
                dest.remove(i);
        }
        else if( dest == other ){
            List<D> toRemove = Lists.newLinkedList();
            for( D i : other )
                if( !this.contains( i ) )
                    toRemove.add(i);
            for( D i : toRemove )
                dest.remove(i);
        }
        else{
            dest.clear();
            for( D i : this )
                if( other.contains( i ) )
                    dest.add( i );
        }
    }
    
    public void difference(FlowSet<? extends D> other) {
        difference(other, this);
    }
    
    /**
     * Takes the set difference removing {@code other} is removed from
     * {@code this}. The result is placed into {@code dest}. The
     * {@code dest} set will contain only the result. All arguments
     * must be non-null
     *
     * @param other    set being removed from this
     * @param dest     set where the result is put
     * @throws NullPointerException   when any argument is null
     */
    public void difference(FlowSet<? extends D> other, FlowSet<? super D> dest) {
        Preconditions.checkNotNull(other);
        Preconditions.checkNotNull(dest);

        if (dest == this && dest == other) {
            dest.clear();
            return;
        }
        else if( other == this ){
            dest.clear();
            return;
        }
        else if( dest == this ){
            for( D i : other )
                dest.remove( i );
            return;
        }
        else if( other == dest )
            other = other.copy();

        dest.clear();
        for( D i : this ){
            if( !other.contains( i ) )
                dest.add( i );
        }
    }
    
    public boolean isEmpty()
    {
        return size() == 0;
    }
    
    public abstract int size();
    
    public abstract void add(D obj);

    @Override
    public abstract boolean remove(Object obj);
    
    public abstract boolean contains(Object obj);
    
    public abstract Iterator<D> iterator();
    
    /**
     * Assumes that if o is a FlowSet, it's contains method does
     * nothing destructive, Even if it throws an exception.
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FlowSet))
            return false;
        FlowSet<?> other = (FlowSet<?>) o;
        if (size() != other.size())
            return false;
        for( D i : this ){
            //Assume the contains method does nothing destructive
            //That way, if other does not contain things of type D,
            //then catch any exception return false
            try{
                @SuppressWarnings("unchecked")
                    FlowSet<D> otherD = (FlowSet<D>)other;
                if( ! otherD.contains( i ) )
                    return false;
            }catch( Exception E ){
                return false;
            }
        }
        return true;
    }
    
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        for( D i : this ){
            result = PRIME * result + i.hashCode();
        }
        return result;
    }
    
    @Override
    public String toString() {
        return String.format("{%s}", Joiner.on(", ").join(this));
    }
}
