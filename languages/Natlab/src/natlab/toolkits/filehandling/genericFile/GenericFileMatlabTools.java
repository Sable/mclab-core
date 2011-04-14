package natlab.toolkits.filehandling.genericFile;

import java.io.File;

/**
 * provides some matlab related helpers using Generic Files
 */


public class GenericFileMatlabTools {
    /**
     * represents a file filter matching on matlab files
     */
    static final public GenericFileFilter MATLAB_FILE_FILTER = new GenericFileFilter() {
        public boolean accept(GenericFile file) {
            return file.getExtension().equalsIgnoreCase("m");
        }
    };
    
    static final public GenericFileFilter PRIVATE_DIRECTORY_FILTER = new GenericFileFilter() {
        public boolean accept(GenericFile file)
        {
            return file.isDir() && file.getName().toLowerCase().equals("private");
        }
    };
    
    static final public GenericFileFilter NON_PRIVATE_DIRECTORY_FILTER = new GenericFileFilter() {
        public boolean accept(GenericFile file)
        {
            return file.isDir() && !file.getName().toLowerCase().equals("private");
        }
    };
    
    
}
