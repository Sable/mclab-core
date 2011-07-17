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

package natlab.toolkits.analysis.test;

import java.util.*;
import ast.*;
import natlab.toolkits.analysis.*;
import natlab.toolkits.analysis.varorfun.*;

/**
 * @author Jesse Doherty
 */
public class UseCollector extends AbstractDepthFirstAnalysis<HashSetFlowSet<String>>
{
    private VFPreorderAnalysis kindAnalysis;

    private HashSetFlowSet<String> fullSet;
    private boolean inLHS = false;

    public UseCollector(ASTNode tree)
    {
        super(tree);
        fullSet = new HashSetFlowSet<String>();
        kindAnalysis = new VFPreorderAnalysis(tree);
        kindAnalysis.analyze();
    }

    public HashSetFlowSet<String> newInitialFlow()
    {
        return new HashSetFlowSet<String>();
    }

    public Set<String> getAllUses()
    {
        return fullSet.getSet();
    }
    public Set<String> getUses( Stmt node )
    {
        HashSetFlowSet<String> set = flowSets.get(node);
        if( set == null )
            return new HashSet<String>();
        else
            return set.getSet();
    }

    public void caseAssignStmt( AssignStmt node )
    {
        HashSetFlowSet<String> prevSet = currentSet;
        inLHS = true;
        currentSet = newInitialFlow();

        analyze(node.getLHS() );
        inLHS = false;
        analyze( node.getRHS() );

        flowSets.put(node, currentSet);
        fullSet.addAll( currentSet );

        if( prevSet != null )
            prevSet.addAll( currentSet );
        currentSet = prevSet;
    }
    public void caseStmt( AssignStmt node )
    {
        HashSetFlowSet<String> prevSet = currentSet;
        currentSet = newInitialFlow();

        caseASTNode( node );

        flowSets.put(node, currentSet);
        fullSet.addAll( currentSet );

        if( prevSet != null )
            prevSet.addAll( currentSet );
        currentSet = prevSet;
    }

    public void caseParameterizedExpr( ParameterizedExpr node )
    {
        analyzeAsNotLHS( node.getArgs() );
        analyze( node.getTarget() );
    }

    public void caseNameExpr( NameExpr node )
    {
        if( !inLHS ){
            if( maybeVar( node ) )
                currentSet.add(node.getName().getID());
        }
    }

    public void analyzeAsNotLHS( ASTNode node ) 
    {
        boolean bakInLHS = inLHS;
        inLHS = false;
        analyze( node );
        inLHS = bakInLHS;
    }

    /**
     * A helper method to abstract away the test to see if an name
     * expression might be a variable.
     */
    public boolean maybeVar( Expr expr )
    {
        if( expr instanceof NameExpr ){
            NameExpr nameExpr = (NameExpr)expr;
            if( nameExpr.tmpVar )
                return true;
            else{
                Name name = nameExpr.getName();
                if (kindAnalysis.getFlowSets().containsKey(name)){
                    kindAnalysis.analyze();
                }
                VFDatum kind = kindAnalysis.getFlowSets().get(name).contains(nameExpr.getName().getID());
                return (kind!=null) && (kind.isVariable() || kind.isID());
            }
        }
        return false;
    }
}
