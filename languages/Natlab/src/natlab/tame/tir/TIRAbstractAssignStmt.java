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

import java.util.ArrayList;

import ast.*;

public abstract class TIRAbstractAssignStmt extends AssignStmt implements TIRStmt {
    private static final long serialVersionUID = 1L;

    public TIRAbstractAssignStmt() {
        super();
    }
    
    /**
     * given a List<Expr>, returns a List<NameExpr> by casting
     * all the elements to NameExpr (i.e. they have to be NameExpr at runtime)
     * @return
     */
    protected static List<NameExpr> exprListToNameExprList(List<Expr> list){
        List<NameExpr> out = new List<NameExpr>();
        for (Expr e : list){
            out.add((NameExpr)e);
        }
        return out;
    }
    
    /**
     * given a List<Expr> which are just name expressions,
     * returns an ArrayList<String> which are the names
     */
    protected static ArrayList<String> exprListToStringList(List<Expr> args){
        ArrayList<String> list = new ArrayList<String>(args.getNumChild());
        for (Expr e : args){
            list.add(((NameExpr)e).getName().getID());
        }
        return list;
    }
    
    /**
     * given a List<NameExpr>, returns a List<Expr> which are the same
     */
    protected static List<Expr> nameExprListToExprList(List<NameExpr> list){
        List<Expr> out = new List<Expr>();
        for (NameExpr n : list) out.add(n);
        return out;
    }
}
