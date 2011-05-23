package natlab.toolkits.path;

import java.io.Serializable;
import java.util.*;

import natlab.toolkits.filehandling.genericFile.*;
import natlab.toolkits.persistent.*;

/**
 * represents a cache of directories that is used by path objects
 * to keep track of the directories that hold .m files.
 * Directories may be stored over multiple sessions to ensure that not too much time
 * is spend searching through directories. It always uses cached directories,
 * to make sure file operations don't take too much time. Directories can
 * also be non-persistent, in which case they do not get stored.
 * 
 * All public methods are static, there is only one instance of this object.
 * The use is basically via put and get.
 * Note that the only way to get a Cached Directory object is to use this' class
 * static get method. Note also that a Cached Directory is a Generic File itself.
 * Thus if Cached Directories are given as arguments to these methods, they will
 * do nothing or return the same object.
 */
public class DirectoryCache extends PersistentlyCachedObject{
    private static final long serialVersionUID = 1L;
    public static final boolean DEFAULT_PERSISTENT_VALUE = false;
    
    /**
     * the number of milliseconds in a week
     */
    public static final long MS_PER_WEEK = 604800000;
    /**
     * how long a directory will survive in the directory cache untouched
     */
    public static final long DIRECTORY_EXPIRATION_TIME_MS = 8*MS_PER_WEEK;
    private static final String key = "";
    private HashMap<GenericFile,CachedDirectory> directories = new HashMap<GenericFile,CachedDirectory>();
    private transient static DirectoryCache cache = getCache();
    
    //private constructor
    private DirectoryCache() {
        super(key);
    }
    
    /**
     * returns the singleton instance for this object
     * - either loads the cache, or creates a new one
     */
    private static DirectoryCache getCache(){
        //if we have already loaded the cache, just return it
        if (cache != null) return cache;
        //we need to load the cache
        cache = PersistentlyCachedObject.load(key, DirectoryCache.class);
        //if the cache cannot be found, create a new one
        if (cache == null){
            cache = new DirectoryCache();
        } else {
            cache.setChanged(false); //if the cache already exists, set it to be unchanged
        }
        return cache;
    }
    
    
    /**
     * puts the given directory into the cache, but only if it is not there already
     * if the argument is already a CachedDirectory, the query gets ignored.
     * If new directory gets created, it will use the given persistent value
     */
    public static void put(GenericFile dir, boolean persistent){
        if (dir instanceof CachedDirectory) return;
        if (!cache.directories.containsKey(dir)){
            cache.directories.put(dir, new CachedDirectory(cache,dir,persistent));
            if (persistent) cache.setChanged(true);
        }
    }

    /**
     * same as put(GenericFile,boolean), but uses the default persistence value
     * as the second argument
     */
    public static void put(GenericFile dir){
        put(dir,DEFAULT_PERSISTENT_VALUE);
    }
    
    /**
     * Returns the directory object for the given file.
     * If this directory does not exist in this cache yet, it will get added to it
     * using put(GenericFile).
     * If the argument is already a CachedDirectory, returns the argument.
     */
    public static CachedDirectory get(GenericFile dir){
        if (dir instanceof CachedDirectory) return (CachedDirectory)dir;
        if (!cache.directories.containsKey(dir)){
            put(dir,DEFAULT_PERSISTENT_VALUE);
        }
        return cache.directories.get(dir);
    }
    
    @Override
    protected void onExit() {
        long t = System.currentTimeMillis();
        //go through and remove any 'old' directories, find whether changed is true
        LinkedList<GenericFile> toRemove = new LinkedList<GenericFile>();
        for (GenericFile k : directories.keySet()){
            CachedDirectory d = directories.get(k);
            //remove all old/nonpersistent dirs - but don't force resaving due to expiration
            if (!d.isPersistent() || d.getLastTouchedTime() + DIRECTORY_EXPIRATION_TIME_MS < t){
                toRemove.add(k);
            } else {
                setChanged(d.hasChangedSinceCreation());
            }
        }
        //actually remove directories, but only if we actually have a changed
        //otherwise the data won't be saved to the cache, anyway
        if (getChanged()){
            for (GenericFile r : toRemove){
                directories.remove(r);
            }
        }
    }
    
    @Override
    public String toString() {
        String s = "directory cache:<";
        for (GenericFile dir : directories.keySet()){
            s += "\n"+dir;
        }
        return s+">";
    }
    
    /**
     * returns a string representation of the cache
     */
    public static String getString(){
        return cache.toString();
    }
}


