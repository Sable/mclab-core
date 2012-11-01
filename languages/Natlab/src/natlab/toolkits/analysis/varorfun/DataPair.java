package natlab.toolkits.analysis.varorfun;

public class DataPair< K, V >
{
    protected K key;
    protected V value;

    public DataPair( K k, V v )
    {
        key = k;
        value = v;
    }

    public V getValue()
    {
        return value;
    }
    public K getKey()
    {
        return key;
    }
    
    @Override
    public boolean equals( Object o )
    {
        try{
            return key.equals( ((DataPair<?,?>)o).getKey() );
        }catch( Exception e){
            return false;
        }
    }
    
    @Override
    public int hashCode()
    {
        return key.hashCode();
    }

    public K setKey( K k )
    {
        return key = k;
    }
    /**
     * Clones the Pair. Shallow clone.
     */
    public DataPair<K, V> copy()
    {
        return new DataPair<K,V>( key, value );
    }

    @Override
    public String toString()
    {
        return String.format("<%s, %s>", key, value);
    }
}