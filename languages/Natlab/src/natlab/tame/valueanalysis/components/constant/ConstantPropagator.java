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

package natlab.tame.valueanalysis.components.constant;

import java.util.List;

import natlab.tame.builtin.Builtin;
import natlab.tame.builtin.BuiltinVisitor;
import natlab.tame.valueanalysis.value.*;

/**
 * propagates constants.
 * For any case, takes the arguments as an args<V>, and returns
 * the result as a constant.
 * If the result is not a constant, returns null.
 * This is a singleton class, whose only instance is returned via 'getInstance()'.
 * Note that a builtin may return a constant, even if the arguments don't have constants
 * associated with them (for example for the function 'class').
 * 
 * 
 * TODO: how to deal with error cases? -- for now just return null...
 * TODO: right now this can only return ONE result. maybe it should be changed to return a list?
 * 
 * Is the value analyses evolve, this should grow big.
 * 
 * 
 * @author adubra
 */

public class ConstantPropagator<V extends Value<V>> extends BuiltinVisitor<Args<V>, Constant>{
    @SuppressWarnings("rawtypes")
	static ConstantPropagator instance = null;
    @SuppressWarnings({ "rawtypes", "unchecked" })
	static public <V extends Value<V>> ConstantPropagator<V> getInstance(){
        if (instance == null) instance = new ConstantPropagator();
        return instance;
    }
    private ConstantPropagator(){} //hidden private constructor
     
    @Override
    public Constant caseBuiltin(Builtin builtin, Args<V> arg) {
        return null;
    }
    
    @Override
    public Constant casePi(Builtin builtin, Args<V> arg) {
    	if (arg.size() > 0) return null;
        return Constant.get(Math.PI);
    }
    
    @Override
    public Constant caseTrue(Builtin builtin, Args<V> arg) {
    	if (arg.size() > 0) return null;
        return Constant.get(true);
    }

    @Override
    public Constant caseFalse(Builtin builtin, Args<V> arg) {
    	if (arg.size() > 0) return null;
        return Constant.get(true);
    }

    @Override
    public Constant caseIsequal(Builtin builtin, Args<V> arg) {
    	List<Constant> constants;
        if (arg.size() == 2 && ((constants=arg.getConstants())!=null)){
            Constant a = constants.get(0);
            Constant b = constants.get(1);
            if (a.getClass().equals(b.getClass())){
                return Constant.get(a.equals(b));
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    
    
    @Override
    public Constant caseEq(Builtin builtin, Args<V> arg) {
        return caseIsequal(builtin, arg);
    }
    
    @Override
    public Constant caseAny(Builtin builtin, Args<V> arg) {
    	List<Constant> constants;
        if (arg.size() == 1 && ((constants=arg.getConstants())!=null)){
            Constant a = constants.get(0);
            if (a instanceof DoubleConstant){
                return Constant.get(((DoubleConstant)a).getValue() != 0);
            } else if (a instanceof LogicalConstant){
                return Constant.get(((LogicalConstant)a).getValue());
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    
    
    @Override
    public Constant caseClass(Builtin builtin, Args<V> arg) {
    	if (arg.size() == 1){
    		return Constant.get(arg.get(0).getMatlabClass().getName());    		
    	}
    	return null;
    }
}




