package natlab;

import java.util.*;

//This class is used to keep track of the number of symbols with the same 
//original name. This is used when splitting a symbol for generating a new 
//symbol name.

public class SymbolCount
{
    private int count;

    SymbolCount()
    {
        count = 0;
    }
    public String newSymbol()
    {
        count++;
        return Integer.toString(count);
    }
    public String getCountStr()
    {
        return Integer.toString(count);
    }
    public int getCountInt()
    {
        return count;
    }
}