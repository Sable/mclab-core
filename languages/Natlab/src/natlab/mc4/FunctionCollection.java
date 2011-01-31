package natlab.mc4;

import java.io.File;
import java.util.*;

import ast.*;

import natlab.CompilationProblem;
import natlab.mc4.builtin.Mc4BuiltinQuery;
import natlab.mc4.symbolTable.*;
import natlab.options.Options;
import natlab.toolkits.filehandling.FunctionFinder;

/**
 * A FunctionCollection is a collection of Mc4Functions.
 * Parsing is done by this object
 * -> we refer to parsing as 'collecting', since we need to parse, build a basic symbol table
 *    and explore the path to find functions in one pass.
 *    At the end all used Matlab functions should be in this collection.
 */


public class FunctionCollection extends HashMap<FunctionReference,Mc4Function>{
    private HashSet<File> loadedFiles = new HashSet<File>(); //files that were loaded so far
    private FunctionReference main = null; //denotes which function is the entry point
    private Options options;
    
    /**
     * The function collection gets created via an options object
     * This will parse the first file in the options object and all associated files
     * @param options Natlab Options - that's where the file is found
     */
    public FunctionCollection(Options options){
        super();
        
        this.options = options;
        
        
        //get main file (entrypoint)
        File main = Mc4.functionFinder.getMain();

        //collect
        ArrayList<CompilationProblem> errors = new ArrayList<CompilationProblem>();
        collect(main,true,errors);

    }
    
    /*** public stuff ***********************************************************************/
    public FunctionReference getMain(){ return main; }
    
    
    /*** Collecting files *******************************************************************/
    /**
     * adds the matlab functions from the given filename to the collection
     * @return returns true on success
     */
    public boolean collect(File file,boolean isMain,ArrayList<CompilationProblem> errList){
        //was the filename already loaded?
        if (loadedFiles.contains(file)) return true;
        
        //parse file
        Program program;
        if (options.matlab()){
            //program = natlab.Main.parseMatlabFile(file.getAbsolutePath(), errList);
            program = natlab.Main.parseMatlabFile(file.getAbsolutePath(), errList); //TODO - matlab->natlab translation seems broken
        } else {
        	program = natlab.Main.parseFile(file.getAbsolutePath(), errList);
        }
        if (program == null){
            Mc4.error("cannot parse file "+file);
            return false;
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
            //create/add function
            Mc4Function function = new Mc4Function(functionAst, file);
            this.put(new FunctionReference(function.getName(),file),function);

            //recursively load referenced functions
            success = success && resolveFunctionsAndCollect(function,errList);
        }

        //TODO we should collect used siblings and get rid of unused ones
        return true;
    }
    
    /**
     * resolves all calls to other functions within given function, and collects functions not in this collection
     */
    private boolean resolveFunctionsAndCollect(Mc4Function function,ArrayList<CompilationProblem> errList){
        boolean success = true;
        
        //collect references to other functions - update symbol table, recursively collect
        for (String otherName : function.getSymbolTable().getSymbols(
                new SymbolFilter() {
                    public boolean accept(Symbol symbolType) {
                        return symbolType instanceof FunctionType;
                    }
                })){
            Mc4.debug("resolving "+otherName);
            
            //LOOKUP SEMANTICS:
            //update symbol table entry based on what the reference is
            //1 functionName could be the function itself
            if (otherName.equals(function.getName())){
                //reference to itself
                function.getSymbolTable().put(otherName, 
                        new FunctionReferenceType(new FunctionReference(otherName,function.getFile())));
                
            //2 functionName could be a nested function
            } else if (function.getAst().getNested().containsKey(otherName)){
                //TODO
                System.err.println("function "+otherName+" referring to nested function unsupported");
            
            //3 functionName could be a sibling function - which should already be loaded
            } else if (function.getSiblings().contains(otherName)){             
                function.getSymbolTable().put(otherName, 
                        new FunctionReferenceType(new FunctionReference(otherName,function.getFile())));

            //4 functionName is a builtin or a function on the path    
            } else {
                //try to find it
                File otherFunction = Mc4.functionFinder.findName(otherName);
                if (otherFunction != null){ // file found
                    success = success && collect(otherFunction,false,errList); //recursively collect other function
                    function.getSymbolTable().put(otherName,  //update symbol table entry
                            new FunctionReferenceType(new FunctionReference(otherName,otherFunction)));
                    
                } else { // file is builtin, or cannot be found
                    if (Mc4.functionFinder.isBuiltin(otherName)){ //builtin
                        function.getSymbolTable().put(otherName, 
                                new FunctionReferenceType(new FunctionReference(otherName)));
                    } else { //not found
                        Mc4.error("reference to "+otherName+" in "+function.getName()+" not found");
                        success = false;
                    }
                }
            }
        }        
        return success;
    }
    
    /**
     * inlines all functions into the main function
     */
    public void inlineAll(){
    	inlineAll(new HashSet<FunctionReference>(),getMain());
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
    	if (function.isBuiltin) return;
    	
    	//add this call to the context
    	context.add(function);
    	
    	//inline all called functions recursively
    	SymbolTable table = get(function).getSymbolTable();    	
    	for (String s : table.keySet()){
    		if (table.get(s) instanceof FunctionReferenceType) {
				FunctionReference ref = ((FunctionReferenceType)table.get(s)).getFunctionReference();
				if (!ref.isBuiltin){
					//inline recursively
					inlineAll(context,ref);
				}
			}
    	}
    	
    	//inline the calls to the given function
    	get(function).inline(this);
    	
    	//remove this context
    	context.remove(function);
    }
}




