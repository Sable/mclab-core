package natlab.toolkits.analysis.callgraph;

/**
 * Represents Labels for call sites.
 */
public class CallSiteLabel implements Comparable<CallSiteLabel>
{
    protected enum LabelType {FUNCTION,HANDLE,UNKNOWN}


    protected static String suffix = "";
    protected static String prefix = "S";

    
    protected LabelType type;


    private void setSuffix( String suffix )
    {
        this.suffix = suffix;
    }
    public String getSuffix()
    {
        return suffix;
    }
    public void setPrefix( String prefix )
    {
        this.prefix = prefix;
    }
    public String getPrefix()
    {
        return prefix;
    }

    protected int id;
    protected CallSiteLabel(int id, LabelType type)
    {
        this.id = id;
        this.type = type;
    }
    public boolean equals( CallSiteLabel o )
    {
        return o.id==id;
    }
    public int compareTo( CallSiteLabel o )
    {
        return id - o.id;
    }
    public int HashCode()
    {
        return id;
    }
    public String toString()
    {
        String typeString = "";
        if( type != null )
            switch ( type ){
            case FUNCTION:
                typeString = "f";
                break;
            case HANDLE:
                typeString = "h";
                break;
            case UNKNOWN:
                typeString = "u";
                break;
            }
        
        return typeString + prefix + Integer.toString(id) + suffix;
    }


    private static int labelCounter = 0;
    protected static CallSiteLabel makeLabel(LabelType type)
    {
        int thisId = labelCounter;
        labelCounter++;
        return new CallSiteLabel(thisId, type);
    }
    public static CallSiteLabel makeHandleLabel()
    {
        return makeLabel(LabelType.HANDLE);
    }
    public static CallSiteLabel makeFunctionLabel()
    {
        return makeLabel(LabelType.FUNCTION);
    }
    public static CallSiteLabel makeUnknownLabel()
    {
        return makeLabel(LabelType.UNKNOWN);
    }
    public static CallSiteLabel makeLabel()
    {
        return makeLabel( null );
    }

}
