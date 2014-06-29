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
import natlab.toolkits.filehandling.GenericFile;

/**
 * A function or script within a Matlab program can not be uniquely identified by a name alone
 * We need a source file as well
 * 
 * This may refer to a function or script or class inside a matlab file on the path,
 * or a builtin
 *
 * This class is immutable
 * 
 * TODO - this should maybe store what class it belongs to
 */

public class FunctionReference {
  public final GenericFile path;
  public final String name;
  public final ReferenceType referenceType;
  public final boolean isBuiltin;
  
  private FunctionReference(String name, GenericFile path, boolean isBuiltin, ReferenceType type) {
    this.name = name;
    this.path = path;
    this.isBuiltin = isBuiltin;
    this.referenceType = type;
  }

  /**
   * creates a Function Reference referring to a function or script inside
   * a matlab file - it will refer to the funciton or script with the same name as the given
   * one
   */
  public FunctionReference(GenericFile path) {
    this(path.getNameWithoutExtension(), path);
  }

  public FunctionReference(GenericFile path, ReferenceType type) {
    this(path.getNameWithoutExtension(), path, false, type);
  }

  /**
   * creates a Function Reference referring to a function inside a matlab file
   * @param name the name of the function
   * @param path the path of the function (as an absolute File)
   */
  public FunctionReference(String name, GenericFile path) {
    this(name, path, false, ReferenceType.UNKNOWN);
  }

  public FunctionReference(GenericFile path, ast.Function f, ReferenceType type) {
    this(f.getName().getID(), path, false, type);
  }

  /**
   * creates a Function Reference referring to a builtin matlab function
   * @param name the name of the builtin
   */
  public FunctionReference(String name) {
    this(name, null, true, ReferenceType.UNKNOWN);
  }

  /**
   * returns the name of the function
   */
  public String getName() {
    return name;
  }

  /**
   * returns the file where the Function which this FunctionReference refers
   * to resides, or null if this file doesn't exist (for builtins).
   */
  public GenericFile getFile() {
    return path;
  }

  /**
   * returns whether this function refers to a builtin
   */
  public boolean isBuiltin() {
    return isBuiltin;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof FunctionReference) {
      FunctionReference ref = (FunctionReference) obj;
      if (isBuiltin) {
        return ref.isBuiltin && ref.name.equals(name);
      } else {
        return !ref.isBuiltin && ref.name.equals(name) && ref.path.equals(path);
      }
    }
    return false;
  }

  @Override
  public String toString() {
    return String.format("%s@%s", name, isBuiltin ? "builtin" : path);
  }

  @Override
  public int hashCode() {
    return name.hashCode() + (isBuiltin ? 0 : path.hashCode());
  }

  public enum ReferenceType{
    UNKNOWN, /* path/name.m. It can be a script, functionlist or class */ 
    PACKAGE, /* +name */ 
    CLASS_CONSTRUCTOR, /* @type/type.m */
    OVERLOADED,  /* @type/name */
    PRIVATE, /* private/name.m */	 
    NESTED, /* Nested function */
    SUBFUNCTION, /* When Multiple functions are in the same file. NOT the first/main  function */
  }
}
