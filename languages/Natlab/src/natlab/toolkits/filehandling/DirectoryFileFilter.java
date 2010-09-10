package natlab.toolkits.filehandling;

import java.io.*;
/**
 * filters only directories 
 */
public class DirectoryFileFilter implements FileFilter
{

    public boolean accept(File file)
    {
        return file.isDirectory();
    }

}
