package natlab.toolkits.analysis;

import ast.*;
import java.util.*;
import natlab.toolkits.analysis.*;

public abstract class AbstractStructuralBackwardAnalysis<A extends FlowSet> extends AbstractStructuralAnalysis<A>
{
    public static boolean DEBUG = false;

    protected Stack<LoopFlowsets> loopStack = new Stack<LoopFlowsets>();

    public AbstractStructuralBackwardAnalysis(ASTNode tree){
        super( tree );
    }

    public void caseASTNode(ASTNode node)
    {
        if( DEBUG ){
            System.out.println( "caseASTNode" );
        }
        currentOutSet = currentInSet;
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
        LoopFlowsets<A> loop = loopStack.peek();

        loop.setBreakInFlow( currentOutSet );
    }
    public void setupContinues()
    {
        LoopFlowsets<A> loop = loopStack.peek();
        loop.setContinueInFlow( currentOutSet );
    }

    protected A saveInSet( ASTNode node )
    {
        A inSet = newInitialFlow();
        copy( currentInSet, inSet );
        inFlowSets.put( node, inSet );
        return inSet;
    }
    protected A backupSet(A outSet)
    {
        A backup = newInitialFlow();
        copy( outSet, backup );
        return backup;
    }
    public void caseForStmt(ForStmt node)
    {
        if(DEBUG)
            System.out.println( "caseForStmt" );


        AssignStmt loopVar = node.getAssignStmt();
        ASTNode body = node.getStmts();

        loopStack.push(new LoopFlowsets(node));

        A loopInSet = saveInSet( node );


        A previousOutSet = newInitialFlow();
        
        setupBreaks();
        caseLoopVarAsCondition(loopVar);

        do {
            previousOutSet = backupSet( currentOutSet );
            currentInSet = currentOutSet;
            caseLoopVarAsUpdate( loopVar );
            setupContinues();
            analyze( body );

            merge( loopInSet, currentOutSet, currentOutSet );

            currentInSet = currentOutSet;
            caseLoopVarAsCondition( loopVar );
        }while(!previousOutSet.equals(currentOutSet));

        currentInSet = currentOutSet;
        caseLoopVarAsInit(loopVar);

        outFlowSets.put(node, backupSet( currentOutSet ) );

        loopStack.pop();
    }


    public void caseWhileStmt(WhileStmt node)
    {
        Expr loopCond = node.getExpr();
        ASTNode body = node.getStmts();

        loopStack.push(new LoopFlowsets(node));

        A loopInSet = saveInSet( node );

        A previousOutSet = newInitialFlow();

        setupBreaks();
        caseCondition(loopCond);

        do {

            previousOutSet = backupSet( currentOutSet );
            currentInSet = currentOutSet;
            setupContinues();
            
            analyze( body );
        
            merge( loopInSet, currentOutSet, currentOutSet );

            currentInSet = currentOutSet;
            
            caseCondition( loopCond );

        }while(!previousOutSet.equals(currentOutSet));

        outFlowSets.put(node, backupSet( currentOutSet ) );

        loopStack.pop();
    }

    public void caseIfStmt( IfStmt node )
    {

        Expr condition;
        ASTNode body;
        

        A ifInSet = saveInSet( node );

        A mergedOuts = null;

        A previousOut = backupSet( ifInSet );

        if( node.hasElseBlock() ){
            ElseBlock block = node.getElseBlock();
            body = block.getStmts();
            currentOutSet = backupSet( ifInSet );
            analyze( body );
            previousOut = currentOutSet;
        }
        for( int i = node.getNumIfBlock()-1; i>=0; i-- ){
            currentOutSet = backupSet( ifInSet );
            body = node.getIfBlock(i).getStmts();
            analyze( body );
            currentInSet = newInitialFlow();
            merge( previousOut, currentOutSet, currentInSet );
            condition = node.getIfBlock(i).getCondition();
            caseCondition( condition );
            previousOut = currentOutSet;
        }

        outFlowSets.put( node, backupSet( currentOutSet ) );
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
    protected class LoopFlowsets<A>
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
