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

package natlab.tame.interproceduralAnalysis.examples;

import java.util.LinkedHashSet;

import natlab.tame.callgraph.StaticFunction;
import natlab.tame.interproceduralAnalysis.Call;
import natlab.tame.interproceduralAnalysis.CallString;
import natlab.tame.interproceduralAnalysis.Callsite;
import natlab.tame.interproceduralAnalysis.FunctionAnalysis;
import natlab.tame.interproceduralAnalysis.InterproceduralAnalysisFactory;
import natlab.tame.interproceduralAnalysis.InterproceduralAnalysisNode;
import natlab.toolkits.path.FunctionReference;
import analysis.AbstractDepthFirstAnalysis;
import ast.Expr;
import ast.Function;
import ast.Name;

public class CallStringAnalysis extends AbstractDepthFirstAnalysis<CallStringAnalysis.CallStrings>
        implements FunctionAnalysis<CallString<?>,CallStringAnalysis.CallStrings>{
    
    /**
     * the flowset
     */
    public static class CallStrings extends LinkedHashSet<CallString<?>>{
        private static final long serialVersionUID = 1L;
        
    }

    /**
     * factory for the intraprocedural analysis - can be used to instiate
     * the interprocedural analysis.
     */
    public static class Factory implements InterproceduralAnalysisFactory
    <CallStringAnalysis, CallString<?>, CallStrings>{
        @Override
        public CallStringAnalysis newFunctionAnalysis(
                StaticFunction function,
                CallString<?> argumentSet,
                InterproceduralAnalysisNode<CallStringAnalysis, CallString<?>, CallStrings> node) {
            return new CallStringAnalysis(function,node);
        }        
    }

    private CallString<?> callstring; //current context
    private StaticFunction function; //need static function to resolve names
    private CallStrings result; //we only flow one set
    private static final Object arg = new Object(); //argument dummy
    private InterproceduralAnalysisNode<CallStringAnalysis, CallString<?>, CallStrings> node;
    private CallStringAnalysis(StaticFunction function,
            InterproceduralAnalysisNode<CallStringAnalysis, CallString<?>, CallStrings> node) {
        super(function.getAst());
        this.node = node;
        this.callstring = node.getCallString();
        this.function = function;
        this.result = newInitialFlow();
        result.add(callstring);
    }
    
    @Override
    public void caseName(Name node) {
        System.out.print("-"+node.getID()+" "+function);
        String name = node.getID();
        System.out.println(function.getCalledFunctions().keySet());
        if (function.getCalledFunctions().containsKey(name)){
            FunctionReference ref = function.getCalledFunctions().get(name);
            if (ref == null){
                System.err.println("unresolved call to "+name);
            } else {
                if (ref.isBuiltin()){
                    result.add(callstring.add(ref, null, null)); //TODO
                } else {
                    System.out.println(" call "+ref);
                    Callsite<CallStringAnalysis,CallString<?>,CallStringAnalysis.CallStrings> callsite = this.node.createCallsiteObject(node);
                    Call<CallString<?>> call = new Call<CallString<?>>(ref, callstring);
                    result.addAll(this.node.analyze(call, callsite));
                }
            }
        }
    }
    
    
    @Override
    public void caseCondition(Expr condExpr) {
        caseASTNode( condExpr );
    }

    @Override
    public CallStrings newInitialFlow() {
        return new CallStrings();
    }

    @Override
    public CallStrings getDefaultResult() {
        return newInitialFlow();
    }

    public ast.Function getTree() {
        return (Function)super.getTree();
    };
    
    @Override
    public CallStrings getResult() {
        return result;
    }
    
}
