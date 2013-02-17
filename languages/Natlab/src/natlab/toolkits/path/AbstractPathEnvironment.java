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

package natlab.toolkits.path;
import java.util.*;
import natlab.toolkits.filehandling.FunctionOrScriptQuery;
import natlab.toolkits.filehandling.GenericFile;

/**
 * Represents a matlab path environment.
 * Will find matlab files, given names and locations.
 * Will also find overloaded files.
 * 
 */

abstract public class AbstractPathEnvironment implements BuiltinQuery{
    private BuiltinQuery builtinQuery;
    public AbstractPathEnvironment(BuiltinQuery builtinQuery){
        this.builtinQuery = builtinQuery;
    }
    
    
    /**
     * returns the location of the main file
     * - the matlab file where execution starts
     */
    public abstract FunctionReference getMain();
    

    @Override
    public boolean isBuiltin(String functionname) {
        if (builtinQuery == null) return false;
        return builtinQuery.isBuiltin(functionname);
    }
    
    
    /**
     * finds a function/script/class based on its name and context
     * @param name - the name
     * @param context - the location where this function is being called
     * @return
     */
    public abstract FunctionReference resolve(String name, GenericFile context); 

    
    /**
     * finds functions/scripts/classes based on its name and context,
     * but does though for overloaded functions. Returns a map
     * type -> file
     * where the type is given as a String, an empty String refer to 
     * not overloaded functions 
     * resolveAll(a,b).get("") should be the same as resolve(a,b)
     * 
     * @param name - the name
     * @param context - the location where this function is being called
     * @return
     */    
    public abstract Map<String,FunctionReference> resolveAll(String name,GenericFile context);    
    
    
    /**
     * finds a matlab file based on its name, and class type - i.e. finds
     * overloaded functions.
     * 
     * @param name
     * @param className
     * @param context
     * @return a function reference to the overloaded function, or null if it cannot be found
     */
    public abstract FunctionReference resolve(String name, String className, GenericFile context);

    
    /**
     * returns all .m files that are overloaded for the given class name, as a map,
     * where the keys are the names of the functions and the values are the file locations
     */
    public abstract Map<String,FunctionReference> getAllOverloaded(String className, GenericFile cotntext);
    
    
    /**
     * returns a  FunctionOrScriptQuery object for the given context
     * @param context
     * @return
     */
    public FunctionOrScriptQuery getFunctionOrScriptQuery(final GenericFile context){
        return new FunctionOrScriptQuery() {
            public boolean isFunctionOrScript(String name) {
                return (builtinQuery.isBuiltin(name)) || (resolve(name,context) != null);
            }
            public boolean isPackage(String name) {
                return false;
            }
        };
    }
    
}
