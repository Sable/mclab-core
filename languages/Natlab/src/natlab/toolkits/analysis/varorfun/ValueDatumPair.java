package natlab.toolkits.analysis.varorfun;

/**
 * A simple pair class used by the VFFlowset. All comparison or
 * hashing is done on the value or first member of the pair. Datum must be Immutable 
 */

public class ValueDatumPair< V, D extends VFDatum>
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
     * Clones the Pair, but only does a deep clone of the datum, not
     * the value.
     */
    public ValueDatumPair<V, D> clone()
    {
        return new ValueDatumPair( value, datum);
    }

    public static <A extends VFDatum> String toString(Object v, A d){
        return "< " + v.toString() + ", " + d.toString() + " >";
    }
    public String toString()
    {
        return toString( value, datum );
    }
        
}
