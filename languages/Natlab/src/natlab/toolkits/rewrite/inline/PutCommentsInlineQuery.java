package natlab.toolkits.rewrite.inline;

import natlab.Static.ir.IRCommentStmt;
import ast.*;

/**
 * Represents a InlineQuery object, which can be used to inline functions in other functions.
 * It will add comments at the beginning and end of the inlined function's body.
 * This will insert IRCommentStmt's with comments (as opposed to EmptyStmts, it's parent class).
 * 
 * @author ant6n
 */
public class PutCommentsInlineQuery implements InlineQuery<Function, Function> {


    public boolean doInline(InlineInfo<Function, Function> i) {
        i.getInlinedScriptOrFunction().getStmtList().insertChild(new IRCommentStmt(getHeader(i)), 0);
        i.getInlinedScriptOrFunction().getStmtList().add(new IRCommentStmt(getFooter(i)));
        return true;
    }

 
    private String getHeader(InlineInfo<Function, Function> info){
        String s = "Start of Function [...] = "+info.getInlinedScriptOrFunction().getName()+"(";
        for (int i = 0; i < info.getParameters().getNumChild(); i++ ){
            s += info.getParameters().getChild(i).getPrettyPrinted()+"->"
                +info.getInlinedScriptOrFunction().getInputParam(i).getPrettyPrinted()
                +((info.getParameters().getNumChild()-1 == i)?"":", ");
        }
        return s+")";
    }

    private String getFooter(InlineInfo<Function, Function> info){
        String s = "End of Function [";
        for (int i = 0; i < info.getTargets().getNumChild(); i++ ){
            s += info.getTargets().getChild(i).getPrettyPrinted()+"<-"
                +info.getInlinedScriptOrFunction().getOutputParam(i).getPrettyPrinted()
                +((info.getTargets().getNumChild()-1 == i)?"":", ");
        }
        return s+"] = "+info.getInlinedScriptOrFunction().getName()+"(...)";
    }

}


