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

import natlab.tame.tir.analysis.TIRNodeCaseHandler;
import beaver.*;
import ast.*;

/**
 * An empty statement
 * All comments should be stored in here
 * Any analysis or rewrite should just ignore these.
 * Also acts as an empty line
 */
public class TIRCommentStmt extends EmptyStmt implements TIRStmt {
    private static final long serialVersionUID = 1L;

    public TIRCommentStmt(){}
    public TIRCommentStmt(java.util.List<Symbol> comments){
        this.setComments(comments);
    }
    public TIRCommentStmt(Symbol comment){
        this.addComment(comment);
    }
    
    /**
     * creates a comment statement from a given string, adding "% " at the beginning.
     */
    public TIRCommentStmt(String comment){
        this(new beaver.Symbol("% "+comment)); 
        //TODO - should be done via parsing the stmt "% "+comment  
    }
    
    
    @Override
    public void irAnalyize(TIRNodeCaseHandler irHandler) {
        irHandler.caseTIRCommentStmt(this);
    }

}
