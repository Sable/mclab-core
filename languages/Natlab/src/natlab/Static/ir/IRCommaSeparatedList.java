package natlab.Static.ir;

import ast.Expr;
import ast.List;
import ast.NameExpr;

/**
 * comma separated list as it appears in function calls, or as the left
 * hand side of assignment statements. In matlab, every item that is syntacitcally
 * in the list does not necessarily represent one item semantically:
 * - cell array indexing gets expanded into multiple items: c{:}, c{t}
 * - struct array indexing might get expanded into multiple items: s.a(:), s.a(t)
 * 
 * @author ant6n
 */


public class IRCommaSeparatedList extends List<Expr>{
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
    public String getName(int index){
        if (this.getChild(index) instanceof NameExpr) {
            return ((NameExpr) this.getChild(index)).getName().getID();
        }
        return null;
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
    
    
}
