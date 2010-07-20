package natlab;

import java.io.IOException;

/**
 *
 */
public class LeftThreeAddressSimplifyPassTestGenerator extends AbstractRewritePassTestGenerator
{

    private LeftThreeAddressSimplifyPassTestGenerator(){
        super("/natlab/LeftThreeAddressSimplifyPassTests.java");
        className = "LeftThreeAddressSimplifyPassTests";
        transformationName = "LeftThreeAddressRewrite";
        rewritePkg = "threeaddress";
    }

    public static void main(String[] args) throws IOException
    {
        new LeftThreeAddressSimplifyPassTestGenerator().generate(args);
    }

}

