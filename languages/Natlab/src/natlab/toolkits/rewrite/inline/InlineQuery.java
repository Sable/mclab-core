package natlab.toolkits.rewrite.inline;

import ast.ASTNode;

/**
 * when inlining a function, there might be some cleanup that needs to be done.
 * This interface allows to either perform cleanup (like dealing with nargin,
 * vargin,vargout etc.), or forbidding the inline action altogether.
 */
public interface InlineQuery<InlinedScriptOrFunction extends ASTNode,TargetScriptOrFunction extends ASTNode>{
    /**
     * Determines whether the current inline action should be performed, and
     * allows updating the function to be inlined via the given QueryObject.
     * @param inlineInfo an Object containing information about the inline action
     * @return true if the inline action should be performed, false otherwise.
     */
    public boolean doInline(InlineInfo<InlinedScriptOrFunction,TargetScriptOrFunction> inlineInfo);
}

