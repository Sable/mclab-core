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

package natlab.toolkits.rewrite.inline;

import natlab.tame.tir.TIRCommentStmt;
import ast.Function;

/**
 * Represents a InlineQuery object, which can be used to inline functions in other functions.
 * It will add comments at the beginning and end of the inlined function's body.
 * This will insert TIRCommentStmt's with comments (as opposed to EmptyStmts, it's parent class).
 * 
 * @author ant6n
 */
public class PutCommentsInlineQuery implements InlineQuery<Function, Function> {


    public boolean doInline(InlineInfo<Function, Function> i) {
        i.getInlinedScriptOrFunction().getStmtList().insertChild(new TIRCommentStmt(getHeader(i)), 0);
        i.getInlinedScriptOrFunction().getStmtList().add(new TIRCommentStmt(getFooter(i)));
        return true;
    }

 
    private String getHeader(InlineInfo<Function, Function> info){
        String s = "Start of Function [...] = "+info.getInlinedScriptOrFunction().getName()+"(";
        for (int i = 0; i < info.getParameters().getNumChild(); i++ ){
            s += info.getParameters().getChild(i).getPrettyPrinted()+"->"
                +info.getInlinedScriptOrFunction().getInputParam(i).getPrettyPrinted()
                +((info.getParameters().getNumChild()-1 == i)?"":", ");
        }
        return s+")";
    }

    private String getFooter(InlineInfo<Function, Function> info){
        String s = "End of Function [";
        for (int i = 0; i < info.getTargets().getNumChild(); i++ ){
            s += info.getTargets().getChild(i).getPrettyPrinted()+"<-"
                +info.getInlinedScriptOrFunction().getOutputParam(i).getPrettyPrinted()
                +((info.getTargets().getNumChild()-1 == i)?"":", ");
        }
        return s+"] = "+info.getInlinedScriptOrFunction().getName()+"(...)";
    }

}


