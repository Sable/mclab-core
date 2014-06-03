package natlab.toolkits.rewrite;

import java.util.HashSet;
import java.util.Set;

import natlab.toolkits.filehandling.FunctionOrScriptQuery;
import ast.ASTNode;
import ast.Program;


/**
 * helps finding free names for function names
 * @author ant6n
 */

public class TempFunctionBuilderHelper {
    static HashSet<String> allNames = new HashSet<String>();
    
    public static String getFreshFunctionName(ASTNode<?> node, FunctionOrScriptQuery query, String startsWith){
        return getFreshFunctionName(node,query,startsWith,new HashSet<String>());
    }
    
    public static String getFreshFunctionName(ASTNode<?> node, FunctionOrScriptQuery query, String startsWith, Set<String> avoidNames){
        //make sure we are on the highest possible node
        if (!(node instanceof Program) || node.getParent() != null){
            return getFreshFunctionName(node.getParent(),query, startsWith, avoidNames);
        }
        
        //get all symbols used under that node
        HashSet<String> usedNames = new HashSet<String>(node.getSymbols()); //TODO check whether this actually captures all Names
        
        //find fresh name by 
        int i = 1;
        while (true){
            String name = startsWith+(i++);
            if (       !usedNames.contains(name)
                    && !avoidNames.contains(name)
                    && !allNames.contains(name)
                    && !query.isFunctionOrScript(name) 
                    && !query.isPackage(name)){
                allNames.add(name);
                return name;
                
            }
        }
    }
}
