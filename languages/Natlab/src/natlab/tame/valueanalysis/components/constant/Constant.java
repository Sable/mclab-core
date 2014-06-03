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

import natlab.tame.classes.reference.PrimitiveClassReference;
import ast.FPLiteralExpr;
import ast.IntLiteralExpr;
import ast.LiteralExpr;
import ast.StringLiteralExpr;

/**
 * represents an actual specific Matlab value.
 * Note that it may not be possible to represent all constant values. Even a specific
 * constant may not be able to represent all values - for example a constant for a specific
 * type may only be scalar, etc.
 * 
 * This is also a factory for constants, via the 'get' methods
 * 
 * @author ant6n
 */

public abstract class Constant {

    /**
     * returns the underlying value of the constant
     */
    public abstract Object getValue();
    
    /**
     * returns the Matlab class of the constant
     */
    public abstract PrimitiveClassReference getMatlabClass();
    
    /**
     * returns the union for this and the other constant
     * - i.e. it returns the same constant if this and the other are the same, null otherwise
     */
    public Constant merge(Constant other){
        if (this.equals(other)) return this;
        return null;
    }
    
    
    /**
     * returns true if the constant is scalar
     */
    abstract public boolean isScalar();
    
    
    /* factory methods */
    public static Constant get(boolean value){ return new LogicalConstant(value); }
    public static Constant get(double  value){ return new DoubleConstant(value); }
    public static Constant get(String  value){ return new CharConstant(value); }
    public static Constant get(LiteralExpr literal){
        if (literal instanceof FPLiteralExpr){
            return Constant.get(((FPLiteralExpr)literal).getValue().getValue().doubleValue());
        } else if (literal instanceof IntLiteralExpr) {
            return Constant.get(((IntLiteralExpr)literal).getValue().getValue().doubleValue());
        } else if (literal instanceof StringLiteralExpr){
            return Constant.get(((StringLiteralExpr)literal).getValue());
        } else {
            throw new UnsupportedOperationException("encountered unsupported literal "+literal);
        }
    }
    
    @Override
    public String toString() {
        return getValue().toString();
    }
    
    /**
     * returns the shape of the constant as a list of dimensions
     */
    abstract public List<Integer> getShape();

	abstract public String getisComplexInfo() ;
}


