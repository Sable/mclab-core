import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * Created by valerie on 08/03/16.
 */
public class Main {
    public static void main(String[] args) throws Exception {
        //OctaveTesting test = new OctaveTesting();
        //test.parse();


        String input = "function f(arg1, arg2, arg3)\n x = !arg1; \n#{\ncomment\n more comment\n#}\nendfunction";
        OctaveLexer lexer = new OctaveLexer(new ANTLRInputStream(input));
        OctaveParser parser = new OctaveParser(new CommonTokenStream(lexer));
        //final OctaveParser.ExprContext context = parser.expr();
        //ParseTree tree = parser.program();
        /*ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new OctaveListenerWalk(), tree);
        System.out.println(visit(context));*/
        ParseTree tree = parser.program();
        ParseTreeWalker.DEFAULT.walk(new OctaveListenerWalk(), tree);
    }
}
