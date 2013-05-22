package natlab.tame.tamerplus;

import java.util.Collections;
import java.util.List;

import natlab.tame.TamerTool;
import natlab.tame.callgraph.Callgraph;
import natlab.tame.callgraph.StaticFunction;
import natlab.tame.classes.reference.PrimitiveClassReference;
import natlab.tame.tamerplus.transformation.TransformationEngine;
import natlab.tame.tamerplus.utils.TamerPlusUtils;
import natlab.tame.valueanalysis.simplematrix.SimpleMatrixValue;
import natlab.tame.valueanalysis.simplematrix.SimpleMatrixValueFactory;
import natlab.toolkits.filehandling.GenericFile;
import natlab.toolkits.path.FileEnvironment;

public class TamerPlusMain
{
    public static void main(String args[])
    {
        String fileName = args[0];
        
        // Get the callgraph of the main function
        Callgraph<SimpleMatrixValue> callgraph = TamerTool.getCallgraph
        (
            new FileEnvironment(GenericFile.create(fileName)),
            Collections.singletonList(new SimpleMatrixValue(null, PrimitiveClassReference.DOUBLE)),
            new SimpleMatrixValueFactory()
        );
            
        List<StaticFunction> functionList = callgraph.getAnalysis().getFunctionCollection().getAllFunctions();
            
        for(StaticFunction function : functionList)
        {
          TamerPlusUtils.debugMode();
          System.out.println(function.getAst().getPrettyPrinted());
          System.err.println(TransformationEngine.forAST(function.getAst()).getTIRToMcSAFIRWithoutTemp().getTransformedTree().getPrettyPrinted());
        }
    }
}

