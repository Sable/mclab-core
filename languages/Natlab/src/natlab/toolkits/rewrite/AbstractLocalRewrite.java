package natlab.toolkits.rewrite;

import ast.*;
import natlab.toolkits.analysis.AbstractNodeCaseHandler;

public abstract class AbstractLocalRewrite extends AbstractNodeCaseHandler
{

    protected ASTNode oldTree, newTree;

    protected ASTNode newNode = null;

    protected java.util.List multiNodes = null;

    public AbstractLocalRewrite( ASTNode tree )
    {
        oldTree = tree;
    }

    //protected 

    public void caseASTNode( ASTNode node )
    {
        int numChild = node.getNumChild();
        for( int i = 0; i<numChild; i++ ){
            newNode = null;
            node.getChild(i).analyze(this);
            if( newNode != null )
                node.setChild( newNode, i );
        }
        newNode = null;
    }

    //public void caseList( List node )
}