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

import java.util.*;

import com.google.common.base.Splitter;

import natlab.NatlabPreferences;
import natlab.toolkits.filehandling.genericFile.*;


/**
 * This class represents an ordered set of directories that act as a Path in matlab
 * where files (*.m, *.mex, *.p) exist.
 * Does not model main, or builtins, or context.
 * Uses the Directory Cache to get quick access to directories.
 *
 *
 * TODO - move towards the folderhandler/context objects,
 * so all the logic of finding functions should be deprecated/removed.
 * This should only store the list of directories.
 * 
 * @author ant6n
 *
 */


public class MatlabPath extends AbstractPathEnvironment {
    ArrayList<CachedDirectory> directories = new ArrayList<CachedDirectory>();
    private boolean persistent;
    
    /**
     * the file extensions for matlab code files
     * - they are in the order of preference
     */
    private String[] codeFileExtensions = new String[]{"m"};
    
    /**
     * creates a path environment from the ';'-separated String of directories
     * if the persistent flag is set, the directories will be cached persistently 
     * (i.e. they will be more quickly available during the next VM session)
     */
    public MatlabPath(String path,boolean persistent){
        super(null);
        //System.out.println(path);
        this.persistent = persistent;
        if (path == null || path.length() == 0){
            return;
        }
        //put all directories in
        for (String s : Splitter.on(System.getProperty("path.separator")).split(path)) {
            GenericFile file = GenericFile.create(s);
            DirectoryCache.put(file,persistent);
            directories.add(DirectoryCache.get(file));
        }
    }
    
    /**
     * creates a path environment from the ';'-separated String of directories
     * this will not be cached persistently
     * @return
     */
    public MatlabPath(String path){
        this(path,false);
    }

    /**
     * creates a path from a single directory - can be used to do lookups
     * in current or private directories
     * this will not be cached persistently
     * @return
     */
    public MatlabPath(GenericFile aPath){
        super(null);
        GenericFile file = aPath;
        DirectoryCache.put(file,persistent);
        directories.add(DirectoryCache.get(file));
    }
    
    /**
     * factory method that returns a path object represented the matlab path stored in 
     * the natlab preferences
     */
    public static MatlabPath getMatlabPath(){
        return new MatlabPath(NatlabPreferences.getMatlabPath(),true);
    }
    /**
     * factory method that returns a path object represented the natlab path stored in 
     * the natlab preferences
     */
    public static MatlabPath getNatlabPath(){
        return new MatlabPath(NatlabPreferences.getNatlabPath(),true);
    }

    
    /**
     * returns the set of directories that are overloaded for the given class name
     * i.e. the the directories whose parents are in the path, and whose name
     * is @ + className
     * The directories are returned as non-persistent cached directories
     */
    public Collection<CachedDirectory> getAllOverloadedDirs(String className){
        ArrayList<CachedDirectory> odirs = new ArrayList<CachedDirectory>();
        for (CachedDirectory dir : directories){
            for (String s : dir.getChildDirNames()){
                if (s.equals("@"+className)){
                    GenericFile childDir = dir.getChild(s);
                    DirectoryCache.put(childDir,false);
                    odirs.add(DirectoryCache.get(childDir));
                }
            }
        }
        return odirs;
    }

    @Override
    public FunctionReference getMain() {
        return null;
    }
    
    
    @Override
    public FunctionReference resolve(String name, GenericFile context) {
        return resolveInDirs(name,directories);
    }
    
    
    @Override
    public FunctionReference resolve(String name, String className, GenericFile context) {
        return resolveInDirs(name,getAllOverloadedDirs(className));
    }

    
    public FunctionReference resolveInDirs(String name, Collection<CachedDirectory> dirs){
        //System.err.println(name+" "+dirs);
        boolean b = true;
        if (dirs == null) return null;
        for (String ext: codeFileExtensions){
            Iterator<CachedDirectory> i = dirs.iterator();
            for (CachedDirectory dir : dirs){
                if (dir.exists()){
                    if (dir.getChildFileNames().contains(name+"."+ext)){
                        return new FunctionReference(dir.getChild(name+"."+ext));
                    }
                }
            }
        }
        return null;
    }
    
    
    @Override
    public Map<String, FunctionReference> resolveAll(String name, GenericFile context) {
        // TODO Auto-generated method stub
        
        return null;
    }
    
    @Override
    public Map<String, FunctionReference> getAllOverloaded(String className, GenericFile cotntext) {
        Collection<CachedDirectory> oDirs = getAllOverloadedDirs(className);
        Collection<GenericFile> list = new ArrayList<GenericFile>();
        Map<String, FunctionReference> map = new HashMap<String,FunctionReference>();
        for (String ext : codeFileExtensions){
            for (CachedDirectory dir : oDirs){
                for (String filename : dir.getChildFileNames()){
                    if (filename.endsWith(ext)){
                        String name = filename.substring(0,filename.length()-ext.length()-1);
                        if (!map.containsKey(name)){
                            map.put(name, new FunctionReference(dir.getChild(filename)));
                        }
                    }
                }
            }
        }
        return map;
    }
    
    public List<FolderHandler> getAsFolderHandlerList(){
    	LinkedList<FolderHandler> list = new LinkedList<FolderHandler>();
    	for (CachedDirectory dir : directories){
    		list.add(FolderHandler.getFolderHandler(dir));
    	}
    	return list;
    }
    
}



