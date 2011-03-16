package natlab.toolkits.filehandling.genericFile;

import java.io.*;
import java.util.*;
import java.util.zip.*;

public class ZippedFile extends genericFile {
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
    
    //private constructors
    private ZippedFile(String filename,ZipFile archive,ZippedFile parent,boolean isDir){
        this.archive = archive;
        this.filename = filename;
        this.parent = parent;
        if (isDir) children = new HashSet<ZippedFile>();
    }
    
    
    //public constructor
    public ZippedFile(String zipFile,String fileInsideZip) throws IOException{
        File file = (new File(zipFile)).getAbsoluteFile().getCanonicalFile();
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
        } else {
            throw new IOException();
        }
        
    }
    
    private void loadZipFile(File zipFile) throws IOException {
        ZipFile archive = new ZipFile(zipFile);
        Enumeration<? extends ZipEntry> ze = archive.entries();

        //load files from zipfile
        HashMap<String,ZippedFile> dirTree = new HashMap<String,ZippedFile>();
        while(ze.hasMoreElements()){
            ZipEntry z = ze.nextElement();
            if (!z.isDirectory()){
                ZippedFile zf = new ZippedFile(z.toString(), archive, null, false);
                dirTree.put(zf.filename, zf);
                putParents(dirTree,zf);
            }
        }
        
        //put in static maps
        archives.put(zipFile,archive);
        roots.put(zipFile, dirTree.get(""));
        allZippedFiles.put(zipFile, dirTree);
    }
    
    //returns parent file of give file name, using the '/' char as delim
    //returns null for ""
    private String getParent(String file){
        if (file.equals("")) return null;
        char delim = '/';
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
                    dirTree.put("",new ZippedFile("", file.archive, null, true));
                }
                dirTree.get("").children.add(file);
            }
        } else { //parent is not root
            if (!dirTree.containsKey(parent.toString())){
                //construct parent and put in dir
                ZippedFile parentDir = new ZippedFile(parent.toString(), file.archive, null, true);
                dirTree.put(parent.toString(), parentDir);
                
                //recursively link/fill parents
                putParents(dirTree,parentDir);                
            }
            dirTree.get(parent.toString()).children.add(file);
        }
    }
    
    
    public boolean isDir(){
        return (children != null);
    }
    
    
    public genericFile getParent() {
        return this.parent;
    }

    public Reader getReader() throws IOException {
        return new BufferedReader(
                new InputStreamReader(archive.getInputStream(archive.getEntry(filename))));
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
    public String getExtension() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public String getFilename() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public Collection<genericFile> listChildren() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public String getPath() {
        return archive.getName()+"!/"+filename;
    }
}
