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

package natlab.toolkits.filehandling;

import java.io.*;

/**
 *
 */
public abstract class AbstractExtensionFileFilter implements FileFilter, FilenameFilter
{

    public abstract String[] getOkFileExtensions();

    public boolean accept(File file)
    {
        for (String extension : getOkFileExtensions()){
            if (file.getName().toLowerCase().endsWith(extension)){
                return true;
            }
        }
        return false;
    }
    
    public boolean accept(File dir, String name)
    {
        for (String extension : getOkFileExtensions()){
            if (name.toLowerCase().endsWith(extension)){
                return true;
            }
        }
        return false;
    }


}
