package natlab.toolkits.analysis;

import java.util.*;

/**
 * Implementation of the FlowSet based on HashSets.
 *
 * @see java.util.HashSet
 */
public class HashSetFlowSet<D> extends AbstractFlowSet<D>
{
    protected HashSet<D> set;

    /**
     * Creates a new HashSet.
     */
    public HashSetFlowSet(){
        set = new HashSet<D>();
    }

    /**
     * Takes in a HashSet to use.
     */
    public HashSetFlowSet( HashSet<D> set){
        this.set = set;
    }
    
    /**
     * Copies the underlying HashSet
     */
    public HashSetFlowSet<D> copy()
    {
        HashSet<D> copySet = new HashSet<D>(set);
        return new HashSetFlowSet<D>( copySet );
    }
    
    /**
     * Clears the underlying HashSet
     */
    public void clear()
    {
        set.clear();
    }

    public boolean isEmpty()
    {
        return set.isEmpty();
    }
    public int size()
    {
        return set.size();
    }
    public void addAll(HashSetFlowSet<? extends D> fs)
    {
        set.addAll( fs.set );
    }
    public void addAll( Collection<? extends D> c )
    {
        set.addAll( c );
    }
    public void add(D obj)
    {
        set.add(obj);
    }
    public boolean remove( Object obj )
    {
        return set.remove( obj );
    }
    public boolean contains( Object obj )
    {
        return set.contains( obj );
    }
    public List<D> toList()
    {
        List<D> list = new ArrayList<D>( set.size() );
        for( D i : set )
            list.add( i );
        return list;
    }

    /**
     * Creates a set containing the contents of this flow-set.
     *
     * @return a new Set with the contents of this flow-set
     */
    public Set<D> getSet()
    {
        return new HashSet<D>( this.set );
    }
    public Iterator<D> iterator()
    {
        return set.iterator();
    }
}