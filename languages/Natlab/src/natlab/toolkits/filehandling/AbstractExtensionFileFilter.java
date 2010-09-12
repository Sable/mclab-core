package natlab.toolkits.filehandling;

import java.io.*;

/**
 *
 */
public abstract class AbstractExtensionFileFilter implements FileFilter, FilenameFilter
{

    public abstract String[] getOkFileExtensions();

    public boolean accept(File file)
    {
        for (String extension : getOkFileExtensions()){
            if (file.getName().toLowerCase().endsWith(extension)){
                return true;
            }
        }
        return false;
    }
    
    public boolean accept(File dir, String name)
    {
        for (String extension : getOkFileExtensions()){
            if (name.toLowerCase().endsWith(extension)){
                return true;
            }
        }
        return false;
    }


}
