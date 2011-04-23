package natlab;

import java.util.*;
import java.util.prefs.Preferences;
import natlab.options.*;

/**
 * This class represents the preferences of Natlab.
 * These are like options, except they are stored in a backing store, and
 * available across different calls (on the same system).
 * 
 * Uses a java.util.prefs.Preferences object on this class
 * to store the preferences, so this represents a thin layer on top of the
 * Preferences object, so that all keys and types are properly defined in 
 * one place.
 * 
 * This class defines a set of keys and default values for every option.
 * Every option can then be set and get.
 * 
 * User Preferences are used for this class.
 * 
 * 
 * TODO - this should eventually be generated automatically
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
        prefs.put(MATLAB_PATH_KEY,path);
    }
    /**
     * sets the Matlab path string, a set of directories of *.m file directories
     * of a matlab installation, separated by ;
     * @param path
     */
    public static String getNatlabPath(){
        return prefs.get(NATLAB_PATH_KEY,NATLAB_PATH_DEFAULT);
    }
    /**
     * sets the Matlab path string, a set of directories of *.m file directories, separated by ;
     * @param path
     */
    public static void setNatlabPath(String path){
        prefs.put(NATLAB_PATH_KEY,path);
    }
    /**
     * sets the Matlab path string, a set of directories of *.m file directories, separated by ;
     * @param path
     */
    public static String getMatlabPath(){
        return prefs.get(NATLAB_PATH_KEY,NATLAB_PATH_DEFAULT);
    }
    
    
}




