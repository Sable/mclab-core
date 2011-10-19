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

package natlab.Static.valueanalysis.value;

import java.util.HashSet;
import java.util.List;

import natlab.Static.classes.reference.FunctionHandleClassReference;
import natlab.toolkits.path.FunctionReference;

/**
 * This represents value which refers to a function handle.
 * Stores along with it the possible set of function hanldes that this can refer to.
 * A null element refers to a reference to a function which is unknown.
 * 
 * TODO - is it bad to have the null element?
 * @author adubra
 */

public class FunctionHandleValue<D extends MatrixValue<D>> implements Value<D> {
    HashSet<FunctionReference> functions = new HashSet<FunctionReference>();
    
    public FunctionHandleValue(){
    }
    public FunctionHandleValue(FunctionReference f){
        functions.add(f);
    }
    public FunctionHandleValue(FunctionHandleValue<D> other){
        functions.addAll(other.functions);
    }
    
    @Override
    public FunctionHandleClassReference getMatlabClass() {
        return FunctionHandleClassReference.getInstance();
    }

    @Override
    public Value merge(Value v) {
        if (v instanceof FunctionHandleValue){
            FunctionHandleValue result = new FunctionHandleValue(this);
            result.functions.addAll(((FunctionHandleValue)v).functions);
            return result;
        } else {
            return null;
        }
    }
    
    /**
     * returns the set of possible functions this function handle represents
     * @return
     */
    public HashSet<FunctionReference> getFunctions(){
        return this.functions;
    }
    
    @Override
    public String toString() {
        return "(function_handle,"+functions+")";
    }
    
    @Override
    public Value<D> subsref(List<Value<D>> indizes) {
        throw new UnsupportedOperationException("function handles do not support indexint");
        //TODO - or should they support it - this is the same as calling a function
    }
    @Override
    public Shape<D> getShape() {
        return Shape.<D>scalar();
    }
    @Override
    public boolean hasShape() {
        return true;
    }
}
