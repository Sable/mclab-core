package natlab.toolkits.rewrite.inline;
import java.util.Map;

/**
 * Inlines A Script within a Script or a Function.
 */


import ast.*;

public class ScriptInliner<ScriptOrFunction extends ASTNode> extends Inliner<Script,ScriptOrFunction> {

    public ScriptInliner(ScriptOrFunction tree, Map<String, Script> map) {
        super(tree, map);
        // TODO Auto-generated constructor stub
    }

}
