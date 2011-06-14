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

package natlab.Static.interproceduralAnalysis;

import ast.Function;
import natlab.toolkits.analysis.Analysis;

/**
 * This interface represents an intra-procedural analysis, as used by a 
 * inter-procedural analysis. A FunctionAnalysis always operates on a Function.
 * It has an argument set, and a result set.
 * Note that instances of this object are created via the
 * InterproceduralAnalysisFactory
 * 
 * @author ant6n
 * @param <A>  the argument set that is given to the function to run the analysis
 * @param <R> the result set that the analysis returns for that function
 */

public interface FunctionAnalysis<A,R> extends Analysis{
    
    
    public Function getTree();
    
    /**
     * returns the result set.
     * Runs the analysis if it has not been computed.
     * @return
     */
    public R getResult();
    
    /**
     * returns a default/empty result which should be an initial result
     * if the funciton is recusive or mutually recursive.
     * This result has to be calculated without much computation or analysis,
     * requiring no results from other functions. It should also be possible to
     * call this method while the actual analysis result is being computed - because
     * this will be called if a recursive call with the same arguments is found
     * while computing said result.
     */
    public R getDefaultResult();
}



