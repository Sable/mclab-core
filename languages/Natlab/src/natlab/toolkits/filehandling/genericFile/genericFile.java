package natlab.toolkits.filehandling.genericFile;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.Collection;


/**
 * A generic file is a File object, but it may not necessarily represent a
 * file on the file directory. It may also represent a file inside a zip
 * or jar file. Thus files can be read from the Natlab.jar, and treated
 * the same way as files on the directory.
 * 
 * genericFile objects have to exist upon their creation.
 * genericFile objects are absolute, even if the filenames provided by the
 * constructors are not.
 * 
 * 
 * @author ant6n
 */

public abstract class genericFile {
    /**
     * returns the parent directory
     */
    abstract public genericFile getParent();
    
    /**
     * returns a reader to the contents of the file
     * @return a reader referring to the contents of the file
     * @throws IOException
     */
    abstract public Reader getReader() throws IOException;
    
    /**
     * @return true iff the generic file refers to a directory
     */
    abstract public boolean isDir();
    
    /**
     * Returns a list of all the children of this file.
     * If this generic file is not a directory, null is returned.
     * @return
     */
    abstract public Collection<genericFile> listChildren();
    
    /**
     * returns the name of this file
     * @return
     */
    abstract public String getFilename();
    
    /**
     * returns the file extension of this file.
     * @return
     */
    abstract public String getExtension();
    
    /**
     * returns the whole path of the zile
     */
    abstract public String getPath();
    
    public String toString() {
        return this.getClass().getSimpleName()+":"+getPath();
    }
    
        
    public static genericFile create(String filename){
        return null;
    }
    public static genericFile create(File file){
        return null;
    }
    public static genericFile create(URL url){
        return null;
    }
    
}
