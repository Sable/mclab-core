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

import natlab.tame.classes.reference.PrimitiveClassReference;

/**
 * currently a scalar double only.
 * 
 * TODO - Make this a vector!
 * @author adubra
 */

public class DoubleConstant extends Constant {
    double value;
    
    /**
     * creates a scalar double constant with the given value
     */
    public DoubleConstant(double value){
        this.value = value;
    }
    
    @Override
    public Double getValue() {
        return value;
    }
    
    @Override
    public PrimitiveClassReference getMatlabClass() {
        return PrimitiveClassReference.DOUBLE;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof DoubleConstant)) return false;
        return ((DoubleConstant)obj).value == value;
    }
    
    @Override
    public boolean isScalar() {
        return true;
    }
    
    @Override
    public List<Integer> getShape() {
    	ArrayList<Integer> list = new ArrayList<Integer>(2);
    	list.add(1);
    	list.add(1);
    	return list;
    }

	@Override
	public String getisComplexInfo() {
		return "REAL";
	}

}
