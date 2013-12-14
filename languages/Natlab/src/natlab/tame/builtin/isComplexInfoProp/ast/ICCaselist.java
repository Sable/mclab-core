package natlab.tame.builtin.isComplexInfoProp.ast;

import java.util.ArrayList;
import java.util.List;

import natlab.tame.builtin.isComplexInfoProp.isComplexInfoPropMatch;
import natlab.tame.builtin.shapeprop.ShapePropMatch;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Value;

public class ICCaselist extends ICNode {

	ICCaselist csl;
	ICCase cs;

	public ICCaselist(ICCaselist csl, ICCase cs) {
		this.csl = csl;
		this.cs = cs;
	}

	public String toString() {
		return (csl == null ? "" : csl.toString() + "||") + cs.toString();
	}

	@Override
	public isComplexInfoPropMatch match(boolean isPatternSide,
			isComplexInfoPropMatch previousMatchResult,
			List<? extends Value<?>> argValues) {

		isComplexInfoPropMatch match;
		if (csl != null) {
			 match = csl.match(isPatternSide, previousMatchResult, argValues);
			 if (match.getIsOutputDone() == true) {
					// System.out.println("matching done successfully!\n");

					return match;
				}
			match = cs.match(isPatternSide, previousMatchResult, argValues);
		}
			else {
				match = cs.match(isPatternSide, previousMatchResult, argValues);
			}
		

		if (match.getIsOutputDone() == true) {
			// System.out.println("matching done successfully!\n");

			return match;
		} else {
			// System.out.println("matching unsuccessful!\n");

			return match;
		}

	}

}
