// =========================================================================== //
//                                                                             //
// Copyright 2008-2011 Andrew Casey, Jun Li, Jesse Doherty,                    //
//   Maxime Chevalier-Boisvert, Toheed Aslam, Anton Dubrau, Nurudeen Lameed,   //
//   Amina Aslam, Rahul Garg, Soroush Radpour, Olivier Savary Belanger,        //
//   Laurie Hendren, Clark Verbrugge and McGill University.                    //
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
//   limitations under the License.                                            //
//                                                                             //
// =========================================================================== //

package natlab.toolkits.rewrite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import ast.ASTNode;
/**
 * A representation of the result of a node transformation. A
 * transformed node can be either empty, a single node or a list of
 * nodes. These two are mutually exclusive.
 */
public class TransformedNode extends ArrayList<ASTNode>
{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;


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
    public TransformedNode( ast.ASTNode node ){
        super();
        add(node);
    }
    /**
     * Creates a multiple transformed node.
     * If there is only one or zero element in the list, creates either a singleNode or emptyNode
     */
    public TransformedNode( ast.ASTNode[] nodes ){
        this( Arrays.asList( nodes ) );
    }
    public TransformedNode( Collection<? extends ASTNode> nodes ){
        super(nodes);
    }

    public boolean isEmptyNode()
    {
        return isEmpty();
    }
    public boolean isSingleNode()
    {
        return size() == 1;
    }
    public boolean isMultipleNodes()
    {
        return size() > 1;
    }

    public ast.ASTNode getSingleNode()
    {
        if( isSingleNode() )
            return get(0);
        else{
            String msg = "Attempted to getSingleNode from non single transformed node.";
            UnsupportedOperationException e;
            e = new UnsupportedOperationException(msg);
            throw e;
        }
    }
    
    /**
     * returns
     * @return
     */
    public List<ast.ASTNode> getMultipleNodes()
    {
        if( isMultipleNodes() )
            return (List<ast.ASTNode>)super.clone();
        else{
            String msg = "Attempted to getMultipleNodes from a non multiple transformed node.";
            UnsupportedOperationException e;
            e = new UnsupportedOperationException(msg);
            throw e;
        }
    }
    
   
    
    
    @Override
    public String toString() {
        String msg = "transformedNode with ";
        if (isSingleNode()){
            msg += "single node: \n"+getSingleNode().getPrettyPrinted();
        } else if (isMultipleNodes()){
            msg += "multiple nodes: \n";
            for (ast.ASTNode node : this){
                msg += "["+node.getPrettyPrinted()+"]\n";
            }
        } else {
            msg += "no node (empty)";
        }
        return msg;
    }    
}


