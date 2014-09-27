package natlab.toolkits.analysis.handlepropagation.handlevalues;

import java.util.EnumSet;

/**
 * Abstract type for all handle values.
 */
public abstract class Value implements Comparable<Value>
{

    /**
     * Represents the different type of values. It is used to enforce
     * a consistent ordering for the compare to 
     */
    protected enum Type {UNDEF,DATAONLY,DATAHANDLEONLY,DATAWITHHANDLES,HANDLE,NAMED,ANON};

    protected Type valueType;
    
    /**
     * Default compareTo that simply uses the enum compareTo for the
     * valueType.
     */
    public int compareTo(Value o)
    {
        return valueType.compareTo(o.valueType);
    }

    /**
     * Default equals implementation that uses compareTo to test.
     */
    public boolean equals( Value o )
    {
        return compareTo( o ) == 0;
    }

    /**
     * Default hashCode that uses Type enum hashCode for valueType.
     */
    public int hashCode()
    {
        return valueType.hashCode();
    }


    /**
     * Default toString that uses the toString of Type enum.
     */
    public String toString()
    {
        return valueType.toString();
    }

    public boolean isHandle() {
      return EnumSet.of(Type.HANDLE, Type.NAMED, Type.ANON).contains(valueType);
    }
}
