package natlab.toolkits.analysis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * An implementation of FlowSet that is based on a HashMap. This
 * implementation should be considered a set in terms of the key.
 * NOTE: union, intersection, difference probably don't behave as
 * expected. 
 */
@Deprecated
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
    @Override
    public HashMapFlowSet<K,V> copy()
    {
        HashMap<K,V> cloneMap = new HashMap<K,V>(map);
        return new HashMapFlowSet<K,V>( cloneMap );
    }

    /**
     * Clears the underlying HashSet
     */
    @Override
    public void clear()
    {
        map.clear();
    }
    @Override
    public boolean isEmpty()
    {
        return map.isEmpty();
    }
    @Override
    public int size()
    {
        return map.size();
    }
    @Override
    public void add(Map.Entry<K,V> entry)
    {
        map.put(entry.getKey(), entry.getValue());
    }
    public void add(K key, V value)
    {
        map.put(key, value);
    }

    //public boolean remove(Map.Entry<K,V> entry)
    @Override
    public boolean remove(Object obj)
    {
        return map.entrySet().remove(obj);
    }
    public boolean removeByKey(K key)
    {
        return map.remove(key) == null;
    }
    @Override
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
    @Override
    public Iterator<Map.Entry<K,V>> iterator()
    {
        return map.entrySet().iterator();
    }

    @Override
    public HashMapFlowSet<K,V> emptySet()
    {
        return new HashMapFlowSet<K,V>( new HashMap<K,V>() );
    }
}
