package natlab.tame.tamerplus.utils;

import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRIfStmt;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.TIRWhileStmt;
import ast.ASTNode;
import ast.AssignStmt;
import ast.ForStmt;
import ast.Function;
import ast.IfStmt;
import ast.Name;
import ast.WhileStmt;

public class NodePrinter
{
    public static String printNode(TIRNode node)
    {
        if (node instanceof TIRAbstractAssignStmt) return ((TIRAbstractAssignStmt) node).getStructureString();
        else if (node instanceof TIRFunction) return ((TIRFunction) node).getStructureString().split("\n")[0];
        else if (node instanceof TIRIfStmt) return ((TIRIfStmt) node).getStructureString().split("\n")[0];
        else if (node instanceof TIRWhileStmt) return ((TIRWhileStmt) node).getStructureString().split("\n")[0];
        else if (node instanceof TIRForStmt) return ((TIRForStmt) node).getStructureString().split("\n")[0];
        else return null;
    }
    
    public static String printNode(ASTNode<?> node)
    {
        if (node instanceof AssignStmt) return ((AssignStmt) node).getStructureString();
        else if (node instanceof Function) return ((Function) node).getStructureString().split("\n")[0];
        else if (node instanceof IfStmt) return ((IfStmt) node).getStructureString().split("\n")[0];
        else if (node instanceof WhileStmt) return ((WhileStmt) node).getStructureString().split("\n")[0];
        else if (node instanceof ForStmt) return ((ForStmt) node).getStructureString().split("\n")[0];
        else return null;
    }
    
    public static String printName(Name node)
    {
        return node.getID();
    }
}
