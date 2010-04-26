package natlab;

import java.io.IOException;
import java.io.PrintWriter;

public class MultireturnSimplifyPassTestGenerator extends AbstractRewritePassTestGenerator {

    private MultireturnSimplifyPassTestGenerator(){
        super("/natlab/MultireturnSimplifyPassTests.java");
        className = "MultireturnSimplifyPassTests";
        transformationName = "MultiReturnRewrite";
    }

    public static void main(String[] args) throws IOException 
    {
        new MultireturnSimplifyPassTestGenerator().generate(args);
    }


}