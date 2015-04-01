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

package extension2;

import ast.*;
import analysis.*;
import natlab.toolkits.analysis.test.NameCollector;
/**
 * @author Jesse Doherty
 */
public class ExtendedNameCollector extends NameCollector
{

    public ExtendedNameCollector( ASTNode tree )
    {
        super(tree);
    }
    public void caseIncStmt( IncStmt node )
    {
        inLHS = true;
        currentSet = newInitialFlow();
        analyze( node.getTarget() );
        flowSets.put(node,currentSet);
        fullSet.addAll( currentSet );
        inLHS = false;
    }

}
