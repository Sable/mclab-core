McLab/Natlab
============

This is Java-based "frontend" of the McLab project, which aims to provide
compiler tools and infrastructure for MATLAB. Although we call it the
frontend, it is starting to grow various backends, for example Fortran
and X10. In practice it houses development for the "static" branch of the
McLab project (as opposed to the "dynamic" branch's McVM project, which
will be made available soon).

Overview
--------

Proper developer documentation will eventually be here. A quick tour of
the code:

* `matlab` includes the Matlab to Natlab translator. 
Natlab (nice Matlab) is a simplified version of Matlab that is easier to
parse. (There are only syntactic differences between the two.)
* `natlab` includes the generated Natlab parser, as well as
`natlab.Main`, the entry point. 
* `natlab.rewrite` is a simple framework for AST transformations and
simplifications. Some useful simplifications, such as conversion to
three-address code, are provided.
* `natlab.toolkits.analysis` is an (intraprocedural) dataflow analysis
framework, and subpackages provide various common analyses. Of particular
interest is the kind analysis, which lives in 
`natlab.toolkits.analysis.varorfun`.
* `natlab.refactoring` includes implementations of a few
different refactorings, such as function and script inlining.
* `natlab.tame` and its subpackages comprise the tamer framework, which
aims to make Matlab more suitable to static compilation. It provides an
intermediate representation (`natlab.tame.tir`), machinery for analyses
to handle Matlab builtins, and an interprocedural analysis framework, among
other things.
* `natlab.backends` houses the Fortran and X10 backends. These build on
top of the tamer, and are a work in progress.

Working with the code
---------------------
There exists an eclipse project (`.project`, `.classpath`) 
which can be imported in eclipse.

There exist ant files (`build.xml`). 
These can either build the projects on the command line, or in eclipse 
(import in the `ant-view`) using the `eclipse.*` targets. The ant targets
generate necessary code (like the Natlab AST). The command line ant targets
also compile everything, while the eclipse targets do not -- eclipse will
compile the code. Matlab depends on the generated/compile Natlab files --
but it seems that the Natlab build targets will automatically generate the
Matlab targets.

It is useful for analyses to have access to the library files of a Matlab
installation. After `Natlab.jar` has been built with `ant jar`, you can run
the `registerMatlabPathWithNatlab.m` script in Matlab; it just calls the
Matlab `path` function and feeds its output to the jar.

License
-------
Apache 2.0
