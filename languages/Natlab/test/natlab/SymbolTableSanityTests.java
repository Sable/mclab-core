package natlab;

import junit.framework.TestCase;

public class SymbolTableSanityTests extends TestCase
{
    public void test_symboltablesanity_insertRemoveEqual() throws Exception
    {
        SymbolTableScope st = new SymbolTableScope();
        SymbolTableEntry se = new SymbolTableEntry("a");

        st.addSymbol(se);
        assertEquals(se,st.getSymbolById("a"));
    }
}
