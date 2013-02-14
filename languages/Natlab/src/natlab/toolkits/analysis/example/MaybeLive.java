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

import natlab.toolkits.analysis.HashSetFlowSet;
import natlab.toolkits.analysis.core.NameCollector;
import analysis.AbstractSimpleStructuralBackwardAnalysis;
import ast.ASTNode;
import ast.AssignStmt;
import ast.Name;
import ast.Stmt;

/**
 * Performs a naive Live Variable analysis. It ignores the possibility
 * of variables being used by function calls, script calls, and
 * evals. Basically it ignores dynamic behaviour and the lack of
 * scope. 
 *
 * @author Jesse Doherty
 */
public class MaybeLive 
    extends AbstractSimpleStructuralBackwardAnalysis<HashSetFlowSet<String>>
{

    private NameCollector nameCollector; 
    private UseCollector useCollector;


    public MaybeLive( ASTNode<?> tree)
    {
        super(tree);

        nameCollector = new NameCollector(tree);
        nameCollector.analyze();
        useCollector = new UseCollector(tree);
        useCollector.analyze();
    }

    /**
     * Merges the two sets by using the union defined by {@link
     * HashSetFlowSet}. 
     */
    public void merge( HashSetFlowSet<String> in1, 
                       HashSetFlowSet<String> in2,
                       HashSetFlowSet<String> out )
    {
        in1.union( in2, out );
    }

    /**
     * Copies {@code in} into {@code out} by using {@code in}'s {@code
     * copy(...)} method.
     */
    public void copy( HashSetFlowSet<String> in,
                      HashSetFlowSet<String> out )
    {
        in.copy(out);
    }
    
    /**
     * Returns a copy of {@code in} by using it's {@code copy()}
     * method. 
     */
    public HashSetFlowSet<String> copy( HashSetFlowSet<String> in)
    {
        return in.copy();
    }

    /**
     * The initial flow is an empty set. Initially, no variables are
     * live. 
     */
    public HashSetFlowSet<String> newInitialFlow()
    {
        return new HashSetFlowSet<String>();
    }

    /**
     * Creates the in-flow for an assignment statement. It uses the
     * {@link NameCollector} and {@link UseCollector} to find the
     * variable names to remove and add, respectively. It associates
     * the out and resulting in to the given node.
     */
    public void caseAssignStmt( AssignStmt node )
    {
        outFlowSets.put( node, currentOutSet );
        //HashSetFlowSet<String> workingInFlow = copy( currentOutSet
        //);
        currentInSet = copy( currentOutSet );
        
        Set<Name> defVars = nameCollector.getNames( node );
        Set<String> useVars = useCollector.getUses( node );

        for( Name def : defVars )
            currentInSet.remove( def.getID() );
        for( String use : useVars )
            currentInSet.add( use );

        inFlowSets.put( node, currentInSet );
        
    }

    /**
     * Creates the in-flow for an arbitrary statement. Uses the {@link
     * UseCollector} to find names to add to the flow. 
     */
    public void caseStmt( Stmt node )
    {
        outFlowSets.put( node, currentOutSet );
        HashSetFlowSet<String> myInSet = copy(currentOutSet);

        caseASTNode( node );

        Set<String> useVars = useCollector.getUses( node );

        for( String use : useVars )
            myInSet.add( use );

        currentInSet = myInSet;
        inFlowSets.put( node, currentInSet );
    }
    
}
