package natlab.toolkits.analysis.handlepropagation;

import ast.LambdaExpr;

/**
 * Represents handle targets that are anonymous functions. This refers
 * to handles created as lambdas.
 */

public class AnonymousHandleTarget implements HandleTarget
{
    protected LambdaExpr node;
    protected String nodeString;
    protected int hashCode;

    public AnonymousHandleTarget( LambdaExpr node )
    {
        this.node = node;
        nodeString = node.getStructureString();
        hashCode = node.hashCode();
    }
    
    public LambdaExpr getLambdaExpr()
    {
        return node;
    }
    public String getNodeString()
    {
        return nodeString;
    }

    public int hashCode()
    {
        return hashCode;
    }
    public boolean equals( HandleTarget o )
    {
        return compareTo(o)==0;
    }

    public int compareTo( HandleTarget o )
    {
        if( o instanceof NamedHandleTarget )
            return -1;
        else if( o instanceof AnonymousHandleTarget )
            return nodeString.compareTo( ((AnonymousHandleTarget)o).getNodeString() );
        else
            return 1;
    }
    
    public String toString()
    {
        return "anonymous target("+nodeString+")";
    }
}