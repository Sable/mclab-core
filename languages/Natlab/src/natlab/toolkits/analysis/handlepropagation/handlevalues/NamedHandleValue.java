package natlab.toolkits.analysis.handlepropagation.handlevalues;

/**
 * Represents a named function handle value.
 */
public class NamedHandleValue extends Value
{
    private String name;

    public NamedHandleValue( String name )
    {
        valueType = Type.NAMED;
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    public int hashCode()
    {
        return name.hashCode();
    }

    public int compareTo( Value o )
    {
        if( o instanceof NamedHandleValue )
            return name.compareTo( ((NamedHandleValue)o).getName() );
        else
            return super.compareTo(o);
    }

    public String toString()
    {
        return "@"+name;
    }


}
