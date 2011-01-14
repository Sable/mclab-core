package natlab;
import java.util.*;
import java.io.*;
import ast.*;

public class LookupFile{ 
    private static HashMap<String, String> builtinClasses = initialize();
    private static HashMap<String, String> builtinFunctions;
    private static HashMap<String, String> builtinPackages;
    private static HashMap<String, String> outputInfo;
    private static HashMap<String, Function> currentFile;
    private static HashMap<String, ASTNode> lib; 
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
	}catch(Exception e){
              e.printStackTrace();
              System.err.println("Library definitions file not found.");
	}
	return null;
    }; 

    public static String getOutputInfo(String function){
	if (outputInfo.containsKey(function))
	    return outputInfo.get(function);
	return "DWH,H";
    }

    public static boolean scriptOrFunctionExists(String s){
	if (currentFile.containsKey(s)) {
	    return true;
	}
	if (lib.containsKey(s)) return true; 
	if (builtinClasses.containsKey(s)) return true; 
	if (builtinPackages.containsKey(s)) return true; 
	if (builtinFunctions.containsKey(s)) return true; 
	return false; 
    } 

    public static void setPrograms(HashMap<String, Program> progs){
	lib.clear();
	lib.putAll(progs);
    }

    public static void setCurrentProgram(Program f){
	currentFile.clear();
	if (!FunctionList.class.isInstance(f))
	    return;
	for (Function function: ((FunctionList)f).getFunctions())
	    currentFile.put(function.getName(), function);
    }
}