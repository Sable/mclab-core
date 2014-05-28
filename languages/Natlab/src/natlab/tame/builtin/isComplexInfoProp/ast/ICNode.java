package natlab.tame.builtin.isComplexInfoProp.ast;


import java.util.List;

import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropMatch;
import natlab.tame.valueanalysis.value.Value;
import beaver.Symbol;

public abstract class ICNode extends Symbol {
	
	abstract public isComplexInfoPropMatch match(boolean isPatternSide, isComplexInfoPropMatch previousMatchResult, List<? extends Value<?>> argValues);

}