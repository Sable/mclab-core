import org.antlr.v4.misc.Graph;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;


public class OctaveListenerWalk extends OctaveBaseListener {
    int tempCounter;
    public String getTemp(){
        tempCounter++;
        return "temp"+tempCounter;
    }
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
        System.out.print("_"+ctx.getText());

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
    public void enterPostfix_expr(OctaveParser.Postfix_exprContext ctx) {
        //find type (only want second rule)
        //save lefthand (var)
        //use lefthand to do expression expansion. (based on token type)
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
            /*case 79://++
                System.out.print("=");
                ParseTree parent = node.getParent();
                while(parent.getChildCount() == 1){
                    parent = parent.getParent();
                }
                System.out.print("+1");
                break;
            case 80: //--
                System.out.print("=");

                System.out.print("-1");
                break;*/
            case 43: //IDENTIFIER - need to underscore all user-given ids
                break;
            default:
                System.out.print(node.getText()); //get the text of the token
                //System.out.println(node.getSymbol().getType()); //get the type of the token
        }
    }
}
