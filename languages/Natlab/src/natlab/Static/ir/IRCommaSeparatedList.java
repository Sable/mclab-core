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

import java.util.*;

import natlab.Static.ir.analysis.IRNodeCaseHandler;
import ast.Expr;
import ast.List;
import ast.Name;
import ast.NameExpr;

/**
 * comma separated list as it appears in function calls, or as the left
 * hand side of assignment statements. In matlab, every item that is syntacitcally
 * in the list does not necessarily represent one item semantically:
 * - cell array indexing gets expanded into multiple items: c{:}, c{t}
 * - struct array indexing might get expanded into multiple items: s.a(:), s.a(t)
 * 
 * 
 * ~ may also be represented here
 * @author ant6n
 */


public class IRCommaSeparatedList extends List<Expr> implements IRNode {
    private static final long serialVersionUID = 1L;

    public IRCommaSeparatedList(){
    }

    public IRCommaSeparatedList(Expr... exprs){
        this();
        for (Expr e : exprs){
            add(e);
        }
    }
    
    @Override
    public List<Expr> add(Expr node) {
        // TODO Auto-generated method stub
        return super.add(node);
    }
    
    
    /**
     * returns the name expression at the given index 
     * if the item at that position is not a name, returns null
     * @param index
     * @return
     */
    public NameExpr getNameExpresion(int index){
        if (this.getChild(index) instanceof NameExpr) {
            return (NameExpr) this.getChild(index);
        }
        return null;
    }

    
    /**
     * returns the name at the given index 
     * if the item at that position is not a name, returns null
     * @param index
     * @return
     */
    public Name getName(int index){
        if (this.getChild(index) instanceof NameExpr) {
            return ((NameExpr) this.getChild(index)).getName();
        }
        return null;
    }

    /**
     * returns a list of all elements as names 
     * causes an error if the elements are not all names
     */
    public java.util.List<Name> asNameList(){
        ArrayList<Name> list = new ArrayList<Name>(this.numChildren());
        for (int i = 0; i < numChildren(); i++){
            list.add(getName(i));
        }
        return list;
    }
    
        
    /**
     * returns true if all the expressions in the list are just name expressions
     */
    //TODO - make this more efficient
    public boolean isAllNameExpressions(){
        for (int i = 0; i < getNumChild(); i++){
            if (!(getChild(i) instanceof NameExpr)) return false;
        }
        return true;
    }
    
    
    @Override
    public void irAnalyize(IRNodeCaseHandler irHandler) {
        irHandler.caseIRCommaSeparatedList(this);
    }

    
    /**
     * returns the number of elements in the list
     * @return
     */
    public int size(){
        return getNumChild();
    }
    
}
