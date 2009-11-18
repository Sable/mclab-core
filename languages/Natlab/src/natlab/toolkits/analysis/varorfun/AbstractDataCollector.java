package natlab.toolkits.analysis.varorfun;

import ast.*;
import natlab.toolkits.analysis.*;

/**
 * A class to collect instances of RHS name uses that are determined
 * to be either a var or a function
 */

public abstract class AbstractDataCollector extends AbstractPreorderAnalysis< DataCollectFlowSet<String, Integer> >
{

    public static boolean DEBUG = false;


    VFFlowset<String, VFDatum> currentStmtSet;
    public AbstractDataCollector( ASTNode tree )
    {
        super( tree );
        if(DEBUG)
            System.out.println("constructing");
        currentSet = newInitialFlow();
    }

    /**
     * Helper method to increment the data for a given name
     */
    public void incrementData( String name )
    {
        if(DEBUG)
            System.out.println("incrementing");
        Integer value = currentSet.contains( name );
        if( value == null )
            currentSet.add( name, new Integer(1) );
        else
            currentSet.add( name, new Integer( value.intValue() + 1) );
    }

    public AbstractDataCollector(){
        super();
    }

    public DataCollectFlowSet<String, Integer> newInitialFlow()
    {
        if(DEBUG)
            System.out.println("creating new init flow");
        return new DataCollectFlowSet();
    }

    public int[] countData(String suffix)
    {
        int foundGood = 0, foundTotal = 0, total = 0;
        String s;
        int i;
        for( DataPair<String,Integer> pair: currentSet.toList() ){
            s = pair.getKey();
            i = pair.getValue().intValue();
            if( !s.endsWith( suffix ) ){
                foundGood += i;
                Integer found = currentSet.contains( s + suffix );
                if( found != null )
                    foundTotal += found.intValue();
            }
            else
                total += i;
        }
        int[] r = {foundGood, foundTotal, total};
        return r;
    }

}            