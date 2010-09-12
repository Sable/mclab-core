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
    
    public String toString() {
        return ref.toString();
    }
}
