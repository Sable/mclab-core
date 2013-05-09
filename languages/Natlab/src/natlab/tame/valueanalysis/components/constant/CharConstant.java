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

import java.util.ArrayList;
import java.util.List;

import natlab.tame.classes.reference.*;

/**
 * This is a char constant (String). This can only store char arrays which are strings,
 * i.e. a [1 n] array of char.
 * @author ant6n
 */
public class CharConstant extends Constant {
    String value;
    
    public CharConstant(String value){
       this.value = value;
    }

    @Override
    public PrimitiveClassReference getMatlabClass() {
        return PrimitiveClassReference.CHAR;
    }

    @Override
    public String getValue() {
        return value;
    }
    
    @Override
    public boolean isScalar() {
        return (value.length() == 1);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof CharConstant)) return false;
        return ((CharConstant)obj).value.equals(value);
    }
    
    @Override
    public List<Integer> getShape() {
    	ArrayList<Integer> list = new ArrayList<Integer>(2);
    	list.add(1);
    	list.add(value.length());
    	return list;
    }

	@Override
	public String getisComplexInfo() {
		return "REAL";
	}
}
