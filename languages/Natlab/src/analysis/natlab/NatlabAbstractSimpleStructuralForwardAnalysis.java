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

import analysis.AbstractStructuralForwardAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.BreakStmt;
import ast.ContinueStmt;

/**
 * A simple abstract implementation of a
 * AbstractStructuralForwardAnalysis. This class provides some simple
 * implementations of methods such as processBreaks and caseBreakStmt.
 *
 * @see AbstractStructuralForwardAnalysis
 */
public abstract class NatlabAbstractSimpleStructuralForwardAnalysis<A> extends analysis.AbstractStructuralForwardAnalysis<A>
{

    public NatlabAbstractSimpleStructuralForwardAnalysis(ASTNode tree){
        super( tree );
    }
    /**
     * A simple implementation of the caseBreakStmt. It copies the
     * in set to the out sets and adds it to the break list. 
     */
    public void caseBreakStmt( BreakStmt node )
    {
        A copiedOutSet = copy(currentInSet);
        if ( !loopStack.isEmpty() )
            loopStack.peek().addBreakSet( copiedOutSet );
    }

    /**
     * A simple implementation of the caseContinueStmt. It copies the
     * in set to the out set and adds it to the continue list.
     */
    public void caseContinueStmt( ContinueStmt node )
    {
        A copiedOutSet = copy(currentInSet);
        if ( !loopStack.isEmpty() )
            loopStack.peek().addContinueSet( copiedOutSet );
    }


    public void caseLoopVar( AssignStmt node )
    {
        caseAssignStmt( node );
    }


    /**
     * A simple implementation of processBreaks. It merges all break
     * sets into a single set and returns it.
     */
    public A processBreaks()
    {
        A mergedSets = null;

        for( A set : loopStack.peek().getBreakOutSets() ){
            if( mergedSets == null ){
              mergedSets = copy(set);
            } else {
              mergedSets = merge( set, mergedSets);
            }
        }

        return mergedSets;
    }

    /**
     * A simple implementation of processContinues. It merges all
     * break sets into a single set and returns it.
     */
    public A processContinues()
    {
        A mergedSets = null;

        for( A set : loopStack.peek().getContinueOutSets() ){
            if( mergedSets == null ){
                mergedSets = copy(set);
            } else {
                mergedSets = merge(set, mergedSets);
            }
        }
        return mergedSets;
    }

}
