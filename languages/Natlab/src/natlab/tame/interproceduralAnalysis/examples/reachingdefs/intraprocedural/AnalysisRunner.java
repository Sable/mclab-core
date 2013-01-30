package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.Collections;
import java.util.List;

import natlab.tame.TamerTool;
import natlab.tame.callgraph.Callgraph;
import natlab.tame.callgraph.StaticFunction;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.valueanalysis.simplematrix.SimpleMatrixValue;
import natlab.tame.valueanalysis.simplematrix.SimpleMatrixValueFactory;
import natlab.toolkits.filehandling.genericFile.GenericFile;
import natlab.toolkits.path.FileEnvironment;

public class AnalysisRunner
{
    public static void main(String args[])
    {
        String file = "/Users/Pepe/Desktop/School/Thesis/myBenchmarks/testback.m";
        if (args.length == 1)
        {
            file = args[0];
        }
        
        // Get the callgraph of the main function
        Callgraph<SimpleMatrixValue> callgraph = TamerTool.getCallgraph
        (
            new FileEnvironment(GenericFile.create(file)),
            Collections.singletonList(new SimpleMatrixValue(PrimitiveClassReference.DOUBLE)),
            new SimpleMatrixValueFactory()
        );
        
        List<StaticFunction> functionList = callgraph.getAnalysis().getFunctionCollection().getAllFunctions();
        
        /* Go through the list of all functions, apply variable name collection analysis
         * and print the result
         */
        // TODO hashmap static Function to VariableNameCollector
//        for(StaticFunction f : functionList)
//        {
//            System.out.println("Function " + f.getName());
//            System.out.println(f.getAst().getPrettyPrinted());
//            VariableNameCollector vnc = new VariableNameCollector(f);
//            for(String s : vnc.getResult())
//            {
//                System.out.println(s);
//            }
//            System.out.println();
//        } 
        
      for(StaticFunction f : functionList)
      {
          System.out.println(f.getAst().getPrettyPrinted());
          ReachingDefinitionsAnalysis rda = new ReachingDefinitionsAnalysis(f);
          rda.analyze();
      }
    }
}
