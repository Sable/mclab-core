// =========================================================================== //
//                                                                             //
// Copyright 2008-2015 Andrew Casey, Jun Li, Jesse Doherty,                    //
//   Maxime Chevalier-Boisvert, Toheed Aslam, Anton Dubrau, Nurudeen Lameed,   //
//   Amina Aslam, Rahul Garg, Soroush Radpour, Olivier Savary Belanger,        //
//   Sameer Jagdale, Laurie Hendren, Clark Verbrugge and McGill University.                    //
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
package natlab.options;

import java.util.ArrayList;
import java.util.List;
import com.beust.jcommander.Parameter;

/** 
 * Contains the command line options supported by McLab-Core. 
 * Supports various options for the parsers, McSAF, Tamer and Tamer+. 
 * The JCommander command line parsing tool is used to parse and generarate the options. The command line options that are supported are defined using annotations. The set of valid values for each command line options are defined by the type of the class member which the annotation manipulates. 
 * @author Sameer Jagdale
 */
public class Options {
	/** Command line options and the variables which are manipulated. 
 	* The Parameter annotation is used the define the possible command line arguments for which the value of the member variable will be changed. The description string gives the description of the command line option, which is used when printing out usage
 	*/
	// General Options
	// Help needs to be set to true in the annotation too allow JCommander to indentify this as the help option. 
	@Parameter(names={"--help", "-h"},description="Display help and exit",help=true)
	protected boolean help=false;		
	@Parameter(names={"-p", "--pretty"}, description="Prettyprint the files")
	protected boolean pretty=false;	
	@Parameter(names={"--simplify", "-s"}, description="Simplify the AST after parsing")
	protected boolean simplify=false;	
	@Parameter(names={"--xml", "-x"},description="Prints the XML IR")
	protected boolean xml=false;
	@Parameter(names={"--json","j"}, description="Prints the JSON IR")
	protected boolean json=false;
	@Parameter(names={"--matlab", "-m"}, description="No-op, allowed for backwards compatibility")
	protected boolean matlab=false;
	@Parameter(names={"--quiet", "-q"}, description="Suppress all information messages")	
	protected boolean quiet=false;
	@Parameter(names={"--natlab","-n"}, description="Use Natlab input")
	protected boolean natlab=false;
	@Parameter(names={"--outdir", "od"}, description="Output everything to this dir rather than stdout")
	protected String od="";
	// Server Options	
	@Parameter(names={"--server"},description="Run frontend in server mode on a given port, default is 47146")
	protected boolean server=false;
	@Parameter(names={"--sport","-sp"}, description="Set the port the server runs on")
	protected String sp="";	
	/**
 	* The server will no longer try to detect broken connections using a the heartbeat 
 	*/
	//signal.
	@Parameter(names={"-nh","--noheart"},description="Turns off the need for a heartbeat signal")
	protected boolean noheart=false;
	/**
 	* Tells the version of the source code from which this jar file was compiled.	
 	*/
	@Parameter(names={"-v", "--version"},description="Get the current version of Natlab")
	protected boolean version=false;
	//McLint Options
	/**
 	* Runs the McLint static analyzer and refactoring helper.
 	*/
	@Parameter(names={"--mclint"},description="Run McLint")
	protected boolean mclint=false;
	//Tamer Options
	/**
 	* Runs the Tamer on a program, and will output a full Matlab program either as a single file or in a directory (via -outdir/od). The programs will be transformed
 	*/
	@Parameter(names={"-t","--tamer"},description="Tame a Matlab program")	
	protected boolean tamer=false;
	/**	
 	* Inlines a whole Matlab program into one function, if it is non-recursive and does not include ambiguous call edges (i.e. overloading).
 	*/
	@Parameter(names="--inline",description="Inline the whole Matlab program in one function, if possible")
	protected boolean inline=false;
	/**
 	* Specifies the type of the arguments to the main function.
 	* The default is "double", i.e. one double. For now this uses the
 	* same syntax as the class specification language for builtin functions.  			
 	*/
	@Parameter(names={"--args","--arguments"}, description="Specifies type of arguments to the main function (default is 'double')")
	protected String arguments="";
	//Tamer+ Options
	/** 
 	* Transform the program in Tamer IR to Tamer+ IR.
	*/
	@Parameter(names={"tamerplus"}, description="Get the Tamer+ version of a program")
	protected boolean tamerplus=false;
	
	//Path and File Options
	/**
 	* Path of locations to find matlab files.
 	*/
	@Parameter(names={"--lpath","-lp"}, description="Path of locations to fine Matlab files")
	List<String> lp=new ArrayList<String>();	
}
