package natlab.Static.callgraph;
import java.io.File;
import java.util.*;
import ast.*;
import natlab.CompilationProblem;
import natlab.toolkits.filehandling.genericFile.*;
import natlab.toolkits.path.AbstractPathEnvironment;

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
    private HashSet<GenericFile> loadedFiles = new HashSet<GenericFile>(); //files that were loaded so far
    private FunctionReference main = null; //denotes which function is the entry point
    private AbstractPathEnvironment path;
    
    /**
     * The function collection gets created via a path environment object
     * This will collect all the files, starting from the main function
     * ('primary function')
     */
    public FunctionCollection(AbstractPathEnvironment path){
        super();
        
        this.path = path;
        
        //get main file (entrypoint)
        GenericFile main = path.getMain();
        
        //collect
        ArrayList<CompilationProblem> errors = new ArrayList<CompilationProblem>();
        collect(main,true,errors);

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
    public boolean collect(GenericFile file,boolean isMain,ArrayList<CompilationProblem> errList){
        System.out.println("collecting "+file);
        //was the filename already loaded?
        if (loadedFiles.contains(file)) return true;
        
        //parse file
        Program program;
        File javaFile = ((FileFile)file).getFileObject();
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
        
        
        loadedFiles.add(file);
        boolean success = true;
        if (isMain){ //put main
        	this.main = new FunctionReference(
        			((FunctionList)program).getFunction(0).getName(),file);
        }
        
        //turn functions into mc4 functions, and go through their function references recursively
        FunctionList functionList = (FunctionList)program;
        for (Function functionAst : functionList.getFunctions()){
            //create/add static function
            FunctionReference ref = new FunctionReference(functionAst.getName(),file);
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
                        new FunctionReference(otherName,function.reference.path));

            //4 functionName is a builtin or a function on the path    
            } else {
                //try to find it
                GenericFile otherFunction = path.resolve(otherName,null);
                if (otherFunction != null){ // file found
                    success = success && collect(otherFunction,false,errList); //recursively collect other function
                    function.getCalledFunctions().put(otherName,  //update symbol table entry
                            new FunctionReference(otherName,otherFunction));
                    
                } else { // file is builtin, or cannot be found
                    if (path.isBuiltin(otherName)){ //builtin
                        function.getCalledFunctions().put(otherName, 
                                new FunctionReference(otherName));
                    } else { //not found
                        unfoundFunctions.add(otherName);
                        success = false;
                    }
                }
            }
        }
        if (unfoundFunctions.size() != 0){
            System.out.println(function.getAst().getPrettyPrinted());
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
			    System.out.println("inlining "+ref);
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
     * Does not alter the the function collection.
     */
    public Function getAsInlinedFunction(){
        FunctionCollection c = new FunctionCollection(this);
        c.inlineAll();
        return c.get(c.getMain()).getAst();
    }
    
}




