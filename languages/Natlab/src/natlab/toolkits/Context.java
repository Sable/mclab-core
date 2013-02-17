/*
Copyright 2011 Soroush Radpour and McGill University.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
*/
package natlab.toolkits;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import natlab.toolkits.filehandling.FunctionOrScriptQuery;
import natlab.toolkits.filehandling.GenericFile;
import natlab.toolkits.path.BuiltinQuery;
import natlab.toolkits.path.FolderHandler;
import natlab.toolkits.path.FunctionReference;
import natlab.utils.NodeFinder;
import ast.ASTNode;
import ast.Function;
import ast.FunctionList;
import ast.Program;

public class Context{
    public final boolean inFunction;
    public final boolean inNested;
    public final ASTNode<?> curFunction; 
    public final FolderHandler pwd; 
    public final FolderHandler fwd; 
    public final java.util.List<FolderHandler> path; 
    public final Program curProgram;
    public final BuiltinQuery builtinQuery;
    
    
    /**
     * @param name the name that we want to resolve.
     * @param dominDominant Dominant parameter type (class). Can be null when the specific is not known.
    */
    public FunctionReference resolve(String name, String dominantType){
	if (inNested){
		//FIXME this doesn't look like proper lookup among nested functions
	    Function f = ((FunctionList)curProgram).getNested().get(name);
	    if( f != null )
		return new FunctionReference(curProgram.getFile(), f, FunctionReference.ReferenceType.NESTED);
	}

	if (inFunction){
		//FIXME the getsiblings has to be set up in the parser somewhere... is there a way to use jastadd functions that don't have to be set explicitly?
	    Function f = ((FunctionList)curProgram).getSiblings().get(name);
	    if( f != null )
		return new FunctionReference(curProgram.getFile(), f, FunctionReference.ReferenceType.SUBFUNCTION);
	    
	    //also do a different search .. this is probably broken, but we'll do a quick fix
	    for (Function f1 : ((FunctionList)curProgram).getFunctions()){
	    	if (f1.getName().equals(name)) return 
	    			new FunctionReference(curProgram.getFile(), f1, FunctionReference.ReferenceType.SUBFUNCTION);
	    }
	}

	if (curProgram.getFile().getNameWithoutExtension().equals(name)) {
	    if (inFunction)
		throw new RuntimeException("Unexpected ?!");
	    return new FunctionReference(curProgram.getFile());

	}

	if (fwd != null){
	    GenericFile lookupResult = fwd.lookupPrivateFunctions(name);
	    if (lookupResult != null)
	    	return new FunctionReference(lookupResult, FunctionReference.ReferenceType.PRIVATE);
	    lookupResult = fwd.lookupFunctions(name);
	    if (lookupResult != null)
	    	return new FunctionReference(lookupResult, FunctionReference.ReferenceType.UNKNOWN);
	    
	}
		
	
	//TODO - is this the right position for the builtin query?
	if (builtinQuery != null){
		if (builtinQuery.isBuiltin(name)){
			return new FunctionReference(name);
		}
	}
	
	for (FolderHandler p: path){
	    GenericFile c = p.lookupClasses(name);
	    if (c!=null) return new FunctionReference(c, FunctionReference.ReferenceType.CLASS_CONSTRUCTOR);
	}
	
	if (dominantType != null){
	    for (FolderHandler p: path){
		GenericFile c = p.lookupSpecialized(name, dominantType);
		if (c!=null) return new FunctionReference(c, FunctionReference.ReferenceType.OVERLOADED);
	    }
	}
	
	for (FolderHandler p: path){
	    GenericFile c = p.lookupPackage(name);
	    if (c!=null) return new FunctionReference(c, FunctionReference.ReferenceType.PACKAGE); 
	}

	if (pwd != null){
	    GenericFile lookupResult = pwd.lookupFunctions(name);
	    if (lookupResult != null)
		return new FunctionReference(lookupResult, FunctionReference.ReferenceType.UNKNOWN);
	}

	for (FolderHandler p: path){
	    GenericFile c = p.lookupFunctions(name);
	    if (c!=null) return new FunctionReference(c, FunctionReference.ReferenceType.UNKNOWN) ;
	}
	return null;
    }

    public FunctionReference resolve(String name){
	return resolve(name, null);
    }

    public FunctionReference resolve(ast.NameExpr name, String dominantType){
	return resolve(name.getName().getID(), dominantType);
    }

    
    public Map<String, GenericFile> getAllOverloads(String functionName){
	HashMap<String, GenericFile> res = new HashMap<String, GenericFile>();
	for (FolderHandler p: path){
	    res.putAll(p.lookupSpecializedAll(functionName));
	}
	return res;
    }
    
    public Context(ASTNode<?> curFunction, FolderHandler pwd, FolderHandler fwd, List<FolderHandler> path,BuiltinQuery query){
    	if (curFunction instanceof Function)
    		this.inFunction = true;
    	else 
    		this.inFunction = false;

    	if (curFunction.getParent() != null && 
    			curFunction.getParent().getParent() instanceof Function){
    		inNested = true;
    	}
    	else
    		inNested = false;

    	if (inNested){
    		curProgram = NodeFinder.findParent(Program.class, curFunction);
    	}
    	else
    		if (inFunction)
    			curProgram = (Program)curFunction.getParent().getParent();
    		else
    			curProgram = (Program)curFunction;

    	this.curFunction = curFunction;
    	this.pwd = pwd;
    	if (inFunction && fwd == null)
    		this.fwd = FolderHandler.getFolderHandler(curProgram.getFile().getParent());
    	else
    		this.fwd = fwd;
    	this.path = path;
    	this.builtinQuery = query;
    }
    

    
    public Context(ASTNode<?> curFunction, FolderHandler pwd, FolderHandler fwd, List<FolderHandler> path){
    	this(curFunction,pwd,fwd,path,null);
    }
    
    public FunctionOrScriptQuery getFunctionOrScriptQuery(){
    	return new FunctionOrScriptQuery() {
			public boolean isPackage(String name) {
				FunctionReference ref = resolve(name);
				return (ref != null 
						&& ref.referenceType == FunctionReference.ReferenceType.PACKAGE);
			}
			public boolean isFunctionOrScript(String name) {
				FunctionReference ref = resolve(name);
				return (ref != null 
						&& ref.referenceType != FunctionReference.ReferenceType.PACKAGE);
			}
		};
    }
}
