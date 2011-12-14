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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import natlab.Static.ir.analysis.IRNodeCaseHandler;
import ast.*;

/**
 * assignments of the form
 * t = @foo
 * or
 * t = @(x1,x2,...)foo(a1,a2,..,x1,x2,...)
 * 
 * where foo is a name (function)
 * 
 * 
 */

public class IRCreateFunctionHandleStmt extends IRAbstractAssignToVarStmt {
    private static final long serialVersionUID = 1L;

    /**
     * creates an asignment of the form
     * lhs = \@function
     */
    public IRCreateFunctionHandleStmt(Name lhs,Name function) {
        super((lhs));
        FunctionHandleExpr fHandleExpr = new FunctionHandleExpr(function);
        setRHS(fHandleExpr);
    }
    
    
    /**
     * creates an assignment of the form
     * lhs = \@(params),function(vars)
     */
    public IRCreateFunctionHandleStmt(Name lhs,Name function,List<Name> params,IRCommaSeparatedList vars){
        super(lhs); 
        if (!vars.isAllNameExpressions()) throw new UnsupportedOperationException("function call in lambda must use only names");
        //TODO - check whether the lambda params match the called vars
        setRHS(new LambdaExpr(
                params,
                new ParameterizedExpr(new NameExpr(function),vars)));
    }
    
    /**
     * returns true if this statement is a lambda, i.e. it uses workspace variables
     */
    public boolean isLambda(){
        return this.getRHS() instanceof LambdaExpr;
    }
    
    
    /**
     * returns the Name of the function on the rhs
     * @return
     */
    public Name getFunctionName(){
        if (isLambda()){
            return ((NameExpr)((ParameterizedExpr)((LambdaExpr)getRHS()).getBody()).getTarget()).getName();
        } else {
            return ((FunctionHandleExpr)getRHS()).getName();
        }
    }
    
    /**
     * returns the lamdba parameters as a list of names - will return an empty list
     * if this is not lambda
     */
    public List<Name> getLambdaParams(){
        if (!isLambda()){
            return new List<Name>();
        } else {
            return ((LambdaExpr)getRHS()).getInputParamList();
        }
        
    }
    
    
    /**
     * returns a list of the variables that are enclosed by this function handle creation
     * (i.e. curried values), i.e. for
     * f = \@(x1,x2,...) function(a1,a2,...,x1,x2,...)
     * it will return [a1,a2,...]
     * If this function handle creation statement doesn't use a lambda, this will return
     * and empty lsit.
     */
    public java.util.List<Name> getEnclosedVars(){
        if (!isLambda()){
            return Collections.emptyList();
        } else {
            LinkedList<Name> result = new LinkedList<Name>();
            List<Name> params = getLambdaParams();
            List<Expr> args = ((ParameterizedExpr)((LambdaExpr)getRHS()).getBody()).getArgs();
            for (int i = 0; i < (args.getNumChild()-params.getNumChild()); i++){
                result.add(((NameExpr)args.getChild(i)).getName());
            }
            return result;
        }
    }
    
    
    @Override
    public void irAnalyize(IRNodeCaseHandler irHandler) {
        irHandler.caseIRCreateFunctionHandleStmt(this);
    }

}

