import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.Charset;
import java.nio.file.Files;


public class Main {
    public static void main(String[] args) throws Exception {
        //OctaveTesting test = new OctaveTesting();
        //test.parse();


        // ANTLRInputStream input = new ANTLRInputStream(new FileInputStream("/home/leoks/EclipseIndigo/workspace2/SO/src/mypackage/MyVisitor.java"));
        try {
            File[] files = new File("/home/valerie/Documents/mclab/antlr4-octave/test/from_octave/").listFiles();
            //File file = new File("/home/valerie/Documents/mclab/antlr4-octave/test/from_octave/08_add_sub.m");
            for (File file : files) {
                PrintStream console = System.out;
                System.out.println("--------------------------------------------\n"+file.getName()
                        +"\n--------------------------------------------");
                System.setOut(new PrintStream(new BufferedOutputStream(new FileOutputStream("test/gen/output"+file.getName())), true));

                byte[] encoded = Files.readAllBytes(file.toPath());
                //String input = new String(encoded, Charset.defaultCharset());
                String input = "a = 4;\n";
                OctaveLexer lexer = new OctaveLexer(new ANTLRInputStream(input));
                OctaveParser parser = new OctaveParser(new CommonTokenStream(lexer));
                ParseTree tree = parser.program();
                ParseTreeWalker.DEFAULT.walk(new OctaveListenerWalk(), tree);
                System.setOut(console);
            }
        } catch(NullPointerException e){
            System.out.println("NullPointerException on file I/O");
        }


        //String input = "function f(arg1, arg2, arg3)\n x = !arg1; \nx++;\n#{\ncomment\n more comment\n#}\nendfunction";
        //OctaveLexer lexer = new OctaveLexer(new ANTLRInputStream(input));
        //OctaveParser parser = new OctaveParser(new CommonTokenStream(lexer));

        //ParseTree tree = parser.program();
        //ParseTreeWalker.DEFAULT.walk(new OctaveListenerWalk(), tree);
    }
}
