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

import java.util.*;
import java.util.prefs.Preferences;
import natlab.options.*;

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
 * TODO - maybe options should be stored in a property file, rather than the registry
 */
public class NatlabPreferences {
    //*** the keys and defaults for the options ********************************
    public static final String MATLAB_PATH_KEY = "matlab_path";
    public static final String MATLAB_PATH_DEFAULT = "";
    public static final String NATLAB_PATH_KEY = "natlab_path";
    public static final String NATLAB_PATH_DEFAULT = "";
    
    //the preferences object
    private static Preferences prefs = Preferences.userNodeForPackage(NatlabPreferences.class);
    
    /**
     * returns all preferences
     * @return a map whose keys are preference keys, and whose values are the associated values
     */
    public static Map<String,Object> getAllPreferences(){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put(MATLAB_PATH_KEY, getMatlabPath());
        map.put(NATLAB_PATH_KEY, getNatlabPath());
        return map;
    }
    
    //*** modifying via options object ***********************************************
    public static void modify(Options options){
        if (!options.set_matlab_path().isEmpty()){
            setMatlabPath(concatList(options.set_matlab_path()));
        }
        if (!options.set_natlab_path().isEmpty()){
            setNatlabPath(concatList(options.set_natlab_path()));
        }
        if (!options.add_natlab_path().isEmpty()){
            List list = new LinkedList();
            list.add(getNatlabPath());
            list.addAll(options.add_natlab_path());
            setNatlabPath(concatList(list));
        }
        if (!options.add_matlab_path().isEmpty()){
            List list = new LinkedList();
            list.add(getMatlabPath());
            list.addAll(options.add_matlab_path());
            String s = concatList(list);
            setMatlabPath(s);
        }
        if (options.show_pref()){
            Map<String,Object> prefs = NatlabPreferences.getAllPreferences();
            for (String key : prefs.keySet()){
                System.out.println(key+":  <"+prefs.get(key)+">");
            }
        }
    }
    
    //takes a list of strings as given by the options object and concats them using ;
    public static String concatList(List strings){
        if (strings == null || strings.isEmpty()) return "";
        StringBuilder builder = new StringBuilder();
        for (Object o : strings){
            String s = o.toString();
            if (s.length() > 0){
                builder.append(s);
                builder.append(";");
            }
        }
        String s = builder.toString();
        return s.length() > 0?s.substring(0,s.length()-1):""; //get rid of last ;
    }
    
    
    //*** the put and get methods ***********************************************
    /**
     * sets the Matlab path string, a set of directories of *.m file directories
     * of a matlab installation, separated by ;
     * @param path
     */
    public static void setMatlabPath(String path){
        putLongString(MATLAB_PATH_KEY,path);
    }
    /**
     * sets the Matlab path string, a set of directories of *.m file directories
     * of a matlab installation, separated by ;
     * @param path
     */
    public static String getMatlabPath(){
        return getLongString(MATLAB_PATH_KEY,NATLAB_PATH_DEFAULT);
    }
    /**
     * sets the Matlab path string, a set of directories of *.m file directories, separated by ;
     * @param path
     */
    public static void setNatlabPath(String path){
        putLongString(NATLAB_PATH_KEY,path);
    }
    /**
     * sets the Matlab path string, a set of directories of *.m file directories, separated by ;
     * @param path
     */
    public static String getNatlabPath(){
        return getLongString(NATLAB_PATH_KEY,NATLAB_PATH_DEFAULT);
    }
    
    
    
    //*** long strings **********************************************************
    /** 
     * stores a long string, across multiple entries, where the entries are
     * key+"_COUNT" - the number of sub strings
     * key+"_"+i    - each string, with 0 <= i < count
     * otherwise these methods act like put/get
     */
    private static final String count = "_count";
    private static void putLongString(String key,String value){
        //delete existing value
        deleteLongString(key);
        
        //find how many strings we need
        int length = prefs.MAX_VALUE_LENGTH;
        int N = 1 + (value.length() / length);
        
        //store the key
        prefs.putInt(key+count, N);
        
        //store the substrings
        for (int i = 0; i < N - 1; i++){
            prefs.put(key+"_"+i, value.substring(length*i,length*(i+1)));
        }
        prefs.put(key+"_"+(N-1), value.substring(length*(N-1)));
    }
    private static String getLongString(String key,String defaultValue){
        //find how many strings there are
        int N = prefs.getInt(key+count, -1);
        if (N == -1) return defaultValue;
                
        //find the individual strings
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < N; i++){
            s.append(prefs.get(key+"_"+i, ""));
        }
        return s.toString();
    }
    private static void deleteLongString(String key){
        //find how many strings there are
        int N = prefs.getInt(key+count, 0);
        
        //delete count
        prefs.remove(key+count);
        
        //delete individual strings
        for (int i = 0; i < N; i++){
            prefs.remove(key+"_"+i);
        }
    }
}




