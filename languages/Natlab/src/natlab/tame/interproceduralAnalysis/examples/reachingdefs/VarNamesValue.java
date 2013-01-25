package natlab.tame.interproceduralAnalysis.examples.reachingdefs;

import natlab.toolkits.analysis.Mergable;

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
