package natlab.mc4.IR.transform;

import java.util.*;

import natlab.mc4.IR.*;
import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import natlab.toolkits.rewrite.*;
import natlab.toolkits.rewrite.simplification.AbstractSimplification;
import natlab.toolkits.rewrite.simplification.FullSimplification;
import natlab.toolkits.rewrite.simplification.RightSimplification;
import ast.*;

/**
 * transforms a given AST, which already is in three address form,
 * to IR form.
 * @author ant6n
 *
 *
 * The three address form of the AST allows
 * - literals inside expressions -> have to be reduced to assignments
 * - expressions get reduced to function forms (binaries, unaries, matrixes,...)
 * 
 * 
 */

public class ThreeAddressToIR extends AbstractSimplification {
    public ThreeAddressToIR(ASTNode<?> tree, VFPreorderAnalysis nameResolver) {
        super(tree,nameResolver);
    }

    @Override
    public Set<Class<? extends AbstractSimplification>> getDependencies() {
        HashSet<Class<? extends AbstractSimplification>> dependencies = 
                new HashSet<Class<? extends AbstractSimplification>>();
        dependencies.add( FullSimplification.class );
        return dependencies;
    }
    
    
    /**
     * case program - get new name resolver
    public void caseProgram( Program node )
    {
        nameResolver = new VFPreorderAnalysis( node );
        nameResolver.analyze();
        rewriteChildren( node );
    }*/
        
    
    /**
     * For the for statement, we have to pull out whatever the initial assignment does
     *
    public void caseForStmt(ForStmt node) {
        //do the initial "assignment"
        rewrite(node.getAssignStmt());
    }*/
    
    
    public void caseAssignStmt(AssignStmt node) {
        //we need to pull out the assignments to literals etc.
        LinkedList<AssignStmt> assignments = new LinkedList<AssignStmt>();
        
        //ArraySet - lhs a parameterized var
        if (isParameterizedVar(node.getLHS())){
            ParameterizedExpr param = (ParameterizedExpr)(node.getLHS());
            node.setRHS(pullOutLiteral(node.getRHS(),assignments));
            node = new IRArraySet((NameExpr)(param.getTarget()), 
                    listToCommaSeparatedList(param.getArgList(), null, false), (NameExpr)node.getRHS());
        } else {
            //there can only be one or more vars on the left hand side
            
            //ArrayGet - rhs is a parametrized var
            if (isParameterizedVar(node.getRHS())){
                ParameterizedExpr param = (ParameterizedExpr)node.getRHS();
                node = new IRArrayGet((NameExpr)node.getLHS(),(NameExpr)param.getTarget(),
                        listToCommaSeparatedList(param.getArgList(),assignments,true));
            } else
                
            //ArrayGet - rhs is a name expression
            if (node.getRHS() instanceof NameExpr && isVar((NameExpr)(node.getRHS()))){
                node = new IRArrayGet((NameExpr)node.getLHS(),(NameExpr)node.getRHS(), new IRCommaSeparatedList());
            } else
            
            //rhs is a call
            if (isCall(node.getRHS())){
                NameExpr function;
                IRCommaSeparatedList args;
                //get args and function
                if (node.getRHS() instanceof NameExpr){
                    function = (NameExpr)(node.getRHS());
                    args = new IRCommaSeparatedList();
                } else if (node.getRHS() instanceof ParameterizedExpr){
                    ParameterizedExpr param = (ParameterizedExpr)(node.getRHS());
                    function = (NameExpr)(param.getTarget());
                    args = listToCommaSeparatedList(param.getArgList(), assignments, true);
                } else {
                    throw new UnsupportedOperationException("expected call, got "+node.getPrettyPrinted());
                }
                
                node = new IRCallStmt(function, 
                        exprToCommaSeparatedList(node.getLHS()),args);
                
                //TODO deal with builtins
                
            } else

            //assigning a literal
            if (node.getRHS() instanceof LiteralExpr){
                node = new IRAssignLiteralStmt((NameExpr)(node.getLHS()),(LiteralExpr)(node.getRHS()));
            } else
                
            //rhs is a  binary operation
            if (node.getRHS() instanceof BinaryExpr){
                BinaryExpr bin = (BinaryExpr)(node.getRHS());
                IRCommaSeparatedList target = new IRCommaSeparatedList();
                target.add(node.getLHS());
                IRCommaSeparatedList args = new IRCommaSeparatedList();
                args.add(pullOutLiteral(bin.getLHS(), assignments));
                args.add(pullOutLiteral(bin.getRHS(), assignments));
                node = new IRCallStmt(new NameExpr(new Name(bin.getOperatorName())), target, args);
                //TODO - these should be builtins
            } else
                
            //rhs is a unary operation
            if (node.getRHS() instanceof UnaryExpr){
                UnaryExpr un = (UnaryExpr)(node.getRHS());
                IRCommaSeparatedList target = new IRCommaSeparatedList();
                target.add(node.getLHS());
                IRCommaSeparatedList args = new IRCommaSeparatedList();
                args.add(pullOutLiteral(un.getOperand(), assignments));
                node = new IRCallStmt(new NameExpr(new Name(un.getOperatorName())), target, args);
                //TODO - builtins
            } else
                
            //matrix expresssion
            if (node.getRHS() instanceof MatrixExpr){
                node = expandMatrix((MatrixExpr)node.getRHS(),(NameExpr)node.getLHS(),assignments);
            } else
                
            //building cell arrays
            if(node.getRHS() instanceof CellArrayExpr){
                //TODO
            } else
                
            //colon expressions
            if(node.getRHS() instanceof RangeExpr){
                RangeExpr re = (RangeExpr)node.getRHS();
                
                
                
            } else
                
            //something not implemented
            {
                throw new UnsupportedOperationException("trying to turn Assignment statement into IR Stamenet failed\n"+
                        "received unimplemented rhs node type "+node.getRHS().getClass()+": "+node.getPrettyPrinted());
            }
        }
        
        //after everything - collect the literal assignments
        assignments.add(node); //after the literal assignments - add this node
        newNode = new TransformedNode<ASTNode>(assignments);
    }
    
    
    
    /**
     * takes an expression and interprets it as an LValue, returning it as a
     * comma separated list - it should not include parameterized expressions
     */
    private IRCommaSeparatedList exprToCommaSeparatedList(Expr expr){
        IRCommaSeparatedList list = new IRCommaSeparatedList();
        if (expr instanceof NameExpr){
            list.add(expr);
        } else if (expr instanceof MatrixExpr) {
            ast.List<Expr> row = (((MatrixExpr)expr).getRow(0)).getElementList();
            return listToCommaSeparatedList(row,null,false);
        } else {
            throw new UnsupportedOperationException(
                    "expected name or [name1,name2,...], but got "+expr.getPrettyPrinted());
        }
        return list;
    }
    
    
    
    /**
     * takes in a matrix expression and expands it into a bunch of assignments into temporaries,
     * which get added to assignList.
     * Returns the last assignment as an IRCallStmt (this one does not get added to assignList).
     */
    private IRAbstractAssignStmt expandMatrix(MatrixExpr matrix,NameExpr target,LinkedList<AssignStmt> assignList){
        if (matrix.getNumRow() == 0){
            throw new UnsupportedOperationException("can't assign empty matrizes yet -- todo");
        } 
        
        //collect rows
        IRCommaSeparatedList rowTemps = new IRCommaSeparatedList();
        for (Row row : matrix.getRowList()){
            if (row.getNumElement() == 1){
                rowTemps.add(pullOutLiteral(row.getElement(0), assignList));                
            }else{
                //collect the row in tmp
                TempFactory tmp = TempFactory.genFreshTempFactory();
                assignList.add(
                        new IRCallStmt(new NameExpr(new Name("horzcat")),
                                new IRCommaSeparatedList(tmp.genNameExpr()),
                                listToCommaSeparatedList(row.getElementList(),assignList,true)));
                rowTemps.add(tmp.genNameExpr());
            }
        }
        
        //either there is one row or multiple
        if (matrix.getNumRow() == 1){
            //only one row - we pull out the assignment of that row, and get the right target
            //in front of it
            IRAbstractAssignStmt assign = (IRAbstractAssignStmt)assignList.removeLast();
            assign.setLHS(target);
            return assign;
        } else {
            TempFactory tmp = TempFactory.genFreshTempFactory();
            return new IRCallStmt(new NameExpr(new Name("vertcat")),
                    new IRCommaSeparatedList(target),
                    rowTemps);
        }
    }
    
    
    /**
     * takes in a expression list, and returns the same list as a comma separated list
     * - pulls out all literals along the way if the flag is set
     */
    private IRCommaSeparatedList listToCommaSeparatedList
        (ast.List<Expr> list,LinkedList<AssignStmt> literalAssignments,boolean pullOutLiterals){
        IRCommaSeparatedList newList =  new IRCommaSeparatedList();
        for (Expr e : list){
            if (pullOutLiterals){
                newList.add(pullOutLiteral(e,literalAssignments));
            } else {
                newList.add(e);
            }
        }
        return newList;
    }
    
    
    /**
     * takes the given expression, which should be a name or a literal
     * - if it is a name expression, return it
     * - if it is a literal l, add t = i to literal Assignments, and return t
     */
    private NameExpr pullOutLiteral(Expr exp,LinkedList<AssignStmt> literalAssignments){
        if (exp instanceof NameExpr){
            return (NameExpr)exp;
        } else if (exp instanceof LiteralExpr){
            TempFactory tmp = TempFactory.genFreshTempFactory();
            literalAssignments.add(new IRAssignLiteralStmt(tmp.genNameExpr(), (LiteralExpr)exp));
            return tmp.genNameExpr();
        } else if (exp instanceof ColonExpr){
            return new NameExpr(new Name("colon")); //TODO
        } else {
            throw new UnsupportedOperationException("expected literal or name, received "+exp.getPrettyPrinted());
        }
    }
    
    /**
     * returns true if this expression is a parametrized expression where the
     * target is a variable
     */
    private boolean isParameterizedVar(Expr expr){
        if (expr instanceof ParameterizedExpr){
            ParameterizedExpr param = (ParameterizedExpr)expr;
            if (param.getTarget() instanceof NameExpr){
                return isVar((NameExpr)(param.getTarget()));
            }        
        }
        return false;
    }
    
    /**
     * returns true if this expression is a call (i.e. a parametrized expression or name expression
     * whose target is a function)
     */
    private boolean isCall(Expr expr){
        if (expr instanceof ParameterizedExpr){
            return !isVar((NameExpr)((ParameterizedExpr)expr).getTarget());
        } else if (expr instanceof NameExpr){
            return !isVar((NameExpr)expr);
        }
        return false;
    }
    
    
    //TODO remove this - part of simplification
    /**
     * returns true if the given name expression refers to a variable
     * - otherwise it is assumed to be a function
     */
    //private boolean isVar(NameExpr nameExpr){
    //    if (nameExpr.tmpVar) return true;
    //    return true; //TODO
    //}


    
}

