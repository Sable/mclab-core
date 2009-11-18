package natlab.toolkits.analysis;

import ast.*;
import java.util.*;

public abstract class AbstractStructuralForwardAnalysis<A extends FlowSet> extends AbstractStructuralAnalysis<A>
{

    public static boolean DEBUG = false;

    protected Stack<LoopFlowsets> loopStack = new Stack<LoopFlowsets>();

    public AbstractStructuralForwardAnalysis(ASTNode tree){
        super( tree );
    }

    /**
     * 
     * @param node The ASTNode that is being visited
     */
    public void caseASTNode(ASTNode node)
    {
        if( DEBUG ){
            System.out.println( "caseASTNode");
        }
        //visit each child node in forward order
        for( int i = 0; i<node.getNumChild(); i++ ){
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

    public abstract A processBreaks();
    public abstract A processContinues();

    public void caseForStmt( ForStmt node)
    {
        if(DEBUG)
            System.out.println( "caseForStmt" );
        //previousOut is used to track changes in the fixed point computation
        A previousOut;

        AssignStmt loopVar = node.getAssignStmt();
        ASTNode body = node.getStmts();

        //saving the in set of the loop
        A loopInSet = newInitialFlow();
        copy( currentInSet, loopInSet );
        inFlowSets.put( node, loopInSet );

        //process loop variable and store result

        //treat as init
        //note In for loop var as init will bee loopInSet
        caseLoopVarAsInit( loopVar );
        
        //treat as condition
        //note In will be out of loop var as init
        currentInSet = currentOutSet;
        caseLoopVarAsCondition( loopVar );

        //setup loop sets for loopstack 
        LoopFlowsets loopFlowsets = new LoopFlowsets( node );
        loopFlowsets.setLoopInSet( loopInSet );
        loopStack.push( loopFlowsets );
        
        A continueSet;
        A breakSet;
        A mergedOut;
        A newOut;
        
        if(DEBUG)
            System.out.println( "  forstmt: starting fixed point");
        do{
            //Store the current out in previousOut for later
            //comparison
            previousOut = newInitialFlow();
            copy( currentOutSet, previousOut );

            //initialize the continue and break lists
            loopFlowsets.initLists();
            
            //analyze the body and collect the continue sets, merge
            //them into one out
            analyze( body );
            continueSet = processContinues();
            mergedOut = newInitialFlow();
            if( continueSet == null )
                copy(currentOutSet, mergedOut);
            else
                merge(currentOutSet, continueSet, mergedOut);

            //setup the current out as the merged out
            //this is the current out before processing the update
            currentOutSet = mergedOut;

            //setup in for loop var as update case
            currentInSet = mergedOut;
            
            caseLoopVarAsUpdate( loopVar );

            //setup in for loop var as condition, in = out of update
            // merged with loop in
            //TODO-JD: Does this even make sense? The three cases
            // should have split in and out sets somehow, ah well, not
            // really possible for matlab semantics is it?
            merge( loopInSet, currentOutSet, currentOutSet );
            currentInSet = currentOutSet;
            
            caseLoopVarAsCondition( loopVar );

            //merge the loop var as condition out and break sets
            //use for new out
            breakSet = processBreaks();
            mergedOut = newInitialFlow();
            if( breakSet == null )
                copy( currentOutSet, mergedOut );
            else
                merge( currentOutSet, breakSet, mergedOut );
            newOut = mergedOut;
            //newOut is the new out of the entire loop

            //loop until there is no change
        }while( !previousOut.equals( newOut ) );

        if(DEBUG)
            System.out.println( "  forstmt: finished fixed point" );
        //set currentOut to the out of the entire loop, e.g. newOut
        currentOutSet = newOut;
        
        outFlowSets.put( node, currentOutSet );
        loopStack.pop();
    }


    public void caseWhileStmt(WhileStmt node)
    {
        //previousOut is used to track changes in the fixed point
        //copmutation
        A previousOut;

        Expr loopCond = node.getExpr();
        ASTNode body = node.getStmts();

        //saving the in set of the loop
        A loopInSet = newInitialFlow();
        copy( currentInSet, loopInSet );
        inFlowSets.put( node, loopInSet );
        
        //process loop conditional and store result
        //note in will be the currentInSet == loopInSet
        caseCondition( loopCond );
        
        //setup loop sets for loopstack
        LoopFlowsets loopFlowsets = new LoopFlowsets( node );
        loopFlowsets.setLoopInSet( loopInSet );
        loopStack.push( loopFlowsets );

        A continueSet;
        A breakSet;
        A mergedOut;
        A newOut;

        //going into the loop the current out will be the out of the
        //loop condition
        do{
            //store the current out in previousOut for later
            //comparison
            previousOut = newInitialFlow();
            copy( currentOutSet, previousOut );

            //initialize the continue and break lists
            loopFlowsets.initLists();

            //analyze the body and collect the continue sets, merge
            //them into one out
            analyze( body );
            continueSet = processContinues();
            mergedOut = newInitialFlow();
            if( continueSet == null )
                copy( currentOutSet, mergedOut );
            else
                merge( currentOutSet, continueSet, mergedOut );

            //setup the current out as the merged out
            //this is the current out before processing the loop
            //condition
            currentOutSet = (A)(mergedOut.clone());

            //setup in for loop condition case
            //first merge the mergedout with the loop in
            merge( loopInSet, mergedOut, mergedOut );
            currentInSet = (A)(mergedOut.clone());

            caseCondition( loopCond );
            
            //merge the loop condition out and break sets
            //use for new out
            breakSet = processBreaks();
            mergedOut = newInitialFlow();
            if( breakSet == null )
                copy( currentOutSet, mergedOut );
            else
                merge( currentOutSet, breakSet, mergedOut );
            newOut = mergedOut;
            //newOut is the new out of the entire loop

            //loop until there is no change
        }while( !previousOut.equals( newOut ) );

        //set currentOut to the out of the entire loop, e.g. newOut
        currentOutSet = newOut;
        
        outFlowSets.put( node, currentOutSet );
        loopStack.pop();
    }

    public void caseIfStmt( IfStmt node )
    {
        Expr condition;
        ASTNode body;

        //copy and save the inset
        A ifInSet = newInitialFlow();
        copy( currentInSet, ifInSet );

        inFlowSets.put( node, ifInSet );

        //setup mergedOuts to capture the outs of each branch
        A mergedOuts = null;

        //setup nextIn to be the in for the next block
        //initialize with the ifInSet
        A nextIn = (A)(ifInSet.clone());

        //process each IfBlock
        for( IfBlock block : node.getIfBlocks() ) {
            if( DEBUG )
                System.out.println( "if block" );
            condition = block.getCondition();
            body = block.getStmts();

            //process condition
            currentInSet = (A)(nextIn.clone());
            caseCondition( condition );
            
            //store the out of the condition as the in for the next
            //block
            nextIn = (A)(currentOutSet.clone());

            currentInSet = nextIn;
            if( DEBUG )
                System.out.println( "!in before body"+currentInSet );
            //process body
            analyze( body );
            if( DEBUG ){
                System.out.println( "!in after body"+currentInSet );
                System.out.println( "!out after body"+currentOutSet );
            }
            //merge out with previous outs
            if( mergedOuts == null ){
                mergedOuts = (A)(currentOutSet.clone());
                if(DEBUG)
                    System.out.println( "first mergedOuts is " + mergedOuts.toString() );
            }
            else{
                if(DEBUG)
                    System.out.println("merging " + currentOutSet.toString() );
                merge( currentOutSet, mergedOuts, mergedOuts );
                if(DEBUG)
                    System.out.println("result " + mergedOuts.toString());
            }
        }
        //if an elseBlock exists, process it, otherwise merge the
        //nextIn into the mergedOuts
        if( node.hasElseBlock() ){
            if( DEBUG )
                System.out.println( "else block" );
            ElseBlock block = node.getElseBlock();
            body = block.getStmts();
            //currentOutSet will become the currentInSet for the body,
            //thanks to the AnalysisHelper
            currentInSet = (A)(nextIn.clone());
            currentOutSet = (A)(nextIn.clone());
            if( DEBUG )
                System.out.println( "!in before body "+currentInSet );
            analyze( body );
            if( DEBUG ){
                System.out.println( "!in after body "+currentInSet );
                System.out.println( "!out after body "+currentOutSet );
            }
            if(DEBUG)
                System.out.println("merging " + currentOutSet.toString() );
            merge( currentOutSet, mergedOuts, mergedOuts );
            if(DEBUG)
                System.out.println("result " + mergedOuts.toString());
        }
        else{
            merge( nextIn, mergedOuts, mergedOuts );
        }

        //set and store the outset
        currentOutSet = mergedOuts;
        if(DEBUG)
            System.out.println("outset is " + currentOutSet.toString());
        outFlowSets.put( node, currentOutSet );
    }
    /**
       Represents the information associated with the containing loops
       for a given point. The loop with the deepest nesting is at the
       top. 
       <p>
       For forward analysis this contains list of all the break and
       continue out sets, the loop in set and the actual loop node.

       @see AbstractStructuralBackwardAnalysis.LoopFlowsets
    **/
    protected class LoopFlowsets
    {
        private LinkedList<A> breakOutSets;
        private A loopInSet;
        private LinkedList<A> continueOutSets;
        private ASTNode loopNode;

        public LoopFlowsets(ASTNode loopNode){
            this.loopNode = loopNode;
            initLists();
        }
        public void initLists()
        {
            breakOutSets = new LinkedList<A>();
            continueOutSets = new LinkedList<A>();
        }
        public void addContinueSet( A flowSet )
        {
            continueOutSets.add( flowSet );
        }
        public void addBreakSet( A flowSet )
        {
            breakOutSets.add( flowSet );
        }
        
        public java.util.List<A> getBreakOutSets()
        {
            return breakOutSets;
        }
        public A getLoopInFlow()
        {
            return loopInSet;
        }
        public void setLoopInSet(A loopInFlow)
        {
            this.loopInSet = loopInFlow;
        }
        public java.util.List<A> getContinueOutSets()
        {
            return continueOutSets;
        }
        public ASTNode getLoopNode()
        {
            return loopNode;
        }
        public void setLoopNode(ASTNode loopNode)
        {
            this.loopNode = loopNode;
        }
    }
}
