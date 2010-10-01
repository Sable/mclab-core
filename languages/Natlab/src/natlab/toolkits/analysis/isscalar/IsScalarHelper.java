package natlab.toolkits.analysis.isscalar;

import java.util.Collection;
import java.util.HashSet;

import ast.Expr;
import ast.NameExpr;

import natlab.toolkits.analysis.varorfun.DataCollectFlowSet;
import natlab.toolkits.analysis.varorfun.DataPair;

public class IsScalarHelper {
	
	public static boolean equals(DataCollectFlowSet<String, IsScalarType> flowSet1, DataCollectFlowSet<String, IsScalarType> flowSet2) {
		if (flowSet1.size() != flowSet2.size()) {
			return false;
		}
		for (DataPair<String, IsScalarType> pair : flowSet1.toList()) {
			IsScalarType type2 = flowSet2.contains(pair.getKey());
			if (type2 != pair.getValue()) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * returns the addition of two flow sets. information is only added nothing gets lost.
	 */
	public static DataCollectFlowSet<String, IsScalarType> getSumOfTypes(
			DataCollectFlowSet<String, IsScalarType> oldSet,
			DataCollectFlowSet<String, IsScalarType> newSet) {
		DataCollectFlowSet<String, IsScalarType> out = new DataCollectFlowSet<String, IsScalarType>();
		Collection<String> remainingKeys = IsScalarHelper.getKeysForFlowSet(newSet);
			
		for (DataPair<String, IsScalarType> pair : oldSet.toList()) {
			String id = pair.getKey();
			IsScalarType oldType = pair.getValue();
			IsScalarType newType = newSet.contains(id);
			IsScalarType addedType = IsScalarHelper.addTypes(oldType, newType);
			out.add(new DataPair<String, IsScalarType>(id, addedType));
			remainingKeys.remove(id);
		}
		
		for (String key : remainingKeys) {
			IsScalarType type = newSet.contains(key);
			out.add(new DataPair<String, IsScalarType>(key, type));
		}
		
		return out;
	}
	
	/**
	 * returns the addition of a IsScalarType information newType to an existing IsScalarType information without destroying information
	 */
	public static IsScalarType addTypes(IsScalarType oldType, IsScalarType newType) {
		if (oldType == null) {
			oldType = IsScalarTypePool.bottom();
		}
		if (newType == null) {
			newType = IsScalarTypePool.bottom();
		}
		if (oldType.isTop() || newType == IsScalarTypePool.top()) {
			return IsScalarTypePool.top();
		}
		if (newType.isBottom()) {
			return oldType;
		}
		return newType;
	}
	
	/**
	 * returns the IsScalarType of a variable at a point of confluency where this variable has IsScalarType a on one and b on another edge
	 */
	public static IsScalarType butterfly(IsScalarType a, IsScalarType b) {
		if (a == null) {
			a = IsScalarTypePool.bottom();
		}
		if (b == null) {
			b = IsScalarTypePool.bottom();
		}
		if (a.isBottom()) {
			return b;
		}
		if (b.isBottom()) {
			return a;
		}
		if (a.isScalar() && b.isScalar()) {
			return IsScalarTypePool.scalar();
		}
		if (a.isNonScalar() && b.isNonScalar()) {
			if (((NonScalar) a).hasSameDimensions((NonScalar) b)) {
				return a;
			}
			return new NonScalar(0,0);
		}
		return IsScalarTypePool.top();
	}

	/**
	 * returns the variable names of the data pairs contained in the passed flowSet
	 */
	public static Collection<String> getKeysForFlowSet(DataCollectFlowSet<String, IsScalarType> flowSet) {
		Collection<String> keys = new HashSet<String>();
		for (DataPair<String, IsScalarType> pair : flowSet.toList()) {
			keys.add(pair.getKey());
		}
		return keys;
	}
	
	/**
	 * returns the id of the given expression if it is a NameExpr
	 */
	public static String getIdForExpr(Expr expr) {
        if (expr instanceof NameExpr) {
        	return ((NameExpr) expr).getName().getID();
        }
        else {
    		System.out.println("unhandled case " + expr.getClass().getSimpleName());
    		return "";
        }
	}
}
