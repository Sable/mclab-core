// =========================================================================== //
//                                                                             //
// Copyright 2008-2011 Andrew Casey, Jun Li, Jesse Doherty,                    //
//   Maxime Chevalier-Boisvert, Toheed Aslam, Anton Dubrau, Nurudeen Lameed,   //
//   Amina Aslam, Rahul Garg, Soroush Radpour, Olivier Savary Belanger,        //
//   Laurie Hendren, Clark Verbrugge and McGill University.                    //
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
//   limitations under the License.                                            //
//                                                                             //
// =========================================================================== //

package natlab.toolkits.filehandling;
/**
 * This is an interface that can be used by analyses (e.g.) to ask whether
 * a certain name exists as a function or script in the environment.
 *
 * This lookup is assumed to only do the lookup of
 * - builtins
 * - private functions
 * - class constructors
 * - overloaded methods
 * - functions in the current folder
 * - functions elsewhere on the path
 * 
 * The local lookup, i.e. within a given Program object has to be done
 * in another manner. These include
 * - recursive calls (the name of the function or script itself)
 * - nested functions
 * - sibling functions
 * 
 * @author ant6n
 */
public interface FunctionOrScriptQuery {
    /**
     * Returns true if a function or script exists in the file path environment
     * represented by this FunctionOrScriptQuery object.
     * 
     * @param name
     * @return true if such a function or script exists
     */
	boolean isPackage(String name);
    boolean isFunctionOrScript(String name);
}
