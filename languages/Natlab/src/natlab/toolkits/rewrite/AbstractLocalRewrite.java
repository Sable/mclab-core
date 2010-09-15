package natlab.toolkits.rewrite;

import ast.*;
import natlab.toolkits.analysis.AbstractNodeCaseHandler;

import java.lang.UnsupportedOperationException;

public abstract class AbstractLocalRewrite extends AbstractNodeCaseHandler
{

    protected ASTNode oldTree, newTree;

    protected TransformedNode newNode = null;

    protected java.util.List multiNodes = null;

    /**
     * Applies the rewrite to a particular node. Also does some book
     * keeping.
     */
    protected void rewrite( ASTNode node )
    {
        newNode = null;
        node.analyze(this);
    }

    /**
     * Applies the rewrite to all children of the given node. 
     */
    protected void rewriteChildren( ASTNode node )
    {
        caseASTNode( node );
    }

    public AbstractLocalRewrite( ASTNode tree )
    {
        oldTree = tree;
    }


    public ASTNode transform()
    {
        rewrite( oldTree );
        ASTNode returnNode;
        if( newNode == null ){
            returnNode = oldTree;
        }
        else{
            returnNode = newNode.getSingleNode();
        }
        return returnNode;
        
    }

    public void caseASTNode( ASTNode node )
    {
        int numChild = node.getNumChild();
        for( int i = 0; i<numChild; i++ ){
            rewrite( node.getChild(i) );
            if( newNode != null )
                if( newNode.isSingleNode() )
                    node.setChild( newNode.getSingleNode(), i );
                else{
                    String msg = "Generic transformation case received non single nodes from " +
                        "transforming a child node. This should only happen when the current " +
                        "case is for a list or otherwise expects this behavior.\n"
                        +node.getPrettyPrinted();
                    throw new UnsupportedOperationException(msg);
                }
        }
        newNode = null;
    }

    public void caseList( List node )
    {
        for( int i=0; i<node.getNumChild(); i++ ){
            ASTNode child = node.getChild(i);
            rewrite(child);
            if( newNode != null )
                if( newNode.isSingleNode() )
                    node.setChild( newNode.getSingleNode(), i );
                else if( newNode.isEmptyNode() ){
                    node.removeChild( i );
                    i--;
                }
                else{
                    java.util.List<ASTNode> nodes = newNode.getMultipleNodes();
                    node.removeChild(i);
                    for( ASTNode newChild : nodes ){
                        node.insertChild( newChild, i );
                        i++;
                    }
                    i--;
                }
        }
        newNode = null;
    }
}