package natlab;

import java.io.IOException;
import java.io.PrintWriter;



/**
 * @author Jesse Doherty
 */
public class SimpleIfPassTestGenerator extends AbstractRewritePassTestGenerator
{

    private SimpleIfPassTestGenerator(){
        super("/natlab/SimpleIfPassTests.java");
        className = "SimpleIfPassTests";
        transformationName = "SimpleIfSimplification";
    }

    public static void main(String[] args) throws IOException
    {
        new SimpleIfPassTestGenerator().generate(args);
    }

}
