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

package natlab.Static.valueanalysis.constant;

import natlab.Static.classes.reference.ClassReference;
import natlab.Static.classes.reference.PrimitiveClassReference;

/**
 * currently a scalar logical constant only.
 *
 * @author adubra
 */
public class LogicalConstant extends Constant {
    private boolean value;
    
    public LogicalConstant(boolean value) {
        this.value = value;
    }
    
    @Override
    public ClassReference getClassReference() {
        return PrimitiveClassReference.LOGICAL;
    }
    
    @Override
    public Boolean getValue() {
        return value;
    }
}
