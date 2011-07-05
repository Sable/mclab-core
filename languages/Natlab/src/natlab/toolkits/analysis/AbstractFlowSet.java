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


    @Override public AbstractFlowSet<D> copy()
    {
        AbstractFlowSet<D> theCopy = emptySet();
        copy( theCopy );
        return theCopy;
    }
    
    
    abstract public AbstractFlowSet<D> emptySet();
    /*public AbstractFlowSet<D> emptySet() {
        AbstractFlowSet<D> t = copy();
        t.clear();
        return t;
        }*/
    
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
        List<D> data = new ArrayList<D>(size());
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
     * @throws IllegalArgumentException   when any argument is null
     */    
    public void union(FlowSet<? extends D> other, FlowSet<? super D> dest) {
        if( other == null )
            throw new IllegalArgumentException( "Null was given as other set for union." );
        if( dest == null )
            throw new IllegalArgumentException( "Null was given as dest set for union." );

        if (dest != this && dest != other)
            dest.clear();
        
        if (dest != this) {
            Iterator<D> thisIt = iterator();
            while (thisIt.hasNext())
                dest.add(thisIt.next());
        }
        
        if (dest != other) {
            Iterator otherIt = other.iterator();
            while (otherIt.hasNext()){
                //otherIt is iterator from other
                //other contains things that extend from D
                //the elements can be safely upcast to D
                @SuppressWarnings("unchecked")
                    D elmnt = (D)otherIt.next();
                dest.add(elmnt);
            }
        }
    }
    
    public void intersection(FlowSet<? extends D> other) {
        intersection(other, this);
    }
    /**
     * @param other    set being intersected with this
     * @param dest     set where the result is put
     * @throws IllegalArgumentException   when any argument is null
     */
    public void intersection(FlowSet<? extends D> other, FlowSet<? super D> dest) {
        if( other == null )
            throw new IllegalArgumentException( "Null was given as other set for intersection." );
        if( dest == null )
            throw new IllegalArgumentException( "Null was given as dest set for intersection." );

        if (dest == this && dest == other)
            return;
        else if( other == this ){
            dest.clear();
            for( D i : this )
                dest.add( i );
        }
        else if( dest == this ){
            List<D> toRemove = new LinkedList<D>();
            for( D i : this )
                if( other.contains( i ) )
                    toRemove.add(i);
            for( D i : toRemove )
                dest.remove(i);
        }
        else if( dest == other ){
            List<D> toRemove = new LinkedList<D>();
            for( D i : other )
                if( this.contains( i ) )
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
     * @throws IllegalArgumentException   when any argument is null
     */
    public void difference(FlowSet<? extends D> other, FlowSet<? super D> dest) {
        if( other == null )
            throw new IllegalArgumentException( "Null was given as other set for difference." );
        if( dest == null )
            throw new IllegalArgumentException( "Null was given as dest set for difference." );

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
    
    /*public void add(D obj, FlowSet<? super D> dest) {
        if (dest != this)
            copy(dest);
        dest.add(obj);
    }*/
    
    @Override
    public abstract boolean remove(Object obj);
    
    /*public boolean remove(D obj, FlowSet<? super D> dest) {
        if (dest != this)
            copy(dest);
        return dest.remove(obj);
        }*/
    
    public abstract boolean contains(Object obj);
    
    public abstract Iterator<D> iterator();
    
    //public abstract List<D> toList();
    
    /**
     * Assumes that if o is a FlowSet, it's contains method does
     * nothing destructive, Even if it throws an exception.
     */
    public boolean equals(Object o) {
        if (!(o instanceof FlowSet))
            return false;
        FlowSet other = (FlowSet) o;
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
    
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        for( D i : this ){
            result = PRIME * result + i.hashCode();
        }
        return result;
    }
    
    public String toString() {
        StringBuffer buffer = new StringBuffer("{");
        boolean first = true;
        for( D i : this ){
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
