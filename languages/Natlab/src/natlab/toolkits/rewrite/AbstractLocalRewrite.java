// =========================================================================== //
//                                                                             //
// Copyright 2011 Jesse Doherty and McGill University.                         //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//  limitations under the License.                                             //
//                                                                             //
// =========================================================================== //

package natlab.toolkits.rewrite;

import nodecases.AbstractNodeCaseHandler;
import ast.ASTNode;
import ast.List;

public abstract class AbstractLocalRewrite extends AbstractNodeCaseHandler
{

    protected ASTNode oldTree, newTree;

    protected TransformedNode newNode = null;

    protected java.util.List multiNodes = null;

    //What rewrite to use as the call back object for the visitor.
    protected AbstractLocalRewrite callback;

    /**
     * Applies the rewrite to a particular node. Also does some book
     * keeping.
     */
    protected void rewrite( ASTNode node )
    {
        newNode = null;
        node.analyze(callback);
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
        callback = this;
    }

    public AbstractLocalRewrite( ASTNode tree, AbstractLocalRewrite callback )
    {
        oldTree = tree;
        this.callback = callback;
    }

    public void setTree( ASTNode tree )
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
                        +"node:\n"+node.getPrettyPrinted()
                        +"\nchild node transform:\n"+newNode.toString();
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