package natlab.Static.simplification;

import java.util.*;

import ast.*;
import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import natlab.toolkits.rewrite.*;
import natlab.toolkits.rewrite.simplification.AbstractSimplification;

/**
 * This simplification will simplify switch statements into if-then else statements
 * 
 * switch (exp)
 *   case c1 ...
 *   case c2 ...
 *   case c3 ...
 *   otherwise
 * end
 * 
 * will be transformed into
 * t = exp
 * if (isequal(t,c1)
 *    ...
 * elseif (isequal(t,c2))
 *    ...
 * elseif (isequal(t,c3)
 *    ...
 * else
 *    ...
 * end
 * 
 * if a case expression (ci) is a cell array expression, i.e.
 *   case {c1,c2,c3}
 * then the check will become
 *   if (isqual(t,c1) || isequal(t,c2) || isequal(t,c3))
 *   
 * This is not a Matlab semantics preserving transformation. Rather, it attempts to keep
 * the same semantics for usual cases, but disallows/allows uncommon cases:
 *  - matlab allows the switch expression and case expressions to be either scalars, or
 *    strings. although the documentation implies that objects may be allowed, they are not.
 *    function handles are not allowed, either. Matlab also allows nonscalars for case
 *    expression, contrary to the documentation.
 *    Using isequal allows any comparison.
 * 
 *  - Case expressions can be cell arrays in Matlab, but Matlab will allow any expression
 *    that evaluates to a cell array - whether there is a cell array will only become available
 *    at runtime. The semantics underlying this Simplification only allow syntactic cell
 *    arrays as the case expression.
 *  
 * @author ant6n
 */

public class SwitchSimplification extends AbstractSimplification{

    public SwitchSimplification(ASTNode tree, VFPreorderAnalysis kind) {
        super(tree, kind);
    }
    
    @Override
    public Set<Class<? extends AbstractSimplification>> getDependencies() {
        return Collections.emptySet();
    }
    
    @Override
    public void caseSwitchStmt(SwitchStmt node) {
        rewriteChildren(node);
        newNode = new TransformedNode();
        
        //init function name
        String matchFunctionName = "isequal";
        
        //check if there are no cases
        if (node.getSwitchCaseBlockList().getNumChild() == 0){
            if (node.hasDefaultCaseBlock()){
                newNode = new TransformedNode(node.getDefaultCaseBlock().getStmtList());
                return;
            } else {
                newNode = new TransformedNode();
            }
        }

        //make t = switch_expr
        TempFactory t = TempFactory.genFreshTempFactory();       
        newNode.add(new AssignStmt(new NameExpr(t.genName()), node.getExpr()));
        
        //generate if/else, by going backwards through the cases, building else blocks
        IfStmt ifStmt = new IfStmt();
        for(SwitchCaseBlock s : node.getSwitchCaseBlockList()){
            ifStmt.addIfBlock(
                new IfBlock(
                    new ParameterizedExpr(new NameExpr(new Name(matchFunctionName)), 
                         new ast.List<Expr>().add(new NameExpr(t.genName())).add(s.getExpr())),
                    s.getStmtList()));
        }
        //otherwise block
        if (node.hasDefaultCaseBlock()){
            ifStmt.setElseBlock(new ElseBlock(node.getDefaultCaseBlock().getStmtList()));
        }
        newNode.add(ifStmt);
    }
}




