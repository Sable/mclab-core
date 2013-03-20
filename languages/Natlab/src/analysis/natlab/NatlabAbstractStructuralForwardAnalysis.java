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

import analysis.*;
import ast.*;
import java.util.*;

public abstract class NatlabAbstractStructuralForwardAnalysis<A> extends analysis.AbstractStructuralAnalysis<A>
{

    public static boolean DEBUG = false;

    protected Stack<LoopFlowsets> loopStack = new Stack<LoopFlowsets>();

    public NatlabAbstractStructuralForwardAnalysis(ASTNode tree){
        super( tree );
    }

    /**
     * 
     * @param node The ASTNode that is being visited
     */
    public void caseASTNode(ASTNode node)
    {
        if( DEBUG ){
            System.out.println( "caseASTNode for node type " + node.getClass());
        }
        currentOutSet = currentInSet;
        //visit each child node in forward order
        for( int i = 0; i<node.getNumChild(); i++ ){
            if( node.getChild(i) != null )
            	analyze( node.getChild(i) );
        }
    }

    /**
     * The out set of a condition if condition evals to true. This set
     * is private because the programmer should never interact
     * directly with it. If it is set, then {@code falseOutSet} should
     * be set too, if they are set then the machinery should set the
     * currentOutSet appropriately.
     */
    private A trueOutSet;
    /**
     * The out set of a condition if condition evals to false. This set
     * is private because the programmer should never interact
     * directly with it. If it is set, then {@code trueOutSet} should
     * be set too, if they are set then the machinery should set the
     * currentOutSet appropriately.
     */
    private A falseOutSet;
    /**
     * Sets the true and false out sets. Used to enforce the contract
     * that either both are set or neither are set.
     */
    protected void setTrueFalseOutSet( A tSet, A fSet )
    {
        trueOutSet = tSet;
        falseOutSet = fSet;
    }
    /**
     * Un-sets the true and false out sets.
     */
    protected void unsetTrueFalseOutSet()
    {
        trueOutSet = null;
        falseOutSet = null;
    }
    /**
     * Gets the true out set. This should be used when creating a
     * branching analysis. If no true out set is defined, returns
     * null.
     * NOTE: the machinery of the if and while cases sets the
     * {@code currentOutSet} to the appropriate set. This is useful if
     * those cases are being overwritten.
     *
     * @return the current {@code trueOutSet} or {@code null} if not set
     */
    protected A getTrueOutSet()
    {
        return trueOutSet;
    }
    /**
     * Gets the false out set. This should be used when creating a
     * branching analysis. If no false out set is defined, returns
     * null.
     * NOTE: the machinery of the if and while cases sets the
     * {@code currentOutSet} to the appropriate set. This is useful if
     * those cases are being overwritten.
     *
     * @return the current {@code falseOutSet} or {@code null} if not set
     */
    protected A getFalseOutSet()
    {
        return falseOutSet;
    }

    //program case

    //Break case
    //deal with the break by using the loop stack's breakInSets

    //continue case
    //deal with continues by using the loop stack's continueInSets

    //return case

    public abstract A processBreaks();
    public abstract A processContinues();

    /**
     * Sets the in data associated with a given node. Abstracts away
     * from how this is being done.
     */
    protected A setInFlow( ASTNode node, A flow )
    {
        return inFlowSets.put( node, flow );
    }
    /**
     * Sets the out data associated with a given node.
     */
    protected A setOutFlow( ASTNode node, A flow )
    {
        return outFlowSets.put( node, flow );
    }


    protected A saveInSet( ASTNode node )
    {
        A inSet = newInitialFlow();
        copy( currentInSet, inSet );
        setInFlow( node, inSet );
        return inSet;
    }
    protected LoopFlowsets setupLoopStack( A loopIn, ForStmt node )
    {
        LoopFlowsets loopFlowsets = new LoopFlowsets( node );
        loopFlowsets.setLoopInSet( loopIn );
        loopStack.push( loopFlowsets );
        return loopFlowsets;
    }


    protected A backupSet(A outSet)
    {
        A backup = newInitialFlow();
        copy( outSet, backup );
        return backup;
    }
    protected A mergeOuts( A outSet, A newSet )
    {
        A merged = newInitialFlow();
        if( newSet == null )
            copy( outSet, merged );
        else
            merge( outSet, newSet, outSet );
        return outSet;
    }

    public void caseForStmt( ForStmt node)
    {
        if(DEBUG)
            System.out.println( "caseForStmt" );
        //previousOut is used to track changes in the fixed point computation
        A previousOut;

        AssignStmt loopVar = node.getAssignStmt();
        ASTNode body = node.getStmts();

        A loopInSet = saveInSet( node );

        //note In for loopVar as init will be loopInSet
        caseLoopVarAsInit( loopVar );
        loopInSet = currentOutSet; //the in for the rest of the loop will be the out of init

        currentInSet = currentOutSet;
        caseLoopVarAsCondition( loopVar );

        LoopFlowsets loopFlowsets = setupLoopStack( loopInSet, node );
        
        A continueSet;
        A breakSet;
        A mergedOut;
        A newOut;
        
        //for debug
        int iterCount = 0;
        int countBad = 100;
        
        newOut = currentOutSet;
        if(DEBUG)
            System.out.println( "  forstmt: starting fixed point");
        do{
            previousOut = backupSet( newOut );
            loopFlowsets.initLists();
            
            analyze( body );
            continueSet = processContinues();
            mergedOut = mergeOuts( currentOutSet, continueSet );

            //this is the current out before processing the update
            currentOutSet = mergedOut;

            //setup in for loop var as update case
            currentInSet = mergedOut;
            
            caseLoopVarAsUpdate( loopVar );

            merge( loopInSet, currentOutSet, currentOutSet );
            currentInSet = currentOutSet;
            
            caseLoopVarAsCondition( loopVar );

            newOut = currentOutSet;

            //loop until there is no change
            if(DEBUG){
                System.out.println("*****\ndone iteration");
                System.out.println("prev: "+previousOut);
                System.out.println("new: "+newOut);
                System.out.println("*****");
                if(iterCount>countBad){
                    System.err.println("!!!!!!!!!!**********\nwoah too many iterations: "+ iterCount);
                    System.err.println( node.getPrettyPrinted() );
                    System.err.println("!!!!!!!!!!: " + iterCount);
                    countBad *= 2;
                }
            }
            iterCount++;
        }while( !previousOut.equals( newOut ) );

        if(DEBUG)
            System.out.println( "  forstmt: finished fixed point" );

        breakSet = processBreaks();
        mergedOut = mergeOuts( currentOutSet, breakSet );

        //set currentOut to the out of the FP computation and the
        //result of processing breaks.
        currentOutSet = mergedOut;




        setOutFlow( node, currentOutSet );
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
        setInFlow( node, loopInSet );
        
        //process loop conditional and store result
        //note in will be the currentInSet == loopInSet
        caseWhileCondition( loopCond );
        
        //setup loop sets for loopstack
        LoopFlowsets loopFlowsets = new LoopFlowsets( node );
        loopFlowsets.setLoopInSet( loopInSet );
        loopStack.push( loopFlowsets );

        A continueSet;
        A breakSet;
        A mergedOut;
        A newOut;


        int iterCount = 0;
        int countBad = 100;

        if(DEBUG)
            System.out.println( "  whilestmt: starting fixed point");
        //going into the loop the current out will be the out of the
        //loop condition
        do{
            if(DEBUG){
                System.out.println(" whilestmt: currentOutSet at start: ");
                System.out.println(currentOutSet);
            }
            //store the current out in previousOut for later
            //comparison
            previousOut = newInitialFlow();
            copy( currentOutSet, previousOut );

            //initialize the continue and break lists
            loopFlowsets.initLists();

            //analyze the body and collect the continue sets, merge
            //them into one out
            analyze( body );
            if(DEBUG){
                System.out.println(" whilestmt: currentOutSet after body: ");
                System.out.println(currentOutSet);
            }
            continueSet = processContinues();
            if(DEBUG){
                System.out.println(" whilestmt: continueSet: ");
                System.out.println(continueSet);
            }
            mergedOut = newInitialFlow();
            if( continueSet == null )
                copy( currentOutSet, mergedOut );
            else
                merge( currentOutSet, continueSet, mergedOut );
            if(DEBUG){
                System.out.println(" whilestmt: merged current and continue: ");
                System.out.println(mergedOut);
            }


            //setup the current out as the merged out
            //this is the current out before processing the loop
            //condition
            currentOutSet = backupSet( mergedOut );

            //setup in for loop condition case
            //first merge the mergedout with the loop in
            merge( loopInSet, mergedOut, mergedOut );
            currentInSet = backupSet( mergedOut );
            if(DEBUG){
                System.out.println(" whilestmt: merged loopInSet and mergedOut: ");
                System.out.println(mergedOut);
            }


            caseWhileCondition( loopCond );
            

            newOut = currentOutSet;
            //merge the loop condition out and break sets
            //use for new out

            //loop until there is no change
            if( DEBUG )
                if(iterCount>=countBad){
                    System.err.println("!!!!!!!!!!**********\nwoah too many iterations: " + iterCount);
                    System.err.println( node.getPrettyPrinted() );
                    System.err.println("!!!!!!!!!!: " + iterCount);
                    countBad*=2;
                }
            iterCount++;
            if(DEBUG){
                System.out.println(" whilestmt: previousOut and newOut: ");
                System.out.println(previousOut);
                System.out.println(newOut);
                System.err.println(" whilestmt: previousOut and newOut: ");
            }
        }while( !previousOut.equals( newOut ) );


        breakSet = processBreaks();
        if(DEBUG){
            System.out.println(" whilestmt: breakSet: ");
            System.out.println(breakSet);
        }
        
        mergedOut = mergeOuts( currentOutSet, breakSet );
        if(DEBUG){
            System.out.println(" whilestmt: merged out of condition, and breaks: ");
            System.out.println(mergedOut);
        }
        
        //set currentOut to the out of the FP computation and the
        //result of processing breaks.
        currentOutSet = mergedOut;
        
        setOutFlow( node, currentOutSet );
        loopStack.pop();
    }

    public void caseIfStmt( IfStmt node )
    {
        //backup the true/false out sets and null them
        A backupTrueSet = getTrueOutSet();
        A backupFalseSet = getFalseOutSet();
        unsetTrueFalseOutSet();

        Expr condition;
        ASTNode body;

        A ifInSet = saveInSet( node );

        //setup mergedOuts to capture the outs of each branch
        A mergedOuts = null;

        //setup nextIn to be the in for the next block
        //initialize with the ifInSet
        A nextIn = backupSet( ifInSet );

        //process each IfBlock
        for( IfBlock block : node.getIfBlocks() ) {
            if( DEBUG )
                System.out.println( "if block" );
            condition = block.getCondition();
            body = block.getStmts();
            currentInSet = backupSet( nextIn );
            caseIfCondition( condition );
            A trueOutSet = getTrueOutSet();
            A falseOutSet = getFalseOutSet();
            if( trueOutSet != null && falseOutSet != null ){
                nextIn = backupSet( falseOutSet );
                currentOutSet = backupSet( trueOutSet );
            }
            else{
                nextIn = backupSet( currentOutSet );
                currentOutSet = backupSet( nextIn );
            }
            analyze( body );
            if ( DEBUG )
                System.out.println("after IF block "+currentOutSet);
            if( mergedOuts == null ){
                mergedOuts = backupSet( currentOutSet );
            }
            else{
                merge( currentOutSet, mergedOuts, mergedOuts );
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
            currentInSet = backupSet( nextIn );
            currentOutSet = backupSet( nextIn );
            if( DEBUG )
                System.out.println( "!in before body "+currentInSet );
            analyze( body );
            if( DEBUG ){
                System.out.println( "!in after body "+currentInSet );
                 System.out.println( "!out after body "+currentOutSet );
            }
            if(DEBUG)
                System.out.println("merging " + currentOutSet.toString()+
                        "\n  and "+mergedOuts );
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
        setOutFlow( node, currentOutSet );

        //restore the true/false out sets to their backups
        setTrueFalseOutSet( backupTrueSet, backupFalseSet );
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
