package natlab.mc4.symbolTable;

public class BuiltinReferenceType extends FunctionType {
    String builtinName;
    
    public BuiltinReferenceType(String builtinName){
        this.builtinName = builtinName;
    }
    
    @Override
    public String toString() {
        return "reference to builtin "+builtinName;
    }
    
}
