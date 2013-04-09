package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import natlab.tame.tir.TIRAbstractAssignStmt;
import natlab.tame.tir.TIRForStmt;
import natlab.tame.tir.TIRFunction;
import natlab.tame.tir.TIRIfStmt;
import natlab.tame.tir.TIRNode;
import natlab.tame.tir.TIRWhileStmt;
import ast.Name;

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
    
    public static String printNode(Name node)
    {
        return node.getID();
    }
}
