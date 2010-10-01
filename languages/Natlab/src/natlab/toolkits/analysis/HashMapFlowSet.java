package natlab.toolkits.analysis;

import java.util.*;


/**
 * An implementation of FlowSet that is based on a HashMap. This
 * implementation should be considered a set in terms of the key. 
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
     * Clones the underlying HashMap
     */
    public HashMapFlowSet<K,V> clone()
    {
        HashMap<K,V> cloneMap = (HashMap<K,V>)map.clone();
        return new HashMapFlowSet( cloneMap );
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

    public boolean remove(Map.Entry<K,V> entry)
    {
        V value = map.remove(entry.getKey());
        if( value.equals(entry.getValue()) )
            return true;
        else if( value != null ){
            add(entry);
            return false;
        }
        else
            return false;
    }
    public boolean removeByKey(K key)
    {
        return map.remove(key) == null;
    }
    public boolean contains( Map.Entry<K,V> entry )
    {
        V value = map.get( entry.getKey() );
        return value.equals( entry.getValue() );
    }
    public boolean containsKey( K key )
    {
        return map.containsKey( key );
    }

    public List<Map.Entry<K,V>> toList()
    {
        return new ArrayList( map.entrySet() );
    }
    public Set<K> keySet()
    {
        return map.keySet();
    }
    public Collection<V> values()
    {
        return map.values();
    }

}
