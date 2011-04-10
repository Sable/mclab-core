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

    protected VFPreorderAnalysis kindAnalysis;

    public AbstractSimplification( ASTNode tree, 
                                   VFPreorderAnalysis kind )
    {
        super(tree);
        kindAnalysis = kind;
    }

    public abstract Set<Class<? extends AbstractSimplification>> getDependencies();
    
    //public void setKindAnalysis(
    

    public boolean isVar( Expr expr )
    {
        if( expr instanceof NameExpr ){
            NameExpr nameExpr = (NameExpr)expr;
            if( nameExpr.tmpVar )
                return true;
            else{
                if (kindAnalysis.getFlowSets().containsKey(nameExpr)){
                    /*if we don't find the node in the kindAnalasys, re-analyze
                      TODO -- is this correct?
                      this was added because a simplification added calls to 'false'
                      which couldn't be found in the flow sets
                    */
                    kindAnalysis.analyze();
                }
                VFDatum kind = kindAnalysis.getFlowSets().get(nameExpr).contains(nameExpr.getName().getID());
                return (kind!=null) && kind.isVariable();
            }
        }
        return false;
    }
    public boolean isFun( Expr expr )
    {
        if( expr instanceof NameExpr ){
            NameExpr nameExpr = (NameExpr)expr;
            if( nameExpr.tmpVar )
                return false;
            else{
                VFDatum kind = kindAnalysis.getFlowSets().get(nameExpr).contains(nameExpr.getName().getID());
                return (kind!=null) && kind.isFunction();
            }
        }
        return false;
    }

}
