package natlab;

import junit.framework.TestCase;


public class SymbolTablePassTests extends TestCase
{
    public void test_symboltablepass_insert1() throws Exception
    {
        SymbolTableScope st = new SymbolTableScope();
        SymbolTableEntry se = new SymbolTableEntry("a");
        assertEquals(true, st.addSymbol(se));
    }
    public void test_symboltablepass_insert2() throws Exception
    {
        SymbolTableScope st = new SymbolTableScope();
        SymbolTableEntry se1 = new SymbolTableEntry("a");
        SymbolTableEntry se2 = new SymbolTableEntry("b","a");
        assertTrue( st.addSymbol(se1));
        assertTrue( st.addSymbol(se2));
    }
    public void test_symboltablepass_insertremove() throws Exception
    {
        SymbolTableScope st = new SymbolTableScope();
        SymbolTableEntry se1 = new SymbolTableEntry("a");
        st.addSymbol(se1);
        assertNotNull(st.getSymbolById("a"));
    }
}
