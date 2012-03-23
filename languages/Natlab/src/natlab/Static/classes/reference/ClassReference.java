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

package natlab.tame.classes.reference;
/**
 * A reference to a class.
 * Objects of this type are immutable.
 * 
 * These should store as much as possible about the class that is statically known,
 * i.e. known without looking at the actual file environment. These means that the
 * information provided by these references should also be independent of the
 * environment.
 * 
 * toString() should have the same result as getName() - at least in the absence of
 * packages.
 * 
 * @author ant6n
 */
public abstract interface ClassReference {
    public abstract boolean isBuiltin();
    public abstract String getName();
}


