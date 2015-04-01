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
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.HashMap;

import natlab.toolkits.filehandling.FunctionOrScriptQuery;
import ast.ASTNode;
import ast.Function;
import ast.Program;

public class LookupFile { 
  private static HashMap<String, String> builtinClasses = initialize();
  public  static HashMap<String, String> builtinFunctions;
  private static HashMap<String, String> builtinPackages;
  private static HashMap<String, String> outputInfo;
  private static HashMap<String, Function> currentFile;
  private static HashMap<String, ASTNode<?>> lib;

  private static HashMap<String, String> initialize(){
    HashMap<String, HashMap<String, String>> map;
    try {
      InputStream fin = LookupFile.class.getResourceAsStream("builtin.ser");
      ObjectInputStream oin = new ObjectInputStream(fin);
      map = (HashMap<String, HashMap<String, String>>) oin.readObject();
    } catch(FileNotFoundException e){
      e.printStackTrace();
      System.err.println("Library definitions file not found.");
      return null;
    } catch(Exception e){
      e.printStackTrace();
      System.err.println("Error while loading library definition file");
      return null;
    }
    builtinPackages = map.get("packages");
    builtinClasses = map.get("classes");
    builtinFunctions = map.get("functions");
    outputInfo = map.get("output_info");
    currentFile = new HashMap<>();
    lib = new HashMap<>();
    return builtinClasses;
  };

  public static String getOutputInfo(String function){
    return outputInfo.get(function);
  }

  public static boolean packageExists(String s) {
    return builtinPackages.containsKey(s);
  }

  public static boolean scriptOrFunctionExists(String s) {
    return currentFile.containsKey(s)
        || lib.containsKey(s)
        || builtinClasses.containsKey(s)
        || builtinFunctions.containsKey(s);
  } 

  public static void setPrograms(HashMap<String, Program> progs){
    lib.clear();
    lib.putAll(progs);
  }

  /**
   * returns a FunctionOrScriptQuery object that allows querying
   * which matlab functions exist in the file environment simulated
   * by LookupFile.
   */
  public static FunctionOrScriptQuery getFunctionOrScriptQueryObject(){
    return new FunctionOrScriptQuery() {
      @Override public boolean isFunctionOrScript(String name) {
        return scriptOrFunctionExists(name);
      }

      @Override public boolean isPackage(String name) {
        return packageExists(name);
      }
    };
  }
}
