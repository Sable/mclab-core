// =========================================================================== //
//                                                                             //
// Copyright 2011 Anton Dubrau and McGill University.                          //
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

package natlab.tame.tir;
import java.util.Collections;
import java.util.LinkedList;

import natlab.tame.tir.analysis.TIRNodeCaseHandler;
import ast.*;

/**
 * assignments of the form
 * t = @foo
 * or
 * t = @(x1,x2,...)foo(a1,a2,..,x1,x2,...)
 * 
 * where foo is a name (function)
 * 
 * 
 */
abstract public class TIRAbstractCreateFunctionHandleStmt extends TIRAbstractAssignToVarStmt {
    private static final long serialVersionUID = 1L;

    
    /**
     * pass through constructor
     */
    public TIRAbstractCreateFunctionHandleStmt(Name lhs){
    	super(lhs);
    }
        
    
    /**
     * returns the Name of the function on the rhs
     * @return
     */
    abstract public Name getFunctionName();
        
        
    
    @Override
    public void irAnalyize(TIRNodeCaseHandler irHandler) {
        irHandler.caseTIRAbstractCreateFunctionHandleStmt(this);
    }

}

