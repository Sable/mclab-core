package natlab.Static.mc4.builtin;

import java.util.*;

import natlab.toolkits.filehandling.FunctionFinder.BuiltinQuery;

public class Mc4BuiltinQuery implements BuiltinQuery {
    HashSet<String> builtins;
    
    public Mc4BuiltinQuery(){
        builtins = new HashSet<String>();
        builtins.add("false");
        builtins.add("true");
        builtins.add("and");
        builtins.add("mean");
        builtins.add("clock");
        builtins.add("exp");        
        builtins.add("abs");
        builtins.add("zeros");
        builtins.add("sum");
        builtins.add("round");
        builtins.add("sqrt");
        builtins.add("i");
        builtins.add("plus");     
        builtins.add("uplus");      
        builtins.add("minus");      
        builtins.add("uminus");     
        builtins.add("mtimes");     
        builtins.add("times");      
        builtins.add("mpower");     
        builtins.add("power");      
        builtins.add("mldivide");   
        builtins.add("mrdivide");   
        builtins.add("ldivide");    
        builtins.add("rdivide");    
        builtins.add("transpose");    
        builtins.add("colon");
        builtins.add("horzcat");
        builtins.add("lt");
    }
    
    public boolean isBuiltin(String functionname) {
        return builtins.contains(functionname);
    }

}
