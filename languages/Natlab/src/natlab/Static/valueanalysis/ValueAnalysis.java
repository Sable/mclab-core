package natlab.Static.valueanalysis;

import natlab.Static.callgraph.FunctionCollection;
import natlab.Static.callgraph.StaticFunction;
import natlab.Static.interproceduralAnalysis.InterproceduralAnalysis;
import natlab.Static.interproceduralAnalysis.InterproceduralAnalysisFactory;
import natlab.Static.interproceduralAnalysis.InterproceduralAnalysisNode;
import natlab.Static.valueanalysis.value.*;

public class ValueAnalysis<D extends MatrixValue<D>>
extends InterproceduralAnalysis<IntraproceduralValueAnalysis<D>,Args<D>,Res<D>>{

    public ValueAnalysis(FunctionCollection callgraph, Args<D> mainArgs, ValueFactory<D> valueFactory) {
        super(new Factory<D>(valueFactory), callgraph, mainArgs);
    }
    

    
    /**
     * the factory that generates the intraprocedural analysis nodes
     */
    private static class Factory<D extends MatrixValue<D>> implements InterproceduralAnalysisFactory<IntraproceduralValueAnalysis<D>, Args<D>, Res<D>>{
        ValueFactory<D> valueFactory;
        public Factory(ValueFactory<D> valueFactory){
            this.valueFactory = valueFactory;
        }
        
        public IntraproceduralValueAnalysis<D> newFunctionAnalysis(
                StaticFunction function,
                Args<D> argumentSet,
                InterproceduralAnalysisNode<IntraproceduralValueAnalysis<D>, Args<D>, Res<D>> node) {
            return new IntraproceduralValueAnalysis<D>(node,function,valueFactory,argumentSet);
        }
    }
}
