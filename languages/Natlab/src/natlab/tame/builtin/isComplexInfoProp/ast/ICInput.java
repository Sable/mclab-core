package natlab.tame.builtin.isComplexInfoProp.ast;

import java.util.List;

import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropMatch;

public abstract class ICInput extends ICNode{

	abstract public isComplexInfoPropMatch match(boolean isPatternSide, isComplexInfoPropMatch previousMatchResult, List<Integer> argValues);
}
