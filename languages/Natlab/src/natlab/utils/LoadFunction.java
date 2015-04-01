// =========================================================================== //
//                                                                             //
// Copyright 2011 Jesse Doherty and McGill University.                         //
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

package natlab.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import ast.Expr;
import ast.StringLiteralExpr;

/**
 * Some helper methods for dealing with the load function.
 */
public class LoadFunction {
  /**
   * Determines what the load function can load.
   * Returns either a set of variables to load or null. If it
   * returns null then it is unknown what variables are loaded. This
   * is a conservative approximation. If a single variable with a
   * wild card or regular expressions are used then it returns
   * null. This might be over an approximation.
   */
  public static Set<String> loadWhat(ast.List<Expr> args) {
    if (args.getNumChild() < 2) {
      return null;
    }

    String arg0 = loadArgument(args.getChild(0));
    String arg1 = loadArgument(args.getChild(1));
    if (arg0 == null || arg1 == null) {
      return null;
    }

    Set<String> set = handleAsciiFlag(arg0, arg1);
    if (set != null) {
      return set;
    }
    set = handleAsciiFlag(arg1, arg0);
    if (set != null) {
      return set;
    }

    set = new HashSet<>();

    boolean noFlag = true;
    String filename = arg0;
    if (arg0.equals("-mat")) {
      noFlag = false;
      filename = arg1;
    }
    if (arg1.equals("-mat")) {
      noFlag = noFlag && true;
    }
    filename = genVarName(filename);

    if (noFlag) {
      set.add(arg1);
    }

    for (int i = 2; i < args.getNumChild(); i++) {
      String arg = loadArgument(args.getChild(i));
      if (arg == null) {
        return null;
      }

      Set<String> asciiSet = handleAsciiFlag(arg, filename);
      if (asciiSet != null) {
        return asciiSet;
      }

      if (!arg.equals("-mat")) {
        set.add(arg);
      }
    }
    return set;
  }
  /**
   * Deals with the ascii flag. Returns a set to return if it is an
   * ascii flag, otherwise returns null.
   */
  private static Set<String> handleAsciiFlag(String arg, String filename) {
    if (arg.equals("-ascii")){
      return Collections.singleton(genVarName(filename));
    }
    return null;
  }

  /**
   * Processes a single argument to load. If it would cause the load
   * to destroy all info then it returns null, otherwise it returns
   * a string with the literal value of the arg.
   * Things that are rejected are -ascii, -regexp, anything
   * containing a *
   */
  private static String loadArgument(Expr arg) {
    if (!(arg instanceof StringLiteralExpr)) {
      return null;
    }
    String value = ((StringLiteralExpr) arg).getValue();
    if (value.equals("-regexp") || value.contains("*")) {
      return null;
    }
    return value.toLowerCase();
  }

  /**
   * generate a correct variable name from a given string. Strips
   * director, extension and turns any undesirable characters into
   * _. If it starts with a number then prepends an X.
   */
  private static String genVarName(String s) {
    int end = s.lastIndexOf('.');
    end = end != -1 ? end : s.length();
    String name = s.substring(s.lastIndexOf('/') + 1, end);
    name = name.replaceAll( "\\W", "_" );
    name = name.matches("\\A\\d.*") ? "X" + name : name;
    return name;
  }
}