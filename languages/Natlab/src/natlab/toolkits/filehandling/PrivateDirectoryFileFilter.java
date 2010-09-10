package natlab.toolkits.filehandling;

import java.io.*;


/**
 * filters private directories
 */
public class PrivateDirectoryFileFilter extends DirectoryFileFilter
{

    public boolean accept(File file)
    {
        return super.accept(file) && isPrivate(file);
    }

    public static boolean isPrivate(File file)
    {
        return file.getName().toLowerCase().equals("private");
    }

}
