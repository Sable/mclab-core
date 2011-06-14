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

package natlab.Static.valueanalysis;

import java.util.HashMap;

import natlab.Static.classes.reference.*;
import natlab.Static.valueanalysis.value.*;

/**
 * This represents the map from matlabclass to abstractvalue.
 * In a value analysis, the flowset is a map from variable name to a map of matlab class to abstract value.
 * This class represents that second class.
 * 
 * TODO: this should be made immutable, probably
 * @author adubra
 */

public class ValueMap<T extends AbstractValue> extends HashMap<ClassReference, T>{
    public static final long serialVersionUID = 0;

}
