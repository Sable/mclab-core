package natlab.tame.valueanalysis.components.shape;

/**
 * This is the value class for shape's dimensions, contain only two fields, 
 * one is Integer value, the other is String symbolic which for the case 
 * we may not know the exact value of this dimension, we can use the symbolic 
 * to represent this dimension or compare this dimension with other shape's 
 * certain dimension.
 * TODO do we also need to add range value info here?
 * @author Xu
 *
 */
public class DimValue {
	Integer value;
	String symbolic = "?";
	
	public DimValue() {}
	
	public DimValue(Integer value, String symbolic) {
		this.value = value;
		this.symbolic = symbolic;
	}
	
	public boolean hasValue() {
		return this.value!=null;
	}
	
	public boolean hasSymbolic() {
		return this.symbolic!=null;
	}
	
	public Integer getValue() {
		return this.value;
	}
	
	public String getSymbolic() {
		return this.symbolic;
	}
	
	/**
	 * used when we want to know whether a certain dimension is equal to 1
	 * @return
	 */
	public boolean equalsOne() {
		if (value!=null&&value==1) return true;
		return false;
	}
	
	public boolean equals(DimValue o) {
		if (value!=null&&o.hasValue()) {
			if (value==o.getValue()) return true;
			return false;
		}
		else if (value==null&&!o.hasValue()) {
			if (symbolic==o.getSymbolic()) return true;
			return false;
		}
		return false;
	}
	@Override
	public String toString() {
		return hasValue() ? value.toString() : symbolic;
	}
}
