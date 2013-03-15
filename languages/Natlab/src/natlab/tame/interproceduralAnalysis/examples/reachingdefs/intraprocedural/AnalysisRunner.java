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
        String file = "/Users/Pepe/Desktop/School/Thesis/myBenchmarks/collapse.m";
//        String file = "/Users/Pepe/Desktop/School/Thesis/myBenchmarks/mcfor_test/adpt/drv_adpt.m";
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

        
        int counter = 0;
      for(StaticFunction f : functionList)
      {
          System.out.println(f.getAst().getPrettyPrintedLessComments());
          UDChain ud = new UDChain(new ReachingDefinitions(f));
          ud.constructUDChain();
          DUChain du = new DUChain(ud);
          du.constructDUChain();
          
//          System.out.println("\n//////// UD Analysis: //////////////\n");
//          ud.printUDChain();
//          System.out.println("\n//////// DU Analysis: //////////////\n");
//          du.printDUChain();
          
          UDDUWeb udduWeb = new UDDUWeb(ud, du);
          udduWeb.constructUDDUWeb();
          
//          RenameVariablesForTIRNodes rv = new RenameVariablesForTIRNodes(udduWeb);
//          rv.analyze();
//          System.out.println(f.getAst().getPrettyPrintedLessComments());

    }
    }
}

