package natlab;

import junit.framework.Assert;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import natlab.toolkits.analysis.varorfun.FunctionVFDatum;

public class FunctionVFDatumTest extends TestCase
{
    public void test_functionvfdatum_makevar() throws Exception
    {
        FunctionVFDatum d = new FunctionVFDatum();
        d.makeVariable();
        assertTrue(FunctionVFDatum.Value.VAR.equals( d.getValue() ));

        d = new FunctionVFDatum();
        d.makeFunction();
        d.makeVariable();
        assertTrue(FunctionVFDatum.Value.TOP.equals( d.getValue() ));

        d = new FunctionVFDatum();
        d.makeAssignedVariable();
        d.makeVariable();
        assertTrue(FunctionVFDatum.Value.VAR.equals( d.getValue() ));

        d = new FunctionVFDatum();
        d.makeVariable();
        d.makeFunction();
        d.makeVariable();
        assertTrue(FunctionVFDatum.Value.TOP.equals( d.getValue() ));
    }

    public void test_functionvfdatum_makeavar() throws Exception
    {
        FunctionVFDatum d = new FunctionVFDatum();
        d.makeAssignedVariable();
        assertTrue(FunctionVFDatum.Value.AVAR.equals( d.getValue() ));

        d = new FunctionVFDatum();
        d.makeFunction();
        d.makeAssignedVariable();
        assertTrue(FunctionVFDatum.Value.TOP.equals( d.getValue() ));

        d = new FunctionVFDatum();
        d.makeVariable();
        d.makeAssignedVariable();
        assertTrue(FunctionVFDatum.Value.VAR.equals( d.getValue() ));

        d = new FunctionVFDatum();
        d.makeVariable();
        d.makeFunction();
        d.makeAssignedVariable();
        assertTrue(FunctionVFDatum.Value.TOP.equals( d.getValue() ));
    }

    public void test_functionvfdatum_makefun() throws Exception
    {
        FunctionVFDatum d = new FunctionVFDatum();
        d.makeFunction();
        assertTrue(FunctionVFDatum.Value.FUN.equals( d.getValue() ));

        d = new FunctionVFDatum();
        d.makeAssignedVariable();
        d.makeFunction();
        assertTrue(FunctionVFDatum.Value.TOP.equals( d.getValue() ));

        d = new FunctionVFDatum();
        d.makeVariable();
        d.makeFunction();
        assertTrue(FunctionVFDatum.Value.TOP.equals( d.getValue() ));

        d = new FunctionVFDatum();
        d.makeFunction();
        d.makeVariable();
        d.makeFunction();
        assertTrue(FunctionVFDatum.Value.TOP.equals( d.getValue() ));
    }

    public void test_functionvftadum_isvariable() throws Exception
    {
        FunctionVFDatum d = new FunctionVFDatum();
        
        assertFalse(d.isVariable());

        d.makeVariable();
        assertTrue(d.isVariable());
        
        d.makeAssignedVariable();
        assertTrue(d.isVariable());

        d.makeFunction();
        assertFalse(d.isVariable());

        d = new FunctionVFDatum();
        d.makeFunction();
        assertFalse(d.isVariable());
    }
}