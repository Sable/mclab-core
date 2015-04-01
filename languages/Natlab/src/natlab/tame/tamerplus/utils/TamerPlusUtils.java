package natlab.tame.tamerplus.utils;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import natlab.tame.tamerplus.analysis.DUChain;
import natlab.tame.tamerplus.analysis.DefinedVariablesNameCollector;
import natlab.tame.tamerplus.analysis.ReachingDefinitions;
import natlab.tame.tamerplus.analysis.TIRToMcSAFIRTableBuilder;
import natlab.tame.tamerplus.analysis.TemporaryVariablesRemoval;
import natlab.tame.tamerplus.analysis.UDChain;
import natlab.tame.tamerplus.analysis.UDDUWeb;
import natlab.tame.tamerplus.analysis.UsedVariablesNameCollector;
import ast.Name;

public class TamerPlusUtils
{
    public static Set<String> getNameListAsStringSet(Iterable<Name> nameList)
    {
      return StreamSupport.stream(nameList.spliterator(), false)
          .filter(Objects::nonNull)
          .map(Name::getID)
          .collect(Collectors.toSet());
    }
    
    public static void debugMode()
    {
        DefinedVariablesNameCollector.DEBUG = true;
        DUChain.DEBUG = true;
        ReachingDefinitions.DEBUG = true;
        TemporaryVariablesRemoval.DEBUG = true;
        TIRToMcSAFIRTableBuilder.DEBUG = true;
        UDChain.DEBUG = true;
        UDDUWeb.DEBUG = true;
        UsedVariablesNameCollector.DEBUG = true;
    }
}
