package natlab.Static.ir.transform;

import java.util.*;

import natlab.DecIntNumericLiteralValue;
import natlab.IntNumericLiteralValue;
import natlab.Static.ir.IRAbstractAssignStmt;
import natlab.Static.ir.IRArrayGetStmt;
import natlab.Static.ir.IRArraySetStmt;
import natlab.Static.ir.IRAssignFunctionHandleStmt;
import natlab.Static.ir.IRAssignLiteralStmt;
import natlab.Static.ir.IRCallStmt;
import natlab.Static.ir.IRCellArrayGet;
import natlab.Static.ir.IRCommaSeparatedList;
import natlab.Static.ir.IRCommentStmt;
import natlab.Static.ir.IRForStmt;
import natlab.Static.ir.IRFunction;
import natlab.Static.ir.IRIfStmt;
import natlab.Static.ir.IRStatementList;
import natlab.Static.ir.IRStmt;
import natlab.Static.ir.IRWhileStmt;
import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import natlab.toolkits.rewrite.*;
import natlab.toolkits.rewrite.simplification.AbstractSimplification;
import natlab.toolkits.rewrite.simplification.CommentSimplification;
import natlab.toolkits.rewrite.simplification.FullSimplification;
import natlab.toolkits.rewrite.simplification.RightSimplification;
import ast.*;
import ast.List;

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
    
    
    @Override
    public void caseFunction(Function node) {
        //System.out.println("case function for 3A->IR, 3A produced this:");
        //System.out.println(node.getPrettyPrinted());
        
        
        rewriteChildren(node);
        //collect nested functions
        List<IRFunction> nesteds = new List<IRFunction>();
        for (Function f : node.getNestedFunctions()){
            nesteds.add((IRFunction)f);
        }
        //create IRFunction
        newNode = new TransformedNode(
                new IRFunction(node.getOutputParamList(),node.getName(),node.getInputParamList(),
                        node.getHelpCommentList(),new IRStatementList(node.getStmtList()),nesteds));
    }
        
    
    /**
     * case Statement - we should've implemented all statements so this shouldn't ever be called
     */
    @Override
    public void caseStmt(Stmt node) {
        throw new UnsupportedOperationException("IR Transformation received unimplemented statment "+node.getClass().getName()
                +"\n"+node.getPrettyPrinted());
    }
    
    /**
     * empty statement - gets removed
     */
    @Override
    public void caseEmptyStmt(EmptyStmt node) {
        newNode = new TransformedNode(new IRCommentStmt(node.getComments()));
    }
    
    @Override
    public void caseIfStmt(IfStmt node) {
        //recursively rewrite children
        rewriteChildren(node);

        if (node instanceof IRStmt) return; //don't redo conversion
        
        //pull out expression
        LinkedList<AssignStmt> stmts = new LinkedList<AssignStmt>();
        NameExpr condition = makeName(node.getIfBlock(0).getCondition(), stmts);

        //get else block
        List<Stmt> elseStmts = 
            ((node.hasElseBlock() && node.getElseBlock().getStmtList()!=null)
            ?(node.getElseBlock().getStmtList()) : (new List<Stmt>()));    

        //we will assume that there is only one if block (via if simplification)
        newNode = new TransformedNode(stmts);
        newNode.add(
                new IRIfStmt(condition.getName(),
                        new IRStatementList(node.getIfBlock(0).getStmtList()),
                        new IRStatementList(elseStmts)));
    }


    public void caseForStmt(ForStmt node) {
        //rewrite body
        rewrite(node.getStmtList());

        if (node instanceof IRStmt) return; //don't redo conversion        
        
        //find range variables
        RangeExpr range = (RangeExpr)node.getAssignStmt().getRHS();
        LinkedList<AssignStmt> assigns = new LinkedList<AssignStmt>();
        
        NameExpr l = (NameExpr)pullOutLiteral(range.getLower(),assigns);
        NameExpr i = (NameExpr)pullOutLiteral(range.hasIncr()?range.getIncr():null,assigns);
        NameExpr u = (NameExpr)pullOutLiteral(range.getUpper(),assigns);
        
        //build transformed node
        newNode = new TransformedNode(assigns);
        newNode.add(new IRForStmt(((NameExpr)(node.getAssignStmt().getLHS())).getName(),
                l.getName(), i!=null?i.getName():null, u.getName(), 
                        new IRStatementList(node.getStmtList())));
        
    }
    
    public void caseWhileStmt(WhileStmt node) {
        rewriteChildren(node);
        
        //pull out expr
        LinkedList<AssignStmt> assign = new LinkedList<AssignStmt>();
        NameExpr condition = makeName(node.getExpr(),assign);
        
        //copy list to end of body
        for (AssignStmt a : assign){
            node.getStmtList().add(a.copy());
        }
        
        //construct transformed node
        newNode = new TransformedNode(assign);
        newNode.add(new IRWhileStmt(condition.getName(), new IRStatementList(node.getStmtList())));
    }
    
    
    public void caseReturnStmt(ReturnStmt node) {
        //TODO - should there be an IRREturn statement? ... probably
    }
    
    public void caseBreakStmt(BreakStmt node) {
        //newNode = new TransformedNode(new IRBreakStmt());
    }
    
    public void caseExprStmt(ExprStmt node) {
        //TODO
    }
    
    public void caseSwitchStmt(SwitchStmt node) {
        // TODO
        rewriteChildren(node);
    }
    
    public void caseCheckScalarStmt(CheckScalarStmt node) {
        //TODO
    }
    
    public void caseAssignStmt(AssignStmt node) {
        if (node instanceof IRStmt) return; //don't redo conversion
        
        //we need to pull out the assignments to literals etc.
        LinkedList<AssignStmt> assignments = new LinkedList<AssignStmt>();
        
        //ArraySet - lhs a parameterized var
        if (isParameterizedVar(node.getLHS())){
            ParameterizedExpr param = (ParameterizedExpr)(node.getLHS());
            node.setRHS(pullOutLiteral(node.getRHS(),assignments));
            node = new IRArraySetStmt((NameExpr)(param.getTarget()), 
                    listToCommaSeparatedList(param.getArgList(), null, false), (NameExpr)node.getRHS());
            
        //CellArraySet
        } else if (node.getLHS() instanceof CellIndexExpr) {
            CellIndexExpr cell = (CellIndexExpr)(node.getLHS());
            node.setRHS(pullOutLiteral(node.getRHS(), assignments));
            node = new IRArraySetStmt((NameExpr)(cell.getTarget()), 
                    listToCommaSeparatedList(cell.getArgList(), null, false), (NameExpr)node.getRHS());
        } else {
            //there can only be one or more vars on the left hand side
            
            //ArrayGet - rhs is a parametrized var
            if (isParameterizedVar(node.getRHS())){
                ParameterizedExpr param = (ParameterizedExpr)node.getRHS();
                node = new IRArrayGetStmt((NameExpr)node.getLHS(),(NameExpr)param.getTarget(),
                        listToCommaSeparatedList(param.getArgList(),assignments,true));
            } else
                
            //ArrayGet - rhs is a name expression
            if (node.getRHS() instanceof NameExpr && isVar((NameExpr)(node.getRHS()))){
                node = new IRArrayGetStmt((NameExpr)node.getLHS(),(NameExpr)node.getRHS(), new IRCommaSeparatedList());
            } else
            
            //rhs is a cell index expr
            if (node.getRHS() instanceof CellIndexExpr){
                CellIndexExpr cell = (CellIndexExpr)(node.getRHS());
                node = new IRCellArrayGet((NameExpr)cell.getTarget(), 
                        exprToCommaSeparatedList(node.getLHS()),
                        listToCommaSeparatedList(cell.getArgs(), assignments, true));
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
                
            //cell arrays
            if(node.getRHS() instanceof CellArrayExpr){
                //TODO
            } else

            //lambda
            if(node.getRHS() instanceof LambdaExpr){
                //TODO
            } else
                
            //function handle creation
            if(node.getRHS() instanceof FunctionHandleExpr){
                node = new IRAssignFunctionHandleStmt(
                        ((NameExpr)(node.getLHS())).getName(), 
                        ((FunctionHandleExpr)(node.getRHS())).getName());
            } else
                    
            //colon expressions
            if(node.getRHS() instanceof RangeExpr){
                RangeExpr re = (RangeExpr)node.getRHS();
                Expr lo = pullOutLiteral(re.getLower(), assignments);
                Expr hi = pullOutLiteral(re.getUpper(), assignments);
                
                node = re.hasIncr()?
                        new IRCallStmt(new Name("colon"), node.getLHS(), 
                                lo, pullOutLiteral(re.getIncr(), assignments), hi):
                        new IRCallStmt(new Name("colon"), node.getLHS(), lo, hi);
            } else
                            
            //end call expression
            if (node.getRHS() instanceof EndCallExpr){
                EndCallExpr end = (EndCallExpr)node.getRHS();
                node = new IRCallStmt(new Name("end"), node.getLHS(),
                        pullOutLiteral(end.getArray(), assignments),
                        pullOutLiteral(new IntLiteralExpr(new DecIntNumericLiteralValue(""+end.getNumDim(),false)), assignments),
                        pullOutLiteral(new IntLiteralExpr(new DecIntNumericLiteralValue(""+end.getWhatDim(),false)), assignments)
                        );
            } else
                
            //something not implemented
            {
                throw new UnsupportedOperationException("trying to turn Assignment statement into IR Stamenet failed\n"+
                        "received unimplemented rhs node type "+node.getRHS().getClass()+": "+node.getPrettyPrinted());
            }
        }
        
        //after everything - collect the literal assignments
        assignments.add(node); //after the literal assignments - add this node
        newNode = new TransformedNode(assignments);
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

        //collect rows
        IRCommaSeparatedList rowTemps = new IRCommaSeparatedList();
        
        //case 0x0 matrix - just say 'target = vertcat()'
        if (matrix.getNumRow() == 0){
            return new IRCallStmt(new NameExpr(new Name("vertcat")),
                   new IRCommaSeparatedList(target),new IRCommaSeparatedList());
        }
        
        //case 1x1 matrix
        if (matrix.getNumRow() == 1){
            Row row = matrix.getRow(0);
            if (row.getNumElement() == 1){
                rowTemps.add(pullOutLiteral(row.getElement(0), assignList));
                return new IRCallStmt(new NameExpr(new Name("vertcat")),
                        new IRCommaSeparatedList(target),rowTemps);
            }
        }
        
        //case more than one element
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
     * takes the given expression, which should be an expression that an 
     * IRCommaSeparatedList can store, i.e. a name, literal, colon 
     * TODO others?
     * - if it is a name expression, return it
     * - if it is a literal l, add t = i to literal Assignments, and return t
     * - if its null, return null
     * - it is a colon expression, return it
     */
    private Expr pullOutLiteral(Expr exp,LinkedList<AssignStmt> literalAssignments){
        if (exp == null) return null;
        
        if (exp instanceof NameExpr){
            return (NameExpr)exp;
        } else if (exp instanceof LiteralExpr){
            TempFactory tmp = TempFactory.genFreshTempFactory();
            literalAssignments.add(new IRAssignLiteralStmt(tmp.genNameExpr(), (LiteralExpr)exp));
            return tmp.genNameExpr();
        } else if (exp instanceof ColonExpr){
            return exp;
        } else {
            throw new UnsupportedOperationException("expected literal or name, received "+exp.getPrettyPrinted());
        }
    }
    
    /**
     * takes in a given expression, which may be a binary, unary, name or literal,
     * and produces a correct assignment statement for it such that the statmenet
     * becomes replaced with just a var
     * for example:
     * 
     * (a + b)
     * ==>
     * [t] = plus(a,b)
     * t
     */
    private NameExpr makeName(Expr exp,LinkedList<AssignStmt> assigns){
        return (NameExpr)exp; //todo
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
}

