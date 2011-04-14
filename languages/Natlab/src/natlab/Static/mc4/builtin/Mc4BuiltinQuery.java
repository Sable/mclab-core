package natlab.Static.builtin;

import java.util.*;

import natlab.toolkits.path.BuiltinQuery;

public class Mc4BuiltinQuery implements BuiltinQuery {
    HashSet<String> builtins;
    
    public Mc4BuiltinQuery(){
        builtins = new HashSet<String>();
        builtins.add("end");
        builtins.add("false");
        builtins.add("true");
        builtins.add("size");
        builtins.add("and");
        builtins.add("or");
        builtins.add("mean");
        builtins.add("clock");
        builtins.add("pi");
        builtins.add("exp");
        builtins.add("abs");
        builtins.add("sin");
        builtins.add("cos");
        builtins.add("zeros");
        builtins.add("sum");
        builtins.add("rank");
        builtins.add("round");
        builtins.add("fix");
        builtins.add("ceil");
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
        builtins.add("ctranspose");
        builtins.add("colon");
        builtins.add("horzcat");
        builtins.add("vertcat");
        builtins.add("lt");
        builtins.add("le");
        builtins.add("eq");
        builtins.add("gt");
        builtins.add("ge");
        builtins.add("ne");
        builtins.add("not");
        builtins.add("length");
        builtins.add("toc");
        builtins.add("tic");
        builtins.add("nargin");
        builtins.add("fprintf");
        builtins.add("floor");
        builtins.add("ones");
        builtins.add("min");
        builtins.add("max");
        builtins.add("dot");
        builtins.add("numel");
        builtins.add("dyaddown");
        builtins.add("sort");
        builtins.add("error");
        builtins.add("flipud");
        builtins.add("toeplitz");
        builtins.add("eig");
        builtins.add("norm");
        builtins.add("conv");
        builtins.add("bitand");
        builtins.add("linspace");
        builtins.add("mod");
        builtins.add("eps");
        builtins.add("imag");
        builtins.add("rand");
        builtins.add("inf");
        builtins.add("sign");
        builtins.add("reshape");
        builtins.add("imwrite");
        builtins.add("sparse");
        builtins.add("feval");
        builtins.add("diag");
        builtins.add("cell");
        builtins.add("blkdiag");
        builtins.add("sprintf");
        builtins.add("load");
        builtins.add("isequal");
        builtins.add("unique");
        builtins.add("squeeze");
        builtins.add("isempty");
        builtins.add("any");
        builtins.add("prod");
        builtins.add("nargout");
        builtins.add("eye");
        builtins.add("disp");
        builtins.add("find");
        
        
    }
    
    public boolean isBuiltin(String functionname) {
        return builtins.contains(functionname);
    }

}
