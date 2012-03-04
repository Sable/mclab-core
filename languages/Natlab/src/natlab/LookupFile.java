/*
Copyright 2011 Anton Dubrau, Soroush Radpour and McGill University.

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

package natlab;
import java.util.*;
import java.io.*;

import natlab.toolkits.filehandling.FunctionOrScriptQuery;
import ast.*;

public class LookupFile{ 
	public  static HashMap<String, String> builtinClasses = initialize();
    public  static HashMap<String, String> builtinFunctions;
    public  static HashMap<String, String> builtinPackages;
    private static HashMap<String, String> outputInfo;
    private static HashMap<String, Function> currentFile;
    public static HashMap<String, ASTNode> lib; 
    private static HashMap<String, String> initialize(){
        try{
            InputStream fin=LookupFile.class.getResourceAsStream("builtin.ser");
            ObjectInputStream oin = new ObjectInputStream(fin);
            HashMap<String, HashMap<String, String>> map = (HashMap<String, HashMap<String, String>>) oin.readObject();
            builtinPackages = map.get("packages");
            builtinClasses = map.get("classes");
            builtinFunctions = map.get("functions");
            outputInfo = map.get("output_info");
            currentFile = new HashMap<String, Function>();
            lib = new HashMap<String, ASTNode>();
            return builtinClasses;
        }
        catch(FileNotFoundException e){
            e.printStackTrace();
            System.err.println("Library definitions file not found.");
        }catch(Exception e){
            e.printStackTrace();
            System.err.println("Error while loading library definition file");
        }
        return null;
    }; 

    public static String getOutputInfo(String function){
        if (outputInfo.containsKey(function))
            return outputInfo.get(function);
        return "DWH,H";
    }

    public static boolean packageExists(String s){
        if (builtinPackages.containsKey(s)) return true; 
        return false;
    }

    public static boolean scriptOrFunctionExists(String s){
        if (currentFile.containsKey(s)) {
            return true;
        }
        if (lib.containsKey(s)) return true; 
        if (builtinClasses.containsKey(s)) return true; 
        if (builtinFunctions.containsKey(s)) return true; 
        return false; 
    } 

    public static void setPrograms(HashMap<String, Program> progs){
        lib.clear();
        lib.putAll(progs);
    }

    //TODO remove this - this does not capture names correctly in the case
    //of nested functions exist - there should be separate methods
    //setCurrentScript and setCurrentFunction, or something like that
    @Deprecated
    public static void setCurrentProgram(Program f){
        currentFile.clear();
        if (!FunctionList.class.isInstance(f))
            return;
        for (Function function: ((FunctionList)f).getFunctions())
            currentFile.put(function.getName(), function);
    }
    
    
    /**
     * returns a FunctionOrScriptQuery object that allows querying
     * which matlab functions exist in the file environment simulated
     * by LookupFile.
     */
    public static FunctionOrScriptQuery getFunctionOrScriptQueryObject(){
        return new FunctionOrScriptQuery() {
            public boolean isFunctionOrScript(String name) {
                boolean res = scriptOrFunctionExists(name);
                return res;
            }

            @Override
            public boolean isPackage(String name) {
                return packageExists(name);
            }
        };
    }
}

