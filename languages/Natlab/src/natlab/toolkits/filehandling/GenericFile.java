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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import natlab.LookupFile;
import natlab.toolkits.path.FileEnvironment;


/**
 * A generic file is a File object, but it may not necessarily represent a
 * file on the file directory. It may for example also represent a file inside a zip
 * or jar file. Thus files can be read from the Natlab.jar, and treated
 * the same way as files in directories.
 * 
 * 
 * 
 * @author ant6n
 */

public abstract class GenericFile implements Serializable {
    private static final long serialVersionUID = 1L;


    /**
     * returns the parent directory
     */
    abstract public GenericFile getParent();
    
    /**
     * Returns the child file for the given file, as if this file
     * represents a directory.
     * @param filename
     * @return
     */
    abstract public GenericFile getChild(String name);
    
    
    /**
     * returns a reader to the contents of the file
     * @return a reader referring to the contents of the file
     * @throws IOException
     */
    abstract public Reader getReader() throws IOException;
    
    public String getContents() throws IOException {
      return CharStreams.toString(getReader());
    }
    
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

    public GenericFile getAnyMatlabFile() {
      if (!isDir()) {
        return null;
      }
      Collection<GenericFile> files = listChildren(GenericFile::isMatlabFile);
      if (files.isEmpty()) {
        return null;
      }
      return files.iterator().next();
    }

    /**
     * Returns a list of all the children that are accepted by the filter
     * returns null if the GenericFile is not a dir, or some exception occurs
     */
    public Collection<GenericFile> listChildren(Predicate<GenericFile> filter){
      return listChildren().stream()
          .filter(filter)
          .collect(Collectors.toList());
    }

    /**
     * returns the name of this file
     * @return
     */
    abstract public String getName();
    
    public String getNameWithoutExtension() {
      return Files.getNameWithoutExtension(getName());
    }
    
    /**
     * returns the file extension of this file.
     * Returns an empty String if there is no file extension.
     * @return
     */
    public String getExtension() {
        return Files.getFileExtension(getName());
   }

    /**
     * returns the whole path of the file
     */
    abstract public String getPath();
    
    public String toString() {
        return this.getClass().getSimpleName()+":"+getPath();
    }
    
    public boolean isMatlabFile() {
      return getExtension().equalsIgnoreCase("m");
    }
    
    public boolean isPrivateDirectory() {
      return isDir() && getName().equalsIgnoreCase("private");
    }
    
    public boolean isPackageDirectory() {
      return isDir() && getName().startsWith("+");
    }
    
    public boolean isClassDirectory() {
      return isDir() && getName().startsWith("@");
    }
    
    /**
     * returns the last modified date of the file
     */
    public abstract long lastModifiedDate();
        
    /**
     * returns true if the file exists
     */
    public abstract boolean exists();
    
        
    public static GenericFile create(String filename){
        return new FileFile(filename);
    }
    public static GenericFile create(File file){
        return new FileFile(file);
    }
    public static GenericFile create(URL url){
        return null;
    }
    @Override
    abstract public boolean equals(Object obj);

    public FunctionOrScriptQuery getFunctionOrScriptQuery() {
      FunctionOrScriptQuery baseline = LookupFile.getFunctionOrScriptQueryObject();
      FunctionOrScriptQuery env = new FileEnvironment(this).getFunctionOrScriptQuery(this);
      return new FunctionOrScriptQuery() {
        @Override public boolean isFunctionOrScript(String name) {
          return env.isFunctionOrScript(name) || baseline.isFunctionOrScript(name);
        }

        @Override public boolean isPackage(String name) {
          return env.isPackage(name) || baseline.isPackage(name);
        }
      };
    }
}
