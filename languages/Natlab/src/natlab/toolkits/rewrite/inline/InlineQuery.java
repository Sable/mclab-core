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

import ast.ASTNode;

/**
 * when inlining a function, there might be some cleanup that needs to be done.
 * This interface allows to either perform cleanup (like dealing with nargin,
 * vargin,vargout etc.), or forbidding the inline action altogether.
 */
public interface InlineQuery<InlinedScriptOrFunction extends ASTNode,TargetScriptOrFunction extends ASTNode>{
    /**
     * Determines whether the current inline action should be performed, and
     * allows updating the function to be inlined via the given QueryObject.
     * @param inlineInfo an Object containing information about the inline action
     * @return true if the inline action should be performed, false otherwise.
     */
    public boolean doInline(InlineInfo<InlinedScriptOrFunction,TargetScriptOrFunction> inlineInfo);
}

