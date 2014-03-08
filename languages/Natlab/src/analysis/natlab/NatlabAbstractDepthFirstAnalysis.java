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

package analysis.natlab;

import nodecases.*;
import analysis.*;

import ast.*;
import java.util.*;

public abstract class NatlabAbstractDepthFirstAnalysis<A>
    extends nodecases.AbstractNodeCaseHandler 
    implements analysis.Analysis
{
    public static boolean DEBUG=false;

    protected Map<ASTNode, A> flowSets;
    protected A currentSet;
    protected ASTNode tree;
    protected boolean analyzed = false;
    protected NodeCaseHandler callback = this;

    public A getCurrentSet()
    {
        return currentSet;
    }
    public NatlabAbstractDepthFirstAnalysis(){
        super();
    }
    public NatlabAbstractDepthFirstAnalysis(ASTNode tree){
        this.tree = tree;
        flowSets = new HashMap<ASTNode, A>();
    }
    @Override
    public void setCallback(NodeCaseHandler handler) {
        this.callback = handler;
    }
    

    public Map<ASTNode, A> getFlowSets()
    {
        return flowSets;
    }

    public void setTree( ASTNode t )
    {
        tree = t;
    }
    public ASTNode getTree()
    {
        return tree;
    }

    /**
     * Returns a boolean signifying whether or not the analysis has
     * been performed on a given AST. This does not take into account
     * changes to the AST.
     *
     * @return Whether or not the analysis has been performed.
     */
    public boolean isAnalyzed()
    {
        return analyzed;
    }

    /**
     * Perform the analysis on the entire AST. Also has the efffect of
     * setting the is analyzed status to true.
     * Note, as of this time the analyzed status has no effect on
     * whether or not the analysis is performed. This results in the
     * analysis being able to be run multiple times on the same
     * AST. This could be desirable if the AST has been modified in
     * some way. If you wish to prevent the analysis from being
     * performed you should check the is analyzed status with the
     * isAnalyzed method. The is analyzed method does not however take
     * into account any changes made to the AST since the status was
     * changed. 
     *
     * @see isAnalyzed()
     */
    public void analyze()
    {
        //initialize set
        currentSet = newInitialFlow();
        analyze( tree );

        analyzed = true;
    }

    //@Override
    protected void analyze( ASTNode node )
    {
        node.analyze( callback );
    }

    public abstract A newInitialFlow();

    public void caseIfCondition( Expr condExpr )
    {
        caseCondition( condExpr );
    }
    public void caseWhileCondition( Expr condExpr )
    {
        caseCondition( condExpr );
    }
    public void caseCondition( Expr condExpr )
    {
        condExpr.analyze(this);
    }

    public void caseASTNode(ASTNode node)
    {
        if(DEBUG)
            System.err.println("in caseASTNode for node type " + node.getClass() );
        //visit each child node in forward order
        for( int i = 0; i<node.getNumChild(); i++ ){
            if( node.getChild(i) != null )
                node.getChild(i).analyze( callback );
        }
    }

    @Override
    public void caseLoopVar( AssignStmt loopVar )
    {
        caseAssignStmt( loopVar );
    }

    @Override
    public void caseSwitchExpr( Expr switchExpr )
    {
        switchExpr.analyze(this);
    }

}
