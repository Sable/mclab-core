package natlab.toolkits.rewrite.simplification;


import java.lang.*;
import java.util.*;

import ast.*;
import natlab.toolkits.rewrite.AbstractLocalRewrite;
import natlab.toolkits.analysis.varorfun.*;

/**
 * A simplification specific implementation of
 * AbstractLocalRewrite. Adds dependencies and requires the kind
 * analysis. 
 *
 * @author Jesse Doherty
 */
public abstract class AbstractSimplification extends AbstractLocalRewrite
{

    VFStructuralForwardAnalysis kindAnalysis;

    public AbstractSimplification( ASTNode tree, 
                                   AbstractLocalRewrite callback, 
                                   VFStructuralForwardAnalysis kind )
    {
        super(tree, callback);
        kindAnalysis = kind;
    }

    public abstract Set<AbstractSimplification> getDependencies();
    
    //public void setKindAnalysis(
    

}
