This folder contains Matlab implementations of several ITL estimators

For more information please see the following papers,
1. "On speeding up computation in information theoretic learning" 
        by Seth and Principe, in IJCNN 2009, and 
2. "A test of independence based on a generalized correlation function" 
        by Rao, Seth, Xu, Chen, Tagare, and Principe in Signal Processing 2011

Content:

centcorren                  : Parametric centered correntropy (Faster implementation)
centcorrenexp               : Parametric centered correntropy using Laplacian kernel (Faster implementation)
centtempcorren              : Centered temporal correntropy (Faster implementation by Steven Van Vaerenbergh)
cip                         : Cross Information potential (Faster implementation)
cipexp                      : Cross Information potential using Laplacian kernel (Faster implementation by Il Park)
corren                      : Parametric correntropy
correncoef                  : Correntropy Coefficient (Faster implementation)
correncoef_2                : Correntropy Coefficient (Direct computation)
d_ed                        : Euclidean distance (Faster implementation)
d_cs                        : Cauchy-Schwartz divergence (Faster implementation)
demo_centcorrenexp          : Demo
demo_centtempcorren         : Demo (by Steven Van Vaerenbergh)
incompleteCholesky.m		: Incomplete Cholesky Decomposition
incompleteCholeskyMulti.m	: Same (multidimensional variable)
incompleteCholeskySigma.m	: Same (with kernel size)
ip                          : Information potential (Faster implementation)
main.m                      : Code for the experiment in IJCNN paper
qmi_cs                      : Cauchy-Schwartz mutual information (Faster implementation)
qmi_cs_2                    : Cauchy-Schwartz mutual information (Direct computation)
qmi_ed                      : Euclidean distance based mutual information (Faster implementaton)

License:
This program is free software: you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by the Free 
Software Foundation, version 3 (http://www.gnu.org/licenses).