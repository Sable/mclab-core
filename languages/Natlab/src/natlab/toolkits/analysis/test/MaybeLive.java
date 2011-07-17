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

import natlab.toolkits.analysis.*;
import natlab.toolkits.analysis.varorfun.*;

import ast.*;


import java.util.Set;
import java.util.HashSet;

/**
 * @author Jesse Doherty
 */
public class MaybeLive 
    extends AbstractSimpleStructuralBackwardAnalysis<HashSetFlowSet<String>>
{

    private NameCollector nameCollector;
    private UseCollector useCollector;

    private Set<String> workingSet;
    

    public MaybeLive( ASTNode tree)
    {
        super(tree);

        nameCollector = new NameCollector(tree);
        nameCollector.analyze();
        useCollector = new UseCollector(tree);
        useCollector.analyze();
    }

    public void merge( HashSetFlowSet<String> in1, 
                       HashSetFlowSet<String> in2,
                       HashSetFlowSet<String> out )
    {
        in1.union( in2, out );
    }

    public void copy( HashSetFlowSet<String> in,
                      HashSetFlowSet<String> out )
    {
        in.copy(out);
    }
    
    public HashSetFlowSet<String> copy( HashSetFlowSet<String> in)
    {
        return in.copy();
    }

    public HashSetFlowSet<String> newInitialFlow()
    {
        return new HashSetFlowSet<String>();
    }


    public void caseAssignStmt( AssignStmt node )
    {
        outFlowSets.put( node, currentOutSet );
        //HashSetFlowSet<String> workingInFlow = copy( currentOutSet
        //);
        currentInSet = copy( currentOutSet );
        
        Set<String> defVars = nameCollector.getNames( node );
        Set<String> useVars = useCollector.getUses( node );

        for( String def : defVars )
            currentInSet.remove( def );
        for( String use : useVars )
            currentInSet.add( use );

        inFlowSets.put( node, currentInSet );
        
    }

    public void caseStmt( Stmt node )
    {
        outFlowSets.put( node, currentOutSet );
        currentInSet = currentOutSet;
        inFlowSets.put( node, currentInSet );
    }
    
}
