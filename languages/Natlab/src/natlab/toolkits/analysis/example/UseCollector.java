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

package natlab.toolkits.analysis.example;

import java.util.Set;

import natlab.toolkits.analysis.varorfun.VFDatum;
import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import analysis.AbstractDepthFirstAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Expr;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.Stmt;

import com.google.common.collect.Sets;

/**
 * @author Jesse Doherty
 */
public class UseCollector extends AbstractDepthFirstAnalysis<Set<String>>
{
    private VFPreorderAnalysis kindAnalysis;

    private Set<String> fullSet;
    private boolean inLHS = false;

    public UseCollector(ASTNode<?> tree)
    {
        super(tree);
        fullSet = Sets.newHashSet();
        kindAnalysis = new VFPreorderAnalysis(tree);
        kindAnalysis.analyze();
    }

    public Set<String> newInitialFlow()
    {
        return Sets.newHashSet();
    }

    /**
     * Gets a set of all uses for the given tree.
     */
    public Set<String> getAllUses()
    {
        return Sets.newHashSet(fullSet);
    }
    /**
     * Gets a set of uses in the given statement. 
     */
    public Set<String> getUses( Stmt node )
    {
        Set<String> set = flowSets.get(node);
        if( set == null )
            return Sets.newHashSet();
        else
            return Sets.newHashSet(set);
    }

    /**
     * Finds all uses in the given assignment. It makes sure that the
     * target of the assignment isn't considered a use. 
     */
    public void caseAssignStmt( AssignStmt node )
    {
        Set<String> prevSet = currentSet;
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

    /**
     * Finds all uses in the given statement. 
     */
    public void caseStmt( AssignStmt node )
    {
        Set<String> prevSet = currentSet;
        currentSet = newInitialFlow();

        caseASTNode( node );

        flowSets.put(node, currentSet);
        fullSet.addAll( currentSet );

        if( prevSet != null )
            prevSet.addAll( currentSet );
        currentSet = prevSet;
    }

    /**
     * Makes sure that targets of assignments aren't considered
     * uses. Also makes sure that the arguments are seen. 
     */
    public void caseParameterizedExpr( ParameterizedExpr node )
    {
        analyzeAsNotLHS( node.getArgs() );
        analyze( node.getTarget() );
    }

    //NOT: More cases would be needed to make complete.

    /**
     * Checks if the name is possible a variable, and not the target
     * of an assignment; if it is, adds it.
     */
    public void caseNameExpr( NameExpr node )
    {
        if( !inLHS ){
            if( maybeVar( node ) )
                currentSet.add(node.getName().getID());
        }
    }

    /**
     * Helper method to analyze a given node, making sure it is
     * treated like it isn't the target of an assignment. It saves and
     * restores the state of {@code inLHS}
     */
    private void analyzeAsNotLHS( ASTNode<?> node ) 
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
                VFDatum kind = kindAnalysis.getFlowSets().get(name).get(nameExpr.getName().getID());
                return (kind!=null) && (kind.isVariable() || kind.isID());
            }
        }
        return false;
    }
}
