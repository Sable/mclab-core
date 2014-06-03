package natlab.tame.builtin.isComplexInfoProp.ast;

import java.util.List;

import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropMatch;
import natlab.tame.valueanalysis.value.Value;

public class ICComplexValueWCExpanded extends ICAbstractValue{
			ICAbstractValue xv;
			ICConditionSet cos;
			
			public ICComplexValueWCExpanded (ICAbstractValue xv, ICConditionSet cos)
			{
				this.xv = xv;
				this.cos =  cos;
			}
			
			public String toString()
			{
				return xv.toString()+"{"+cos.toString()+"}";
			}

			@Override
			public isComplexInfoPropMatch match(boolean isPatternSide,
					isComplexInfoPropMatch previousMatchResult,
					List<? extends Value<?>> argValues) {
				// TODO Auto-generated method stub
				return null;
			}
}
