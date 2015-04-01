package natlab.toolkits.analysis.handlepropagation;


/**
 * Represents handle targets that could be any anonymous function.
 */
public class TopAnonymousHandleTarget implements HandleTarget
{
    private static int hashvalue = TopAnonymousHandleTarget.class.hashCode();

    public TopAnonymousHandleTarget()
    {
        super();
    }

    public int hashCode()
    {
        return hashvalue;
    }
    public boolean equals( HandleTarget o )
    {
        return o instanceof TopAnonymousHandleTarget;
    }
    public int compareTo( HandleTarget o )
    {
        if( o instanceof TopAnonymousHandleTarget )
            return 0;
        else if( o instanceof TopHandleTarget )
            return 1;
        else
            return -1;
    }
    public String toString()
    {
        return "Top anonymous target";
    }

}
