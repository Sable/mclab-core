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

package analysis.extension2;

import nodecases.*;
import analysis.*;
import ast.*;

/**
 * @author Jesse Doherty
 */
public class Extension2BackwardsAnalysisHelper<A> extends analysis.natlab.NatlabBackwardsAnalysisHelper<A>
{
    public Extension2BackwardsAnalysisHelper( StructuralAnalysis<A> helpee )
    {
        super( helpee );
    }
    public Extension2BackwardsAnalysisHelper( StructuralAnalysis<A> helpee , NodeCaseHandler callback )
    {
        super(helpee, callback);
    }

    public void caseIncStmt( IncStmt node )
    {
        helpee.setCurrentOutSet( helpee.getCurrentInSet() );
        callback.caseIncStmt( node );
    }
}
