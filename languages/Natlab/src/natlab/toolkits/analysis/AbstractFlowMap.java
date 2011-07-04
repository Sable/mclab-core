package natlab.toolkits.analysis;

import java.util.*;

/**
 * @author Jesse Doherty
 */
public abstract class AbstractFlowMap<K,V> implements FlowMap<K,V>
{
    protected Merger<V> merger;

    public AbstractFlowMap( Merger<V> m ){
        merger = m;
    }

    public void setMerger(Merger<V> m)
    {
        merger = m;
    }
    public FlowMap<K,V> copy(){
        FlowMap<K,V> theCopy = emptyMap(); 
        copy( theCopy );
        return theCopy;
    }
    public void copy(FlowMap<K,V> dest)
    {
        if( this == dest ) return;
        dest.clear();
        for( K k : keySet() )
            dest.put(k, get(k));
        dest.setMerger( merger );
    }

    public void clear()
    {
        for( K i : copy().keySet() )
            remove( i );
    }
    public boolean isEmpty()
    {
        return size()==0;
    }
    abstract public int size();
    abstract public void put(K key, V value );
    public void mergePut(K key, V value)
    {
        V curVal = get( key );
        if( merger != null )
            put( key, merger.merge( curVal, value ) );
        else if( curVal instanceof Mergable ){
            @SuppressWarnings("unchecked")
            Mergable<V> mergableCurVal = (Mergable<V>)curVal;
            put( key, mergableCurVal.merge(value) );
        }
        else
            throw new ClassCastException( "Values are not mergable and no merger was given" );
    }
            
    public void mergePut(Merger<V> m, K key, V value)
    {
        V curVal = get( key );
        put( key, m.merge(curVal, value) );
    }
    abstract public V get( Object K );
    abstract public boolean remove( Object key );
    abstract public boolean containsKey( Object key );
    abstract public Set<K> keySet();
    
}
