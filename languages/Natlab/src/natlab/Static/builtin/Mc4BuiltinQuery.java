// =========================================================================== //
//                                                                             //
// Copyright 2008-2011 Andrew Casey, Jun Li, Jesse Doherty,                    //
//   Maxime Chevalier-Boisvert, Toheed Aslam, Anton Dubrau, Nurudeen Lameed,   //
//   Amina Aslam, Rahul Garg, Soroush Radpour, Olivier Savary Belanger,        //
//   Laurie Hendren, Clark Verbrugge and McGill University.                    //
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
//   limitations under the License.                                            //
//                                                                             //
// =========================================================================== //

package natlab.Static.builtin;

import java.util.*;

import natlab.toolkits.path.BuiltinQuery;

public class Mc4BuiltinQuery implements BuiltinQuery {
    HashSet<String> builtins;
    
    public Mc4BuiltinQuery(){
        builtins = new HashSet<String>();
        builtins.add("false");
        builtins.add("true");
        builtins.add("size");
        builtins.add("and");
        builtins.add("mean");
        builtins.add("clock");
        builtins.add("pi");
        builtins.add("exp");
        builtins.add("abs");
        builtins.add("sin");
        builtins.add("cos");
        builtins.add("zeros");
        builtins.add("sum");
        builtins.add("round");
        builtins.add("fix");
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
    }
    
    public boolean isBuiltin(String functionname) {
        return builtins.contains(functionname);
    }

}
