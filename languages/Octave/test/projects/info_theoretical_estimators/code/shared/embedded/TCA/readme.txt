+------------+
|    TCA     |
+------------+


Version 1.0 - September 25th, 2003
----------------------------------


Description
-----------

Paper:

Francis R. Bach, Michael I. Jordan. Beyond independent components: 
trees and clusters, to appear in Journal of Machine Learning Research, 2003.

The tca package is Copyright (c) 2003 by Francis Bach. If you
have any questions or comments regarding this package, or if you want to
report any bugs, please send me an e-mail to fbach@cs.berkeley.edu. The
current version 1.0 has been released on September, 25th 2003. It has been
tested on matlab 6.  Check regularly the following for
newer versions: http://www.cs.berkeley.edu/~fbach

License
-------

GPLv2 or later.


Installation
------------

1. Unzip all the .m files in the same directory



2. (Optional) if you want a faster implementation which uses pieces of C
code: at the matlab prompt, in the directory where the package is
installed, type:

 >> mex chol_gauss.c

It should create compiled files whose extensions depends on the platform
you are using:
      Windows: chol_gauss.dll   
      Solaris: chol_gauss.mexsol
      Linux  : chol_gauss.mexglx

To check if the file was correcly compiled, type

 >> which chol_gauss

and the name of the compiled versions should appear. If you have any
problems with the C file of if you are using a platform i did not
mention, please e-mail me.