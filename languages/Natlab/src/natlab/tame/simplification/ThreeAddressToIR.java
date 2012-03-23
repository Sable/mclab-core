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

package natlab.tame.simplification;

import java.util.*;

import natlab.DecIntNumericLiteralValue;
import natlab.tame.ir.*;
import natlab.toolkits.analysis.varorfun.*;
import natlab.toolkits.rewrite.*;
import natlab.toolkits.rewrite.simplification.*;
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
 * This requires that LambdaSimplification has been performed on the IR,
 * Since the LambdaSimplification creates new functions, and works on functionLists,
 * 
 */
public class ThreeAddressToIR extends AbstractSimplification {
    public static boolean DEBUG = false;
    
    public ThreeAddressToIR(ASTNode<?> tree, VFPreorderAnalysis nameResolver) {
        super(tree,nameResolver);
    }

    @Override
    public Set<Class<? extends AbstractSimplification>> getDependencies() {
        LinkedHashSet<Class<? extends AbstractSimplification>> dependencies = 
                new LinkedHashSet<Class<? extends AbstractSimplification>>();
        dependencies.add( SwitchSimplification.class );
        dependencies.add( FullSimplification.class );
        return dependencies;
    }
    
    
    @Override
    public void caseFunction(Function node) {
        if (DEBUG){
            System.out.println("case function for 3A->IR, 3A produced this:");
            System.out.println(node.getPrettyPrinted());
        }
        
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
        NameExpr condition = (NameExpr)(expandExpr(node.getIfBlock(0).getCondition(),stmts));
        
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
        
        NameExpr l = (NameExpr)expandExpr(range.getLower(),assigns);
        NameExpr i = (NameExpr)expandExpr(range.hasIncr()?range.getIncr():null,assigns);
        NameExpr u = (NameExpr)expandExpr(range.getUpper(),assigns);
        
        //build transformed node
        newNode = new TransformedNode(assigns);
        newNode.add(new IRForStmt(((NameExpr)(node.getAssignStmt().getLHS())).getName(),
                l.getName(), i!=null?i.getName():null, u.getName(), 
                        new IRStatementList(node.getStmtList())));
        
    }
    
    public void caseWhileStmt(WhileStmt node) {
        rewriteChildren(node);
        
        if (node instanceof IRStmt) return; //don't redo conversion        
        
        //pull out expr
        LinkedList<AssignStmt> assign = new LinkedList<AssignStmt>();
        NameExpr condition = (NameExpr)expandExpr(node.getExpr(),assign);
        
        //copy list to end of body
        for (AssignStmt a : assign){
            node.getStmtList().add(a.copy());
        }
        
        //construct transformed node
        newNode = new TransformedNode(assign);
        newNode.add(new IRWhileStmt(condition.getName(), new IRStatementList(node.getStmtList())));
    }
    
    
    public void caseReturnStmt(ReturnStmt node) {
        if (node instanceof IRStmt) return; //don't redo conversion        
        newNode = new TransformedNode(new IRReturnStmt());
    }
    
    public void caseBreakStmt(BreakStmt node) {
        if (node instanceof IRStmt) return; //don't redo conversion        
        newNode = new TransformedNode(new IRBreakStmt());
    }
    
    @Override
    public void caseContinueStmt(ContinueStmt node) {
        if (node instanceof IRStmt) return; //don't redo conversion        
        newNode = new TransformedNode(new IRContinueStmt());
    }
    
    @Override
    public void casePersistentStmt(PersistentStmt node) {
        if (node instanceof IRStmt) return; //don't redo conversion        
        newNode = new TransformedNode(new IRPersistentSmt(node.getNameList()));
    }
    
    @Override
    public void caseGlobalStmt(GlobalStmt node) {
        if (node instanceof IRStmt) return; //don't redo conversion        
        newNode = new TransformedNode(new IRGlobalStmt(node.getNameList()));
    }
    
    @Override
    public void caseTryStmt(TryStmt node) {
        rewriteChildren(node);
        if (node instanceof IRStmt) return; //don't redo conversion        
        
        newNode = new TransformedNode(new IRTryStmt(
                        new IRStatementList(node.getTryStmtList()),
                        new IRStatementList(node.getCatchStmtList())));
    }
    
    @Override
    public void caseShellCommandStmt(ShellCommandStmt node) {
        // TODO Auto-generated method stub
        super.caseShellCommandStmt(node);
    }
    
    
    public void caseExprStmt(ExprStmt node) {
        //TODO
        LinkedList<AssignStmt> assignments = new LinkedList<AssignStmt>();
        Expr expr = expandExpr(node.getExpr(), assignments);
        //check if last assign is a call - remove target
        if (assignments.size() > 0){
            if (assignments.getLast() instanceof IRCallStmt){
                IRCallStmt call = (IRCallStmt)assignments.getLast();
                assignments.set(assignments.size()-1, 
                        new IRCallStmt(call.getFunctionName(), 
                                new IRCommaSeparatedList(), 
                                call.getArguments()));
            }
        }
        
        newNode = new TransformedNode(assignments);
    }
    
    public void caseSwitchStmt(SwitchStmt node) {
        // TODO
    }
    
    public void caseCheckScalarStmt(CheckScalarStmt node) {
        //TODO - for now just delete node
        newNode = new TransformedNode();
    }
    
    Random rnd = new Random();
    public void caseAssignStmt(AssignStmt node) {
        if (node instanceof IRStmt) return; //don't redo conversion
        
        //we need to pull out the assignments to literals etc.
        LinkedList<AssignStmt> assignments = new LinkedList<AssignStmt>();
        
        //get IR version of assignment
        node = transformAssignment(node, assignments);
        
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
    
    
    private AssignStmt transformAssignment(AssignStmt node, LinkedList<AssignStmt> assignments){
        //TODO - collect lhs as matrix in general
        //ArraySet - lhs a parameterized var
        if (isParameterizedVar(node.getLHS())){
            ParameterizedExpr param = (ParameterizedExpr)(node.getLHS());
            node.setRHS(expandExpr(node.getRHS(),assignments));
            node = new IRArraySetStmt(((NameExpr)(param.getTarget())).getName(), 
                    listToCommaSeparatedList(param.getArgList(), assignments, true), ((NameExpr)node.getRHS()).getName());
        //CellArraySet
        } else if (node.getLHS() instanceof CellIndexExpr) {
            CellIndexExpr cell = (CellIndexExpr)(node.getLHS());
            node.setRHS(expandExpr(node.getRHS(), assignments));
            node = new IRCellArraySetStmt(((NameExpr)(cell.getTarget())).getName(), 
                    listToCommaSeparatedList(cell.getArgList(), assignments, true), ((NameExpr)node.getRHS()).getName());
        //DotArraySet
        } else if (node.getLHS() instanceof DotExpr) {
            DotExpr dot = (DotExpr)(node.getLHS());
            node.setRHS(expandExpr(node.getRHS(), assignments));
            node = new IRDotSetStmt(
                    ((NameExpr)dot.getTarget()).getName(),dot.getField(),
                    ((NameExpr)node.getRHS()).getName());
            
        //there can only be one or more vars on the left hand side
        } else {
            //ArrayGet - rhs is a parametrized var
            if (isParameterizedVar(node.getRHS())){
                ParameterizedExpr param = (ParameterizedExpr)node.getRHS();
                node = new IRArrayGetStmt(((NameExpr)node.getLHS()).getName(),((NameExpr)param.getTarget()).getName(),
                        listToCommaSeparatedList(param.getArgList(),assignments,true));
            
            //copy - rhs is a name expression
            } else if (node.getRHS() instanceof NameExpr && isVar((NameExpr)(node.getRHS()))){
                node = new IRCopyStmt(
                        ((NameExpr)node.getLHS()).getName(),
                        ((NameExpr)node.getRHS()).getName());
            
            //DotGet
            } else if (node.getRHS() instanceof DotExpr && isVar(((DotExpr)node.getRHS()).getTarget())){
                    DotExpr dot = (DotExpr)node.getRHS();
                    node = new IRDotGetStmt(new IRCommaSeparatedList(node.getLHS()),
                            ((NameExpr)dot.getTarget()).getName(),dot.getField());
            
            //rhs is a cell index expr
            } else if (node.getRHS() instanceof CellIndexExpr){
                CellIndexExpr cell = (CellIndexExpr)(node.getRHS());
                node = new IRCellArrayGetStmt((NameExpr)cell.getTarget(), 
                        exprToCommaSeparatedList(node.getLHS()),
                        listToCommaSeparatedList(cell.getArgs(), assignments, true));
            
            //rhs is a call
            } else if (isCall(node.getRHS())){
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
                                
            //assigning a literal
            } else if (node.getRHS() instanceof LiteralExpr){
                node = new IRAssignLiteralStmt(((NameExpr)(node.getLHS())).getName(),
                        (LiteralExpr)(node.getRHS()));
                
            //rhs is a  binary operation            
            } else if (node.getRHS() instanceof BinaryExpr){
                BinaryExpr bin = (BinaryExpr)(node.getRHS());
                IRCommaSeparatedList target = new IRCommaSeparatedList();
                target.add(node.getLHS());
                IRCommaSeparatedList args = new IRCommaSeparatedList();
                args.add(expandExpr(bin.getLHS(), assignments));
                args.add(expandExpr(bin.getRHS(), assignments));
                node = new IRCallStmt(new NameExpr(new Name(bin.getOperatorName())), target, args);
                
            //rhs is a unary operation
            } else if (node.getRHS() instanceof UnaryExpr){
                UnaryExpr un = (UnaryExpr)(node.getRHS());
                IRCommaSeparatedList target = new IRCommaSeparatedList();
                target.add(node.getLHS());
                IRCommaSeparatedList args = new IRCommaSeparatedList();
                args.add(expandExpr(un.getOperand(), assignments));
                node = new IRCallStmt(new NameExpr(new Name(un.getOperatorName())), target, args);

            //matrix expresssion
            } else if (node.getRHS() instanceof MatrixExpr){
                node = expandMatrix((MatrixExpr)node.getRHS(),(NameExpr)node.getLHS(),assignments);
                
            //cell array expression
            } else if(node.getRHS() instanceof CellArrayExpr){
                node = expandCellArray((CellArrayExpr)node.getRHS(),(NameExpr)node.getLHS(),assignments);

            //lambda
            } else if(node.getRHS() instanceof LambdaExpr){
                //previous transformations should've turned this into just a call
                LambdaExpr lambda = (LambdaExpr)(node.getRHS());
                node = new IRCreateFunctionHandleStmt(
                        ((NameExpr)(node.getLHS())).getName(), 
                        ((NameExpr)((ParameterizedExpr)lambda.getBody()).getTarget()).getName(),
                        lambda.getInputParamList(),
                        listToCommaSeparatedList(
                                ((ParameterizedExpr)lambda.getBody()).getArgList(), null, false));
            
            //function handle creation
            } else if(node.getRHS() instanceof FunctionHandleExpr){
                node = new IRCreateFunctionHandleStmt(
                        ((NameExpr)(node.getLHS())).getName(), 
                        ((FunctionHandleExpr)(node.getRHS())).getName());
                    
            //colon expressions
            } else if(node.getRHS() instanceof RangeExpr){
                RangeExpr re = (RangeExpr)node.getRHS();
                Expr lo = expandExpr(re.getLower(), assignments);
                Expr hi = expandExpr(re.getUpper(), assignments);
                
                node = re.hasIncr()?
                        new IRCallStmt(new Name("colon"), node.getLHS(), 
                                lo, expandExpr(re.getIncr(), assignments), hi):
                        new IRCallStmt(new Name("colon"), node.getLHS(), lo, hi);
                        
            //end call expression
            } else if (node.getRHS() instanceof EndCallExpr){
                EndCallExpr end = (EndCallExpr)node.getRHS();
                node = new IRCallStmt(new Name("end"), node.getLHS(),
                        expandExpr(end.getArray(), assignments),
                        expandExpr(new IntLiteralExpr(new DecIntNumericLiteralValue(""+end.getNumDim(),false)), assignments),
                        expandExpr(new IntLiteralExpr(new DecIntNumericLiteralValue(""+end.getWhatDim(),false)), assignments)
                        );
                
            //something not implemented
            } else {
                throw new UnsupportedOperationException("trying to turn Assignment statement into IR Stamenet failed\n"+
                        "received unimplemented rhs node type "+node.getRHS().getClass()+": "+node.getPrettyPrinted());
            }
        }
        
        return node;
    }
    
        
    /**
     * expands a matrix expression into temporary assignments and a valid ir assign
     */
    private IRAbstractAssignStmt expandMatrix(MatrixExpr matrix,NameExpr target,LinkedList<AssignStmt> assignList){
        return expandRows(matrix.getRowList(),target,assignList,"vertcat","horzcat");
    }
    
    /**
     * expands a cell array expression into temporary assignments and a valid ir assign
     */
    private IRAbstractAssignStmt expandCellArray(CellArrayExpr cellArray,NameExpr target,LinkedList<AssignStmt> assignList){
        //TODO
        return expandRows(cellArray.getRowList(),target,assignList,"cellvertcat","cellhorzcat");
    }
    
    /**
     * takes in a list of rows and expands it into a bunch of assignments into temporaries,
     * which get added to assignList.
     * vertcat and horzcat are the names of functions for horizontal and vertical concatenation
     * Returns the last assignment as an IRCallStmt (this one does not get added to assignList).
     */
    private IRAbstractAssignStmt expandRows(List<Row> rows,NameExpr target,LinkedList<AssignStmt> assignList,
            String vertcat, String horzcat){
        //collect rows
        IRCommaSeparatedList rowTemps = new IRCommaSeparatedList();
        
        //case 0x0 matrix - just say 'target = vertcat()'
        if (rows.getNumChild() == 0){
            return new IRCallStmt(new NameExpr(new Name(vertcat)),
                   new IRCommaSeparatedList(target),new IRCommaSeparatedList());
        }
        
        //case 1x1 matrix
        if (rows.getNumChild() == 1){
            Row row = rows.getChild(0);
            if (row.getNumElement() == 1){
                rowTemps.add(expandExpr(row.getElement(0), assignList));
                return new IRCallStmt(new NameExpr(new Name(vertcat)),
                        new IRCommaSeparatedList(target),rowTemps);
            }
        }
        
        //case more than one element
        for (Row row : rows){
            if (row.getNumElement() == 1){
                rowTemps.add(expandExpr(row.getElement(0), assignList));                
            }else{
                //collect the row in tmp
                TempFactory tmp = TempFactory.genFreshTempFactory();
                assignList.add(
                        new IRCallStmt(new NameExpr(new Name(horzcat)),
                                new IRCommaSeparatedList(tmp.genNameExpr()),
                                listToCommaSeparatedList(row.getElementList(),assignList,true)));
                rowTemps.add(tmp.genNameExpr());
            }
        }
        
        //either there is one row or multiple
        if (rows.getNumChild() == 1){
            //only one row - we pull out the assignment of that row, and get the right target
            //in front of it
            IRAbstractAssignStmt assign = (IRAbstractAssignStmt)assignList.removeLast();
            assign.setLHS(target);
            return assign;
        } else {
            return new IRCallStmt(new NameExpr(new Name(vertcat)),
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
                newList.add(expandExpr(e,literalAssignments));
            } else {
                newList.add(e);
            }
        }
        return newList;
    }
    
    
    /**
     * takes the given expression, and expands it to just a Name (exceptions 
     * for colons). If it's another expression, this creates assignments to
     * temporaries and then returns that temporary as an expression. For example:
     * - if it is a name expression, return it
     * - if it is a literal l, add 't = i' to assignments, and return t
     * - if it's a binary expression a [f] b, add 't = f(a,b) to assignments, then return t
     * - if its null, return null
     * - it is a colon expression, return it
     */
    private Expr expandExpr(Expr exp,LinkedList<AssignStmt> assignments){
        if (exp == null) return null;
        
        if (exp instanceof NameExpr){
            return (NameExpr)exp;
        } else if (exp instanceof LiteralExpr){
            TempFactory tmp = TempFactory.genFreshTempFactory();
            assignments.add(new IRAssignLiteralStmt(tmp.genName(), (LiteralExpr)exp));
            return tmp.genNameExpr();
        } else if (exp instanceof ColonExpr){
            return exp;
        } else {
            TempFactory tmp = TempFactory.genFreshTempFactory();
            assignments.add(
                    transformAssignment(
                            new AssignStmt(new NameExpr(tmp.genName()), exp),
                            assignments));
            return tmp.genNameExpr();
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
}

