/*
Copyright 2011 Soroush Radpour and McGill University.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
 */

package natlab.toolkits.path;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import natlab.toolkits.filehandling.genericFile.FileFile;
import natlab.toolkits.filehandling.genericFile.GenericFile;

import com.google.common.base.Predicate;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

/* For a folder finds all the files that are accessible */
public class FolderHandler{
  private final Set<String> ACCEPTED_EXTENSIONS= ImmutableSet.of(".m");
  private Map<String, GenericFile> classes = Maps.newHashMap(); //constructors?
  private Map<String, GenericFile> packages = Maps.newHashMap(); //overloaded methods besides constructors?
  private Map<String, GenericFile> functions = Maps.newHashMap();
  private Table<String, String, GenericFile> specializedFunction = HashBasedTable.create();
  private Map<String, GenericFile> privateFunctions = Maps.newHashMap();

  private Collection<GenericFile> getFilteredChildren(GenericFile dir){
    return Lists.newLinkedList(Iterables.filter(dir.listChildren(), new Predicate<GenericFile>() {
      @Override public boolean apply(GenericFile file) {
        return !file.isDir() && ACCEPTED_EXTENSIONS.contains(file.getExtensionComplete());
      }
    }));
  }

  public static String getFileName(GenericFile f){
    String extension = f.getExtensionComplete();
    return f.getName().substring(0, f.getName().length() - extension.length());
  }

  private FolderHandler addPath(GenericFile path){
    for (GenericFile f: path.listChildren()){
      String fname = f.getName();	
      if (f.isDir()){
        if (fname.charAt(0)=='@'){
          fname=fname.substring(1);
          for (GenericFile subfile:getFilteredChildren(f)){
            if (getFileName(subfile).equals(fname)){
              classes.put(getFileName(subfile), subfile);
            }
            else {
              specializedFunction.put(getFileName(subfile), fname, subfile);
            }
          }
        }
        if (fname.charAt(0)=='+'){
          fname=fname.substring(1);
          packages.put(fname, f);
        }
        if (fname.toLowerCase().equals("private")){
          for (GenericFile subfile:getFilteredChildren(f)){
            privateFunctions.put(getFileName(subfile), subfile);
          }
        }
      }
      if (ACCEPTED_EXTENSIONS.contains(f.getExtensionComplete()))
        functions.put(getFileName(f), f);
    }
    return this;
  }

  public static FolderHandler getFolderHandler(GenericFile folder) {
    return new FolderHandler().addPath(folder);
  }

  public GenericFile lookupPrivateFunctions(String name){
    return privateFunctions.get(name);
  }

  public GenericFile lookupPackage(String name){
    return packages.get(name);
  }

  public GenericFile lookupClasses(String name){
    return classes.get(name);
  }

  public GenericFile lookupSpecialized(String name, String type){
    return specializedFunction.get(name, type);
  }

  public List<GenericFile> getAllSpecialized(String classname){
    List<GenericFile> list = Lists.newArrayList();
    for (Map<String,GenericFile> map : specializedFunction.rowMap().values()){
      if (map.containsKey(classname)) {
        list.add(map.get(classname));
      }
    }
    return list;
  }

  public Map<String, GenericFile> lookupSpecializedAll(String name){
    if (specializedFunction.containsRow(name)) {
      return Maps.newHashMap(specializedFunction.rowMap().get(name));
    }
    return Maps.newHashMap();
  }

  public GenericFile lookupFunctions(String name){
    return functions.get(name);
  }

  public static void main(String[] args){
    FolderHandler self = FolderHandler.getFolderHandler(new FileFile("/home/soroush/Examples/PathEx"));	
    System.out.println(""+self.classes+ self.packages+ self.functions+self.specializedFunction);
  }
}
