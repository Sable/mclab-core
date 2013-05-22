package natlab.tame.interproceduralAnalysis.examples.reachingdefs.interprocedural;

import java.util.Collections;

import natlab.tame.TamerTool;
import natlab.tame.callgraph.Callgraph;
import natlab.tame.callgraph.StaticFunction;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.interproceduralAnalysis.InterproceduralAnalysis;
import natlab.tame.interproceduralAnalysis.InterproceduralAnalysisFactory;
import natlab.tame.interproceduralAnalysis.InterproceduralAnalysisNode;
import natlab.tame.valueanalysis.simplematrix.SimpleMatrixValue;
import natlab.tame.valueanalysis.simplematrix.SimpleMatrixValueFactory;
import natlab.toolkits.analysis.HashSetFlowSet;
import natlab.toolkits.filehandling.GenericFile;
import natlab.toolkits.path.FileEnvironment;

public class VariableNameAnalysis 
extends InterproceduralAnalysis<IntraproceduralVarNamesAnalysis, VarNamesInput, HashSetFlowSet<VarNamesValue>>
{
    private static class Factory implements InterproceduralAnalysisFactory<IntraproceduralVarNamesAnalysis, VarNamesInput, HashSetFlowSet<VarNamesValue>>
    {
        public IntraproceduralVarNamesAnalysis newFunctionAnalysis
        (
            StaticFunction function,
            VarNamesInput argumentSet,
            InterproceduralAnalysisNode<IntraproceduralVarNamesAnalysis, VarNamesInput, HashSetFlowSet<VarNamesValue>> node
        ) 
        {
            return new IntraproceduralVarNamesAnalysis(node, argumentSet);
        }
    }
    
    public VariableNameAnalysis(InterproceduralAnalysis<?, ?, ?> analysis, HashSetFlowSet<VarNamesValue> input)
    {
        super(new Factory(), analysis.getFunctionCollection(), new VarNamesInput(analysis.getMainNode(), input));
    }
    
    public static void main(String[] args)
    {
        String file = "/Users/Pepe/Desktop/School/Thesis/myBenchmarks/hello.m";
        if (args.length == 1){
            file = args[0];
        }
        
        Callgraph<SimpleMatrixValue> callgraph = TamerTool.getCallgraph
        (
            new FileEnvironment(GenericFile.create(file)),
            Collections.singletonList(new SimpleMatrixValue(PrimitiveClassReference.DOUBLE)),
            new SimpleMatrixValueFactory()
        );
        
        
        VariableNameAnalysis analysis = new VariableNameAnalysis(callgraph.getAnalysis(), new HashSetFlowSet<VarNamesValue>());
        for (int i = 0; i < analysis.getNodeList().size(); i++){
            System.out.println(
                analysis.getNodeList().get(i).getFunction().getAst().getAnalysisPrettyPrinted(
                    analysis.getNodeList().get(i).getAnalysis(),true,true));
        }
        
//        Intraprocedural version of the VarNames analysis        
//        System.out.println(callgraph.prettyPrint());
//        VariableNameCollector variableNameCollector = new VariableNameCollector(callgraph.getAnalysis().getMainNode().getFunction().getAst());
//        variableNameCollector.getResult();
        
        
        
    }
    
}
