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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import natlab.toolkits.filehandling.GenericFile;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

/* For a folder finds all the files that are accessible */
public class FolderHandler{
  private Map<String, GenericFile> classes = new HashMap<>(); //constructors?
  private Map<String, GenericFile> packages = new HashMap<>(); //overloaded methods besides constructors?
  private Map<String, GenericFile> functions = new HashMap<>();
  private Table<String, String, GenericFile> specializedFunction = HashBasedTable.create();
  private Map<String, GenericFile> privateFunctions = new HashMap<>();

  private FolderHandler addPath(GenericFile path){
    for (GenericFile f : path.listChildren()) {
      if (f.isClassDirectory()) {
        String className = f.getName().substring(1);
        for (GenericFile subfile : f.listChildren(GenericFile::isMatlabFile)) {
          String name = subfile.getNameWithoutExtension();
          if (name.equals(className)){
            classes.put(name, subfile);
          } else {
            specializedFunction.put(name, className, subfile);
          }
        }
      }
      if (f.isPackageDirectory()) {
        String packageName = f.getName().substring(1);
        packages.put(packageName, f);
      }
      if (f.isPrivateDirectory()) {
        for (GenericFile subfile: f.listChildren(GenericFile::isMatlabFile)) {
          privateFunctions.put(subfile.getNameWithoutExtension(), subfile);
        }
      }
      if (f.isMatlabFile()) {
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
    return specializedFunction.rowMap().values().stream()
        .filter(map -> map.containsKey(classname))
        .map(map -> map.get(classname))
        .collect(Collectors.toList());
  }

  public Map<String, GenericFile> lookupSpecializedAll(String name){
    if (specializedFunction.containsRow(name)) {
      return new HashMap<>(specializedFunction.rowMap().get(name));
    }
    return new HashMap<>();
  }

  public GenericFile lookupFunctions(String name){
    return functions.get(name);
  }
}
