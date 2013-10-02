package natlab.tame.valueanalysis.components.rangeValue;

/**
 * the range value domain is: -inf, real numbers, real numbers with +/-, 
 * and +inf;
 * 
 * inside this class, we should also define some operations on the value 
 * in the range value domain, for example, what does it mean of -inf * inf?
 * 
 * there are 11 operators on the value in the domain: min, max, equals, 
 * unary -, unary +, binary -, binary +, times, division, log and exp.
 */	
public class DomainValue {
	// the data members are in the order of they are in the domain.
	boolean negativeInf = false;
	double realNum;
	boolean superscriptPlus = false;
	boolean superscriptMinus = false;
	boolean positiveInf = false;
	
	// default constructor
	public DomainValue() {}
	
	public DomainValue(double value) {
		this.realNum = value;
	}
	/**
	 * my own clone method, not using java clone.
	 * @param other
	 * @return
	 */
	DomainValue cloneThisValue() {
		DomainValue instance = new DomainValue();
		instance.negativeInf = this.negativeInf;
		instance.realNum = this.realNum;
		instance.superscriptPlus = this.superscriptPlus;
		instance.superscriptMinus = this.superscriptMinus;
		instance.positiveInf = this.positiveInf;
		return instance;
	}
	
	public int getIntValue() {
		return (int)this.realNum;
	}
	
	public void setSuperPlus() {
		this.superscriptPlus = true;
	}
	
	public void setSuperMinus() {
		this.superscriptMinus = true;
	}
	
	public boolean lessThanZero() {
		if (this.negativeInf) {
			return true;
		}
		else if (this.positiveInf) {
			return false;
		}
		else {
			if (this.realNum < 0) {
				return true;
			}
			else if (this.realNum == 0) {
				if (this.superscriptMinus) {
					return true;
				}
				else {
					return false;
				}
			}
			else {
				return false;
			}
		}
	}
	
	public boolean isEqualToZero() {
		if (this.negativeInf || this.positiveInf) {
			return false;
		}
		else if (this.realNum == 0 
				&& !this.superscriptMinus 
				&& !this.superscriptPlus) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean greaterThanZero() {
		return !this.lessThanZero() && !this.isEqualToZero();
	}
	
	DomainValue min(DomainValue other) {
		DomainValue res = new DomainValue();
		if (this.negativeInf || other.negativeInf) {
			res.negativeInf = true;
			return res;
		}
		if (this.positiveInf && other.positiveInf) {
			res.positiveInf = true;
		}
		else if (this.positiveInf) {
			res.realNum = other.realNum;
			res.superscriptMinus = other.superscriptMinus;
			res.superscriptPlus = other.superscriptPlus;
		}
		else if (other.positiveInf) {
			res.realNum = this.realNum;
			res.superscriptMinus = this.superscriptMinus;
			res.superscriptPlus = this.superscriptPlus;
		}
		else {
			if (this.realNum < other.realNum) {
				res.realNum = this.realNum;
				res.superscriptMinus = this.superscriptMinus;
				res.superscriptPlus = this.superscriptPlus;
			}
			else if (this.realNum > other.realNum) {
				res.realNum = other.realNum;
				res.superscriptMinus = other.superscriptMinus;
				res.superscriptPlus = other.superscriptPlus;
			}
			else {
				if (this.superscriptMinus || other.superscriptMinus) {
					res.realNum = this.realNum;
					res.superscriptMinus = true;
				}
				else if (this.superscriptPlus && other.superscriptPlus) {
					res.realNum = this.realNum;
					res.superscriptPlus = true;
				}
				else {
					res.realNum = this.realNum;
				}
			}
		}
		return res;
	}
	
	DomainValue max(DomainValue other) {
		DomainValue res = new DomainValue();
		if (this.positiveInf || other.positiveInf) {
			res.positiveInf = true;
			return res;
		}
		if (this.negativeInf && other.negativeInf) {
			res.negativeInf = true;
			return res;
		}
		else if (this.negativeInf) {
			res.realNum = other.realNum;
			res.superscriptMinus = other.superscriptMinus;
			res.superscriptPlus = other.superscriptPlus;
			return res;
		}
		else if (other.negativeInf) {
			res.realNum = this.realNum;
			res.superscriptMinus = this.superscriptMinus;
			res.superscriptPlus = this.superscriptPlus;
		}
		else {
			if (this.realNum < other.realNum) {
				res.realNum = other.realNum;
				res.superscriptMinus = other.superscriptMinus;
				res.superscriptPlus = other.superscriptPlus;
			}
			else if (this.realNum > other.realNum) {
				res.realNum = this.realNum;
				res.superscriptMinus = this.superscriptMinus;
				res.superscriptPlus = this.superscriptPlus;
			}
			else {
				if (this.superscriptPlus || other.superscriptPlus) {
					res.realNum = this.realNum;
					res.superscriptPlus = true;
				}
				else if (this.superscriptMinus && other.superscriptMinus) {
					res.realNum = this.realNum;
					res.superscriptMinus = true;
				}
				else {
					res.realNum = this.realNum;
				}
			}
		}
		return res;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		else if (obj instanceof DomainValue) {
			DomainValue other = (DomainValue) obj;
			if (this.negativeInf && other.negativeInf 
					|| this.positiveInf && other.positiveInf) {
				return true;
			}
			else if (this.realNum == other.realNum 
						&& (this.superscriptMinus == other.superscriptMinus 
								|| this.superscriptPlus == other.superscriptPlus)) {
				return true;
			}
			else return false;
		}
		else return false;
	}
	
	// based on max, min and equals, we can have <= and >=.
	boolean isLessThanEq(DomainValue other) {
		return this.equals(this.min(other));
	}
	
	boolean isGreaterThanEq(DomainValue other) {
		return this.equals(this.max(other));
	}
	
	DomainValue unary_plus() {
		DomainValue res = new DomainValue();
		res.negativeInf = this.negativeInf;
		res.realNum = this.realNum;
		res.superscriptMinus = this.superscriptMinus;
		res.superscriptPlus = this.superscriptPlus;
		res.positiveInf = this.positiveInf;
		return res;
	}
	
	DomainValue unary_minus() {
		DomainValue res = new DomainValue();
		if (this.negativeInf) {
			res.positiveInf = true;
		}
		else if (this.positiveInf) {
			res.negativeInf = true;
		}
		else if (this.superscriptMinus || this.superscriptPlus) {
			res.superscriptMinus = !this.superscriptMinus;
			res.superscriptPlus = !this.superscriptPlus;
			res.realNum = -this.realNum;
		}
		else {
			res.realNum = -this.realNum;
		}
		return res;
	}
	
	DomainValue binary_plus(DomainValue other) {
		DomainValue res = new DomainValue();
		if (this.negativeInf || other.negativeInf) {
			res.negativeInf = true;
		}
		else if (this.positiveInf || other.positiveInf) {
			res.positiveInf = true;
		}
		else if (this.superscriptMinus || other.superscriptMinus) {
			res.superscriptMinus = true;
			res.realNum = this.realNum + other.realNum;
		}
		else if (this.superscriptPlus || other.superscriptPlus) {
			res.superscriptPlus = true;
			res.realNum = this.realNum + other.realNum;
		}
		else {
			res.realNum = this.realNum + other.realNum;
		}
		return res;
	}
	
	DomainValue binary_minus(DomainValue other) {
		DomainValue res = new DomainValue();
		if (this.negativeInf) {
			res.negativeInf = true;
		}
		else if (this.positiveInf) {
			res.positiveInf = true;
		}
		else if (other.negativeInf) {
			res.positiveInf = true;
		}
		else if (other.positiveInf) {
			res.negativeInf = true;
		}
		else if (other.superscriptPlus || this.superscriptMinus) {
			res.superscriptMinus = true;
			res.realNum = this.realNum - other.realNum;
		}
		else if (this.superscriptPlus || other.superscriptMinus) {
			res.superscriptPlus = true;
			res.realNum = this.realNum - other.realNum;
		}
		else {
			res.realNum = this.realNum - other.realNum;
		}
		return res;
	}
	
	DomainValue times(DomainValue other) {
		DomainValue res = new DomainValue();
		if (this.negativeInf) {
			if (other.lessThanZero()) {
				res.positiveInf = true;
			}
			else if (other.isEqualToZero()) {
				res.realNum = 0;
			}
			else {
				res.negativeInf = true;
			}
		}
		else if (other.negativeInf) {
			if (this.lessThanZero()) {
				res.positiveInf = true;
			}
			else if (this.isEqualToZero()) {
				res.realNum = 0;
			}
			else {
				res.negativeInf = true;
			}
		}
		else if (this.positiveInf) {
			if (other.lessThanZero()) {
				res.negativeInf = true;
			}
			else if (other.isEqualToZero()) {
				res.realNum = 0;
			}
			else {
				res.positiveInf = true;
			}
		}
		else if (other.positiveInf) {
			if (this.lessThanZero()) {
				res.negativeInf = true;
			}
			else if (this.isEqualToZero()) {
				res.realNum = 0;
			}
			else {
				res.positiveInf = true;
			}
		}
		else {
			res.realNum = this.realNum * other.realNum;
			if (this.superscriptPlus && !other.superscriptMinus && !other.superscriptPlus) {
				res.superscriptMinus = other.superscriptMinus;
				res.superscriptPlus = other.superscriptPlus;
			}
			else if (this.superscriptPlus && other.superscriptPlus) {
				double temp = this.realNum + other.realNum;
				if (temp > 0) res.superscriptPlus = true;
				else if (temp < 0) res.superscriptMinus = true;
			}
			else if (!this.superscriptMinus && !this.superscriptPlus && other.superscriptPlus) {
				res.superscriptMinus = this.superscriptMinus;
				res.superscriptPlus = this.superscriptPlus;
			}
			else if (this.superscriptMinus && !other.superscriptMinus && !other.superscriptPlus) {
				res.superscriptMinus = !other.superscriptMinus;
				res.superscriptPlus = !other.superscriptPlus;
			}
			else if (this.superscriptMinus && other.superscriptMinus) {
				double temp = this.realNum + other.realNum;
				if (temp > 0) res.superscriptMinus = true;
				else if (temp < 0) res.superscriptPlus = true;
			}
			else if (!this.superscriptMinus && !this.superscriptPlus && other.superscriptMinus) {
				res.superscriptMinus = !this.superscriptMinus;
				res.superscriptPlus = !this.superscriptPlus;
			}
			else if ((this.superscriptPlus && other.superscriptMinus) 
					|| (this.superscriptMinus && other.superscriptPlus)) {
				double temp = this.realNum - other.realNum;
				if (temp > 0) res.superscriptPlus = true;
				else if (temp < 0) res.superscriptMinus = true;
			}
		}
		return res;
	}
	
	DomainValue divide(DomainValue other) {
		DomainValue res = new DomainValue();
		if (this.negativeInf && other.lessThanZero()) {
			res.positiveInf = true;
		}
		else if (this.negativeInf && other.greaterThanZero()) {
			res.negativeInf = true;
		}
		else if (this.lessThanZero() && other.negativeInf) {
			res.realNum = 0;
			res.superscriptPlus = true;
		}
		else if (this.greaterThanZero() && other.negativeInf) {
			res.realNum = 0;
			res.superscriptMinus = true;
		}
		else if (this.positiveInf && other.lessThanZero()) {
			res.negativeInf = true;
		}
		else if (this.positiveInf && other.greaterThanZero()) {
			res.positiveInf = true;
		}
		else if (this.lessThanZero() && other.positiveInf) {
			res.realNum = 0;
			res.superscriptMinus = true;
		}
		else if (this.greaterThanZero() && other.positiveInf) {
			res.realNum = 0;
			res.superscriptPlus = true;
		}
		else {
			res.realNum = this.realNum / other.realNum;
			if (this.superscriptPlus && !other.superscriptMinus && !other.superscriptPlus) {
				res.superscriptMinus = other.superscriptMinus;
				res.superscriptPlus = other.superscriptPlus;
			}
			else if (this.superscriptPlus && other.superscriptPlus) {
				double temp = other.realNum - this.realNum;
				if (temp > 0) res.superscriptPlus = true;
				else if (temp < 0) res.superscriptMinus = true;
			}
			else if (!this.superscriptMinus && !this.superscriptPlus && other.superscriptPlus) {
				res.superscriptMinus = !this.superscriptMinus;
				res.superscriptPlus = !this.superscriptPlus;
			}
			else if (this.superscriptMinus && !other.superscriptMinus && !other.superscriptPlus) {
				res.superscriptMinus = !other.superscriptMinus;
				res.superscriptPlus = !other.superscriptPlus;
			}
			else if (this.superscriptMinus && other.superscriptMinus) {
				double temp = this.realNum - other.realNum;
				if (temp > 0) res.superscriptPlus = true;
				else if (temp < 0) res.superscriptMinus = true;
			}
			else if (!this.superscriptMinus && !this.superscriptPlus && other.superscriptMinus) {
				res.superscriptMinus = this.superscriptMinus;
				res.superscriptPlus = this.superscriptPlus;
			}
			else if (this.superscriptPlus && other.superscriptMinus) {
				double temp = this.realNum + other.realNum;
				if (temp > 0) res.superscriptPlus = true;
				else if (temp < 0) res.superscriptMinus = true;
			}
			else if (this.superscriptMinus && other.superscriptPlus) {
				double temp = this.realNum + other.realNum;
				if (temp > 0) res.superscriptMinus = true;
				else if (temp < 0) res.superscriptPlus = true;
			}
		}
		return res;
	}
	
	DomainValue log() {
		DomainValue res = new DomainValue();
		if (this.positiveInf) {
			res.positiveInf = true;
		}
		else if (this.greaterThanZero()) {
			if (this.superscriptMinus || this.superscriptPlus) {
				if (this.realNum == 0) {
					res.negativeInf = true;
				}
				else {
					res.realNum = Math.log(this.realNum);
					res.superscriptMinus = this.superscriptMinus;
					res.superscriptPlus = this.superscriptPlus;
				}
			}
		}
		else {
			// for log, we only consider the input arg is greater than 0.
			System.err.println("the input arg for log in range domain is euqal to or less than 0.");
		}
		return res;
	}
	
	DomainValue exp() {
		DomainValue res = new DomainValue();
		if (this.negativeInf) {
			res.realNum = 0;
		}
		else if (this.positiveInf) {
			res.positiveInf =true;
		}
		else {
			res.realNum = Math.exp(this.realNum);
			res.superscriptMinus = this.superscriptMinus;
			res.superscriptPlus = this.superscriptPlus;
		}
		return res;
	}
	
	DomainValue abs() {
		DomainValue res;
		if (this.lessThanZero()) {
			res = this.unary_minus();
		}
		else {
			res = this.cloneThisValue();
		}
		return res;
	}
	
	@Override
	public String toString() {
		if (this.negativeInf) return "-inf";
		else if (this.positiveInf) return "+inf";
		else if (this.superscriptMinus) return (int)this.realNum + "-";
		else if (this.superscriptPlus) return (int)this.realNum + "+";
		else return (int)this.realNum+"";
	}
}
