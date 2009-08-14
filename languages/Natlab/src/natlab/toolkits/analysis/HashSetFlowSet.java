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
     * Clones the underlying HashSet
     */
    public HashSetFlowSet<D> clone()
    {
        HashSet<D> cloneSet = (HashSet<D>)set.clone();
        return new HashSetFlowSet( cloneSet );
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
    public void add(D obj)
    {
        set.add(obj);
    }
    public boolean remove( D obj )
    {
        return set.remove( obj );
    }
    public boolean contains( D obj )
    {
        return set.contains( obj );
    }
    public List<D> toList()
    {
        List<D> list = new ArrayList( set.size() );
        for( D i : set )
            list.add( i );
        return list;
    }
}