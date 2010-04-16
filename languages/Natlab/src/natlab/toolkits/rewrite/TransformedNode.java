package natlab.toolkits.rewrite;

import java.util.*;
import java.lang.UnsupportedOperationException;
/**
 * A representation of the result of a node transformation. A
 * transformed node can be either a single node or a list of
 * nodes. These two are mutually exclusive. 
 */
public class TransformedNode<T extends ast.ASTNode>
{
    private T singleNode = null;
    private LinkedList<T> multipleNodes = null;

    public TransformedNode( T node ){
        singleNode = node;
    }
    public TransformedNode( T[] nodes ){
        this( Arrays.asList( nodes ) );
    }
    public <E extends T> TransformedNode( Collection<E> nodes ){
        multipleNodes = new LinkedList<T>( nodes );
    }

    public boolean isSingleNode()
    {
        return singleNode != null;
    }
    public boolean isMultipleNodes()
    {
        return !isSingleNode();
    }

    public T getSingleNode()
    {
        if( isSingleNode() )
            return singleNode;
        else{
            String msg = "Attempted to getSingleNode from non single transformed node.";
            UnsupportedOperationException e;
            e = new UnsupportedOperationException(msg);
            throw e;
        }
    }
    public List<T> getMultipleNodes()
    {
        return multipleNodes;
    }
}