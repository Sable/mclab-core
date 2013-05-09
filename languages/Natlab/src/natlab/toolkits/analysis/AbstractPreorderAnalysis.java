package natlab.toolkits.analysis;

import analysis.*;
import ast.*;
import java.util.*;

/**
 * This class is deprecated. AbstractDepthFirstAnalysis should be used
 * instead. 
 *
 * @see AbstractDepthFirstAnalysis
 */
@Deprecated
public abstract class AbstractPreorderAnalysis<A extends FlowSet> 
    extends AbstractDepthFirstAnalysis<A>
{
    public static boolean DEBUG=false;

    public AbstractPreorderAnalysis(){
        super();
    }
    public AbstractPreorderAnalysis(ASTNode tree){
        this.tree = tree;
        flowSets = new HashMap<ASTNode, A>();
    }

}
