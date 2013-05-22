McLab/Natlab
============


[![Build status](https://travis-ci.org/Sable/mclab.png)](https://travis-ci.org/Sable/mclab)

This is the Java-based infrastructure of the McLab project, which aims to
provide compiler tools and infrastructure for MATLAB (and potentially other
scientific languages in the future). In practice this repository houses
development for the project's "static" branch, which includes the frontend
(parsing, static analysis, refactoring, etc.), and work towards static
compilation. (The "dynamic" branch's McVM project will be made available
soon.)

Overview
--------

Proper developer documentation will eventually be here. A quick tour of
the code:

* `matlab` includes the Matlab to Natlab translator. 
Natlab (nice Matlab) is a simplified version of Matlab that is easier to
parse. (There are only syntactic differences between the two.)
* `natlab` includes the generated Natlab parser, as well as
`natlab.Main`, the entry point. 
* `natlab.toolkits.rewrite` is a simple framework for AST transformations
and simplifications. Some useful simplifications, such as conversion to
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
compile the code.

(The build process contains a lot of cruft, duplication, some things are
broken, etc. There is an ongoing effort to clean it up.)

It is useful for analyses to have access to the library files of a Matlab
installation. After `Natlab.jar` has been built with `ant jar`, you can run
the `registerMatlabPathWithNatlab.m` script in Matlab; it just calls the
Matlab `path` function and feeds its output to the jar.

Disclaimer
----------
Some of this code is confusing, poorly documented, and not well understood,
as the grad students who wrote much of it have moved on to bigger and
better things.  (This is fairly typical of research software.) Most of the
documentation can be found in the various 
[McLab publications](http://www.sable.mcgill.ca/mclab/Publications.html),
but in many cases there are discrepancies between terminology used in
papers and what's in the code. There is an ongoing effort to clean up the
code and the build process, and document everything, so please bear with
us as we work through this.

Copyright and License
---------------------
Copyright 2008-2013 Amina Aslam, Toheed Aslam, Ismail Badawi, Andrew Bodzay,
Andrew Casey, Maxime Chevalier-Boisvert, Jesse Doherty, Anton Dubrau,
Rahul Garg, Vineet Kumar, Nurudeen Lameed, Jun Li, Xu Li, Soroush Radpour,
Olivier Savary Belanger, Laurie Hendren, Clark Verbrugge and McGill
University.

Licensed under the Apache License, Version 2.0 (the "License"); you may not
use this work except in compliance with the License. You may obtain a copy
of the License in the LICENSE file, or at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
License for the specific language governing permissions and limitations
under the LICENSE.
