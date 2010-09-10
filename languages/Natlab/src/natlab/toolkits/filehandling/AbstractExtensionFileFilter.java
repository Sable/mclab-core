package natlab.toolkits.filehandling;

import java.io.*;

/**
 *
 */
public abstract class AbstractExtensionFileFilter implements FileFilter, FilenameFilter
{

    protected String[] okFileExtensions = new String[0];

    public boolean accept(File file)
    {
        for (String extension : okFileExtensions){
            if (file.getName().toLowerCase().endsWith(extension)){
                return true;
            }
        }
        return false;
    }
    
    public boolean accept(File dir, String name)
    {
        for (String extension : okFileExtensions){
            if (name.toLowerCase().endsWith(extension)){
                return true;
            }
        }
        return false;
    }


}
