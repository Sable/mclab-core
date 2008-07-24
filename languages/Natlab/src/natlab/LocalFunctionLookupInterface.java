package natlab;

import java.util.*;
import natlab.ast.*;

public interface LocalFunctionLookupInterface
{
    public Map<String, Function> getNested();
    public Map<String, Function> getSiblings();
    public LocalFunctionLookupInterface getParentFunction();
    public Function lookupFunction(String name);

    //helper function, expensive call
    public Map<String, Function> getVisible();
}