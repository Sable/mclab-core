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

package natlab.Static.interproceduralAnalysis;

/**
 * TODO 
 * node in the interprocedural analysis 
 * - stores function, functionanalysis, callstring, argument, result
 * - takes care of a map of <callsite,interproc analysis node>
 * 
 * @param <F> the FunctionAnalysis type used to analyse each function/argument pair
 * @param <A>  the argument set that is given to the function to run the analysis
 * @param <R> the result set that the analysis returns for that function
 */


import java.util.*;

import analysis.StructuralAnalysis;
import ast.ASTNode;
import natlab.Static.callgraph.*;
import natlab.toolkits.path.FunctionReference;

public class InterproceduralAnalysisNode<FAnalysis extends FunctionAnalysis<Arg,Res>, Arg, Res> {
    private StaticFunction function;
    private FAnalysis functionAnalysis;
    private CallString<Arg> callString;
    private InterproceduralAnalysis<FAnalysis,Arg,Res> interprocAnalysis;
    private InterproceduralAnalysisFactory<FAnalysis,Arg,Res> factory;
    private Arg argument;
    private FunctionCollection callgraph;
    private HashMap<ASTNode<?>,InterproceduralAnalysisNode<FAnalysis, Arg, Res>> callsites = 
        new HashMap<ASTNode<?>,InterproceduralAnalysisNode<FAnalysis,Arg,Res>>();
    static final boolean DEBUG = false;
    private boolean isRecursive = false; //this may change during analysis
    private Res currentRecursiveResult = null;
    
    /**
     * constructs an InterproceduralAnalysisNode,
     * registers the node with the analysis engine,
     * runs the analysis 
     * 
     * @param tree the function for this node
     * @param analysis the analysis for the function
     * @param callString the call string of this node's call (must 
     *        include the call to the function this analysises)
     */
    protected InterproceduralAnalysisNode(
            InterproceduralAnalysis<FAnalysis,Arg,Res> interprocAnalysis,
            FunctionCollection callgraph,
            InterproceduralAnalysisFactory<FAnalysis, Arg, Res> analysisFactory,
            FunctionReference ref,
            CallString<Arg> callString,
            Arg argument){
        if (DEBUG) System.out.println("new intra proc anal node  "+ref+"("+argument+") - "+callString);
        
        //initialize/assign data
        this.function = callgraph.get(ref);
        this.callString = callString;
        this.interprocAnalysis = interprocAnalysis;
        this.argument = argument;
        this.factory = analysisFactory;
        this.callgraph = callgraph;
        
        //register with analysis
        this.interprocAnalysis.putNode(ref, argument, this);
        
        //create analysis
        functionAnalysis = analysisFactory.newFunctionAnalysis(function, argument, this);
        
        //run analysis
        functionAnalysis.analyze();
        
        //if it was found that this is a recursive call, do fixed point iteration
        if (isRecursive){
            if (DEBUG) System.out.println("fixed point itreation on "+this+"\n first result"+functionAnalysis.getResult());

            //fixed point is reached when the current result equals the new result
            while (!currentRecursiveResult.equals(functionAnalysis.getResult())){
                currentRecursiveResult = functionAnalysis.getResult();
                functionAnalysis = analysisFactory.newFunctionAnalysis(function, argument, this);
                functionAnalysis.analyze();
                if (DEBUG) System.out.println(" newer result "+currentRecursiveResult);
            }
            if (DEBUG) System.out.println(" finished iteration, got "+currentRecursiveResult);
        }
    }
    
    
    /**
     * computes the Result for the given function, argument set, where the
     * call is happening at the given callsite, using the given 
     * function analysis factory.
     * 
     * If the given function/argument combination has already been analyzed,
     * then it will not be again analyzed, but rather the previously computed
     * result will be returned.
     * 
     * If the given function/argument combination is currently being processed
     * (i.e. it's in the current call string), then this is a recursive call,
     * then this will return the default result of the function analysis 
     * created by the function analysis factory
     * 
     * This should only be called by Function Analyses, during the analysis
     * phase.
     * Note that this should work even if the supplied callsite is null (but
     * call strings will be incomplete).
     */
    public Res analyze(FunctionReference function,Arg arg,ASTNode callsite){
        Res result = null;
        InterproceduralAnalysisNode<FAnalysis, Arg, Res> node = null;
        
        //check whether this is a recursive call
        if (callString.contains(function, arg)){
            node =  interprocAnalysis.getNode(function, arg);
            if (DEBUG) System.out.println("found recursive call "+callString+" -- "+node);
            if (node.currentRecursiveResult == null){
                node.currentRecursiveResult = node.getAnalysis().getDefaultResult();
                if (DEBUG) System.out.println("created default result "+node.currentRecursiveResult);
            }
            //set the node to be recursive
            node.isRecursive = true;
            return node.currentRecursiveResult;
        } else {
            //not a recursive call - try to find the result in the interprocedural analysis
            if (DEBUG) System.out.println("try to find node "+function.name+"("+arg+")");
            node =  interprocAnalysis.getNode(function, arg);
            if (node == null){
                if (DEBUG) System.out.println("creating new node "+function.name+"("+arg+")");
                //create new interpocedural analysis
                node = new InterproceduralAnalysisNode<FAnalysis, Arg, Res>(
                        interprocAnalysis, callgraph, factory, function,
                        callString.add(function, arg, callsite), arg);
            } else {
                if (DEBUG) System.out.println("found existing node "+node.function.getName()+"("+node.argument+")");
            }
            result = node.getResult();
        }
        
        //register call site - will overwrite old, invalidated value
        //TODO
        setNodeForCallsite(callsite, node);
        return result;
    }

    /**
     * sets/overrides the callsite to call the given node
     */
    protected void setNodeForCallsite(ASTNode<?> callsite, 
            InterproceduralAnalysisNode<FAnalysis, Arg, Res> node){
        callsites.put(callsite, node);        
    }
    
    public StaticFunction getFunction(){
        return function;
    }
    public FunctionAnalysis<Arg, Res> getAnalysis(){
        return functionAnalysis;
    }
    public CallString<Arg> getCallString(){
        return callString;
    }
    /**
     * returns the result of the function analysis.
     */
    public Res getResult(){
        return functionAnalysis.getResult();
    }
    
    @Override
    public String toString() {
        return "AnalysisNode: "+function.getName()+"("+argument+")  --> "+getResult();
    }
    
    /**
     * prints the complete analysis node with code and flowsets 
     */
    public String getPrettyPrinted(){
        return "AnalysisNode: "+function.getName()+"("+argument+"):\n"
            + function.getAst().getAnalysisPrettyPrinted((StructuralAnalysis<?>)functionAnalysis, true, true)
            + "\nresult: "+getResult();
    }
}


