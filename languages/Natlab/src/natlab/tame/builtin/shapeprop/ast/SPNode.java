package natlab.tame.builtin.shapeprop.ast;

import beaver.Symbol;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.*;

/**
 * this is the superclass of all the shape equation language node classes, 
 * every subclass should inherit or implement this abstract method, which 
 * I call it matching algorithm method.
 * 
 * @author XU
 *
 */
public abstract class SPNode<V extends Value<V>> extends Symbol {
	 
	abstract public ShapePropMatch<V> match(
			 boolean isPatternSide, ShapePropMatch<V> previousMatchResult
			 , Args<V> argValues, int Nargout); 
}