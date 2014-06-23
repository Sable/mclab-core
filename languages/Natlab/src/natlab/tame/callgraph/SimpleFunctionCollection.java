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

package natlab.tame.callgraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import natlab.CompilationProblem;
import natlab.Parse;
import natlab.backends.vrirGen.ColonExprSimplification;
import natlab.tame.classes.ClassRepository;
import natlab.tame.simplification.LambdaSimplification;
import natlab.toolkits.Context;
import natlab.toolkits.analysis.varorfun.VFPreorderAnalysis;
import natlab.toolkits.filehandling.GenericFile;
import natlab.toolkits.path.FileEnvironment;
import natlab.toolkits.path.FunctionReference;
import natlab.toolkits.rewrite.Simplifier;
import ast.ClassDef;
import ast.Function;
import ast.FunctionList;
import ast.Program;
import ast.Script;

/**
 * A SimpleFunctionCollection is a collection of static functions. The
 * collection of functions is done by this object. We refer to parsing as
 * 'collecting', since we need to parse, resolve names as functions or variables
 * and explore the path to find functions in one pass.
 * 
 * Given a main function, all functions that may be used during execution are in
 * this collection. This can only be ensured if certain dynamic Matlab builtins
 * are not used (thus this is part of the Static package): - cd into directories
 * which have requried matlab functions - eval, ... - calls to builtin(..) - any
 * creation of function handles to these builtins
 * 
 */

public class SimpleFunctionCollection extends
		HashMap<FunctionReference, StaticFunction> implements
		FunctionCollection {
	private static final long serialVersionUID = 1L;
	private HashSet<GenericFile> loadedFiles = new HashSet<GenericFile>(); // files
																			// that
																			// were
																			// loaded
																			// so
																			// far
	private FunctionReference main = null; // denotes which function is the
											// entry point
	private FileEnvironment fileEnvironment;
	private static boolean DEBUG = false;
	private ClassRepository classRepository;
	public static boolean convertColonToRange = false; // Determines whether to
														// run transformation
														// which converts
														// ColonExpr to
														// RangeExpr.

	/**
	 * The function collection gets created via a path environment object This
	 * will collect all the files, starting from the main function ('primary
	 * function')
	 */
	public SimpleFunctionCollection(FileEnvironment fileEnvironment) {
		super();

		this.fileEnvironment = fileEnvironment;

		// build class repository
		this.classRepository = new ClassRepository(fileEnvironment);

		// get main file (entrypoint)
		main = fileEnvironment.getMainFunctionReference();

		// collect
		ArrayList<CompilationProblem> errors = new ArrayList<CompilationProblem>();
		collect(main, errors);
	}

	/**
	 * creates a deep copy of this function collection
	 */
	public SimpleFunctionCollection(SimpleFunctionCollection other) {
		super();
		this.loadedFiles.addAll(other.loadedFiles);
		this.main = other.main;
		this.fileEnvironment = other.fileEnvironment;
		this.classRepository = other.classRepository;

		for (FunctionReference ref : other.keySet()) {
			this.put(ref, other.get(ref).clone());
		}
	}

	/*** public stuff ***********************************************************************/
	public FunctionReference getMain() {
		return main;
	}

	/*** Collecting files *******************************************************************/
	public boolean collect(FunctionReference ref) {
		return collect(ref, new ArrayList<CompilationProblem>());
	}

	/**
	 * adds the matlab functions from the given filename to the collection
	 * 
	 * @return returns true on success
	 */
	public boolean collect(FunctionReference funcRef,
			ArrayList<CompilationProblem> errList) {
		if (DEBUG)
			System.out.println("collecting " + funcRef);
		// was the filename already loaded?
		if (loadedFiles.contains(funcRef.getFile()))
			return true;
		if (funcRef.isBuiltin)
			return true;

		Program program = Parse.parseMatlabFile(funcRef.getFile(), errList);
		if (program == null) {
			throw new UnsupportedOperationException("cannot parse file "
					+ funcRef + ":\n" + errList);
		}
		program.setFile(funcRef.getFile());

		// check whether the matlab file has a good type
		if (program instanceof Script) {
			System.err
					.println("The tamer does not suport scripts at this point."); // TODO
			return false;
		} else if (program instanceof ClassDef) {
			System.err
					.println("The tamer does not support classes at this point.");
			// TODO - also add the class to the class repository
		} else if (!(program instanceof FunctionList)) {
			System.err
					.println("The tamer encountered Matlab file of unknown/unsupported type "
							+ program.getClass() + ".");
		}

		// We reduce lambda expressions at this point, because they create extra
		// functions
		if (convertColonToRange) {
			ColonExprSimplification.analyze(program);
		}
		program = (Program) Simplifier.simplify(
				program,
				new VFPreorderAnalysis(program, fileEnvironment
						.getFunctionOrScriptQuery(funcRef.path)),
				LambdaSimplification.class);
		loadedFiles.add(funcRef.getFile());
		boolean success = true;

		// turn functions into static functions, and go through their function
		// references recursively
		FunctionList functionList = (FunctionList) program;
		for (Function functionAst : functionList.getFunctions()) {
			// create/add static function
			FunctionReference ref = new FunctionReference(
					functionAst.getName().getID(), funcRef.getFile());
			Context context = fileEnvironment.getContext(functionAst,
					funcRef.getFile());
			StaticFunction function = new StaticFunction(functionAst, ref,
					context);
			this.put(ref, function);

			// recursively load referenced functions
			success = success && resolveFunctionsAndCollect(function, errList);
		}

		// TODO we should collect used siblings and get rid of unused ones
		return success;
	}

	/**
	 * resolves all calls to other functions within given function, and collects
	 * functions not in this collection
	 */
	private boolean resolveFunctionsAndCollect(StaticFunction function,
			ArrayList<CompilationProblem> errList) {
		boolean success = true;
		LinkedList<String> unfoundFunctions = new LinkedList<String>();

		// collect references to other functions - update symbol table,
		// recursively collect
		for (String otherName : function.getCalledFunctions().keySet()) {
			FunctionReference ref = function.getCalledFunctions()
					.get(otherName);

			if (ref == null) {
				unfoundFunctions.add(otherName);
				success = false;
			}
			if (success) {
				success = success && collect(ref, errList);
			}
		}
		if (unfoundFunctions.size() != 0) {
			// TODO - should we do anything here? Just throw error if it's no an
			// incremental collection
			if (!(this instanceof IncrementalFunctionCollection)) {
				throw new UnsupportedOperationException("reference to "
						+ unfoundFunctions + " in " + function.getName()
						+ " not found");
			}
		}

		return success;
	}

	/**
	 * inlines all functions into the main function
	 */
	public void inlineAll() {
		inlineAll(new HashSet<FunctionReference>(), getMain());
		System.out.println("finished inlining all");
	}

	// inlines all calls inside the given function
	// throws a unspported operation if there is an attempt to inline a function
	// that is alraedy in the context -- cannot inline recursive functions
	private void inlineAll(Set<FunctionReference> context,
			FunctionReference function) {
		// error check
		if (context.contains(function)) {
			throw new UnsupportedOperationException(
					"trying to inline recursive function " + function);
		}
		if (!containsKey(function)) {
			throw new UnsupportedOperationException(
					"trying to inline function " + function
							+ ", which is not loaded");
		}
		if (function.isBuiltin())
			return;

		// add this call to the context
		context.add(function);

		// inline all called functions recursively
		for (FunctionReference ref : get(function).getCalledFunctions()
				.values()) {
			if (!ref.isBuiltin()) {
				// inline recursively
				System.out.println("go inline " + ref);
				inlineAll(context, ref);
			}
		}

		// inline the calls to the given function
		get(function).inline(this);

		// remove this context
		context.remove(function);
	}

	/**
	 * returns a single inlined function representing this function collection.
	 * Does not alter the the function collection. Only works for non-recursive
	 * callgraphs.
	 */
	public Function getAsInlinedFunction() {
		return getAsInlinedStaticFunction().getAst();
	}

	/**
	 * returns a single inlined StaticFunction representing this function
	 * collection. Does not alter the function collection. Only works for
	 * non-recursive callgraphs.
	 */
	public StaticFunction getAsInlinedStaticFunction() {
		SimpleFunctionCollection c = new SimpleFunctionCollection(this);
		c.inlineAll();
		return c.get(c.getMain());
	}

	/**
	 * returns all function references that are either in this function
	 * collection or that are being referred to by and function in this
	 * collection
	 */
	public HashSet<FunctionReference> getAllFunctionReferences() {
		HashSet<FunctionReference> result = new HashSet<FunctionReference>();
		for (FunctionReference ref : this.keySet()) {
			result.add(ref);
			result.addAll(this.get(ref).getCalledFunctions().values());
		}
		return result;
	}

	/**
	 * returns all function references to builtins among the whole call graph
	 */
	public HashSet<FunctionReference> getAllFunctionBuiltinReferences() {
		HashSet<FunctionReference> result = new HashSet<FunctionReference>();
		for (FunctionReference ref : this.keySet()) {
			for (FunctionReference ref2 : this.get(ref).getCalledFunctions()
					.values()) {
				if (ref2.isBuiltin())
					result.add(ref2);
			}
		}
		return result;
	}

	public String getPrettyPrinted() {
		String s = "";
		for (FunctionReference f : this.keySet()) {
			s += ("\n" + this.get(f));
		}
		return s;
	}

	@Override
	public ClassRepository getClassRepository() {
		return this.classRepository;
	}

	@Override
	public List<StaticFunction> getAllFunctions() {
		List<StaticFunction> functionList = new ArrayList<StaticFunction>();
		for (FunctionReference ref : this.keySet()) {
			functionList.add(this.get(ref));
		}
		return functionList;
	}

}
