package natlab.refactoring;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import natlab.toolkits.analysis.varorfun.VFDatum;
import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import nodecases.AbstractNodeCaseHandler;
import nodecases.NodeCaseHandler;
import ast.ASTNode;
import ast.AssignStmt;
import ast.CompilationUnits;
import ast.Function;
import ast.FunctionHandleExpr;
import ast.NameExpr;

public class SimpleCallGraphBuilder{
    public SimpleCallGraphBuilder(CompilationUnits cu){
        final Map<ASTNode, TreeSet<String>> handles = new HashMap<ASTNode, TreeSet<String>>();  /*Function or Script to Set of handles*/
        final Map<Function, Map<String, Set<String>>> functionDeps=new HashMap<Function, Map<String, Set<String>>>(); /* depedency tree not propagated yet*/
        final CompilationUnits c = cu;
        NodeCaseHandler dependencyAnalysis = new AbstractNodeCaseHandler(){
                private VFPreorderAnalysis kindAnalysis;
                private ASTNode function = null;
                private Map<String, Set<String>> curDeps;
                private Set<String> curRHS;
                private Set<String> curNames;
                @Override public void caseASTNode(ASTNode node){
                    for( int i = 0; i<node.getNumChild(); i++ ){
                        if( node.getChild(i) != null )
                            node.getChild(i).analyze( this );
                    }
                }
                @Override public void caseFunction(Function n){
                    kindAnalysis = new VFPreorderAnalysis(c);
                    n.analyze(kindAnalysis);
                    curNames = new TreeSet<String>();
                    function=n;
                    handles.put(n, new TreeSet<String>());
                    curDeps=new HashMap<String, Set<String>>();
                    caseASTNode(n);
                    function=null;
                    for (String k: curNames){
                        for (String i: curNames){
                            for (String j: curNames){
                                if (curDeps.containsKey(i) && curDeps.get(i).contains(k) &&
                                    curDeps.containsKey(k) && curDeps.get(k).contains(j))
                                    curDeps.get(i).add(j);
                            }
                        }
                        //                        for (String 
                    }
                    functionDeps.put(n, curDeps);
                    curDeps=null;
                    curNames=null;
                }
                @Override public void caseFunctionHandleExpr(FunctionHandleExpr h){
                    if (curNames!=null) 
                        curNames.add(h.getName().getID());
                    //handles.get(function).add(h.getName().getID());
                    if (curRHS!=null)
                        curRHS.add(h.getName().getID());
                }
                @Override public void caseNameExpr(NameExpr n){
                    VFDatum d = kindAnalysis.getFlowSets().get(n).get(n.getName().getID());
                    if (d==null)
                        System.out.println(n.getName().getID());
                    if ((d!=null) && (!VFDatum.FUN.equals(d))){ //TODO FUNCTION CALLS
                        System.out.println("Adding to RHS "+ n.getName().getID()+" with kind "+d);
                        if (curRHS!=null){
                            curRHS.add(n.getName().getID());
                        }
                        if (curNames!=null){
                            curNames.add(n.getName().getID());
                        }
                    }
                    else System.out.println("Found Function Call "+ n.getName().getID());
                }

                @Override public void caseAssignStmt(AssignStmt a){
                    curRHS=new TreeSet<String>();
                    a.getRHS().analyze(this);
                    System.out.println("RHS: "+ curRHS);
                    for (NameExpr l: a.getLHS().getNameExpressions()){
                        curNames.add(l.getName().getID());
                        if (curDeps.containsKey(l.getName().getID())){
                            Set<String> mergedCurDeps=new TreeSet(curRHS);
                            mergedCurDeps.addAll(curDeps.get(l.getName().getID()));
                            curDeps.put(l.getName().getID(), mergedCurDeps);
                        }
                        else
                            curDeps.put(l.getName().getID(), curRHS);
                    }
                    curRHS=null;
                    a.getLHS().analyze(this);
                }
            };
        cu.analyze(dependencyAnalysis);
        System.out.println(functionDeps);
    }
}