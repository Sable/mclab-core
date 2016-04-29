        |--------------------|
        | FAST KERNEL ICA    |
        |--------------------|

Version 1.0 - February 2007
MPL license, see below


This package contains a Matlab implementation of the Fast Kernel ICA 
algorithm as described in [1]. 

Kernel ICA is based on minimizing a kernel measure of statistical
independence, namely the Hilbert-Schmidt norm of the covariance
operator in feature space (see [3]: this is called HSIC).  Given an (n
x m) matrix W of n samples from m mixed sources, the goal is to find a
demixing matrix X such that the dependence between the estimated
unmixed sources X'*W is minimal.  FastKICA uses an approximate Newton
method to perfom this optimization.  For more information on the
algorithm, read [1], and for more information on HSIC, refer to [3].

The functions 'chol_gauss' and 'amariD' are taken from and based on,
respectively, code from Francis Bach (available at
http://cmm.ensmp.fr/~bach/kernel-ica/index.htm). The derivative is
computed as described in [2] (for incomplete Cholesky decomposition).

Finally, please read the notes on OBTAINING GOOD PERFORMANCE later in this
file.


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%% CONTENT OF THE DIRECTORY                                           %%

Unzipping will create a directory called 'fastKICA', which should 
contain the following files:

fastkica.m      main routine

for demonstration:
demo.m          Demo file
amariD.m        Amari divergence (based on the code by Francis Bach)
source2.wav     data for demo
source3.wav     data for demo
source4.wav     data for demo

directory 'utils':
chol_gauss.c        incomplete Cholesky decomposition (from Francis Bach)
dChol2.c            Mex code for derivative (Windows)
dChol2Lin.c         Mex code for derivative (Linux)
dKmn.c, dKmnLin.c   Mex code, also for derivative (Win/Linux)
getKern.c           Mex code to compute Gaussian kernel
compDerivChol.m     Matlab interface for the gradient
dChol.m, dCholLin.m needed for the gradient (Win/Linux)
hsicChol.m          computes HSIC
hessChol.m          computes the Hessian

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%% COMPILING THE MEX FILES                                            %%

All files with the ending '.c' are MEX files and need to be compiled. 
To do that, change to the 'utils' directory by typing

'cd utils'

%%% PC %%%
On the PC, the command to compile depends on the compiler you are using. 
If you are using Lcc, then do

'mex <FILENAME>.c <matlabroot>\extern\lib\win32\lcc\libmwlapack.lib'

For the 
Microsoft Visual C++ compiler, do

'mex <FILENAME>.c <matlabroot>\extern\lib\win32\microsoft\libmwlapack.lib'

Replace <FILENAME> by each of the file names ending in '.c' in the 'utils' 
directory, and replace <matlabroot> by your Matlab root directory, e.g.
'mex dChol2.c c:\Matlab701\extern\lib\win32\lcc\libmwlapack.lib'. You
will not need those files ending in 'Lin.c', they are for Linux/UNIX
systems.

The command should create a file <FILENAME.dll>, e.g. for the example above
'dChol2.dll'. You can check this by typing 'which dChol' (or any other
file you are looking for instead of dChol).

%%% Other machines %%%
For Unix etc., compile with the command

'mex <FILENAME>.c'

If your compiler  has problems with the linking with that command, try
to add '-lmwlapack' (e.g. with Ubuntu):

'mex <FILENAME>.c -lmwlapack'

Of those files where two versions exist, one <name>.c and one
<name>Lin.c, you will need the latter version only.

For further details on compiling MEX files, see the the section "Building 
the C MEX-File" in the Matlab Dosumentation:
www.mathworks.com/access/helpdesk/help/techdoc/matlab_external/f45091.html


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%% USING THE CODE                                                     %%



fastkica is used as follows (for n samples, m sources):

[X, XS, hsics] = fastkica(MS, Xin, maxiter, sigma, thresh)
%
% MS:       mixed signals (n x m). These must each have zero mean and 
%           unit standard deviation
% Xin:        initial demixing matrix (m x m): 
%           Xin' * MS are estimated sources (initial guess)
% maxiter:  maximum number of iterations
% sigma:    width of the Gaussian kernel
% thresh:   convergence threshold: algorithm terminates when the difference
%           in subsequent values of HSIC is less than thresh

and returns
% X:        demixing matrix: X' * MX are estimated sources
% XS:       sequence of X for each iteration. The matrix XS(:,:,i)
            is the estimated demixing matrix at iteration i.
% hsics:    HSIC at each iteration

The file 'demo.m' demonstrates how to use the code. Source data is
loaded and mixed and then fastkica called to demix the data. 


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%% GETTING GOOD PERFORMANCE                                           %%

A. INITIALISATION

Like many ICA methods that adapt to the source distributions, fast
kernel ICA needs to optimise over a non-convex loss function
(specifically a kernel independence measure, HSIC). This loss has
local minima, which increase in number as more sources are
present. There are two ways to get around this problem:

(1) Initialise using another algorithm where applicable, e.g. fast ICA
or Jade. This works in many cases, although certain source
distributions may be difficult for classical algorithms to tell apart
(see experiments in [1])

(2) Start the search from multiple different initial guesses. As
discussed in [1], fastKICA converges to the global optimum from a
large proportion of the different starting guesses; and this optimum
can easily be distinguished from local solutions, since it has a lower
HSIC (you should get multiple solutions with HSIC scores clustered
about this value). The number of restarts required will increase with
the number of sources.

B. ITERATIONS TO CONVERGENCE, THRESHOLD FOR CHECKING CONVERGENCE

The number of iterations to convergence increases with the number of
sources. Convergence can be checked by looking at the "hsics"
variable: if it flattens out, the algorithm has converged. If not,
increase maxiter and/or decrease thresh.
For example, with a good initialisation (Jade), 5-10 iterations were
sufficient for convergence with 16 sources and 40,000 samples (see [1]
for a plot of the convergence).
In our experiments with 40,000 samples and 8 sources, we used a
convergence threshold between 1e-5 and 1e-8.

C. KERNEL SIZE SELECTION

When a good initial guess is given, a kernel size (sigma) of 0.5 seems
to work reasonably well; otherwise, first use a kernel size of 1.0 to
get an initial estimate of the solution (via multiple restarts), and
then optimise from this guess with a kernel size of 0.5. Cross
validation should be used to select the kernel size where possible. 

D. PRECISION OF THE CHOLESKY APPROXIMATION

In the code, the factor matrices of the incomplete Cholesky
decomposition are restricted to have at most 60 columns in order to
avoid excessive memory requirements. Whether this limit is met
depends on the data and the width of the kernel. A smaller sigma
usually leads to more columns  being retained in the approximation. 
When the kernel size was 1.0, this limit was not reached.

You can change the restriction on the maximum size of the incomplete
Cholesky approximation by changing the value of 'maxdim' in the file
fastkica.m (line 58, check is done in line 74).  In addition, the
precision of the Cholesky approximation is set to 1e-6. To change it,
set the variable 'etas' in line 57 in the same file. Runtime can be
improved by decreasing the precision required of the incomplete
Cholesky approximation, but performance may become worse.

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%% REFERENCES                                                         %%

[1] H. Shen, S. Jegelka and A. Gretton. Fast Kernel ICA using an 
    approximate Newton method. AISTATS 2007.
[2] S. Jegelka and A. Gretton. Brisk Kernel ICA. In: Large Scale Kernel
    Machines, MIT Press, Cambridge, Massachusetts (to appear, 2007).
[3] A. Gretton, O. Bousquet, A. Smola and B. Schoelkopf: Measuring 
    Statistical Dependence with Hilbert-Schmidt Norms. Algorithmic 
    Learning Theory: 16th International Conference, ALT 2005, 63-78.




%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%% LICENSE								%%

Version: MPL 1.1/GPL 2.0/LGPL 2.1

The contents of this file are subject to the Mozilla Public License Version
1.1 (the "License"); you may not use this file except in compliance with
the License. You may obtain a copy of the License at
http://www.mozilla.org/MPL/

Software distributed under the License is distributed on an "AS IS" basis,
WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
for the specific language governing rights and limitations under the
License.

The Original Code is Fast Kernel Independent Component Analysis using
an Approximate Newton Method.

The Initial Developers of the Original Code are
Stefanie Jegelka, Hao Shen,  Arthur Gretton, and Francis Bach.
Portions created by the Initial Developers are Copyright (C) 2007
the Initial Developers. All Rights Reserved.

Contributors:
Stefanie Jegelka,
Hao Shen,
Arthur Gretton,
Francis Bach

Alternatively, the contents of this file may be used under the terms of
either the GNU General Public License Version 2 or later (the "GPL"), or
the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
in which case the provisions of the GPL or the LGPL are applicable instead
of those above. If you wish to allow use of your version of this file only
under the terms of either the GPL or the LGPL, and not to allow others to
use your version of this file under the terms of the MPL, indicate your
decision by deleting the provisions above and replace them with the notice
and other provisions required by the GPL or the LGPL. If you do not delete
the provisions above, a recipient may use your version of this file under
the terms of any one of the MPL, the GPL or the LGPL.


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%