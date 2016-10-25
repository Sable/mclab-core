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

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

/**
 * Contains the command line options supported by McLab-Core. Supports various
 * options for the parsers, McSAF, Tamer and Tamer+. The JCommander command line
 * parsing tool is used to parse and generate the options. The command line
 * options that are supported are defined using annotations. The set of valid
 * values for each command line options are defined by the type of the class
 * member which the annotation manipulates.
 * 
 * @author Sameer Jagdale
 */
public class Options {
	/**
	 * Command line options and the variables which are manipulated. The
	 * Parameter annotation is used the define the possible command line
	 * arguments for which the value of the member variable will be changed. The
	 * description string gives the description of the command line option,
	 * which is used when printing out usage
	 */
	// General Options
	@Parameter(description = "list of files")
	List<String> files = new ArrayList<String>();

	public List<String> files() {
		return files;
	}

	/**
	 * Help needs to be set to true in the annotation too allow JCommander to
	 * identify this as the help option.
	 */
	@Parameter(names = { "--help", "-h" }, description = "Display help and exit", help = true)
	protected boolean help = false;

	/**
	 * Getter method for the help member variable.
	 * 
	 * @return Returns the boolean value of the help variable.
	 */
	public boolean help() {
		return help;
	}

	/**
	 * Pretty prints the files after parsing.
	 */
	@Parameter(names = { "-p", "--pretty" }, description = "Prettyprint the files")
	protected boolean pretty = false;

	/**
	 * Getter method for the pretty member variable
	 * 
	 * @return
	 */
	public boolean pretty() {
		return pretty;
	}

	/**
	 * Simplify the AST after parsing.
	 */
	@Parameter(names = { "--simplify", "-s" }, description = "Simplify the AST after parsing")
	protected boolean simplify = false;

	/**
	 * Getter method for the simplify command line option.
	 * 
	 * @return value of simplify variable
	 */
	public boolean simplify() {
		return simplify;
	}

	/**
	 * Prints the XML IR.
	 */
	@Parameter(names = { "--xml", "-x" }, description = "Prints the XML IR")
	protected boolean xml = false;

	/**
	 * Option to Print the XML IR
	 * 
	 * @return returns a boolean value which determines whether the IR is to be
	 *         printed.
	 */
	public boolean xml() {
		return xml;
	}

	@Parameter(names = { "--json", "-j" }, description = "Prints the JSON IR")
	protected boolean json = false;

	/**
	 * Option to print the JSON IR
	 * 
	 * @return returns a boolean value which when set to true signifies the IR
	 *         to be printed in JSON format
	 */
	public boolean json() {
		return json;
	}

	@Deprecated
	@Parameter(names = { "--matlab", "-m" }, description = "No-op, allowed for backwards compatibility")
	protected boolean matlab = false;

	@Deprecated
	public boolean matlab() {
		return matlab;
	}

	@Parameter(names = { "--quiet", "-q" }, description = "Suppress all information messages")
	protected boolean quiet = false;

	/**
	 * Option to suppress all information messages
	 * 
	 * @return returns the quiet boolean variable which when true signifies that
	 *         all messages need to be suppressed
	 */
	public boolean quiet() {
		return quiet;
	}

	@Parameter(names = { "--natlab", "-n" }, description = "Use Natlab input")
	protected boolean natlab = false;

	/**
	 * Option when set to true uses Natlab input
	 * 
	 * @return the boolean value of the natlab member.
	 */
	public boolean natlab() {
		return natlab;
	}

	@Parameter(names = { "--outdir", "-od" }, description = "Output everything to this dir rather than stdout")
	protected String od = "";

	/**
	 * Output everything to this dir rather than stdout
	 * 
	 * @return the output directory path
	 */
	public String od() {
		return od;
	}

	// Server Options
	@Parameter(names = { "--server" }, description = "Run frontend in server mode on a given port, default is 47146")
	protected boolean server = false;

	/**
	 * Run frontend in server mode on a given port, default is 47146
	 * 
	 * @return value of server. If true starts mclab in server mode
	 */
	public boolean server() {
		return server;
	}

	@Parameter(names = { "--sport", "-sp" }, description = "Set the port the server runs on")
	protected String sp = "";

	public String sp() {
		return sp;
	}

	@Parameter(names = { "-nh", "--noheart" }, description = "Turns off the need for a heartbeat signal")
	protected boolean noheart = false;

	/**
	 * The server will no longer try to detect broken connections using the
	 * heartbeat signal.
	 */
	public boolean noheart() {
		return noheart;
	}

	/**
	 * Tells the version of the source code from which this jar file was
	 * compiled.
	 */
	@Parameter(names = { "-v", "--version" }, description = "Get the current version of Natlab")
	protected boolean version = false;

	/**
	 * Tells the version of the source code from which this jar file was
	 * compiled.
	 */
	public boolean version() {
		return version;
	}

	// McLint Options

	@Parameter(names = { "--mclint" }, description = "Run McLint")
	protected boolean mclint = false;

	/**
	 * Runs the McLint static analyzer and refactoring helper.
	 */
	public boolean mclint() {
		return mclint;
	}

	// Tamer Options

	@Parameter(names = { "-t", "--tamer" }, description = "Tame a Matlab program")
	protected boolean tamer = false;

	/**
	 * Runs the Tamer on a program, and will output a full Matlab program either
	 * as a single file or in a directory (via -outdir/od). The programs will be
	 * transformed
	 */
	public boolean tamer() {
		return tamer;
	}

	@Parameter(names = "--inline", description = "Inline the whole Matlab program in one function, if possible")
	protected boolean inline = false;

	/**
	 * Inlines a whole Matlab program into one function, if it is non-recursive
	 * and does not include ambiguous call edges (i.e. overloading).
	 */
	public boolean inline() {
		return inline;
	}

	@Parameter(names = { "--args", "--arguments" }, description = "Specifies type of arguments to the main function (default is 'double')")
	protected String arguments = "";

	/**
	 * Specifies the type of the arguments to the main function. The default is
	 * "double", i.e. one double. For now this uses the same syntax as the class
	 * specification language for builtin functions.
	 */
	public String arguments() {
		return arguments;
	}

	// Tamer+ Options
	/**
	 * Transform the program in Tamer IR to Tamer+ IR.
	 */
	@Parameter(names = { "--tamerplus" }, description = "Get the Tamer+ version of a program")
	protected boolean tamerplus = false;

	/**
	 * Transform the program in Tamer IR to Tamer+ IR.
	 */
	public boolean tamerplus() {
		return tamerplus;
	}

	// Path and File Options

	@Parameter(names = { "--lpath", "-lp" }, description = "Path of locations to fine Matlab files")
	protected List<String> lp = new ArrayList<String>();

	/**
	 * Path of locations to find matlab files.
	 */
	public List<String> lp() {
		return lp;
	}

	@Parameter(names = { "--in" }, description = "Files to be used as input")
	protected List<String> in = new ArrayList<String>();

	/**
	 * Files to be used as input. If no main file is specified then the first
	 * file is taken to be the main.
	 */
	public List<String> in() {
		return in;
	}

	@Parameter(names = { "--main" }, description = "File taken to be the main file and entry point of the program.Note: this can also be specified by a single file as argument to the compiler.")
	protected String main = "";

	/**
	 * File taken to be the main file and entry point of the program. Note: this
	 * can also be specified by a single file as argument to the compiler.
	 */
	public String main() {
		return main;
	}

	// Setting Natlab Stored Preferences

	@Parameter(names = { "--pref", "--preferences" }, description = "	performs the specified preference operation(s), then exits")
	protected boolean pref = false;

	/**
	 * performs the specified preference operation(s), then exits
	 */
	public boolean pref() {
		return pref;
	}

	@Parameter(names = { "--set_matlab_path" }, description = "Set Path ( all path directories) of a Matlab installation")
	protected List<String> set_matlab_path = new ArrayList<String>();

	public List<String> set_matlab_path() {
		return set_matlab_path;
	}

	@Parameter(names = { "--add_matlab_path" }, description = "adds the given paths to the Matlab installation path")
	protected List<String> add_matlab_path = new ArrayList<String>();

	/**
	 * Adds the given paths to the Matlab installation path
	 */
	public List<String> add_matlab_path() {
		return add_matlab_path;
	}

	@Parameter(names = { "--add_natlab_path" }, description = "Adds given paths to the Natlab path")
	protected List<String> add_natlab_path = new ArrayList<String>();

	/**
	 * Adds given paths to the Natlab path
	 */
	public List<String> add_natlab_path() {
		return add_natlab_path;
	}

	@Parameter(names = "--set_natlab_path", description = "Set path directories where to find source code and packages")
	protected List<String> set_natlab_path = new ArrayList<String>();

	public List<String> set_natlab_path() {
		return set_natlab_path;
	}

	@Parameter(names = { "--show_pref", "--show_preferences" }, description = "Displays all stored preferences")
	protected boolean show_pref = false;

	@Parameter(names = "--debug", description = "Display additional debugging information")
	protected boolean debug = false;

	public boolean debug() {
		return debug;
	}

	/**
	 * Displays all stored preferences
	 */
	public boolean show_pref() {
		return show_pref;
	}

	/**
	 * An instance of JCommander. Required to print usage.
	 */
	protected JCommander jct = null;

	/**
	 * Parses the given command line arguments. The result is stored in the
	 * instance of the Options(this) class
	 * 
	 * @param args
	 *            .Array of Strings containing the command line arguments.
	 */
	public void parse(String[] args) {
		jct = new JCommander(this, args);
	}

	/**
	 * Prints out the usage of the command line options. Uses the default
	 * JCommander format.
	 */
	public void getUsage() {
		jct.usage();
	}
}
