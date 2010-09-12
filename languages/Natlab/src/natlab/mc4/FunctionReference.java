package natlab.mc4;

import java.io.File;

/**
 * A function within a Matlab program can not uniquely identified by a name a lone
 * We add the source file as well
 *
 */


public class FunctionReference {
    File path;
    String name;

    public FunctionReference(String name, File path){
        this.path = path;
        this.name = name;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FunctionReference){
            FunctionReference ref = (FunctionReference)obj;
            return ref.name.equals(name) && ref.path.equals(path);
        } else return false;
    }
    
    @Override
    public String toString() {
        return "function "+name+"@"+path.getAbsolutePath();
    }
}


