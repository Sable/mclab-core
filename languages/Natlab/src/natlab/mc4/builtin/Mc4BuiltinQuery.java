package natlab.mc4.builtin;

import java.util.*;

import natlab.toolkits.filehandling.FunctionFinder.BuiltinQuery;

public class Mc4BuiltinQuery implements BuiltinQuery {
    HashSet<String> builtins;
    
    public Mc4BuiltinQuery(){
        builtins = new HashSet<String>();
        builtins.add("mean");        
        builtins.add("clock");
        builtins.add("exp");        
        builtins.add("abs");
        builtins.add("zeros");
        builtins.add("sum");
        builtins.add("round");
        builtins.add("sqrt");
        builtins.add("i");
    }
    
    public boolean isBuiltin(String functionname) {
        return builtins.contains(functionname);
    }

}
