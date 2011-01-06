package natlab.mc4.builtin;

public interface BuiltinVisitor<T,U> {
    public T getArg();
    public void setReturn(U ret);
    public U visit(T arg);
    
    public U casePlus(Plus node,T arg);
}
