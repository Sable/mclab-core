package natlab.toolkits.rewrite.simplification;

import java.util.*;

import ast.*;
import natlab.DecIntNumericLiteralValue;
import natlab.toolkits.rewrite.*;
import natlab.toolkits.analysis.varorfun.*;


/**
 * Makes the use of end as an index more explicit. Removes uses of end
 * and replaces them with a variable containing the index value. This
 * value is given the value with the use of the non syntactic end
 * call. 
 * @author Jesse Doherty
 */
public class EndSimplification extends AbstractSimplification
{
    public EndSimplification( ASTNode tree, VFPreorderAnalysis kind )
    {
        super( tree, kind );
    }

    /**
     * Builds a singleton start set containing this class.
     */ 
    public static Set<Class<? extends AbstractSimplification>> getStartSet()
    {
        HashSet<Class<? extends AbstractSimplification>> set = new HashSet();
        set.add( EndSimplification.class );
        return set;
    }

    public Set<Class<? extends AbstractSimplification>> getDependencies()
    {
        HashSet<Class<? extends AbstractSimplification>> dependencies = new HashSet();
        return dependencies;
    }

}
