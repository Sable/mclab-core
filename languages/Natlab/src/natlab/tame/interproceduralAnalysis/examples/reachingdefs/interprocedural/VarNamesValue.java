package natlab.tame.interproceduralAnalysis.examples.reachingdefs.interprocedural;

import natlab.toolkits.analysis.Mergable;
/**
 * Encapsulates the variable name which is a String.
 * It implements Mergable, at first thought merging 
 * would just return this
 * @author Amine Sahibi
 *
 */
public class VarNamesValue implements Mergable<VarNamesValue>
{
    private String fVariableName;
    
    public VarNamesValue(String variableName)
    {
        fVariableName = variableName;
    }
    
    @Override
    public VarNamesValue merge(VarNamesValue o)
    {
        return this;
    }
    
    @Override
    public String toString()
    {
        return fVariableName;
    }
    
    @Override
    public boolean equals(Object o)
    {
        if (o instanceof VarNamesValue)
        {
            return ((VarNamesValue) o).getVariableName().equals(fVariableName);
        }
        return false;
    }
    
    @Override
    public int hashCode() 
    {
        return fVariableName.hashCode();
    }
    
    public String getVariableName()
    {
        return fVariableName;
    }
}
