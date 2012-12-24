package natlab.toolkits.analysis.varorfun;

/**
 * A simple pair class used by the VFFlowset. All comparison or
 * hashing is done on the value or first member of the pair. Datum must be Immutable 
 */

public class ValueDatumPair< V, D >
{
    protected final V value;
    protected final D datum;

    public static <A, B> ValueDatumPair<A, B> create(A a, B b) {
      return new ValueDatumPair<A, B>(a, b);
    }
    
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
        return value.equals( ((ValueDatumPair<?,?>)o).getValue() );
    }

    public int hashCode()
    {
        return value.hashCode();
    }
    /**
     * Shallow copies the Pair.
     */
    public ValueDatumPair<V, D> copy()
    {
        return new ValueDatumPair<V,D>( value, datum);
    }

    public String toString()
    {
      return String.format("<%s, %s>", value, datum);
    }
}
