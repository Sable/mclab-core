package natlab.mc4.symbolTable;

import natlab.mc4.*;

/**
 * a symbol table entry that refers to another function via a function reference
 *
 */

public class FunctionReferenceType extends FunctionType {
    FunctionReference ref;
    
    public FunctionReferenceType(FunctionReference ref){
        this.ref = ref;
    }
    
    public FunctionReference getFunctionReference(){
    	return ref;
    }
    
    public String toString() {
        return "call to: "+ref.toString();
    }

	public FunctionReferenceType copy() {
		return new FunctionReferenceType(ref);
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof FunctionReferenceType) {
			FunctionReferenceType fRef = (FunctionReferenceType) obj;
			return fRef.ref.equals(ref);
		}else return false;
	}
}
