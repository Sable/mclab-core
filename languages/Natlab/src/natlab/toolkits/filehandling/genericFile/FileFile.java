package natlab.toolkits.filehandling.genericFile;

import java.io.*;
import java.util.*;

public class FileFile extends GenericFile {
    File file;
    
    public FileFile(File file){
        this.file = file.getAbsoluteFile();
    }
    
    @Deprecated
    public java.io.File getFileObject(){
        return file;
    }
    
    public FileFile(String name){
        file = new File(name);
    }
    
    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public FileFile getParent() {
        return new FileFile(file.getParentFile());
    }

    @Override
    public String getPath() {
        return file.getPath();
    }

    @Override
    public Reader getReader() throws IOException {
        return new BufferedReader(new FileReader(file));
    }

    @Override
    public boolean isDir() {
        return file.isDirectory();
    }

    @Override
    public Collection<GenericFile> listChildren() {
        File[] files = file.listFiles();
        if (files == null) return null;
        Collection<GenericFile> list = new ArrayList<GenericFile>();
        for (File f : files){
            list.add(new FileFile(f));
        }
        return list;
    }

    
    @Override
    public int hashCode() {
        return file.hashCode();
    }
}
