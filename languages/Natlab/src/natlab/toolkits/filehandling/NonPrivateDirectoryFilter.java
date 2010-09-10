package natlab.toolkits.filehandling;

import java.io.*;


/**
 * filters non private directories
 */
public class NonPrivateDirectoryFilter extends DirectoryFileFilter
{

    public boolean accept(File file)
    {
        return super.accept(file) && !PrivateDirectoryFileFilter.isPrivate(file);
    }

}
