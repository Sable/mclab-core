package natlab.tame.builtin.shapeprop.ast;

import java.util.*;

import beaver.Symbol;

import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.*;

public abstract class SPNode extends Symbol
{
	 abstract public ShapePropMatch match(boolean isPatternSide, ShapePropMatch previousMatchResult, List<? extends Value<?>> argValues);
	 
	 
}