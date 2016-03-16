import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

/**
 * Created by valerie on 08/03/16.
 */
public class OctaveListenerWalk extends OctaveBaseListener {
    @Override
    public void enterQuiet_element_separator_list(OctaveParser.Quiet_element_separator_listContext ctx) {
        java.util.List<TerminalNode> e = ctx.getTokens(1);
        System.out.print(e.get(0).getText()+ "<- THIS");
    }
    @Override
    public void enterLiteral(OctaveParser.LiteralContext ctx) {
        //System.out.print(ctx.getText());
    }
    @Override
    public void enterExpr(OctaveParser.ExprContext ctx) {
        //System.out.println(ctx.getText());
    }
    @Override
    public void enterT_NOT(OctaveParser.T_NOTContext ctx) {
        //System.out.print("~");
    }
    @Override
    public void enterName(OctaveParser.NameContext ctx) {
        //System.out.print(ctx.getText());
    }

    public void enterMaybe_cmd(OctaveParser.Maybe_cmdContext ctx) {
        //java.util.List<TerminalNode> e = ctx.getTokens(4);

       // System.out.print('=');
    }
   /* @Override
    public void enterASSIGN(OctaveParser.ASSIGNContext ctx) {
        String left = ctx.getChild(0).getText();
        String right = null;
        int count = ctx.getChildCount();
        int i = 1;
        while(count > i){
            if(ctx.getChild(i).getChildCount() == 0){
                right = ctx.getChild(i).getText();
            }
            i++;
        }
        System.out.println(left+"="+right);

    }*/
   @Override
   public void enterEveryRule(ParserRuleContext ctx) {
       TerminalNode compare = null;
       //if(ctx.getClass().equals(compare.getClass()));
           //System.out.print("{"+ctx.getText()+"}");
   }

    @Override
    public void visitTerminal(TerminalNode node) {
        //Token thisToken = (Token) node.getPayload();
        switch (node.getSymbol().getType()){
            case 10://multiple ends (endfunction,etc)
            case 11:
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 22:
                System.out.print("end");
                break;
            case 88: //bracket_comment
            case 89: //comment
                break;
            case 65: //NOT
                System.out.print("~");
                break;
            case -1: //<EOF>
                System.out.println();
                break;
            default:
                System.out.print(node.getText()); //get the text of the token
                //System.out.println(node.getSymbol().getType()); //get the type of the token
        }
    }
}
