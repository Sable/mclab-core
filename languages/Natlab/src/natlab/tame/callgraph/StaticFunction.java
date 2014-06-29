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

package natlab.tame.callgraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import natlab.tame.simplification.ThreeAddressToIR;
import natlab.tame.tir.TIRFunction;
import natlab.toolkits.Context;
import natlab.toolkits.analysis.varorfun.VFDatum;
import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import natlab.toolkits.path.FunctionReference;
import natlab.toolkits.rewrite.RenameSymbols;
import natlab.toolkits.rewrite.Simplifier;
import natlab.toolkits.rewrite.inline.Inliner;
import natlab.toolkits.rewrite.inline.PutCommentsInlineQuery;
import natlab.toolkits.rewrite.simplification.AbstractSimplification;
import natlab.toolkits.rewrite.simplification.CommentSimplification;
import ast.Function;
import ast.FunctionList;

/**
 * 
 * This represents a static function, as stored in a SimpleFunctionCollection Object.
 * It stores the AST of a function in IR form, it's location (via a FunctionReference),
 * and all the functions called (sort of a basic symbol table).
 * and other useful information.
 * 
 */
public class StaticFunction implements Cloneable {
    public static boolean DEBUG = false;
    Function function; //ast tree
    FunctionReference reference; //reference to this function
    String name;
    Set<String> siblings; //TODO - make these a hashmap<string,StaticFunction>
    Context context;

    /**
     * references to functions being called
     * this represents only non-class specific (overloaded) functions
     * a FunctionReference of null signifies that the function could not be found
     * (the kind is bottom)
     *- either because it doesn't exist, or because it is only overloaded,
     *  or is a variable coming from a script
     */
    Map<String,FunctionReference> calledFunctions;
    

    /**
     * Creates a Mc4 Function, given an ast and the source file.
     * This transforms the function to 3 address code and builds the symbol table.
     * 
     * @param function the AST of the function, will get copied
     * @param source the file where it is from
     * @param a function finder, to resolve IDs into Function references
     */
    public StaticFunction(Function function, FunctionReference reference, Context context) {
        this.function = function.fullCopy();
        this.reference = reference;
        this.calledFunctions = new HashMap<>();
        this.name = function.getName().getID();
        this.context = context;
        
        //set siblings
        siblings = new HashSet<>(function.getSiblings().keySet());
        
        //transform to IR
        transformToIR();
        
        //create first symbol table
        findAndResolveFunctions();
    }
    
    public StaticFunction clone(){
        StaticFunction f = null;
        try {
            f = (StaticFunction)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new UnsupportedOperationException(e);
        }
        f.calledFunctions = new HashMap<>(getCalledFunctions());
        f.function = function.fullCopy();
        f.siblings = new HashSet<>(siblings);
        return f;
    }
    
    //creates and puts first order approximation of the symbol table - variable or function
    private void findAndResolveFunctions(){
        //perform variable or function analysis on function and get result
        VFPreorderAnalysis functionAnalysis = new VFPreorderAnalysis(this.function,this.context.getFunctionOrScriptQuery());
        functionAnalysis.analyze();
        Map<String, VFDatum> flowset; 
        flowset = functionAnalysis.getFlowSets().get(function);
        
        //go through all symbols, and put them in the symbol table
        for (Map.Entry<String, VFDatum> entry : flowset.entrySet()) {
            String name = entry.getKey();
            VFDatum vf = entry.getValue();
            
            if (vf.isFunction() || !vf.isVariable()){
                calledFunctions.put(name, this.context.resolve(name));
            }
        }
        //TODO deal with errors - unresolved functions? - they may be overloaded functions
    }
    
    
    /**
     * apply simplification on the ast
     */
    public void applySimplification(Class<? extends AbstractSimplification> simplification){
        FunctionList fList = new FunctionList();
        fList.addFunction(function);
        function = Simplifier.simplify(fList,simplification).getFunction(0);
    }
    
    
    //transforms the underlying AST to 3 address code
    private void transformToIR(){
        try{
            if (this.function instanceof TIRFunction){
                VFPreorderAnalysis anal = new VFPreorderAnalysis(function);
                function = (Function)new CommentSimplification(function, anal).transform();
                anal = new VFPreorderAnalysis(function);
                function = (Function)new ThreeAddressToIR(function,anal).transform();
            } else {
                applySimplification(CommentSimplification.class);
                applySimplification(ThreeAddressToIR.class);
            }
        }catch (RuntimeException e){
            System.out.println("error transforming to IR:\n"+function.getName().getID());
            throw e;
        }
    }
    
    
    //getter methods
    public Map<String,FunctionReference> getCalledFunctions(){ return calledFunctions; }
    public String getName(){ return name; }
    public Function getAst(){ return function; }
    public FunctionReference getReference(){ return reference; }
    public Set<String> getSiblings(){ return siblings; }
    public Context getContext(){ return context; }
    
    
    
    /**
     * inlines a copy of the given function into this, if it is called from this function.
     * Note: in order to inline multiple functions, use inline(Map<FunctionReference, StaticFunction>)
     */
    public void inline(StaticFunction other){
        Map<FunctionReference,StaticFunction> map = new HashMap<>();
        map.put(other.reference, other);
        inline(map);
    }
    
    
    /**
     * inlines a copy of all functions that are in the given map,
     * and which are called from this function
     */
    public void inline(Map<FunctionReference, StaticFunction> map){
    	Map<String, Function> inlinerMap = new HashMap<>();
    	
    	System.out.println("inlining everything in "+name);
    	
    	//build inliner map
    	for (FunctionReference ref : map.keySet()){
    	    if (calledFunctions.containsValue(ref)){ //we need to inline ref
    	        //ref is in the map and is called from this - we need to inline it
    	        StaticFunction otherFunction = map.get(ref);
    	        
    	        //create a copy of the function
    	        otherFunction = otherFunction.clone();
    	        
    	        //merge symbol tables
    	        String oName = otherFunction.name;
    	        mergeSymbols(otherFunction, oName.substring(0,Math.min(4,oName.length()))+"_");
    	        
    	        //for any name that maps to ref, we need to inline it
    	        for (String name : calledFunctions.keySet()){
    	            if (calledFunctions.get(name).equals(ref)){
    	                inlinerMap.put(name, otherFunction.function);
    	            }
    	        }
    	        
    	        //update called functions table to remove all references to the other function
    	        for (String name : inlinerMap.keySet()){
    	            calledFunctions.remove(name);
    	        }
    		}
    	}

    	if (inlinerMap.size() > 0){
    	    System.out.println("inlining in "+name+": "+inlinerMap.keySet());
    	    //actually inline
    	    Inliner<Function,Function> inliner = 
    	        new Inliner<Function,Function>(this.function,inlinerMap,new PutCommentsInlineQuery());
    	    this.function = (Function)inliner.transform();

    	    System.out.println("finished inlining, transforming to IR");

    	    //the inline creates non IR nodes, turn back into IR
    	    transformToIR();
    	    

    	    System.out.println("finished transforming to IR");
    	} else {
    	    System.out.println("nothing to inline");
    	}
    }
    
        
    @Override
    public String toString() {
        return 
            "Function: "+name+
            "\ncalled functions:\n"+calledFunctions+
            "\ncode:\n"+function.getPrettyPrinted();
    }
    
    
    
    /**
     * merges the symbols of this Function with the given other Function
     * This will rename all symbols of the other function that are
     * already present in this symbol table - unless they refer to the same function,
     * or if they refer to a function in the other function but not in this, in which
     * case the variable in this will be renamed. Unless it is a builtin, which
     * won't get renamed.
     * 
     * adds the called functions of the other map to this.
     * 
     * Variables get renamed to <prefix><original name><postfix>,
     * where the prefix is a given argument, and the postfix are
     * some set of characters (usually numbers) that ensure uniqueness.
     * To rename variables in this, the prefix will be empty.
     * 
     */
    public void mergeSymbols(StaticFunction otherFunction,String prefix){
        //the rename maps for both functions
        Map<String, String> renameOther = new HashMap<>();
        Map<String, String> renameThis = new HashMap<>();

        Set<String> symbols = function.getSymbols();
        Set<String> otherSymbols = otherFunction.function.getSymbols();
        
        //build rename maps - find name conflicts
        for (String name : otherSymbols){
            if (symbols.contains(name)){ //name conflict
                
                boolean doRenameOther = true; //we always rename other except for the following cases               
                //case: name is fun in other only - rename in this
                if (otherFunction.calledFunctions.containsKey(name)
                        && !calledFunctions.containsKey(name)){
                    doRenameOther = false;
                }
                
                if (otherFunction.calledFunctions.containsKey(name)
                        && calledFunctions.containsKey(name)){
                    if(name.equals("sign")){
                    System.out.println("merging sign among "+otherFunction.name+" "+this.name);
                    }
                    
                    //case: name is fun in both and they refer to the same - don't rename 
                    if(calledFunctions.get(name).equals(otherFunction.calledFunctions.get(name))){
                        continue; // don't rename
                    } else {
                    //case: name is fun in both, refer to different functions, and other is builtin - rename this
                        if(otherFunction.calledFunctions.get(name).isBuiltin()) doRenameOther = false;
                    }
                }
                
                //generate new name and put in correct rename map
                String newName = RenameSymbols.getNewName(prefix + name,
                        symbols,otherSymbols,renameThis.values(),renameOther.values());
                if (doRenameOther){
                    renameOther.put(name, newName);
                }else{
                    renameThis.put(name, newName);
                }
            }
        }
        
        
        //actually rename
        rename(renameThis);
        otherFunction.rename(renameOther);
        
        //add the others' calls
        calledFunctions.putAll(otherFunction.calledFunctions);
        
    }
 
    /**
     * renames all symbols in this function according to the given map. That
     * is, all symbols that are keys in the map are renamed to the corresponding value.
     * This will also update the calledFunctions set.
     * @param renameMap
     */
    public void rename(Map<String,String> renameMap){
        //update AST
        RenameSymbols rename = new RenameSymbols(function, renameMap);
        
        //update called Functions map
        for (String key : renameMap.keySet()){
            if (calledFunctions.containsKey(key)){
                calledFunctions.put(renameMap.get(key),calledFunctions.remove(key));
            }
        }
        
        function = (Function)rename.transform();        
    }
}



