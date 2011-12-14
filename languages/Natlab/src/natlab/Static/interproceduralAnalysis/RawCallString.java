package natlab.Static.interproceduralAnalysis;

import natlab.toolkits.path.FunctionReference;
import ast.ASTNode;

/**
 * A Call String that is not generic in any argument Object.
 * Any arguments will be ignored, null will be used.
 * 
 * @author ant6n
 */
public class RawCallString extends CallString<Object> {
    public RawCallString() {
        super();
    }
    
    public RawCallString(FunctionReference ref){
        super(ref,null);
    }

    public RawCallString(RawCallString parent,FunctionReference ref,ASTNode callsite){
        super(parent,ref,null,callsite);
    }

    
    /**
     * the argument gets ignored
     */
    @Override
    public RawCallString add(FunctionReference ref, Object argumentSet,ASTNode callsite) {
        return new RawCallString(this,ref,callsite);
    }
    
    
    
}


