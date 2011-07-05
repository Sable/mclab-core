package natlab.toolkits.analysis;

import java.util.*;


/**
 * An implementation of FlowSet that is based on a HashMap. This
 * implementation should be considered a set in terms of the key.
 * NOTE: union, intersection, difference probably don't behave as
 * expected. 
 */
public class HashMapFlowSet<K,V> extends AbstractFlowSet<Map.Entry<K,V>>
{
    protected HashMap<K,V> map;

    /**
     * Creates a new HashMap.
     */
    public HashMapFlowSet()
    {
        map = new HashMap<K,V>();
    }

    /**
     * Takes a HashMap to use directly.
     */
    public HashMapFlowSet( HashMap<K,V> map ){
        this.map = map;
    }

    /**
     * Shallow copies the underlying HashMap
     */
    public HashMapFlowSet<K,V> copy()
    {
        HashMap<K,V> cloneMap = new HashMap<K,V>(map);
        return new HashMapFlowSet<K,V>( cloneMap );
    }

    /**
     * Clears the underlying HashSet
     */
    public void clear()
    {
        map.clear();
    }
    public boolean isEmpty()
    {
        return map.isEmpty();
    }
    public int size()
    {
        return map.size();
    }
    public void add(Map.Entry<K,V> entry)
    {
        map.put(entry.getKey(), entry.getValue());
    }
    public void add(K key, V value)
    {
        map.put(key, value);
    }

    //public boolean remove(Map.Entry<K,V> entry)
    public boolean remove(Object obj)
    {
        return map.entrySet().remove(obj);
    }
    public boolean removeByKey(K key)
    {
        return map.remove(key) == null;
    }
    public boolean contains( Object entry )
    {
        return map.entrySet().contains( entry );
    }
    public boolean containsKey( K key )
    {
        return map.containsKey( key );
    }

    public List<Map.Entry<K,V>> toList()
    {
        return new ArrayList<Map.Entry<K,V>>( map.entrySet() );
    }
    public Set<K> keySet()
    {
        return map.keySet();
    }
    public Collection<V> values()
    {
        return map.values();
    }
    public Iterator<Map.Entry<K,V>> iterator()
    {
        return map.entrySet().iterator();
    }

    public HashMapFlowSet<K,V> emptySet()
    {
        return new HashMapFlowSet<K,V>( new HashMap<K,V>() );
    }
}
