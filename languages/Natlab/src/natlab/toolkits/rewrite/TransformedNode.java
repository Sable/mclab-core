package natlab.toolkits.rewrite;

import java.util.*;
import java.lang.UnsupportedOperationException;
/**
 * A representation of the result of a node transformation. A
 * transformed node can be either empty, a single node or a list of
 * nodes. These two are mutually exclusive.
 */
public class TransformedNode<T extends ast.ASTNode>
{
    private T singleNode = null;
    private LinkedList<T> multipleNodes = null;

    /**
     * Creates an empty transformed node e.g. the result of deleting a
     * node. 
     */
    public TransformedNode(){
        super();
    }
    /**
     * Creates a single node.
     */
    public TransformedNode( T node ){
        singleNode = node;
    }
    /**
     * Creates a multiple transformed node.
     */
    public TransformedNode( T[] nodes ){
        this( Arrays.asList( nodes ) );
    }
    public <E extends T> TransformedNode( Collection<E> nodes ){
        multipleNodes = new LinkedList<T>( nodes );
    }

    public boolean isEmptyNode()
    {
        return !isSingleNode() && !isMultipleNodes();
    }
    public boolean isSingleNode()
    {
        return singleNode != null;
    }
    public boolean isMultipleNodes()
    {
        return multipleNodes != null;
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
        if( isMultipleNodes() )
            return (List<T>)multipleNodes.clone();
        else{
            String msg = "Attempted to getMultipleNodes from a non multiple transformed node.";
            UnsupportedOperationException e;
            e = new UnsupportedOperationException(msg);
            throw e;
        }
    }

    public void add( T node )
    {
        if( isEmptyNode() )
            singleNode = node;
        else{
            if( isSingleNode() ){
                multipleNodes = new LinkedList();
                multipleNodes.add( singleNode );
                singleNode = null;
            }
            multipleNodes.add( node );
        }
    }
}