package natlab.toolkits.analysis.handlepropagation;


/**
 * Represents handle targets that are simple named functions.
 */
public class NamedHandleTarget implements HandleTarget
{
    private String name;

    public NamedHandleTarget( String name )
    {
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

    public boolean equals( HandleTarget o )
    {
        return compareTo(o)==0;
    }
    public int compareTo( HandleTarget o )
    {
        if( o instanceof NamedHandleTarget )
            return name.compareTo( ((NamedHandleTarget)o).getName() );
        else
            return 1;
    }
    public String toString()
    {
        return "named target("+name+")";
    }
}