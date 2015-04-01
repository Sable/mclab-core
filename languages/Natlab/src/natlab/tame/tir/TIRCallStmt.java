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

package natlab.tame.tir;
import natlab.tame.tir.analysis.TIRNodeCaseHandler;
import ast.Expr;
import ast.Name;
import ast.NameExpr;
import ast.ParameterizedExpr;

/**
 * a call is of the form
 * 
 * [lhs1,lhs2,...] = f(arg1,arg2,...)
 * 
 * where the number of left-hand-side and right-hand-side variables
 * is 0 or more
 * 
 * Note that with 0 variables on the left hand side, this becomes
 * [] = ...
 * which is invalid Matlab.
 * The pretty print method is overriden to still produce valid matlab code,
 * but analyses should be aware of this.
 * 
 * TODO
 * - while an IR node has to extend an existing node, it's children do not
 * --> we can have IRnodes that have a different possible children,
 * ---> we can deal with some things?
 * 
 * 
 * @author ant6n
 *
 */
public class TIRCallStmt extends TIRAbstractAssignToListStmt {
    private static final long serialVersionUID = 1L;
    
    public TIRCallStmt(Name function,Expr target,Expr... args) {
        this(new NameExpr(function),
                new TIRCommaSeparatedList(target),new TIRCommaSeparatedList(args));
    }
    
    public TIRCallStmt(Name function,TIRCommaSeparatedList targets,TIRCommaSeparatedList args) {
        this(new NameExpr(function),targets,args);
    }
    
    public TIRCallStmt(NameExpr function,TIRCommaSeparatedList targets,TIRCommaSeparatedList args) {
        //set lhs
        super(targets);
        
        //set rhs
        setRHS(new ParameterizedExpr(function, args));
    }
    
    //function name get
    public Name getFunctionName(){
        return ((NameExpr)(((ParameterizedExpr)getRHS()).getTarget())).getName();
    }
        
    //get arguments
    public TIRCommaSeparatedList getArguments(){
         return (TIRCommaSeparatedList)(((ParameterizedExpr)getRHS()).getArgList());
    }    
    
    
    @Override
    public void tirAnalyze(TIRNodeCaseHandler irHandler) {
        irHandler.caseTIRCallStmt(this);
    }

}



