package natlab.tame.valueanalysis;

import natlab.tame.callgraph.SimpleFunctionCollection;
import natlab.tame.callgraph.StaticFunction;
import natlab.tame.interproceduralAnalysis.InterproceduralAnalysis;
import natlab.tame.interproceduralAnalysis.InterproceduralAnalysisFactory;
import natlab.tame.interproceduralAnalysis.InterproceduralAnalysisNode;
import natlab.tame.valueanalysis.value.Args;
import natlab.tame.valueanalysis.value.Res;
import natlab.tame.valueanalysis.value.Value;
import natlab.tame.valueanalysis.value.ValueFactory;

public class ValueAnalysis<V extends Value<V>>
extends InterproceduralAnalysis<IntraproceduralValueAnalysis<V>,Args<V>,Res<V>>{

    public ValueAnalysis(SimpleFunctionCollection callgraph, Args<V> mainArgs, ValueFactory<V> valueFactory) {
        super(new Factory<V>(valueFactory), callgraph, mainArgs);
        
       
      
        
    }
    

    
    /**
     * the factory that generates the intraprocedural analysis nodes
     */
    private static class Factory<V extends Value<V>> implements InterproceduralAnalysisFactory<IntraproceduralValueAnalysis<V>, Args<V>, Res<V>>{
        ValueFactory<V> valueFactory;
        public Factory(ValueFactory<V> valueFactory){
            this.valueFactory = valueFactory;
        }
        
        public IntraproceduralValueAnalysis<V> newFunctionAnalysis(
                StaticFunction function,
                Args<V> argumentSet,
                InterproceduralAnalysisNode<IntraproceduralValueAnalysis<V>, Args<V>, Res<V>> node) {
            return new IntraproceduralValueAnalysis<V>(node,function,valueFactory,argumentSet);
        }
    }
}
