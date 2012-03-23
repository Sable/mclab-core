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

package natlab.tame.interproceduralAnalysis;

import natlab.tame.callgraph.StaticFunction;

/**
* @param <F> the FunctionAnalysis type used to analyse each function/argument pair
* @param <A>  the argument set that is given to the function to run the analysis
* @param <R> the result set that the analysis returns for that function
*/
public interface InterproceduralAnalysisFactory<F extends FunctionAnalysis<A,R>, A,R> {
   public F newFunctionAnalysis(
            StaticFunction function, A argumentSet,
            InterproceduralAnalysisNode<F, A,R> node);
}
