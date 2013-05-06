package natlab.tame.interproceduralAnalysis.examples.reachingdefs.intraprocedural;

import java.util.List;
import java.util.Set;

import ast.Name;

import com.google.common.collect.Sets;

public class TamerPlusUtils
{
    
    public static Set<String> getNameListAsStringSet(List<Name> nameList)
    {
        Set<String> nameSet = Sets.newHashSet();
        for(Name variableName : nameList)
        {
            if (variableName != null)
            {
                nameSet.add(variableName.getID());
            }
        }
        return nameSet;
    }
    
    public static Set<String> getNameListAsStringSet(ast.List<Name> nameList)
    {
        Set<String> nameSet = Sets.newHashSet();
        for(Name variableName : nameList)
        {
            if (variableName != null)
            {
                nameSet.add(variableName.getID());
            }
        }
        return nameSet;
    }
}
