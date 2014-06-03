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

package natlab.toolkits.filehandling;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * This represents file inside a zipped archive, which includes files inside a .jar
 * 
 * One may think of the zip/jar acrhive as a partition. The file basically
 * operates like a File Object within that partition.
 *
 */

public class ZippedFile extends GenericFile {
    //constants
    public final static char FILENAME_DELIMITER = '/';
    
    //static vars
    private static HashMap<File,ZipFile> archives = new HashMap<File,ZipFile>();
    private static HashMap<File,ZippedFile> roots = new HashMap<File,ZippedFile>();
    private static HashMap<File,HashMap<String,ZippedFile>> allZippedFiles 
        = new HashMap<File,HashMap<String,ZippedFile>>();
    
    //instance vars
    private ZipFile archive = null; //the zipfile where the files come from
    private ZippedFile parent = null; //the zip file that's the parent
    private String filename = null; //the file name, "" is the root
    private HashSet<ZippedFile> children; //the files inside the dir, null if not a dir
    private File archiveFile = null;
    
    //private constructors
    private ZippedFile(String filename,File archiveFile,ZipFile archive,ZippedFile parent,boolean isDir){
        this.archive = archive;
        this.filename = filename;
        this.parent = parent;
        this.archiveFile = archiveFile;
        if (isDir) children = new HashSet<ZippedFile>();
    }
    
    
    //public constructor
    public ZippedFile(String zipFile,String fileInsideZip) throws IOException{
        this((new File(zipFile)).getAbsoluteFile().getCanonicalFile(),fileInsideZip);
    }

    //public constructor
    public ZippedFile(File file,String fileInsideZip) throws IOException{
        //zip File is already loaded
        if (!archives.containsKey(file)){
            loadZipFile(file);
        }
        ZippedFile zf = allZippedFiles.get(file).get(fileInsideZip);
        if (zf != null){
            this.archive = zf.archive;
            this.children = zf.children;
            this.filename = zf.filename;
            this.parent = zf.parent;
            this.archiveFile = file;
        } else {
            throw new IOException();
        }
    }
    
    
    //loads a .zip/.jar file
    private void loadZipFile(File zipFile) throws IOException {
        ZipFile archive = new ZipFile(zipFile);
        Enumeration<? extends ZipEntry> ze = archive.entries();

        //load files from zipfile
        HashMap<String,ZippedFile> dirTree = new HashMap<String,ZippedFile>();
        while(ze.hasMoreElements()){
            ZipEntry z = ze.nextElement();
            if (!z.isDirectory()){
                ZippedFile zf = new ZippedFile(z.toString(), zipFile, archive, null, false);
                dirTree.put(zf.filename, zf);
                putParents(dirTree,zf);
            }
        }
        
        //put in static maps
        archives.put(zipFile,archive);
        roots.put(zipFile, dirTree.get(""));
        allZippedFiles.put(zipFile, dirTree);
    }
    
    //returns the zip entry for this file, or null if it doesn't exist
    private ZipEntry getZipEntry(){
        return archive.getEntry(filename);
    }
    
    //returns parent file of give file name, using the '/' char as delim
    //returns null for ""
    private String getParent(String file){
        if (file.equals("")) return null;
        char delim = FILENAME_DELIMITER;
        if (file.length() > 0 &&
            file.charAt(file.length()-1)==delim) file = file.substring(0,file.length()-1);
        int i = file.lastIndexOf(delim);
        if (i == -1) return "";
        return file.substring(0,i+1);
    }
    
    //auxiliary method for testing
    private void printTree(String file,HashMap<String,ZippedFile> tree,ZipFile archive){
        ZippedFile zf = tree.get(file);
        System.out.println("<"+file+">");
        if (zf.isDir()) for (ZippedFile cf : zf.children){
           printTree(cf.filename,tree,archive);
        }
    }
    
    //puts in the parents of the given file, and connects them to the children
    private void putParents(HashMap<String,ZippedFile> dirTree,ZippedFile file){
        String parent = getParent(file.filename);
        
        if (parent == null){ //parent is root,
            if ( !file.filename.equals("")){ // but file is not root itself
                if (!dirTree.containsKey("")){
                    dirTree.put("",new ZippedFile("", file.archiveFile, file.archive, null, true));
                }
                dirTree.get("").children.add(file);
            }
        } else { //parent is not root
            if (!dirTree.containsKey(parent.toString())){
                //construct parent and put in dir
                ZippedFile parentDir = new ZippedFile(parent.toString(), file.archiveFile, file.archive, null, true);
                dirTree.put(parent.toString(), parentDir);
                
                //recursively link/fill parents
                putParents(dirTree,parentDir);                
            }
            dirTree.get(parent.toString()).children.add(file);
        }
    }
    
    
    public boolean isDir(){
        ZipEntry entry = getZipEntry();
        if (entry == null) return false;
        return entry.isDirectory();
    }
    
    
    public ZippedFile getParent() {
        return this.parent;
    }

    public Reader getReader() throws IOException {
        ZipEntry entry = getZipEntry();
        if (entry != null){
            return new BufferedReader(
                    new InputStreamReader(archive.getInputStream(entry)));
        } else {
            throw new FileNotFoundException(filename+" in "+archive.getName()+" not found");
        }
    }

    
    
    /**
     * Closes the zip file in which this zip file resides.
     */
    public void closeZip(){
        //TODO
    }
    
    /**
     * closes all zip
     */
    public static void closeAllZips(){
        //TODO
    }



    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public Collection<GenericFile> listChildren() {
        // TODO Auto-generated method stub
        return null;
    }
    
    @Override
    public String getPath() {
        return archive.getName()+"!"+FILENAME_DELIMITER+filename;
    }
    
    @Override
    public ZippedFile getChild(String name) {
        try {
            return new ZippedFile(this.archiveFile, this.filename+FILENAME_DELIMITER+name);
        } catch (Exception e) {
        }
        return null;
    }
    
    @Override
    public int hashCode() {
        return this.archive.hashCode()*31 + this.filename.hashCode();
    }
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ZippedFile) {
            ZippedFile o = (ZippedFile) obj;
            return this.archive.equals(o.archive) && this.filename.equals(o.filename);
        }
        return false;
    }


    @Override
    public long lastModifiedDate() {
        ZipEntry entry = getZipEntry();
        return entry.getTime();
    }
    
    @Override
    public boolean exists() {
        return (getZipEntry() != null);
    }
}
