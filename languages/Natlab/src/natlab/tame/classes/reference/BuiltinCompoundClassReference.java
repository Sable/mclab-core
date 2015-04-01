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

public enum BuiltinCompoundClassReference implements BuiltinClassReference {
    STRUCT,
    CELL;
    
    public boolean isInt(){ return false; }
    public boolean isFloat(){ return false; }
    public boolean isNumeric(){ return false; }
    public boolean isMatrix(){ return false; }
    
    private String name = this.name().toLowerCase();
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }
    
    @Override
    public String toString() {
        return getName();
    }

}
