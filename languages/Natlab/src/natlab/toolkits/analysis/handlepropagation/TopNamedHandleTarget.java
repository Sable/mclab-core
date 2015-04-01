package natlab.toolkits.analysis.handlepropagation;


/**
 * Represents handles targets that could be any named function.
 */
public class TopNamedHandleTarget implements HandleTarget
{

    private static int hashvalue = TopNamedHandleTarget.class.hashCode();

    public TopNamedHandleTarget()
    {
        super();
    }

    public int hashCode()
    {
        return hashvalue;
    }
    public boolean equals( HandleTarget o )
    {
        return o instanceof TopNamedHandleTarget;
    }
    public int compareTo( HandleTarget o )
    {
        if( o instanceof TopNamedHandleTarget )
            return 0;
        else if( o instanceof TopAnonymousHandleTarget )
            return 1;
        else if( o instanceof TopHandleTarget )
            return 1;
        else
            return -1;
    }

    public String toString()
    {
        return "Top named target";
    }

}
