package natlab.tame.builtin.isComplexInfoProp.ast;


import java.util.*;

import beaver.Symbol;

import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropMatch;
import natlab.tame.valueanalysis.value.*;

public abstract class ICNode extends Symbol {
	
	abstract public isComplexInfoPropMatch match(boolean isPatternSide, isComplexInfoPropMatch previousMatchResult, List<? extends Value<?>> argValues);

}