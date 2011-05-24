package natlab.Static.interproceduralAnalysis;

import annotations.ast.*;
import natlab.toolkits.path.FunctionReference;

/**
 * represents a call string
 * This should allow printing stack traces
 * This should be immutable
 * @author ant6n
 * 
 * TODO should this be parametric in arg?
 * TODO how to put the call site in there?
 */

public class CallString<Arg> {
    Element<Arg> element = null;
    
    public CallString(){
    }
    
    public CallString(FunctionReference ref,Arg argumentSet){
        this.element = new Element<Arg>(false,new CallString<Arg>(),ref,argumentSet);
    }
    
    private CallString(Element<Arg> elt){
        this.element = elt;
    }
    
    /**
     * returns a new call String where the given call gets added
     * @param ref
     * @param argumentSet
     * @return
     */
    public CallString<Arg> add(FunctionReference ref,Arg argumentSet,ASTNode<?> callsite){
        //TODO recursive
        return new CallString<Arg>(new Element<Arg>(false,this,ref,argumentSet));
    }
    
    /**
     * returns true if the call string contains the given
     * FunctionRefrence/Arg combination
     */
    public boolean contains(FunctionReference ref,Arg arg){
        return false; //todo
    }
    
    /**
     * an element of a call String
     * immutable
     * @author ant6n
     */
    public static class Element<Arg> {
        boolean isRecursive;
        FunctionReference functionRef;
        Arg argument;
        CallString<Arg> parent;
        
        private Element(boolean isRecursive,CallString<Arg> parent,
                FunctionReference functionRef,Arg argument){
            this.isRecursive = isRecursive;
            this.functionRef = functionRef;
            this.argument = argument;
            this.parent = parent;
        }
    }
    
    @Override
    public String toString() {
        if (element == null) return "";
        return element.parent==null?"":element.parent.toString()
                +" : "+element.functionRef.getname();
    }
}
