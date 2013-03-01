package natlab.tame.builtin.shapeprop.ast;

import beaver.Symbol;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.*;

public abstract class SPNode<V extends Value<V>> extends Symbol{
	 abstract public ShapePropMatch<V> match(boolean isPatternSide, ShapePropMatch<V> previousMatchResult, Args<V> argValues, int num); 
}