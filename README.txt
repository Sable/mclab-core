

Mclab/Natlab project

This is the Mclab/Natlab project source code. It is mostly written in java. The overall project consists of subprojects, that are split into 'languages'. The main ones are:

Matlab
- provides the Matlab to Natlab translator (the Natlab (nice Matlab) language has slight syntactic differences with matlab)

Natlab
- provides parser for Natlab, as well as the analysis framework, frefactoring tools, and tamer framework


There exists an eclipse project (note the (hidden?) .project direcotry at the top level), wich can be imported in eclipse.


In the languages directories there exist ant files ('build.xml'). These can either build the projects on the command line, or in eclipse (import in the 'ant-view') using the elcipse.* targets. The ant targets generate necessary code (like the Matlab AST). The command line ant targets also compile everything, while the eclipse targets do not -- eclipse will compile the code. Matlab depends on the generated/compile Natlab files -- but it seems that the Natlab build targets will automatically generate the Matlab targets.


The Natlab project may need access to the library files of a Matlab installation. In order to enable Natlab to use a matlab library, one has to build the jar target int the Natlab ant file; and then execute the "registerMatlabPathWithNatlab.m" in Matlab.




