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
    public boolean equals( Object o )
    {
        try{
            return key.equals( ((DataPair)o).getKey() );
        }catch( Exception e){
            return false;
        }
    }
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

    public static String toString(Object k, Object v){
        return "< " + k.toString() + ", " + v.toString() + " >";
    }
    public String toString()
    {
        return toString( key, value );
    }
        
}