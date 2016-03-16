import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeListener;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class OctaveTesting {

    public void parse() throws Exception {

        String input = "4+3";
        OctaveLexer lexer = new OctaveLexer(new ANTLRInputStream(input));
        OctaveParser parser = new OctaveParser(new CommonTokenStream(lexer));
        final OctaveParser.ExprContext context = parser.expr();
        //ParseTree tree = parser.program();
        /*ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new OctaveListenerWalk(), tree);
        System.out.println(visit(context));*/
        ParseTree tree = parser.program();
        ParseTreeWalker.DEFAULT.walk(new OctaveListenerWalk(), tree);
    }
    private String visit(final OctaveParser.ExprContext context){
        return context.toString();
    }


}

/**
 * Created by valerie on 08/03/16.
 */
/*public class Testing {

    public String testString = "array = [1 2 3];\n";
    public void testRule() throws IOException {
        ANTLRInputStream input = new ANTLRInputStream(this.testString);
        OctaveLexer lexer = new OctaveLexer((CharStream)input);
        org.antlr.runtime.TokenStream tokens = new CommonTokenStream(lexer);

        OctaveParser parser = new OctaveParser(tokens);

        parser.removeErrorListeners();
        parser.setErrorHandler(new ExceptionThrowingErrorHandler());

        if (this.testValid) {
            ParserRuleContext ruleContext = parser.rule_set();
            assertNull(ruleContext.exception);
        } else {
            try {
                ParserRuleContext ruleContext = parser.rule_set();
                fail("Failed on \"" + this.testString + "\"");
            } catch (RuntimeException e) {
                // deliberately do nothing
            }
        }
    }
}*/
