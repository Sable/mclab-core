// =========================================================================== //
//                                                                             //
// Copyright 2008-2011 Andrew Casey, Jun Li, Jesse Doherty,                    //
//   Maxime Chevalier-Boisvert, Toheed Aslam, Anton Dubrau, Nurudeen Lameed,   //
//   Amina Aslam, Rahul Garg, Soroush Radpour, Olivier Savary Belanger,        //
//   Laurie Hendren, Clark Verbrugge and McGill University.                    //
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
//   limitations under the License.                                            //
//                                                                             //
// =========================================================================== //

package natlab;

import java.util.Arrays;
import java.util.Map;
import java.util.prefs.Preferences;

import natlab.options.Options;

import com.google.common.base.Joiner;
import com.google.common.base.StandardSystemProperty;
import com.google.common.collect.ImmutableMap;

/**
 * This class represents the preferences of Natlab.
 * These are like options, except they are stored in a backing store, and
 * available across different executions (on the same system/user).
 * 
 * Uses a java.util.prefs.Preferences object on this class
 * to store the preferences, so this represents a thin layer on top of the
 * Preferences object, so that all keys and types are properly defined in 
 * one place.
 * 
 * This class defines a set of keys and default values for every option.
 * Every option can then be set and get.
 * 
 * User Preferences are used for this class, rather than system ones.
 * 
 * 
 * TODO - this should eventually be generated automatically
 */
public class NatlabPreferences {
  //*** the keys and defaults for the options ********************************
  private static final String PATH_SEPARATOR = StandardSystemProperty.PATH_SEPARATOR.value();
  private static final String MATLAB_PATH_KEY = "matlab_path";
  private static final String MATLAB_PATH_DEFAULT = "";
  private static final String NATLAB_PATH_KEY = "natlab_path";
  private static final String NATLAB_PATH_DEFAULT = "";

  private static Preferences prefs = Preferences.userNodeForPackage(NatlabPreferences.class);

  /**
   * returns all preferences
   * @return a map whose keys are preference keys, and whose values are the associated values
   */
  public static Map<String, Object> getAllPreferences() {
    return ImmutableMap.<String, Object>of(
        MATLAB_PATH_KEY, getMatlabPath(),
        NATLAB_PATH_KEY, getNatlabPath());
  }

  public static void modify(Options options) {
    Joiner joiner = Joiner.on(PATH_SEPARATOR);
    if (!options.set_matlab_path().isEmpty()){
      setMatlabPath(joiner.join(options.set_matlab_path()));
    }
    if (!options.set_natlab_path().isEmpty()) {
      setNatlabPath(joiner.join(options.set_natlab_path()));
    }
    if (!options.add_natlab_path().isEmpty()) {
      setNatlabPath(joiner.join(
          Arrays.asList(getNatlabPath(), joiner.join(options.add_natlab_path()))));
    }
    if (!options.add_matlab_path().isEmpty()) {
      setMatlabPath(joiner.join(
          Arrays.asList(getMatlabPath(), joiner.join(options.add_matlab_path()))));
    }
  }

  private static void setMatlabPath(String path) {
    putLongString(MATLAB_PATH_KEY, path);
  }

  public static String getMatlabPath(){
    return getLongString(MATLAB_PATH_KEY, MATLAB_PATH_DEFAULT);
  }

  private static void setNatlabPath(String path) {
    putLongString(NATLAB_PATH_KEY,path);
  }

  public static String getNatlabPath() {
    return getLongString(NATLAB_PATH_KEY, NATLAB_PATH_DEFAULT);
  }

  //*** long strings **********************************************************
  /** 
   * stores a long string, across multiple entries, where the entries are
   * key+"_COUNT" - the number of sub strings
   * key+"_"+i    - each string, with 0 <= i < count
   * otherwise these methods act like put/get
   */
  private static final String count = "_count";
  private static void putLongString(String key, String value) {
    deleteLongString(key);

    int length = Preferences.MAX_VALUE_LENGTH;
    int N = 1 + (value.length() / length);

    prefs.putInt(key+count, N);

    for (int i = 0; i < N - 1; i++){
      prefs.put(key + "_" + i, value.substring(length * i, length * (i + 1)));
    }
    prefs.put(key + "_" + (N - 1), value.substring(length * (N - 1)));
  }

  private static String getLongString(String key, String defaultValue) {
    int N = prefs.getInt(key + count, -1);
    if (N == -1) {
      return defaultValue;
    }

    StringBuilder s = new StringBuilder();
    for (int i = 0; i < N; i++) {
      s.append(prefs.get(key + "_" + i, ""));
    }
    return s.toString();
  }

  private static void deleteLongString(String key){
    int N = prefs.getInt(key + count, 0);

    prefs.remove(key+count);

    for (int i = 0; i < N; i++) {
      prefs.remove(key + "_"+  i);
    }
  }
}
