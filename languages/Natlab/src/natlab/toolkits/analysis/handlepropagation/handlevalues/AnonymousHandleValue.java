package natlab.toolkits.analysis.handlepropagation.handlevalues;

import ast.LambdaExpr;

/**
 * Represents an anonymous handle value. These are values created with
 * lambdas.
 */
public class AnonymousHandleValue extends Value
{
    protected LambdaExpr node;
    protected String nodeString;
    protected int hashCode;

    //keep an id for each AnonymousHandleValue to help in comparing
    //them.
    protected static int idCount = 0;
    protected int id;

    public AnonymousHandleValue( LambdaExpr node )
    {
        valueType=Type.ANON;
        this.node = node;
        nodeString = node.getStructureString();
        hashCode = node.hashCode();
        id = idCount++;
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
    
    public int compareTo( Value o )
    {
        if( o instanceof AnonymousHandleValue ){
            //should only be equal if they are the exact same lambda,
            //not just syntactically the same
            AnonymousHandleValue value = (AnonymousHandleValue)o;
            if( value.node.equals( node ) )
                return 0;
            int c = nodeString.compareTo( value.getNodeString() );
            if( c == 0 )
                return id - value.id;
            return c;
        }
        else
            return super.compareTo(o);
    }

    public String toString()
    {
        return nodeString;
    }

}
