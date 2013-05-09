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

import java.util.Stack;

import analysis.AbstractStructuralForwardAnalysis;
import analysis.BackwardsAnalysisHelper;
import ast.ASTNode;
import ast.AssignStmt;
import ast.ElseBlock;
import ast.Expr;
import ast.ForStmt;
import ast.IfStmt;
import ast.WhileStmt;


public abstract class NatlabAbstractStructuralBackwardAnalysis<A> extends analysis.AbstractStructuralAnalysis<A>
{

    public static boolean DEBUG = false;

    protected Stack<LoopFlowsets> loopStack = new Stack<LoopFlowsets>();

    public NatlabAbstractStructuralBackwardAnalysis(ASTNode tree){
        super( tree );
        helper = new BackwardsAnalysisHelper<A>( this );
    }

    public void caseASTNode(ASTNode node)
    {
        if( DEBUG ){
            System.out.println( "caseASTNode" );
        }
        currentInSet = currentOutSet;
        //visit each child node in backwards order
        for( int i = node.getNumChild()-1; i>=0; i-- ){
            if( node.getChild(i) != null )
                analyze( node.getChild(i) );
        }
    }

    //program case

    //Break case
    //deal with the break by using the loop stack's breakInSets

    //continue case
    //deal with continues by using the loop stack's continueInSets

    //return case

    //protected abstract void setupBreaks(A inFlow);
    //protected abstract void setupContinues(A inFlow);
    public void setupBreaks()
    {
        LoopFlowsets loop = loopStack.peek();

        loop.setBreakInFlow( currentOutSet );
    }
    public void setupContinues()
    {
        LoopFlowsets loop = loopStack.peek();
        loop.setContinueInFlow( currentOutSet );
    }

    protected A saveOutSet( ASTNode node )
    {
        A outSet = copy(currentOutSet);
        outFlowSets.put( node, outSet );
        return outSet;
    }
    protected A backupSet(A outSet)
    {
        return copy(outSet);
    }
    public void caseForStmt(ForStmt node)
    {
        if(DEBUG)
            System.out.println( "caseForStmt" );


        AssignStmt loopVar = node.getAssignStmt();
        ASTNode body = node.getStmts();

        loopStack.push(new LoopFlowsets(node));

        A loopOutSet = saveOutSet( node );


        A previousInSet = newInitialFlow();
        
        setupBreaks();
        caseLoopVarAsCondition(loopVar);

        do {
            previousInSet = backupSet( currentInSet );
            currentOutSet = currentInSet;
            caseLoopVarAsUpdate( loopVar );
            setupContinues();
            analyze( body );

            currentInSet = merge(loopOutSet, currentInSet);

            currentOutSet = currentInSet;
            caseLoopVarAsCondition( loopVar );
        }while(!previousInSet.equals(currentInSet));

        currentOutSet = currentInSet;
        caseLoopVarAsInit(loopVar);

        inFlowSets.put(node, backupSet( currentInSet ) );

        loopStack.pop();
    }


    public void caseWhileStmt(WhileStmt node)
    {
        Expr loopCond = node.getExpr();
        ASTNode body = node.getStmts();

        loopStack.push(new LoopFlowsets(node));

        A loopOutSet = saveOutSet( node );

        A previousInSet = newInitialFlow();

        setupBreaks();
        caseCondition(loopCond);

        do {

            previousInSet = backupSet( currentInSet );
            currentOutSet = currentInSet;
            setupContinues();
            
            analyze( body );
        
            currentInSet = merge(loopOutSet, currentInSet);

            currentOutSet = currentInSet;
            
            caseCondition( loopCond );

        }while(!previousInSet.equals(currentInSet));

        inFlowSets.put(node, backupSet( currentInSet ) );

        loopStack.pop();
    }

    public void caseIfStmt( IfStmt node )
    {

        Expr condition;
        ASTNode body;
        

        A ifOutSet = saveOutSet( node );

        A mergedIns = null;

        A previousIn = backupSet( ifOutSet );

        if( node.hasElseBlock() ){
            ElseBlock block = node.getElseBlock();
            body = block.getStmts();
            currentInSet = backupSet( ifOutSet );
            analyze( body );
            previousIn = currentInSet;
        }
        //here
        for( int i = node.getNumIfBlock()-1; i>=0; i-- ){
            currentInSet = backupSet( ifOutSet );
            body = node.getIfBlock(i).getStmts();
            analyze( body );
            currentOutSet = merge(previousIn, currentInSet);
            condition = node.getIfBlock(i).getCondition();
            caseCondition( condition );
            previousIn = currentInSet;
        }

        inFlowSets.put( node, backupSet( currentInSet ) );
    }


    /**
       Represents the information associated with the containing loops
       for a given point. The loop with the deepest nesting is at the
       top.  
       <p> 
       For backward analysis this contains the break and
       continue out sets, the loop in set and the actual loop node.

       @see AbstractStructuralForwardAnalysis.LoopFlowsets
    **/
    protected class LoopFlowsets
    {
        private A breakInFlow;
        private A loopInFlow;
        private A continueInFlow;
        private ASTNode loopNode;
        
        public LoopFlowsets( ASTNode loopNode ){
            this.loopNode = loopNode;
        }
        
        public A getBreakInFlow()
        {
            return breakInFlow;
        }
        public void setBreakInFlow( A breakInFlow )
        {
            this.breakInFlow = breakInFlow;
        }
        
        public A getLoopInFlow()
        {
            return loopInFlow;
        }
        public void setLoopInFlow(A loopInFlow)
        {
            this.loopInFlow = loopInFlow;
        }
        
        public A getContinueInFlow()
        {
            return continueInFlow;
        }
        public void setContinueInFlow(A continueInFlow)
        {
            this.continueInFlow = continueInFlow;
        }
        
        public ASTNode getLoopNode()
        {
            return loopNode;
        }
        public void setLoopNode( ASTNode loopNode )
        {
            this.loopNode = loopNode;
        }
    }
}
