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

import analysis.AbstractStructuralBackwardAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.BreakStmt;
import ast.ContinueStmt;
import ast.Expr;

/**
 * A simple abstract implementation of a
 * AbstractStructuralBackwardAnalysis. This class provides some simple
 * implementations of methods such as processBreaks and caseBreakStmt.
 *
 * @see AbstractStructuralBackwardAnalysis
 */
public abstract class NatlabAbstractSimpleStructuralBackwardAnalysis<A> extends analysis.AbstractStructuralBackwardAnalysis<A>
{

    public NatlabAbstractSimpleStructuralBackwardAnalysis(ASTNode tree){
        super( tree );
    }

    public void caseLoopVar( AssignStmt node )
    {
        caseAssignStmt( node );
    }


    public void caseBreakStmt( BreakStmt node )
    {
        LoopFlowsets loop = loopStack.peek();
        currentOutSet = copy(loop.getBreakInFlow());
    }
    public void caseContinueStmt( ContinueStmt node )
    {
        LoopFlowsets loop = loopStack.peek();
        currentOutSet = copy(loop.getBreakInFlow());
    }
    public void caseCondition( Expr condExpr )
    {
        caseExpr( condExpr );
    }
    public void caseLoopVarAsCondition( AssignStmt node )
    {
        caseLoopVar( node );
    }
    public void caseLoopVarAsInit( AssignStmt node )
    {
        caseLoopVar( node );
    }
    public void caseLoopVarAsUpdate( AssignStmt node )
    {
        caseLoopVar( node );
    }
        
}
