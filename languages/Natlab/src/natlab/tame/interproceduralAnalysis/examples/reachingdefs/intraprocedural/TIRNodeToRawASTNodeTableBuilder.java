package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;


import java.util.HashMap;
import java.util.LinkedList;

import natlab.tame.tir.TIRAbstractAssignFromVarStmt;
import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRAbstractAssignToListStmt;
import natlab.tame.tir.TIRAbstractAssignToVarStmt;
import natlab.tame.tir.TIRArrayGetStmt;
import natlab.tame.tir.TIRArraySetStmt;
import natlab.tame.tir.TIRAssignLiteralStmt;
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
import ast.Expr;
import ast.ForStmt;
import ast.Function;
import ast.IfStmt;
import ast.List;
import ast.MatrixExpr;
import ast.NameExpr;
import ast.ParameterizedExpr;
import ast.RangeExpr;
import ast.Row;
import ast.Stmt;
import ast.WhileStmt;

@SuppressWarnings("rawtypes")
public class TIRNodeToRawASTNodeTableBuilder extends TIRAbstractNodeCaseHandler
{
    public static boolean DEBUG = false;
    private LinkedList<TIRNode> fVisitedNodes;
    private HashMap<TIRNode, ASTNode> fIRToRawASTTable;
    
    // Case loop var will require a specialized builder and also expander!
    public TIRNodeToRawASTNodeTableBuilder(LinkedList<TIRNode> visitedNodes)
    {
        fVisitedNodes = visitedNodes;
        initializeIRToRawASTTable();
    }
    
    public void initializeIRToRawASTTable()
    {
        fIRToRawASTTable = new HashMap<TIRNode, ASTNode>();
        for (TIRNode visitedNode : fVisitedNodes)
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
        getFunctionNode().tirAnalyze(this);
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
        try
        {
            TIRFunction functionClone = (TIRFunction) node.clone();
            Function function = new Function();
            function.setComments(functionClone.getComments());
            function.setName(functionClone.getName());
            function.setInputParamList(functionClone.getInputParamList());
            function.setOutputParamList(functionClone.getOutputParamList());
            function.setNestedFunctionList(functionClone.getNestedFunctionList());
            function.setStmtList(functionClone.getStmts());
            fIRToRawASTTable.put(node, function);
           if (DEBUG) printTableEntry(node, function);
            caseASTNode(node);
        } catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void caseTIRForStmt(TIRForStmt node)
    {
        try
        {
            TIRForStmt forStmtClone = (TIRForStmt) node.clone();
            ForStmt forStmt = new ForStmt();
            
            forStmt.setAssignStmt(cloneAssignStmtOfTIRForStmt(forStmtClone));
            
            forStmt.setStmtList(forStmtClone.getStmtList().clone());
            fIRToRawASTTable.put(node, forStmt);
            if (DEBUG) printTableEntry(node, forStmt);
            caseForStmt(node);
        } catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void caseTIRIfStmt(TIRIfStmt node)
    {
        try
        {
            TIRIfStmt ifStmtClone = (TIRIfStmt) node.clone();
            IfStmt ifStmt = new IfStmt();
            ifStmt.setIfBlockList(ifStmtClone.getIfBlockList());
            ifStmt.setElseBlock(ifStmtClone.getElseBlock());
            fIRToRawASTTable.put(node, ifStmt);
            if (DEBUG) printTableEntry(node, ifStmt);
            caseIfStmt(node);
        } catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void caseTIRWhileStmt(TIRWhileStmt node)
    {
        try
        {
            TIRWhileStmt whileStmtClone = (TIRWhileStmt) node.clone();
            WhileStmt whileStmt = new WhileStmt();
            whileStmt.setExpr(whileStmtClone.getCondition());
            whileStmt.setStmtList(whileStmtClone.getStmtList());
            fIRToRawASTTable.put(node, whileStmt);
            if (DEBUG) printTableEntry(node, whileStmt);
            caseWhileStmt(node);
        } catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void caseTIRCallStmt(TIRCallStmt node)
    {
        try
        {
            TIRCallStmt callStmtClone = (TIRCallStmt) node.clone();
            AssignStmt callStmt = new AssignStmt();
            ParameterizedExpr rhs = new ParameterizedExpr();
            rhs.setTarget(new NameExpr(callStmtClone.getFunctionName()));
            addIndicesToParametrizedExpr((TIRCommaSeparatedList) callStmtClone.getArguments(), rhs);
            MatrixExpr lhs = new MatrixExpr();
            addTargetsToMatrixExpr((TIRCommaSeparatedList) callStmtClone.getTargets(), lhs);
            callStmt.setLHS(lhs);
            callStmt.setRHS(rhs);
            fIRToRawASTTable.put(node, callStmt);
            if (DEBUG) printTableEntry(node, callStmt);
        } catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void caseTIRAbstractAssignStmt(TIRAbstractAssignStmt node)
    {
        try
        {
            AssignStmt assignStmt = new AssignStmt();
            if (node instanceof TIRAbstractAssignToVarStmt)
            {
                if (node instanceof TIRCopyStmt)
                {
                    TIRCopyStmt copyStmtClone = (TIRCopyStmt) node.clone();
                    assignStmt.setLHS(copyStmtClone.getLHS());
                    assignStmt.setRHS(copyStmtClone.getRHS());
                }
                if (node instanceof TIRAssignLiteralStmt)
                {
                    TIRAssignLiteralStmt assignLiteralStmtClone = (TIRAssignLiteralStmt) node.clone();
                    assignStmt.setLHS(assignLiteralStmtClone.getLHS());
                    assignStmt.setRHS(assignLiteralStmtClone.getRHS());
                }
                // Does not handle TIRAbstractCreateFunctionHandleStmt
            }
            else if (node instanceof TIRAbstractAssignFromVarStmt)
            {
                if (node instanceof TIRArraySetStmt)
                {
                    TIRArraySetStmt arraySetStmtClone = (TIRArraySetStmt) node.clone();
                    ParameterizedExpr lhs = new ParameterizedExpr();
                    NameExpr arrayName = new NameExpr(arraySetStmtClone.getArrayName());
                    lhs.setTarget(arrayName);
                    addIndicesToParametrizedExpr(arraySetStmtClone.getIndizes(), lhs);
                    assignStmt.setLHS(lhs);
                    assignStmt.setRHS(arraySetStmtClone.getRHS());
                }
                else if (node instanceof TIRCellArraySetStmt)
                {
                    TIRCellArraySetStmt cellArraySetStmtClone = (TIRCellArraySetStmt) node;
                    CellIndexExpr lhs = new CellIndexExpr();
                    NameExpr cellArrayName = new NameExpr(cellArraySetStmtClone.getCellArrayName());
                    lhs.setTarget(cellArrayName);
                    addIndicesToCellIndexExpr(cellArraySetStmtClone.getIndizes(), lhs);
                    assignStmt.setLHS(lhs);
                    assignStmt.setRHS(cellArraySetStmtClone.getRHS());
                }
                else if (node instanceof TIRDotSetStmt)
                {
                    TIRDotSetStmt dotSetStmtClone = (TIRDotSetStmt) node.clone();
                    DotExpr lhs = new DotExpr();
                    NameExpr dotName = new NameExpr(dotSetStmtClone.getDotName());
                    lhs.setTarget(dotName);
                    lhs.setField(dotSetStmtClone.getFieldName());
                    assignStmt.setLHS(lhs);
                    assignStmt.setRHS(dotSetStmtClone.getRHS());
                }
                else 
                {
                    throw new UnsupportedOperationException("unknown assign from var stmt " + node); 
                }
            }
            fIRToRawASTTable.put(node, assignStmt);
            if (DEBUG) printTableEntry(node, assignStmt);
        }
        catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
        }
    }
    
    @Override
    public void caseTIRAbstractAssignToListStmt(TIRAbstractAssignToListStmt node)
    {
        try
        {
            AssignStmt assignStmt = new AssignStmt();
            if (node instanceof TIRArrayGetStmt)
            {
                TIRArrayGetStmt arrayGetStmtClone = (TIRArrayGetStmt) node.clone();
                ParameterizedExpr rhs = new ParameterizedExpr();
                NameExpr arrayName = new NameExpr(arrayGetStmtClone.getArrayName());
                rhs.setTarget(arrayName);
                addIndicesToParametrizedExpr(arrayGetStmtClone.getIndizes(), rhs);
                assignStmt.setLHS(arrayGetStmtClone.getLHS());
                assignStmt.setRHS(rhs);
            }
            else if (node instanceof TIRDotGetStmt)
            {
                TIRDotGetStmt dotGetStmtClone = (TIRDotGetStmt) node.clone();
                DotExpr rhs = new DotExpr();
                NameExpr dotName = new NameExpr(dotGetStmtClone.getDotName());
                rhs.setTarget(dotName);
                rhs.setField(dotGetStmtClone.getFieldName());
                assignStmt.setRHS(rhs);
                assignStmt.setLHS(dotGetStmtClone.getLHS());
            }
            else if (node instanceof TIRCellArrayGetStmt)
            {
                TIRCellArrayGetStmt cellArrayGetStmtClone = (TIRCellArrayGetStmt) node.clone();
                CellIndexExpr rhs = new CellIndexExpr();
                NameExpr cellArrayName = new NameExpr(cellArrayGetStmtClone.getCellArrayName());
                rhs.setTarget(cellArrayName);
                addIndicesToCellIndexExpr(cellArrayGetStmtClone.getIndices(), rhs);
                assignStmt.setRHS(rhs);
                assignStmt.setLHS(cellArrayGetStmtClone.getLHS());
            }
            else
            {
                throw new UnsupportedOperationException("unknown assign from var stmt " + node);
            }
            fIRToRawASTTable.put(node, assignStmt);
            if (DEBUG) printTableEntry(node, assignStmt);
        } catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
        }
    }
    
    public AssignStmt cloneAssignStmtOfTIRForStmt(TIRForStmt node)
    {

        Expr lhsOfAssignStmt = node.getAssignStmt().getLHS();
        
        RangeExpr forStmtRangeExpr = new RangeExpr();
        RangeExpr cloneOfTIRForStmtRangeExpr = (RangeExpr) node.getAssignStmt().getRHS();
        try
        {
            Expr low = cloneOfTIRForStmtRangeExpr.getLower().clone();
            Expr incr = cloneOfTIRForStmtRangeExpr.hasIncr() ? cloneOfTIRForStmtRangeExpr.getIncr().clone() : null;
            Expr up = cloneOfTIRForStmtRangeExpr.getUpper().clone();
            
            forStmtRangeExpr.setLower(low);
            if (cloneOfTIRForStmtRangeExpr.hasIncr()) forStmtRangeExpr.setIncr(incr);
            forStmtRangeExpr.setUpper(up);
        } catch (CloneNotSupportedException e)
        {
            e.printStackTrace();
        }
        
        return new AssignStmt(lhsOfAssignStmt, forStmtRangeExpr);
    }
    
    public void addIndicesToParametrizedExpr(TIRCommaSeparatedList indices, ParameterizedExpr parameterizedExpr)
    {
        int indicesCount = indices.size();
        for (int i = 0; i < indicesCount; i++)
        {
            try
            {
                parameterizedExpr.setArg(indices.getChild(i).clone(), i);
            } catch (CloneNotSupportedException e)
            {
                e.printStackTrace();
            }
        }
    }
    
    public void addIndicesToCellIndexExpr(TIRCommaSeparatedList indices, CellIndexExpr cellIndexExpr)
    {
        int indicesCount = indices.size();
        for (int i = 0; i < indicesCount; i++)
        {
            NameExpr indexNameExpr;
            try
            {
                indexNameExpr = indices.getNameExpresion(i).clone();
                if (indexNameExpr != null)
                {
                    cellIndexExpr.setArg(indexNameExpr, i);
                }
            } catch (CloneNotSupportedException e)
            {
                e.printStackTrace();
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
            NameExpr targetNameExpr;
            try
            {
                targetNameExpr = targets.getNameExpresion(i).clone();
                if (targetNameExpr != null)
                {
                    returnValues.add(targetNameExpr);
                }
            } catch (CloneNotSupportedException e)
            {
                e.printStackTrace();
            }

        }
       row.setChild(returnValues, 0);
       matrixExpr.setRow(row, 0);
    }
    
    public HashMap<TIRNode,  ASTNode> getIRToRawASTTable()
    {
        return fIRToRawASTTable;
    }
    
    private TIRNode getFunctionNode()
    {
        return fVisitedNodes.get(0);
    }
    
    private String printTIRNode(TIRNode node)
    {
        if (node instanceof TIRAbstractAssignStmt) return ((TIRAbstractAssignStmt) node).getStructureString();
        else if (node instanceof TIRFunction) return ((TIRFunction) node).getStructureString().split("\n")[0];
        else if (node instanceof TIRIfStmt) return ((TIRIfStmt) node).getStructureString().split("\n")[0];
        else if (node instanceof TIRWhileStmt) return ((TIRWhileStmt) node).getStructureString().split("\n")[0];
        else if (node instanceof TIRForStmt) return ((TIRForStmt) node).getStructureString().split("\n")[0];
        else return null;
    }
    
    private String printASTNode(ASTNode node)
    {
        if (node instanceof AssignStmt) return ((AssignStmt) node).getStructureString();
        else if (node instanceof Function) return ((Function) node).getStructureString().split("\n")[0];
        else if (node instanceof IfStmt) return ((IfStmt) node).getStructureString().split("\n")[0];
        else if (node instanceof WhileStmt) return ((WhileStmt) node).getStructureString().split("\n")[0];
        else if (node instanceof ForStmt) return ((ForStmt) node).getStructureString().split("\n")[0];
        else return null;
    }
    
    private void printTableEntry(TIRNode key, ASTNode value)
    {
       System.out.println(printTIRNode(key) + " ---> " + printASTNode(value));
    }
}
