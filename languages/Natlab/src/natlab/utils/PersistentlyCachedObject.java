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

package natlab.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Represents an object which is persistent 
 * - upon vm exit, an object of this type gets stored as a temp file
 * - the location of the temp file is stored in the Preferences (registry), using a key
 * - this key is a combination of the stored object's class name + a supplied key
 * - the file only gets stored if 'changed' is true
 * 
 * Any object that implements this should use a factory method rather than a constructor,
 * which should try to load itself - if that fails, it should get created. If an
 * object changes, it should set the 'changed' flag to true - alternatively this can
 * be set to be always true, except that may lead to unnecessary storing of fiels.
 * 
 * Note: to speed up serialization of objections contained in a PersistentlyCaachedObject,
 * they should implement Externalizable rather than serializable. 
 * 
 * TODO - maybe file locations should be stored in a property file, rather than the registry
 * @author ant6n
 */

public abstract class PersistentlyCachedObject implements Serializable {
    private static final long serialVersionUID = 2L;

    //the preferences object is used to find the temp file again
    static Preferences prefs = Preferences.userNodeForPackage(PersistentlyCachedObject.class);
    
    transient File tempFile; //the temp file where the object will be stored after terminating
    transient boolean storeOnExit = true; //if set to false, the object won't get stored
    transient String key; //the key used in the prefs to find the file
    transient boolean changed; //only store if the data was changed
    
    //static initializer - delete all keys that point to files that don't exist
    static {
        try {
            String[] keys = prefs.keys();
            for (String k : keys){
                //System.out.println(k);
                if (!(new File(prefs.get(k, "")).exists())){
                    prefs.remove(k);
                }
            }
        } catch (BackingStoreException e) {
        }
    }
    
    /**
     * constructor takes a key which will be used to store the location of the temp
     * file in the Preferences object (which is the Preferences object of this class).
     * In the actual Preferences object, the key will actually be stored as
     * this.class.getName()+"."+key.
     * So if multiple objects of the same class use the same key, then there might 
     * be a problem.
     * If a cached object of this type and key is already stored, it will get deleted
     */
    protected PersistentlyCachedObject(String aKey) {
        //sets the key
        key = getPrefkey(aKey, this.getClass());
        
        //delete any file that might exist already
        String filename = prefs.get(key,"");
        if (filename.length() > 0) (new File(filename)).delete();
        
        //create temp file
        try {
            tempFile = File.createTempFile(key,null);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        
        //register shutdown hook
        registerShutdownHook();
        
        //this data was changed, trivially so
        changed = true;
    }
    
    //registers a trhead that, upon vm exit, will store the object if needed
    private void registerShutdownHook(){
        //set up shutdown hook that will store this object in the temp file
        final PersistentlyCachedObject aThis = this;
        Runtime.getRuntime().addShutdownHook(new Thread(){
            public void run() {
                //System.err.println("on persistently cached object exit, write "+tempFile);
                onExit();
                if (storeOnExit){
                    if (changed){
                        try{
                            FileOutputStream fStream = new FileOutputStream(tempFile);
                            ObjectOutputStream oStream = new ObjectOutputStream(fStream);
                            oStream.writeObject(aThis);
                            fStream.close();
                            prefs.put(key, tempFile.getAbsolutePath());
                            //System.err.println("write succesful");
                        } catch (IOException e) {
                            prefs.remove(key);
                            //System.err.println("write error "+e);
                        }
                    } else {
                        //System.err.println("object unchagend");
                    }
                } else {
                    prefs.remove(key);
                    tempFile.delete();
                }
            }
        });
    }
    
    //given the string key and a class, returns they registry key that it is used
    //to store its temp file
    private static String getPrefkey(String key,Class<? extends PersistentlyCachedObject> aClass){
        return aClass.getName()+"."+key;
    }
        
    /**
     * deletes the persistency of this object, i.e. do not store the object in a temp
     * file at shutdown,
     */
    public void delete(){
        storeOnExit = false;
        prefs.remove(key);
        tempFile.delete();
    }
        
    
    /**
     * The object only gets stored again if it is changed, otherwise just leave it.
     * The default is that an object is unchanged.
     * Once the object is set changed, it will always be set changed;
     */
    public void setChanged(boolean changed){
        this.changed |= changed;
    }
    
    /**
     * returns the changed flag for this object
     */
    public boolean getChanged(){
        return changed;
    }
    
    
    /**
     * loads a Persistent object
     * @param <T> the type of the persistent object
     * @param aKey the key it was created with
     * @param c the class
     * @return
     */
    protected static <T extends PersistentlyCachedObject> T load(String aKey,Class<T> c){
        //get key
        String key = getPrefkey(aKey, c);
        
        //find temp file
        String tFile = prefs.get(key, "");
        if (tFile.length() == 0){ //registry doesn't know about this key
            prefs.remove(key);
            return null;
        }
        File tempFile = new File(tFile);
        if (!tempFile.exists()){ //file doesn't actually exist
            prefs.remove(key);
            return null;
        }
        
        //read/return actual object
        FileInputStream fStream = null;
        boolean removeTmp = false;
        try{
            fStream = new FileInputStream(tempFile);
            ObjectInputStream oStream = new ObjectInputStream(fStream);
            @SuppressWarnings("unchecked")
            T object = (T)(oStream.readObject());
            object.registerShutdownHook();
            object.key = key;
            object.tempFile = tempFile;
            object.storeOnExit = true;
            object.changed = false;
            return object;
        } catch (Exception e) {
            //something bad happened - unregister key and delete temporary file
            //System.err.println("read exception "+e);
            //e.printStackTrace();
            removeTmp = true;
            prefs.remove(key);
        } finally {
            try{
                fStream.close();
            } catch (Exception e) {
            }
            if (removeTmp) tempFile.delete();
        }
        return null;
    }
    
    
    /**
     * this method gets executed before the shutdown hook which stores this object.
     * Calling 'delete' would allow this object to not be stored.
     */
    protected void onExit(){
    }
}




