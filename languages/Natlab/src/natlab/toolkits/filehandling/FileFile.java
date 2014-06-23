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
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
/**
 * a FileFile is a GenericFile that uses a java.io.File as the underlying file object.
 */
public class FileFile extends GenericFile {
    private static final long serialVersionUID = 1L;
    File file;
    
    public FileFile(File file){
        this.file = file.getAbsoluteFile();
    }

    public FileFile(String name){
        this(new File(name));
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
    public GenericFile getChild(String name) {
        return new FileFile(new java.io.File(file,name));
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
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FileFile) {
            FileFile o = (FileFile) obj;
            return file.equals(o.file);
        }
        return false;
    }

    @Override
    public long lastModifiedDate() {
        return file.lastModified();
    }
    
    @Override
    public boolean exists() {
        return file.exists();
    }
}


