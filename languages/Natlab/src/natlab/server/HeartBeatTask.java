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

package natlab.server;

import java.util.*;
import java.io.*;

public class HeartBeatTask extends TimerTask
{
    private HeartBeatFlag flag;
    private PrintWriter out;
    private boolean quiet;

    public HeartBeatTask( HeartBeatFlag flag, PrintWriter out, boolean quiet )
    {
        this.flag = flag;
        this.out = out;
        this.quiet = quiet;
    }
    public void run()
    {
        if( !flag.readFlagAndUnset() ){
            if( !quiet )
                System.err.println("server timed out, aborting");
            //TODO: is this thread safe? probably not
            out.print("<errorlist><error>server timed out, aborting</error></errorlist>\0");
            out.flush();
            //TODO-JD: make the exiting nicer
            System.exit(1);
        }
    }
}