package natlab.toolkits.filehandling.genericFile;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;


/**
 * A generic file is a File object, but it may not necessarily represent a
 * file on the file directory. It may also represent a file inside a zip
 * or jar file. Thus files can be read from the Natlab.jar, and treated
 * the same way as files in directories.
 * 
 * genericFile objects are absolute, even if the filenames provided by the
 * constructors are not. (is this a good idea?)
 * 
 * 
 * @author ant6n
 */

public abstract class GenericFile {
    /**
     * returns the parent directory
     */
    abstract public GenericFile getParent();
    
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
    abstract public Collection<GenericFile> listChildren();
    
    
    /**
     * Returns a list of all the children that are accepted by the filter
     * returns null if the GenericFile is not a dir, or some exception occurs
     */
    public Collection<GenericFile> listChildren(GenericFileFilter filter){
        ArrayList<GenericFile> list = new ArrayList<GenericFile>();
        if (list == null) return null;
        for (GenericFile file : listChildren()){
            if (filter.accept(file)) list.add(file);
        }
        return list;
    }
    
    
    
    /**
     * returns the name of this file
     * @return
     */
    abstract public String getName();
    
    /**
     * returns the file extension of this file.
     * Returns an empty String if there is no file extension.
     * @return
     */
    public String getExtension() {
        String name = getName();
        if (name == null) return "";
        int pos = name.lastIndexOf('.');
        if (pos == -1) return "";
        return name.substring(pos+1);
   }
    
    /**
     * returns the whole path of the file
     */
    abstract public String getPath();
    
    public String toString() {
        return this.getClass().getSimpleName()+":"+getPath();
    }
    
        
    public static GenericFile create(String filename){
        return new FileFile(filename); //TODO
    }
    public static GenericFile create(File file){
        return null;
    }
    public static GenericFile create(URL url){
        return null;
    }
    
    
    @Override
    abstract public int hashCode();
}
