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

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import natlab.toolkits.filehandling.GenericFile;

/**
 * represents a directory as stored in the directory cache
 * It is a generic file, so allows the same operations as usual directories
 * 
 * A Cached Directory is both lazy and eager - eager in the sense that it will
 * compute all necessary information about the directory and store it, lazy in the
 * sense that it will only compute it once it is accessed, and will only recompute
 * it if the directory has changed and if some time has passed since the last compute.
 */
public class CachedDirectory extends GenericFile implements Externalizable {
    private static final long serialVersionUID = 3L;
    public static final long TIME_BETWEEN_UPDATES_MS = 4000; //the information will be updated at most this often
    
    GenericFile directory;
    long lastModifiedDate;
    long lastTouchedDate; //the last time the directory info was actually updated
    HashSet<String> childFiles = new HashSet<String>();
    HashSet<String> childDirs = new HashSet<String>();
    private boolean exists = false;
    transient boolean directoryChangedSinceCreation = false;
    //transient boolean persistent = false; //should we cache this dir across sessions?

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        directory = (GenericFile)in.readObject();
        lastModifiedDate = in.readLong();
        int n = in.readInt();
        for (int i = 0; i < n; i++){ childFiles.add(in.readUTF()); }
        n = in.readInt();
        for (int i = 0; i < n; i++){ childDirs.add(in.readUTF()); }
        exists = in.readBoolean();
        lastTouchedDate = in.readLong();
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(directory);
        out.writeLong(lastModifiedDate);
        out.writeInt(childFiles.size());
        for (String s : childFiles){ out.writeUTF(s); }
        out.writeInt(childDirs.size());
        for (String s : childDirs){ out.writeUTF(s); }
        out.writeBoolean(exists);
        out.writeLong(lastTouchedDate);
    }

    /**
     * public empty constructor, only to be used by the serialization mechanism
     * stores null as its directory.
     */
    public CachedDirectory(){
        directory = null;
    }

    /**
     * constructor takes a directory file
     * This should only be called by the DirectoryCache, thus an object of that
     * type has to be supplied (which cannot be created elsewhere).
     * Construct a Cached Directory via the directory cache.
     */
    protected CachedDirectory(DirectoryCache cache, GenericFile directory){
        if (cache == null) 
            throw new UnsupportedOperationException("a CachedDirectory can only be created by the DirectoryCache");
        this.directory = directory;
        this.lastModifiedDate = 0;
        this.lastTouchedDate = 0;
        //this.persistent = persistent;
    }
    
    /**
     * updates the stored information about the directory
     */
    public void update(){
        long current = System.currentTimeMillis();
        if (current - lastTouchedDate > TIME_BETWEEN_UPDATES_MS){
            //we need to update
            lastTouchedDate = current;
            //check whether the directory has been modified, or doesn't exist anymore
            long date = directory.lastModifiedDate();
            if (date != lastModifiedDate || (lastModifiedDate == 0)){
                if (!directory.exists()){
                    exists = false;
                    childFiles = null;
                    childDirs = null;
                } else {
                    exists = true;                    
                    Collection<? extends GenericFile> children = directory.listChildren();
                    for (GenericFile f : children){
                        if (f.isDir()){
                            childDirs.add(f.getName());
                        } else {
                            childFiles.add(f.getName());
                        }
                    }
                    lastModifiedDate = date;
                }
                this.directoryChangedSinceCreation = true;
            }
        }
    }
    
    /**
     * returns the file object of this directory
     */
    public GenericFile getDirectoryFile(){
        return directory;
    }
    
    /**
     * returns true if the directory still exists
     */
    public boolean exists(){
        update();
        return this.exists;
    }
    
    /**
     * returns true if the underlying data the directory stores has changed since
     * creation of the object.
     */
    public boolean hasChangedSinceCreation(){
        return this.directoryChangedSinceCreation;
    }
    
    /**
     * returns true if this directory is to be stored in the directory cache
     */
    //public boolean isPersistent(){
    //    return persistent;
    //}
    
    /**
     * gets the last time this directory was updated
     */
    protected long getLastTouchedTime(){
        return lastTouchedDate;
    }

    /**
     * all cached directories referring to the same underlying directory should
     * have the same instance
     */
    @Override
    public boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public String getName() {
        return directory.getName();
    }

    @Override
    public GenericFile getParent() {
        return directory.getParent();
    }
    
    @Override
    public GenericFile getChild(String name) {
        return directory.getChild(name);
    }
    
    @Override
    public String getPath() {
        return directory.getPath();
    }
    
    /**
     * should throw an error
     */
    @Override
    public Reader getReader() throws IOException {
        return directory.getReader();
    }

    @Override
    public int hashCode() {
        return directory.hashCode();
    }

    /**
     * returns true iff this file exists and is a dir
     * - this info is not cached, and will be forwarded to the underlying Generic File
     */
    @Override
    public boolean isDir() {
        return directory.isDir();
    }

    @Override
    public long lastModifiedDate() {
        update();
        return lastModifiedDate;
    }

    @Override
    public Collection<GenericFile> listChildren() {
        update();
        if (!isDir()) return null;
        Collection<GenericFile> list = listChildDirs();
        list.addAll(listChildFiles());
        return list;
    }
    
    /**
     * like listChildren, except the list only contains directories
     */
    public Collection<GenericFile> listChildDirs() {
        update();
        return getChildList(childDirs);
    }
    
    public Collection<GenericFile> listChildFiles() {
        update();
        return getChildList(childFiles);
    }
    
    
    
    
    /**
     * given a collection of Strings that refer to child names, returns 
     * the list of actual child files - or null if this is not a directory
     */
    private Collection<GenericFile> getChildList(Collection<String> children){
        update();
        if (!isDir()) return null;
        List<GenericFile> list = new ArrayList<GenericFile>();
        for (String s : children){
            list.add(getChild(s));
        }
        return list;
    }
    
    protected HashSet<String> getChildDirNames(){
        update();
        return childDirs;
    }
    protected HashSet<String> getChildFileNames(){
        update();
        return childFiles;
    }
}