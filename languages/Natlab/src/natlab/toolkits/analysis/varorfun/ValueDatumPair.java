package natlab.toolkits.analysis.varorfun;

/**
 * A simple pair class used by the VFFlowset. All comparison or
 * hashing is done on the value or first member of the pair. Datum must be Immutable 
 */

public class ValueDatumPair< V, D >
{
    protected V value;
    protected D datum;

    public ValueDatumPair( V v, D d )
    {
        value = v;
        datum = d;
    }

    public V getValue()
    {
        return value;
    }
    public D getDatum()
    {
        return datum;
    }
    public boolean equals( Object o )
    {
        try{
            return value.equals( ((ValueDatumPair)o).getValue() );
        }catch( Exception e){
            return false;
        }
    }
    public int hashCode()
    {
        return value.hashCode();
    }

    public D setDatum( D d )
    {
        return datum = d;
    }
    /**
     * Shallow copies the Pair.
     */
    public ValueDatumPair<V, D> copy()
    {
        return new ValueDatumPair<V,D>( value, datum);
    }

    public static <A> String toString(Object v, A d){
        return "< " + v.toString() + ", " + d.toString() + " >";
    }
    public String toString()
    {
        return toString( value, datum );
    }
        
}
