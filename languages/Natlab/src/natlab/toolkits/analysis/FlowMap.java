package natlab.toolkits.analysis;

import java.util.*;


/**
 * @author Jesse Doherty
 */
public interface FlowMap<K,V> extends FlowData
{
    public FlowMap<K,V> copy();
    public void copy(FlowMap<K,V> dest);
    public void clear();
    public boolean isEmpty();
    public int size();
    public void put(K key, V value );
    public void mergePut(K key, V value);
    public void mergePut(Merger<V> m, K key, V value);
    public V get( Object K );
    public boolean remove( Object key );
    public boolean containsKey( Object key );
    public Set<K> keySet();
    public FlowMap<K,V> emptyMap();
    public void setMerger(Merger<V> m);

}
