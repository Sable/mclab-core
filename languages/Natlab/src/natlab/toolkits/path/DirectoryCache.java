// =========================================================================== //
//                                                                             //
// Copyright 2011 Anton Dubrau and McGill University.                          //
//                                                                             //
//   Licensed under the Apache License, Version 2.0 (the "License");           //
//   you may not use this file except in compliance with the License.          //
//   You may obtain a copy of the License at                                   //
//                                                                             //
//       http://www.apache.org/licenses/LICENSE-2.0                            //
//                                                                             //
//   Unless required by applicable law or agreed to in writing, software       //
//   distributed under the License is distributed on an "AS IS" BASIS,         //
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  //
//   See the License for the specific language governing permissions and       //
//  limitations under the License.                                             //
//                                                                             //
// =========================================================================== //

package natlab.toolkits.path;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;

import natlab.toolkits.filehandling.GenericFile;
import natlab.utils.PersistentlyCachedObject;

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
    // the persistent directories
    private HashMap<GenericFile,CachedDirectory> directories = new HashMap<GenericFile,CachedDirectory>();
    // the transient directories
    transient private HashMap<GenericFile,SoftReference<CachedDirectory>> transientDirectories = 
        new HashMap<GenericFile,SoftReference<CachedDirectory>>();
    private transient static DirectoryCache cache = getCache();
    
    //private constructor
    private DirectoryCache() {
        super(key);
    }
    
    private void readObject(java.io.ObjectInputStream in)
    throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        transientDirectories = new HashMap<GenericFile,SoftReference<CachedDirectory>>();
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
        }
        return cache;
    }
    
    
    /**
     * returns true if the given directory is in this cache, or if the given
     * file is a cached diretory
     */
    public static boolean contains(GenericFile dir){
        return (dir instanceof CachedDirectory) 
            || cache.directories.containsKey(dir)
            || cache.transientDirectories.containsKey(dir);
    }
    
    
    
    /**
     * puts the given directory into the cache, but only if it is not there already
     * if the argument is already a CachedDirectory, the query gets ignored.
     * If new directory gets created, it will use the given persistent value
     */
    public static CachedDirectory put(GenericFile dir, boolean persistent){
        if (dir instanceof CachedDirectory) return (CachedDirectory)dir;
        if (!cache.directories.containsKey(dir) && !cache.transientDirectories.containsKey(dir)){
            CachedDirectory result = new CachedDirectory(cache,dir);
            if (persistent){
                cache.directories.put(dir, result);
                cache.setChanged(true);
                return result;
            } else {
                cache.transientDirectories.put(dir, 
                        new SoftReference<CachedDirectory>(result));
                return result;
            }
        } else {
            return get(dir);
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
     * same as put(GenericFile,false), i.e. puts the directory in the cache in a
     * non persistent way
     */
    public static CachedDirectory putTransient(GenericFile dir){
        return put(dir,false);
    }

    /**
     * Returns the directory object for the given file.
     * If this directory does not exist in this cache yet, it will get added to it
     * using put(GenericFile).
     * If the argument is already a CachedDirectory, returns the argument.
     */
    public static CachedDirectory get(GenericFile dir){
        if (dir instanceof CachedDirectory) return (CachedDirectory)dir;
        if (cache.transientDirectories.containsKey(dir)){
            CachedDirectory adir = cache.transientDirectories.get(dir).get();
            if (adir == null){
                adir = new CachedDirectory(cache,dir);                
                cache.transientDirectories.put(dir, new SoftReference<CachedDirectory>(adir));
                return adir;
            }
            return adir;
        }
        if (!cache.directories.containsKey(dir)){
            put(dir,DEFAULT_PERSISTENT_VALUE);
        }
        return cache.directories.get(dir);
    }
    
    @Override
    protected void onExit() {
        long t = System.currentTimeMillis();
        //go through and remove any 'old' directories, 
        //but only when changed is true
        if (getChanged()){
            Iterator<GenericFile> i = directories.keySet().iterator();
            while(i.hasNext()){
                CachedDirectory d = directories.get(i.next());
                //remove all old dirs - but don't force resaving due to expiration
                if (d.getLastTouchedTime() + DIRECTORY_EXPIRATION_TIME_MS < t){
                    i.remove(); 
                }
            }
        }
    }
    
    @Override
    public String toString() {
        String s = "directory cache:<";
        for (GenericFile dir : directories.keySet()){
            s += "\n"+dir;
        }
        for (GenericFile dir : transientDirectories.keySet()){
            s += "\n"+dir;
        }
        return s+">";
    }
    
    public static void printCache(){
        System.out.println(cache);
    }
    
    /**
     * returns a string representation of the cache
     */
    public static String getString(){
        return cache.toString();
    }
    
    public static void touchAll(){
        for (CachedDirectory d : cache.directories.values()){
            d.update();
        }
    }
    
    public static void main(String[] args) {
        DirectoryCache.putTransient(GenericFile.create("."));
        DirectoryCache.printCache();
        System.out.println(DirectoryCache.get(GenericFile.create(".")));        
    }
}


