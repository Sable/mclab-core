
package natlab.toolkits.scalar;

/**
 * Reaching Definitions Analysis
 * It uses structural flow analysis (StructuralForwardFlowAnalysis),
 * which intend to keep the same analysis-interface as soot.  
 * 
 * Change Log:
 *  - 2008.06 created by Jun Li
 *  	- Specify the <N,A> to <ASTNode, ArraySparseSet> 
 */
import ast.*;
import natlab.toolkits.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.TreeSet;
import java.util.Comparator;

/**
 *   Reaching Definitions Analysis based on StructuralForwardFlowAnalysis
 */
public class ReachingDefs extends StructuralForwardFlowAnalysis<ASTNode, FlowSet> 
{
	FlowSet emptySet;

    /**
     *   Computes the analysis given a AST-tree computed from a
     *   method.  
     *
     *   @param tree a AST-tree on which to compute the analysis.
     *   
     */
    public ReachingDefs(ASTNode tree)
    {
        super(tree);
        
        emptySet = new ArraySparseSet();
        
        /* In soot interface, 'kill sets' and 'generate sets' are computed
         * here, and stored in maps, such as: 
			    Map<Unit, FlowSet> unitToGenerateSet;
			    Map<Unit, FlowSet> unitToKillSet;
			    Map<Unit, FlowSet> unitToDefSet;
         * In current interface, those sets are computed when analysis
         * visits that node, which is in flowThrough().
         */

        doAnalysis(this);
        
    }

    protected FlowSet newInitialFlow()
    {
        return emptySet.clone();
    }

    protected FlowSet entryInitialFlow()
    {
        return emptySet.clone();
    }
        
    protected void flowThrough(FlowSet inValue, ASTNode unit, FlowSet outValue)
    {
        FlowSet in = (FlowSet) inValue, out = (FlowSet) outValue;

        // Usually, in soot, the code will look like this.
        // Perform kill
        //    in.difference(unitToKillSet.get(unit), out);
        // Perform generation
        //    out.union(unitToGenerateSet.get(unit), out);

        // Current implementation:
        // Creating generate sets
        FlowSet genSet = emptySet.clone();
		for(ValueBox vb : (java.util.List<ValueBox>) ((ASTNode)unit).getDefBoxes()) {
	        genSet.add((ASTNode)unit, genSet);	        
		}
		
		// Creating kill set and 
		// kill set = {all definitions of in-set 
		//				that are for same variables of new gen-set}
        // Perform generation &  Perform kill
        ((ArraySparseSet)out).difference(in, genSet, out);
		
		// System.out.println(" [flowThrough]:" + out.size());
    }

    protected void merge(FlowSet in1, FlowSet in2, FlowSet out)
    {
    	in1.union(in2, out);
    }
    
    protected void copy(FlowSet source, FlowSet dest)
    {
    	source.copy(dest);
    }

    public FlowSet getEmptySet() {
    	return new ArraySparseSet();
    }
}
