// =========================================================================== //
//                                                                             //
// Copyright 2011 Anton Dubrau and McGill University.                          //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//  limitations under the License.                                             //
//                                                                             //
// =========================================================================== //

package natlab.tame.interproceduralAnalysis;

import natlab.toolkits.path.FunctionReference;
import ast.ASTNode;

/**
 * represents a call string
 * This should allow printing stack traces when during an analysis an error is encountered.
 * 
 * This should be immutable
 * TODO some methods are not implemented (but easy), ast nodes (callsites) not properly implemented
 * TODO 
 *  should callsites be callsite objects, rather than ast nodes? 
 *  Should the interprocedural analysis node be stored as well?
 * 
 * @param <A> The argument set used for each call
 */

public class CallString<A> {
    private Call<A> call;
    private CallString<A> parent = null;
    
    public CallString(){
    }
    
    public CallString(FunctionReference ref,A argumentSet){
        this.call = new Call<A>(ref,argumentSet);
    }
    
    
    public CallString(CallString<A> parent,FunctionReference ref,A argument,ASTNode callsite){
        this.parent = parent;
        this.call = new Call<A>(ref,argument);
    }
    
    
    /**
     * returns the number of elements in the call string
     */
    public int size(){
    	if (parent == null) return 1;
    	return parent.size() + 1;
    }
    
    /**
     * returns the most recent call in the call string
     */
    public Call<A> getTopCall(){
    	return call;
    }
    
    /**
     * returns the parent call string
     */
    public CallString<A> getParent(){
    	return parent;
    }
    
    
    /**
     * returns a new call String where the given call gets added
     * @param ref
     * @param argumentSet
     * @return
     */
    public CallString<A> add(FunctionReference ref,A argumentSet,ASTNode<?> callsite){
        return new CallString<A>(this,ref,argumentSet,callsite);
    }
    
    /**
     * returns true if the call string contains the given
     * FunctionRefrence/Arg combination
     */
    public boolean contains(FunctionReference ref,A arg){
        if (new Call<A>(ref,arg).equals(call)) return true;
        return parent==null?false:parent.contains(ref, arg);
    }
    
    @Override
    public String toString() {
        return (parent==null?"":parent.toString())+" : "+call;
    }
    
    @Override
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException(); //TODO
    }
    
    @Override
    public int hashCode() {
        throw new UnsupportedOperationException(); //TODO
    }
}
