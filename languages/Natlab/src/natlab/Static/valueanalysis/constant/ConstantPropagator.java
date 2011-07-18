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

import java.util.List;

import natlab.Static.builtin.Builtin;
import natlab.Static.builtin.BuiltinVisitor;

/**
 * propagates constants.
 * For any case, takes the arguments as a list, and returns the result as a constant.
 * If the result is not a constant, returns null.
 * This is a singleton class, whose only instance is returned via 'getInstance()'.
 * 
 * TODO: how to deal with error cases?
 * 
 * 
 * @author adubra
 */

public class ConstantPropagator extends BuiltinVisitor<List<Constant>, Constant>{
    static ConstantPropagator instance = null;
    static ConstantPropagator getInstance(){
        if (instance == null) instance = new ConstantPropagator();
        return instance;
    }
    
    private ConstantPropagator(){}
     
    @Override
    public Constant caseBuiltin(Builtin builtin, List<Constant> arg) {
        return null;
    }
    
    /* the constants 
     * TODO - check whether there are no arguments */
    @Override
    public Constant casePi(Builtin builtin, List<Constant> arg) {
        return Constant.get(Math.PI);
    }
   
    @Override
    public Constant caseTrue(Builtin builtin, List<Constant> arg) {
        //TODO - deal with args
        return Constant.get(true);
    }
    @Override
    public Constant caseFalse(Builtin builtin, List<Constant> arg) {
        return Constant.get(false);
    }
}



