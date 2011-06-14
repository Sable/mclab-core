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
import ast.Program;
import ast.Script;
import ast.FunctionList;
import beaver.Parser;

/**
 * Used to resolve fn and script names. Contains lists of scripts, 
 * functions and class constructors. Resolution process is intertwined 
 * with the loading process, and they share a queue of files that need 
 * processing. This queue is produced by Resolver and consumed by the 
 * loader
 */

public class Resolver
{
    private Queue<ProgramEntry<Program> > toProcess;

    private List<ProgramEntry<Script> > scripts;

    private List<ProgramEntry<FunctionList> > functions;

    Resolver(Queue<ProgramEntry<Program> > toP)
    {
        toProcess = toP;
        scripts = new LinkedList<ProgramEntry<Script> >();
        functions = new LinkedList<ProgramEntry<FunctionList> >();
    }

    public ProgramEntry<Script> addScript(Script s, String name)
    {
        ProgramEntry<Script> script = new ProgramEntry<Script>(s, name);
        scripts.add(script);
        //        toProcess.offer(script); //TODO-JD add some protection
        return script; 
    }
}