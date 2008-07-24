package fir.ast;
//TODO - type propagation
public enum BinaryOperator implements ASTnode {
		ADD{public String toString(){return "+";}},
		MUL{public String toString(){return "*";}},
		SUB{public String toString(){return "-";}},
		DIV{public String toString(){return "/";}},
		CMUL{public String toString(){return ".*";}},
		EQ{public String toString(){return "==";}},
		LT{public String toString(){return "<";}},
		LTE{public String toString(){return "<=";}},
		INVALID{public String toString(){return "INVALID_OPERATOR";}};
		
		abstract public String toString();
		
		public static BinaryOperator fromString(String operator){
			if (operator.equals("+")) return ADD;
			if (operator.equals("*")) return MUL;
			if (operator.equals("-")) return SUB;
			if (operator.equals("/")) return DIV;
			if (operator.equals(".*")) return CMUL;
			if (operator.equals("==")) return EQ;
			if (operator.equals("<")) return LT;
			if (operator.equals("<=")) return LTE;
			return INVALID;
		}
	}

	
}
