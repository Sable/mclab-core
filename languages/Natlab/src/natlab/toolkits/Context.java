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
import java.util.*;
import ast.*;
import natlab.toolkits.utils.NodeFinder;
import natlab.toolkits.filehandling.*;
import natlab.toolkits.filehandling.genericFile.*;
import natlab.toolkits.path.*;

public class Context{
    public final boolean inFunction;
    public final boolean inNested;
    public final ASTNode curFunction; 
    public final FolderHandler pwd; 
    public final FolderHandler fwd; 
    public final java.util.List<FolderHandler> path; 
    public final Program curProgram;
    
    
    /**
     * @param name the name that we want to resolve.
     * @param dominDominant Dominant parameter type (class). Can be null when the specific is not known.
    */
    public FunctionReference resolve(String name, String dominantType){
	if (inNested){
	    Function f = ((FunctionList)curProgram).getNested().get(name);
	    if( f != null )
		return new FunctionReference(curProgram.getFile(), f, FunctionReference.ReferenceType.NESTED);
	}

	if (inFunction){
	    Function f = ((FunctionList)curProgram).getSiblings().get(name);
	    if( f != null )
		return new FunctionReference(curProgram.getFile(), f, FunctionReference.ReferenceType.SUBFUNCTION);
	}

	if (curProgram.getName() == name){
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
    
    public Context(ASTNode curFunction, FolderHandler pwd, FolderHandler fwd, java.util.List<FolderHandler> path){
	if (curFunction instanceof Function)
	    this.inFunction=true;
	else 
	    this.inFunction=false;

	if (curFunction.getParent()!=null && 
	    curFunction.getParent().getParent() instanceof Function){
	    inNested=true;
	}
	else
	    inNested=false;

	if (inNested){
	    curProgram=NodeFinder.findParent(curFunction, Program.class);
	}
	else
	    if (inFunction)
		curProgram=(Program)curFunction.getParent().getParent();
	    else
		curProgram=(Program)curFunction;
    
	this.curFunction= curFunction;
	this.pwd = pwd;
	if (inFunction && fwd==null)
	    this.fwd = FolderHandler.getFolderHandler(curProgram.getFile().getParent());
	else
	    this.fwd = fwd;
	this.path=path;
    }
}
