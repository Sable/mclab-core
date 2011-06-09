package natlab.Static.interproceduralAnalysis;

import annotations.ast.*;
import natlab.toolkits.path.FunctionReference;

/**
 * represents a call string
 * This should allow printing stack traces
 * This should be immutable
 * @author ant6n
 * 
 * TODO how to put the call site in there?
 * TODO - deal with recursive well
 * TODO - intern this?
 * TODO - possibly keep a hashset of parents, to make containment checks faster?
 * 
 * @param <A> The argument set used for each call
 */

public class CallString<A> {
    Element<A> element = null;
    
    public CallString(){
    }
    
    public CallString(FunctionReference ref,A argumentSet){
        this.element = new Element<A>(false,new CallString<A>(),ref,argumentSet);
    }
    
    
    public CallString(CallString<A> parent,FunctionReference ref,A argument,ASTNode callsite){
        //TODO do recursive
        this(new Element<A>(false,parent,ref,argument));
    }
    
    private CallString(Element<A> elt){
        this.element = elt;
    }    
    
    /**
     * returns a new call String where the given call gets added
     * @param ref
     * @param argumentSet
     * @return
     */
    public CallString<A> add(FunctionReference ref,A argumentSet,ASTNode callsite){
        return new CallString<A>(this,ref,argumentSet,callsite);
    }
    
    /**
     * returns true if the call string contains the given
     * FunctionRefrence/Arg combination
     */
    public boolean contains(FunctionReference ref,A arg){
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
