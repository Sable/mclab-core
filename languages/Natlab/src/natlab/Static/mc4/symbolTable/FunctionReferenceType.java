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

package natlab.Static.mc4.symbolTable;

import natlab.Static.mc4.*;
import natlab.toolkits.path.FunctionReference;

/**
 * a symbol table entry that refers to another function via a function reference
 *
 */

public class FunctionReferenceType extends FunctionType {
    FunctionReference ref;
    
    public FunctionReferenceType(FunctionReference ref){
        this.ref = ref;
    }
    
    public FunctionReference getFunctionReference(){
    	return ref;
    }
    
    public String toString() {
        return "call to: "+ref.toString();
    }

	public FunctionReferenceType copy() {
		return new FunctionReferenceType(ref);
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof FunctionReferenceType) {
			FunctionReferenceType fRef = (FunctionReferenceType) obj;
			return fRef.ref.equals(ref);
		}else return false;
	}
}
