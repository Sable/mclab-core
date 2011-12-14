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

package natlab.Static.ir;
import natlab.Static.ir.analysis.IRNodeCaseHandler;
import ast.*;

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
public class IRCallStmt extends IRAbstractAssignToListStmt {
    private static final long serialVersionUID = 1L;
    
    public IRCallStmt(Name function,Expr target,Expr... args) {
        this(new NameExpr(function),
                new IRCommaSeparatedList(target),new IRCommaSeparatedList(args));
    }
    
    public IRCallStmt(Name function,IRCommaSeparatedList targets,IRCommaSeparatedList args) {
        this(new NameExpr(function),targets,args);
    }
    
    public IRCallStmt(NameExpr function,IRCommaSeparatedList targets,IRCommaSeparatedList args) {
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
    public IRCommaSeparatedList getArguments(){
         return (IRCommaSeparatedList)(((ParameterizedExpr)getRHS()).getArgList());
    }    
    
    
    @Override
    public void irAnalyize(IRNodeCaseHandler irHandler) {
        irHandler.caseIRCallStmt(this);
    }

}



