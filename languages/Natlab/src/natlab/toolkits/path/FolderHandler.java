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

import java.util.List;
import java.util.Map;

import natlab.toolkits.filehandling.GenericFile;
import natlab.toolkits.filehandling.GenericFileFilters;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

/* For a folder finds all the files that are accessible */
public class FolderHandler{
  private Map<String, GenericFile> classes = Maps.newHashMap(); //constructors?
  private Map<String, GenericFile> packages = Maps.newHashMap(); //overloaded methods besides constructors?
  private Map<String, GenericFile> functions = Maps.newHashMap();
  private Table<String, String, GenericFile> specializedFunction = HashBasedTable.create();
  private Map<String, GenericFile> privateFunctions = Maps.newHashMap();

  private FolderHandler addPath(GenericFile path){
    for (GenericFile f : path.listChildren()) {
      if (GenericFileFilters.CLASS_DIRECTORY.accept(f)) {
        String className = f.getName().substring(1);
        for (GenericFile subfile : f.listChildren(GenericFileFilters.MATLAB)) {
          String name = subfile.getNameWithoutExtension();
          if (name.equals(className)){
            classes.put(name, subfile);
          } else {
            specializedFunction.put(name, className, subfile);
          }
        }
      }
      if (GenericFileFilters.PACKAGE_DIRECTORY.accept(f)) {
        String packageName = f.getName().substring(1);
        packages.put(packageName, f);
      }
      if (GenericFileFilters.PRIVATE_DIRECTORY.accept(f)) {
        for (GenericFile subfile: f.listChildren(GenericFileFilters.MATLAB)) {
          privateFunctions.put(subfile.getNameWithoutExtension(), subfile);
        }
      }
      if (GenericFileFilters.MATLAB.accept(f)) {
        functions.put(f.getNameWithoutExtension(), f);
      }
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
    for (Map<String,GenericFile> map : specializedFunction.rowMap().values()) {
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
}
