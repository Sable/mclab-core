package natlab;

import junit.framework.TestCase;
import java.util.*;

public class SymbolTableSanityTests extends TestCase
{
    public void test_symboltablesanity_insertRemoveEqual() throws Exception
    {
        SymbolTableScope st = new SymbolTableScope();
        SymbolTableEntry se = new SymbolTableEntry("a");

        st.addSymbol(se);
        assertEquals(se,st.getSymbolById("a"));
    }

    public void test_Syboltablesanity_splitnumbering() throws Exception
    {
        SymbolTableScope st = new SymbolTableScope();
        SymbolTableEntry se1 = new SymbolTableEntry("a");
        SymbolTableEntry se2;

        st.addSymbol(se1);
        se2 = st.splitSymbol(se1);

        assertEquals(se1.getOriginal(), se2.getOriginal());
        assertEquals("a1", se2.getSymbol());
        assertEquals(se2,st.getSymbolById("a1"));
        HashMap<String, SymbolTableEntry> otable = st.getSymbolsByOName("a");
        assertEquals(se2, otable.get("a1"));
    }
}
