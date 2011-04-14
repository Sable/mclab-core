package natlab.Static.builtin;

/**
 * this represents a builtin function
 *
 */
public class Builtin {
    String name;
    
    public Builtin(String name){
        this.name = name;
    }
    
    
    
    public <Arg,Ret> Ret visit(BuiltinVisitor<Arg,Ret> visitor,Arg arg){
        return visitor.casePlus(this,arg);
        
    }
    
    
    
}
