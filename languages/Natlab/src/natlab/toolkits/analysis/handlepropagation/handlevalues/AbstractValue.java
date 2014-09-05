package natlab.toolkits.analysis.handlepropagation.handlevalues;

/**
 * Represents the abstract value types. These are the non named
 * handle and non anonymous handle types.
 */
public class AbstractValue extends Value
{

    /**
     * Constructs an AbstractValue with a particular type.
     */
    private AbstractValue( Type t )
    {
        if( t.equals(Type.NAMED) || t.equals(Type.ANON) )
            throw new UnsupportedOperationException("AbstractValue instances cannot be NAMED or ANON");
        valueType = t;
    }


    public static AbstractValue newUndef()
    {
        return new AbstractValue(Type.UNDEF);
    }
    public boolean isUndef()
    {
        return valueType.equals(Type.UNDEF);
    }
    public static AbstractValue newDataOnly()
    {
        return new AbstractValue(Type.DATAONLY);
    }
    public boolean isDataOnly()
    {
        return valueType.equals(Type.DATAONLY);
    }

    public static AbstractValue newDataHandleOnly()
    {
        return new AbstractValue(Type.DATAHANDLEONLY);
    }
    public boolean isDataHandleOnly()
    {
        return valueType.equals(Type.DATAHANDLEONLY);
    }

    public static AbstractValue newDataWithHandles()
    {
        return new AbstractValue(Type.DATAWITHHANDLES);
    }
    public boolean isDataWithHandles()
    {
        return valueType.equals(Type.DATAWITHHANDLES);
    }

    public static AbstractValue newHandle()
    {
        return new AbstractValue(Type.HANDLE);
    }
}
