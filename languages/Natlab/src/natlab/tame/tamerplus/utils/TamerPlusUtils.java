package natlab.tame.tamerplus.utils;

import java.util.Set;

import natlab.tame.tamerplus.analysis.DUChain;
import natlab.tame.tamerplus.analysis.DefinedVariablesNameCollector;
import natlab.tame.tamerplus.analysis.ReachingDefinitions;
import natlab.tame.tamerplus.analysis.TIRToMcSAFIRTableBuilder;
import natlab.tame.tamerplus.analysis.TemporaryVariablesRemoval;
import natlab.tame.tamerplus.analysis.UDChain;
import natlab.tame.tamerplus.analysis.UDDUWeb;
import natlab.tame.tamerplus.analysis.UsedVariablesNameCollector;
import natlab.utils.AstFunctions;
import ast.Name;

import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Sets;

public class TamerPlusUtils
{
    public static Set<String> getNameListAsStringSet(Iterable<Name> nameList)
    {
        return Sets.newHashSet(FluentIterable.from(nameList)
            .filter(Predicates.notNull())
            .transform(AstFunctions.nameToID()));
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
