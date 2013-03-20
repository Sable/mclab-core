package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;


import java.util.HashMap;
import java.util.LinkedList;

import natlab.tame.tir.TIRAbstractAssignFromVarStmt;
import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRAbstractAssignToListStmt;
import natlab.tame.tir.TIRAbstractAssignToVarStmt;
import natlab.tame.tir.TIRArrayGetStmt;
import natlab.tame.tir.TIRArraySetStmt;
import natlab.tame.tir.TIRCallStmt;
import natlab.tame.tir.TIRCellArrayGetStmt;
import natlab.tame.tir.TIRCellArraySetStmt;
import natlab.tame.tir.TIRCommaSeparatedList;
import natlab.tame.tir.TIRCopyStmt;
import natlab.tame.tir.TIRDotGetStmt;
import natlab.tame.tir.TIRDotSetStmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRIfStmt;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.TIRWhileStmt;
import natlab.tame.tir.analysis.TIRAbstractNodeCaseHandler;
import ast.ASTNode;
import ast.AssignStmt;
import ast.CellIndexExpr;
import ast.DotExpr;
import ast.ForStmt;
import ast.Function;
import ast.IfStmt;
import ast.List;
import ast.MatrixExpr;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.Row;
import ast.Stmt;
import ast.WhileStmt;

@SuppressWarnings("rawtypes")
public class IRNodeToRawASTNodeTableBuilder extends TIRAbstractNodeCaseHandler
{
    private LinkedList<ASTNode> fVisitedNodes;
    private HashMap<ASTNode, ASTNode> fIRToRawASTTable;
    
    // Case loop var will require a specialized builder and also expander!
    public IRNodeToRawASTNodeTableBuilder(LinkedList<ASTNode> visitedNodes)
    {
        fVisitedNodes = visitedNodes;
        initializeIRToRawASTTable();
    }
    
    public void initializeIRToRawASTTable()
    {
        fIRToRawASTTable = new HashMap<ASTNode, ASTNode>();
        for (ASTNode visitedNode : fVisitedNodes)
        {
            if (visitedNode instanceof Function)
            {
                fIRToRawASTTable.put(visitedNode, new Function());
            }
            else if (visitedNode instanceof Stmt)
            {
                fIRToRawASTTable.put(visitedNode, new Stmt() {});
            }
            else 
            {
                throw new UnsupportedOperationException("AST node with class " + visitedNode.getClass() + " is not supported!");
            }
        }
    }

    public void build()
    {
        for (ASTNode node : fVisitedNodes)
        {
            if (node instanceof TIRFunction)
            {
                caseTIRFunction((TIRFunction) node);
            }
            (node).analyze(this);
        }
    }
    
    @Override
    public void caseASTNode(ASTNode node)
    {
        int nodeChildrenCount = node.getNumChild();
        for (int i = 0; i < nodeChildrenCount; i++)
        {
            if (node.getChild(i) instanceof TIRNode)
            {
                ((TIRNode) node.getChild(i)).tirAnalyze(this);
            }
            else 
            {
                node.getChild(i).analyze(this);
            }
        }
    }
    
    @Override
    public void caseTIRFunction(TIRFunction node)
    {
        TIRFunction tameIRFunction = (TIRFunction) node;
        Function function = new Function();
        function.setComments(tameIRFunction.getComments());
        function.setName(tameIRFunction.getName());
        function.setInputParamList(tameIRFunction.getInputParamList());
        function.setOutputParamList(tameIRFunction.getOutputParamList());
        function.setNestedFunctionList(tameIRFunction.getNestedFunctionList());
        function.setStmtList(tameIRFunction.getStmts());
        fIRToRawASTTable.put(node, function);
    }
    
    @Override
    public void caseTIRForStmt(TIRForStmt node)
    {
        TIRForStmt tameIRForStmt = (TIRForStmt) node;
        ForStmt forStmt = new ForStmt();
        forStmt.setAssignStmt(tameIRForStmt.getAssignStmt());
        forStmt.setStmtList(tameIRForStmt.getStmtList());
        fIRToRawASTTable.put(node,forStmt);
    }
    
    @Override
    public void caseTIRIfStmt(TIRIfStmt node)
    {
        TIRIfStmt tameIRIfStmt = (TIRIfStmt) node;
        IfStmt ifStmt = new IfStmt();
        ifStmt.setIfBlockList(tameIRIfStmt.getIfBlockList());
        ifStmt.setElseBlock(tameIRIfStmt.getElseBlock());
        fIRToRawASTTable.put(node, ifStmt);
    }
    
    @Override
    public void caseTIRWhileStmt(TIRWhileStmt node)
    {
        TIRWhileStmt tameIRWhileStmt = (TIRWhileStmt) node;
        WhileStmt whileStmt = new WhileStmt();
        whileStmt.setExpr(tameIRWhileStmt.getCondition());
        whileStmt.setStmtList(tameIRWhileStmt.getStmtList());
        fIRToRawASTTable.put(node, whileStmt);
    }
    
    @Override
    public void caseTIRCallStmt(TIRCallStmt node)
    {
        AssignStmt assignStmt = new AssignStmt();
        TIRCallStmt callStmt = (TIRCallStmt) node;
        ParameterizedExpr rhs = new ParameterizedExpr();
        rhs.setTarget(new NameExpr(callStmt.getFunctionName()));
        addIndicesToParametrizedExpr(callStmt.getArguments(), rhs);
        MatrixExpr lhs = new MatrixExpr();
        addTargetsToMatrixExpr(callStmt.getTargets(), lhs);
        assignStmt.setLHS(lhs);
        assignStmt.setRHS(rhs);
        fIRToRawASTTable.put(node, assignStmt);
    }
    
    @Override
    public void caseTIRAbstractAssignStmt(TIRAbstractAssignStmt node)
    {
        AssignStmt assignStmt = new AssignStmt();
        if (node instanceof TIRAbstractAssignToVarStmt)
        {
            if (node instanceof TIRCopyStmt)
            {
                TIRCopyStmt copyStmt = (TIRCopyStmt) node;
                assignStmt.setLHS(copyStmt.getLHS());
                assignStmt.setRHS(copyStmt.getRHS());
            }
            // TIRLiteralAssignStmt is not affected
            // Does not handle TIRAbstractCreateFunctionHandleStmt
        }
        else if (node instanceof TIRAbstractAssignFromVarStmt)
        {
            if (node instanceof TIRArraySetStmt)
            {
                TIRArraySetStmt arraySetStmt = (TIRArraySetStmt) node;
                ParameterizedExpr lhs = new ParameterizedExpr();
                NameExpr arrayName = new NameExpr(arraySetStmt.getArrayName());
                lhs.setTarget(arrayName);
                addIndicesToParametrizedExpr(arraySetStmt.getIndizes(), lhs);
                assignStmt.setLHS(lhs);
                assignStmt.setRHS(node.getRHS());
            }
            else if (node instanceof TIRCellArraySetStmt)
            {
                TIRCellArraySetStmt cellArraySetStmt = (TIRCellArraySetStmt) node;
                CellIndexExpr lhs = new CellIndexExpr();
                NameExpr cellArrayName = new NameExpr(cellArraySetStmt.getCellArrayName());
                lhs.setTarget(cellArrayName);
                addIndicesToCellIndexExpr(cellArraySetStmt.getIndizes(), lhs);
                assignStmt.setLHS(lhs);
                assignStmt.setRHS(node.getRHS());
            }
            else if (node instanceof TIRDotSetStmt)
            {
                TIRDotSetStmt dotSetStmt = (TIRDotSetStmt) node;
                DotExpr lhs = new DotExpr();
                NameExpr dotName = new NameExpr(dotSetStmt.getDotName());
                lhs.setTarget(dotName);
                lhs.setField(dotSetStmt.getFieldName());
                assignStmt.setLHS(lhs);
                assignStmt.setRHS(dotSetStmt.getRHS());
            }
            else 
            {
                throw new UnsupportedOperationException("unknown assign from var stmt " + node); 
            }
        }
        fIRToRawASTTable.put(node, assignStmt);
    }
    
    @Override
    public void caseTIRAbstractAssignToListStmt(TIRAbstractAssignToListStmt node)
    {
        AssignStmt assignStmt = new AssignStmt();
        if (node instanceof TIRArrayGetStmt)
        {
            TIRArrayGetStmt arrayGetStmt = (TIRArrayGetStmt) node;
            ParameterizedExpr rhs = new ParameterizedExpr();
            NameExpr arrayName = new NameExpr(arrayGetStmt.getArrayName());
            rhs.setTarget(arrayName);
            addIndicesToParametrizedExpr(arrayGetStmt.getIndizes(), rhs);
            assignStmt.setLHS(arrayGetStmt.getLHS());
            assignStmt.setRHS(rhs);
        }
        else if (node instanceof TIRDotGetStmt)
        {
            TIRDotGetStmt dotGetStmt = (TIRDotGetStmt) node;
            DotExpr rhs = new DotExpr();
            NameExpr dotName = new NameExpr(dotGetStmt.getDotName());
            rhs.setTarget(dotName);
            rhs.setField(dotGetStmt.getFieldName());
            assignStmt.setRHS(rhs);
            assignStmt.setLHS(dotGetStmt.getLHS());
        }
        else if (node instanceof TIRCellArrayGetStmt)
        {
            TIRCellArrayGetStmt cellArrayGetStmt = (TIRCellArrayGetStmt) node;
            CellIndexExpr rhs = new CellIndexExpr();
            NameExpr cellArrayName = new NameExpr(cellArrayGetStmt.getCellArrayName());
            rhs.setTarget(cellArrayName);
            addIndicesToCellIndexExpr(cellArrayGetStmt.getIndices(), rhs);
            assignStmt.setRHS(rhs);
            assignStmt.setLHS(cellArrayGetStmt.getLHS());
        }
        else
        {
            throw new UnsupportedOperationException("unknown assign from var stmt " + node);
        }
        fIRToRawASTTable.put(node, assignStmt);
    }
    
    public void addIndicesToParametrizedExpr(TIRCommaSeparatedList indices, ParameterizedExpr parameterizedExpr)
    {
        int indicesCount = indices.size();
        for (int i = 0; i < indicesCount; i++)
        {
            NameExpr indexNameExpr = indices.getNameExpresion(i);
            if (indexNameExpr != null)
            {
                parameterizedExpr.setArg(indexNameExpr, i);
            }
        }
    }
    
    public void addIndicesToCellIndexExpr(TIRCommaSeparatedList indices, CellIndexExpr cellIndexExpr)
    {
        int indicesCount = indices.size();
        for (int i = 0; i < indicesCount; i++)
        {
            NameExpr indexNameExpr = indices.getNameExpresion(i);
            if (indexNameExpr != null)
            {
                cellIndexExpr.setArg(indexNameExpr, i);
            }
        }
    }
    
    public void addTargetsToMatrixExpr(TIRCommaSeparatedList targets, MatrixExpr matrixExpr)
    {
        int targetsCount = targets.size();
        Row row = new Row();
        List<ASTNode> returnValues = new List<ASTNode>();
        for(int i = 0; i < targetsCount; i++)
        {
            NameExpr targetNameExpr = targets.getNameExpresion(i);
            if (targetNameExpr != null)
            {
                returnValues.add(targetNameExpr);
            }
        }
       row.setChild(returnValues, 0);
       matrixExpr.setRow(row, 0);
    }
    
    public HashMap<ASTNode,  ASTNode> getIRToRawASTTable()
    {
        return fIRToRawASTTable;
    }
}
