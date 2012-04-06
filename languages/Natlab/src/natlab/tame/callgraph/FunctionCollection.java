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
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import natlab.CompilationProblem;
import natlab.tame.simplification.LambdaSimplification;
import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import natlab.toolkits.filehandling.genericFile.FileFile;
import natlab.toolkits.filehandling.genericFile.GenericFile;
import natlab.toolkits.path.AbstractPathEnvironment;
import natlab.toolkits.path.FunctionReference;
import natlab.toolkits.rewrite.Simplifier;
import ast.ClassDef;
import ast.EmptyProgram;
import ast.Function;
import ast.FunctionList;
import ast.Program;
import ast.Script;

/**
 * A FunctionCollection is a collection of static functions.
 * The collection of functions is done by this object.
 * We refer to parsing as 'collecting', since we need to parse, resolve names as functions
 * or variables and explore the path to find functions in one pass.
 * 
 * Given a main function, all functions that may be used during execution
 * are in this collection. This can only be ensured if certain dynamic Matlab
 * builtins are not used (thus this is part of the Static package):
 * - cd into directories which have requried matlab functions
 * - eval, ...
 * - calls to builtin(..)
 * - any creation of function handles to these builtins
 * 
 */

public class FunctionCollection extends HashMap<FunctionReference,StaticFunction>{
    private static final long serialVersionUID = 1L;
    private HashSet<GenericFile> loadedFiles = new HashSet<GenericFile>(); //files that were loaded so far
    private FunctionReference main = null; //denotes which function is the entry point
    private AbstractPathEnvironment path;
    private static boolean DEBUG = false;
    
    /**
     * The function collection gets created via a path environment object
     * This will collect all the files, starting from the main function
     * ('primary function')
     */
    public FunctionCollection(AbstractPathEnvironment path){
        super();
        
        this.path = path;
        
        //get main file (entrypoint)
        main = path.getMain();
        
        //collect
        ArrayList<CompilationProblem> errors = new ArrayList<CompilationProblem>();
        collect(main    ,errors);

    }
    
    /**
     * creates a deep copy of this function collection
     */
    public FunctionCollection(FunctionCollection other){
        super();
        this.loadedFiles.addAll(other.loadedFiles);
        this.main = other.main;
        this.path = other.path;
        
        for (FunctionReference ref : other.keySet()){
            this.put(ref,other.get(ref).clone());
        }
    }
    
    /*** public stuff ***********************************************************************/
    public FunctionReference getMain(){ return main; }
    
    
    /*** Collecting files *******************************************************************/
    /**
     * adds the matlab functions from the given filename to the collection
     * @return returns true on success
     */
    public boolean collect(FunctionReference file,ArrayList<CompilationProblem> errList){
        if (DEBUG) System.out.println("collecting "+file);
        //was the filename already loaded?
        if (loadedFiles.contains(file)) return true;
        
        //parse file
        Program program;
        File javaFile = ((FileFile)file.getFile()).getFileObject();
        program = natlab.Parse.parseMatlabFile(javaFile.getAbsolutePath(), errList);
        if (program == null){
            throw new UnsupportedOperationException("cannot parse file "+file+":\n"+errList);
        }
        
        //check whether the matlab file has a good type
        if (program instanceof Script){
            System.err.println("Mc4 does not suport scripts at this point."); //TODO
            return false;
        } else if (program instanceof ClassDef){
            System.err.println("Mc4 does not support classes at this point.");
        } else if (program instanceof EmptyProgram){
            System.err.println("Mc4 does not support empty files at this point."); //TODO
        } else if (!(program instanceof FunctionList)){
            System.err.println("Mc4 encountered Matlab file of unknown/unsupported type "+
                    program.getClass()+".");
        }
        
        //We reduce lambda expressions at this point, because they create extra functions
        program = (Program)Simplifier.simplify(program, 
                new VFPreorderAnalysis(program, path.getFunctionOrScriptQuery(file.path)), 
                LambdaSimplification.class); //TODO hook up with proper path environment
        //System.out.println(program.getPrettyPrinted());
        
        loadedFiles.add(file.getFile());
        boolean success = true;
        
        //turn functions into mc4 functions, and go through their function references recursively
        FunctionList functionList = (FunctionList)program;
        for (Function functionAst : functionList.getFunctions()){
            //create/add static function
            FunctionReference ref = new FunctionReference(functionAst.getName(),file.getFile());
            StaticFunction function = new StaticFunction(functionAst, ref);
            this.put(ref,function);

            //recursively load referenced functions
            success = success && resolveFunctionsAndCollect(function,errList);
        }

        //TODO we should collect used siblings and get rid of unused ones
        return success;
    }
    
    /**
     * resolves all calls to other functions within given function, and collects functions not in this collection
     */
    private boolean resolveFunctionsAndCollect(StaticFunction function,ArrayList<CompilationProblem> errList){
        boolean success = true;
        LinkedList<String> unfoundFunctions = new LinkedList<String>();
        
        //collect references to other functions - update symbol table, recursively collect
        for (String otherName : function.getCalledFunctions().keySet()){            
            //LOOKUP SEMANTICS:
            //update symbol table entry based on what the reference is
            //1 functionName could be the function itself
            if (otherName.equals(function.getName())){
                //reference to itself
                function.getCalledFunctions().put(otherName, function.getReference());
                
            //2 functionName could be a nested function
            } else if (function.getAst().getNested().containsKey(otherName)){
                //TODO
                System.err.println("function "+otherName+" referring to nested function unsupported");
            
            //3 functionName could be a sibling function - which should already be loaded
            } else if (function.getSiblings().contains(otherName)){             
                function.getCalledFunctions().put(otherName, 
                        new FunctionReference(otherName,function.reference.getFile()));

            //4 functionName is a builtin or a function on the path    
            } else {
                //try to find it
                FunctionReference otherFunction = path.resolve(otherName,null);
                if (otherFunction != null){ // file found
                    if(!otherFunction.isBuiltin()){
                        success = success && collect(otherFunction,errList); //recursively collect other function
                        function.getCalledFunctions().put(otherName,otherFunction); //update symbol table entry
                    } else { // file is builtin
                        function.getCalledFunctions().put(otherName, otherFunction);
                    }
                } else { //file not found
                    unfoundFunctions.add(otherName);
                    success = false;
                }
            }
        }
        if (unfoundFunctions.size() != 0){
            //System.out.println(function.getAst().getPrettyPrinted());
            throw new UnsupportedOperationException("reference to "+unfoundFunctions+" in "+function.getName()+" not found");
        }
        return success;
    }
    
    /**
     * inlines all functions into the main function
     */
    public void inlineAll(){
    	inlineAll(new HashSet<FunctionReference>(),getMain());
    	System.out.println("finished inlining all");
    }
    //inlines all calls inside the given function
    //throws a unspported operation if there is an attempt to inline a function
    //that is alraedy in the context -- cannot inline recursive functions
    private void inlineAll(Set<FunctionReference> context,FunctionReference function){
    	//error check
    	if (context.contains(function)){
    		throw new UnsupportedOperationException("trying to inline recursive function "+function);
    	}
    	if (!containsKey(function)){
    		throw new UnsupportedOperationException("trying to inline function "+function+", which is not loaded");
    	}
    	if (function.isBuiltin()) return;
    	
    	//add this call to the context
    	context.add(function);
    	
    	//inline all called functions recursively
    	for (FunctionReference ref : get(function).getCalledFunctions().values()){
			if (!ref.isBuiltin()){
				//inline recursively
			    System.out.println("go inline "+ref);
				inlineAll(context,ref);
			}
    	}
    	
    	//inline the calls to the given function
    	get(function).inline(this);
    	
    	//remove this context
    	context.remove(function);    	
    }
    
    /**
     * returns a single inlined function representing this function collection.
     * Does not alter the the function collection. Only works for non-recursive callgraphs.
     */
    public Function getAsInlinedFunction(){
        return getAsInlinedStaticFunction().getAst();
    }
    
    /**
     * returns a single inlined StaticFunction representing this function collection.
     * Does not alter the function collection. Only works for non-recursive callgraphs.
     */
    public StaticFunction getAsInlinedStaticFunction(){
        FunctionCollection c = new FunctionCollection(this);
        c.inlineAll();
        return c.get(c.getMain());
    }
    
    
    /**
     * returns all function references that are either in this function collection or that
     * are being referred to by and function in this collection
     */
    public HashSet<FunctionReference> getAllFunctionReferences(){
        HashSet<FunctionReference> result = new HashSet<FunctionReference>();
        for (FunctionReference ref : this.keySet()){
            result.add(ref);
            result.addAll(this.get(ref).getCalledFunctions().values());
        }
        return result;
    }


    /**
     * returns all function references to builtins among the whole call graph
     */
    public HashSet<FunctionReference> getAllFunctionBuiltinReferences(){
        HashSet<FunctionReference> result = new HashSet<FunctionReference>();
        for (FunctionReference ref : this.keySet()){
            for (FunctionReference ref2 : this.get(ref).getCalledFunctions().values()){
                if (ref2.isBuiltin()) result.add(ref2);
            }
        }
        return result;
    }
    


    public String getPrettyPrinted(){
        String s = "";
        for (FunctionReference f : this.keySet()){
            s += ("\n"+this.get(f));            
        }
        return s;
    }

}





