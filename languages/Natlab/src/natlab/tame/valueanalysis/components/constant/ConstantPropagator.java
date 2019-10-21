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

import natlab.tame.builtin.Builtin;
import natlab.tame.builtin.BuiltinVisitor;
import natlab.tame.valueanalysis.components.constant.*;
import natlab.tame.valueanalysis.components.shape.DimValue;
import natlab.tame.valueanalysis.components.shape.HasShape;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;
import natlab.tame.valueanalysis.basicmatrix.BasicMatrixValue;
import natlab.tame.valueanalysis.components.shape.Shape;
import java.util.List;

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
    private boolean DEBUG = false;
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

	/**
	 * Six built-ins to propagate here, Eye, Inf, Eye, Ones, Zeros, Magic (Incorrectly placed here)
	 * @param builtin
	 * @param arg
	 * @return
	 */
    @Override
    public Constant caseAbstractNumericalByShapeAndTypeMatrixCreation(Builtin builtin, Args<V> arg){



    	return null;
	}
	@Override
	public Constant caseMagic(Builtin builtin, Args<V> args){
    	return null;
	}
    @Override
	public Constant caseEnd(Builtin builtin, Args<V> arg){
		if(arg.size() == 3 && arg.get(0) instanceof BasicMatrixValue && arg.get(1) instanceof BasicMatrixValue && arg.get(2) instanceof BasicMatrixValue){
            BasicMatrixValue matrix = (BasicMatrixValue)arg.get(0);
			BasicMatrixValue indices_num_matval = (BasicMatrixValue)arg.get(1);

            BasicMatrixValue dim = (BasicMatrixValue) arg.get(2);
			DoubleConstant constDouble;
			DoubleConstant indices_num_matvalDouble;
			if(dim.hasConstant()&& indices_num_matval.hasConstant()){
				Constant dimConst = dim.getConstant();
				Constant indices_num_matvalConst = indices_num_matval.getConstant();
				if (dimConst instanceof DoubleConstant && indices_num_matvalConst instanceof DoubleConstant){
					constDouble = (DoubleConstant)dimConst;
					indices_num_matvalDouble = (DoubleConstant)indices_num_matvalConst;
					if(matrix.hasShape()){
						Shape shape = matrix.getShape();
						int dimVal = constDouble.getValue().intValue();
						int indicesVal = indices_num_matvalDouble.getValue().intValue();
						if(shape.getDimensions().size() < dimVal) return null;
						if(indicesVal == 1){
							int numel = shape.getHowManyElements(0);
							if(numel > -1) return Constant.get(numel);
						}else if(shape.getDimensions().get(dimVal-1).hasIntValue()){
							return Constant.get(shape.getDimensions().get(dimVal-1).getIntValue());
						}
					}
				}
			}
		}
    	return null;
	}

    @Override
    public Constant caseFalse(Builtin builtin, Args<V> arg) {
    	if (arg.size() > 0) return null;
        return Constant.get(false);
        //Originaly, it's "true" inside the parenthesis, Xu corrected it to "false" @ 25,Dec,2012,6:23pm
    }

    @Override
    public Constant caseIsequal(Builtin builtin, Args<V> arg) {
    	List<Constant> constants;
        if (arg.size() == 2 && ((constants=arg.getConstants())!=null)){
            Constant a = constants.get(0);
            Constant b = constants.get(1);

            if (a.getClass() == LogicalConstant.class) {
                if (DEBUG) System.out.println("Coercing value " + a + " to double ");
                a = toDouble((LogicalConstant)a);
            }
            if (b.getClass() == LogicalConstant.class) {
                if (DEBUG) System.out.println("Coercing value " + b + " to double ");
                b = toDouble((LogicalConstant)b);
            }

            if (a.getClass().equals(b.getClass())) {
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
    
    @Override
    /**
	 * unary plus
	 */
    public Constant caseUplus(Builtin builtin, Args<V> arg) {
    	if(arg.size() == 1) {
    		if (((HasConstant)arg.get(0)).getConstant() instanceof DoubleConstant) {
    			Double res = ((DoubleConstant)((HasConstant)arg.get(0))
    					.getConstant()).getValue();
    			return new DoubleConstant(res);
    		}
    	}
    	return null;
    }
    
    @Override
    /**
	 * binary plus.
	 */
    public Constant casePlus(Builtin builtin, Args<V> arg) {
    	if (arg.size() == 2) {
    		if (((HasConstant)arg.get(0)).getConstant() instanceof DoubleConstant 
    				&& ((HasConstant)arg.get(1)).getConstant() instanceof DoubleConstant) {
    			Double res = ((DoubleConstant)((HasConstant)arg.get(0)).getConstant()).getValue() + 
    					((DoubleConstant)((HasConstant)arg.get(1)).getConstant()).getValue();
    			return new DoubleConstant(res);
    		}
    	}
    	return null;
    }
    
    @Override
    /**
	 * unary minus.
	 */
    public Constant caseUminus(Builtin builtin, Args<V> arg) {
    	if(arg.size() == 1) {
    		if (((HasConstant)arg.get(0)).getConstant() instanceof DoubleConstant) {
    			Double res = -((DoubleConstant)((HasConstant)arg.get(0))
    					.getConstant()).getValue();
    			return new DoubleConstant(res);
    		}
    	}
    	return null;
    }
    
    @Override
    /**
	 * binary minus.
	 */
    public Constant caseMinus(Builtin builtin, Args<V> arg) {
    	if (arg.size() == 2) {
    		if (((HasConstant)arg.get(0)).getConstant() instanceof DoubleConstant 
    				&& ((HasConstant)arg.get(1)).getConstant() instanceof DoubleConstant) {
    			Double res = ((DoubleConstant)((HasConstant)arg.get(0)).getConstant()).getValue() - 
    					((DoubleConstant)((HasConstant)arg.get(1)).getConstant()).getValue();
    			return new DoubleConstant(res);
    		}
    	}
    	return null;
    }
    
    @Override
	/**
	 * element-by-element multiplication.
	 */
    public Constant caseTimes(Builtin builtin, Args<V> arg) {
    	if (arg.size() == 2) {
    		if (((HasConstant)arg.get(0)).getConstant() instanceof DoubleConstant 
    				&& ((HasConstant)arg.get(1)).getConstant() instanceof DoubleConstant) {
    			Double res = ((DoubleConstant)((HasConstant)arg.get(0)).getConstant()).getValue() * 
    					((DoubleConstant)((HasConstant)arg.get(1)).getConstant()).getValue();
    			return new DoubleConstant(res);
    		}
    	}
    	return null;
    }
    
    @Override
	/**
	 * matrix multiplication, when the arguments are both scalars, 
	 * it works the same as element-by-element multiplication.
	 */
    public Constant caseMtimes(Builtin builtin, Args<V> arg) {
    	return caseTimes(builtin, arg);
    }
    
    @Override
	/**
	 * element-by-element rdivision.
	 */
    public Constant caseRdivide(Builtin builtin, Args<V> arg) {
    	if (arg.size() == 2) {
    		if (((HasConstant)arg.get(0)).getConstant() instanceof DoubleConstant 
    				&& ((HasConstant)arg.get(1)).getConstant() instanceof DoubleConstant) {
    			Double res = ((DoubleConstant)((HasConstant)arg.get(0)).getConstant()).getValue() / 
    					((DoubleConstant)((HasConstant)arg.get(1)).getConstant()).getValue();
    			return new DoubleConstant(res);
    		}
    	}
    	return null;
    }
    
    @Override
	/**
	 * matrix rdivision, when the arguments are both scalars, 
	 * it works the same as element-by-element rdivision.
	 */
    public Constant caseMrdivide(Builtin builtin, Args<V> arg) {
    	return caseRdivide(builtin, arg);
    }
    
    @Override
	/**
	 * element-by-element power.
	 */
    public Constant casePower(Builtin builtin, Args<V> arg) {
    	if (arg.size() == 2) {
    		if (((HasConstant)arg.get(0)).getConstant() instanceof DoubleConstant 
    				&& ((HasConstant)arg.get(1)).getConstant() instanceof DoubleConstant) {
    			Double res = Math.pow(((DoubleConstant)((HasConstant)arg.get(0)).getConstant()).getValue(), 
    					((DoubleConstant)((HasConstant)arg.get(1)).getConstant()).getValue());
    			return new DoubleConstant(res);
    		}
    	}
    	return null;
    }
    
    @Override
    /**
     * array power, when the arguments are both scalars, 
     * it works the same as element-by-element power.
     */
    public Constant caseMpower(Builtin builtin, Args<V> arg) {
    	return casePower(builtin, arg);
    }
    
    @Override
	/**
	 * exponential
	 */
    public Constant caseExp(Builtin builtin, Args<V> arg) {
    	if (arg.size() == 1) {
    		if (((HasConstant)arg.get(0)).getConstant() instanceof DoubleConstant) {
    			Double res = Math.exp(((DoubleConstant)((HasConstant)arg.get(0)).getConstant()).getValue());
    			return new DoubleConstant(res);
    		}
    	}
    	return null;
    }
    
    @Override
    /**
     * length
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Constant caseLength(Builtin builtin, Args<V> arg) {
    	if (arg.size() == 1) {
    		if (arg.get(0) instanceof HasShape 
    				&& ((HasShape)arg.get(0)).getShape() != null 
    				&& ((HasShape)arg.get(0)).getShape().isConstant()) {
    			/*
    			 * actually, according to the rule in Matlab, 
    			 * length return the number of the largest dimension.
    			 */
    			int max = 0;
    			for (DimValue i : (List<DimValue>)((HasShape)arg.get(0))
    					.getShape().getDimensions()) {
    				if (i.getIntValue() > max) {
    					max = i.getIntValue();
    				}
    			}
    			return new DoubleConstant(max);
    		}
    	}
    	return null;
    }
    
    @Override
    /**
     * fix
     */
    public Constant caseFix(Builtin builtin, Args<V> arg) {
    	if (arg.size() == 1) {
    		if (((HasConstant)arg.get(0)).getConstant() instanceof DoubleConstant) {
    			int res = ((DoubleConstant)((HasConstant)arg.get(0)).getConstant()).getValue().intValue();
    			return new DoubleConstant(res);
    		}
    	}
    	return null;
    }

    private Constant toDouble(LogicalConstant a) {
        return new DoubleConstant(a.getValue() == new Boolean(true) ? 1 : 0);
    }

    private Constant toDouble(CharConstant a) {
        return new DoubleConstant(a.getValue().charAt(0) + 0);
    }
}




