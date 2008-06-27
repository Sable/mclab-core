package natlab;

import junit.framework.TestCase;

public class SymbolTableFailTests extends TestCase
{
    public void test_symboltablefail_insertDuplicate() throws Exception
    {
        SymbolTableScope st = new SymbolTableScope();
        SymbolTableEntry se1 = new SymbolTableEntry("a");
        SymbolTableEntry se2 = new SymbolTableEntry("a","a2");

        st.addSymbol(se1);
        assertFalse(st.addSymbol(se2));
    }
    public void test_symboltablefail_removenonexistent()
    {
        SymbolTableScope st = new SymbolTableScope();
        assertNull(st.getSymbolById("a"));
    }
}

        