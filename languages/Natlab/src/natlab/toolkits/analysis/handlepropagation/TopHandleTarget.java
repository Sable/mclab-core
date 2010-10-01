package natlab.toolkits.analysis.handlepropagation;

/**
 * Represents handle targets that could be anything
 */
public class TopHandleTarget implements HandleTarget
{
    private static int hashvalue = TopHandleTarget.class.hashCode();

    public TopHandleTarget()
    {
        super();
    }

    public int hashCode()
    {
        return hashvalue;
    }
    public boolean equals( HandleTarget o )
    {
        return compareTo( o )==0;
    }
    public int compareTo( HandleTarget o )
    {
        if( o instanceof TopHandleTarget )
            return 0;
        else
            return -1;
    }
    public String toString()
    {
        return "Top target";
    }

}
