package natlab.toolkits.rewrite.multireturn;

import ast.*;
import natlab.toolkits.analysis.AbstractNodeCaseHandler;
import natlab.toolkits.rewrite.AbstractLocalRewrite;

public class MultiReturnRewrite extends AbstractLocalRewrite
{

    public MultiReturnRewrite( ASTNode tree )
    {
        super( tree );
    }

    public ASTNode go()
    {
        newNode = null;
        oldTree.analyze( this );

        ASTNode returnNode;
        if( newNode == null ){
            returnNode = oldTree;
        }
        else{
            returnNode = newNode;
        }
        return returnNode;
        
    }

    public void caseName( Name node )
    {
        Name newName = new Name( node.getID() + "JESSE" );
        newNode = newName;
    }

}