#Information Theoretical Estimators (ITE) Toolbox

News: ITE has

- appeared in JMLR \[[abstract](http://jmlr.org/papers/v15/szabo14a.html), [PDF](http://jmlr.org/papers/volume15/szabo14a/szabo14a.pdf)\]
- been presented at [NIPS-2013: MLOSS workshop](http://mloss.org/workshop/nips13/) \[[PDF](http://www.gatsby.ucl.ac.uk/~szabo/publications/szabo13information.pdf), [PDF (highlight)](http://www.gatsby.ucl.ac.uk/~szabo/publications/szabo13information_highlight.pdf)\].

* * *

ITE is capable of estimating many different variants of entropy, mutual information, divergence, association measures, cross quantities, and kernels on distributions. Thanks to its highly modular design, ITE supports additionally 

- the combinations of the estimation techniques, 
- the easy construction and embedding of novel information theoretical estimators, and 
- their immediate application in information theoretical optimization problems. 

ITE is 

- written in Matlab/Octave,
- multi-platform (tested extensively on Windows and Linux),
- free and open source (released under the GNU GPLv3(>=) license).

* * *

ITE can estimate 

- `entropy (H)`: Shannon entropy, Rényi entropy, Tsallis entropy (Havrda and Charvát entropy), complex entropy, Phi-entropy (f-entropy), Sharma-Mittal entropy,
- `mutual information (I)`: generalized variance, kernel canonical correlation analysis, kernel generalized variance, Hilbert-Schmidt independence criterion, Shannon mutual information (total correlation, multi-information), L2 mutual information, Rényi mutual information, Tsallis mutual information, copula-based kernel dependency, multivariate version of Hoeffding's Phi, Schweizer-Wolff's sigma and kappa, complex mutual information, Cauchy-Schwartz quadratic mutual information, Euclidean distance based quadratic mutual information, distance covariance, distance correlation, approximate correntropy independence measure, chi-square mutual information (Hilbert-Schmidt norm of the normalized cross-covariance operator, squared-loss mutual information,  mean square contingency), Lancaster three-variable interaction,
- `divergence (D)`: Kullback-Leibler divergence (relative entropy, I directed divergence), L2 divergence, Rényi divergence, Tsallis divergence, Hellinger distance, Bhattacharyya distance, maximum mean discrepancy (kernel distance), J-distance (symmetrised Kullback-Leibler divergence, J divergence), Cauchy-Schwartz divergence, Euclidean distance based divergence, energy distance (specially the Cramer-Von Mises distance), Jensen-Shannon divergence, Jensen-Rényi divergence, K divergence, L divergence, f-divergence (Csiszár-Morimoto divergence, Ali-Silvey distance), non-symmetric Bregman distance (Bregman divergence), Jensen-Tsallis divergence, symmetric Bregman distance, Pearson chi square divergence (chi square distance), Sharma-Mittal divergence,
- `association measures (A)`, including `measures of concordance`: multivariate extensions of Spearman's rho (Spearman's rank correlation coefficient, grade correlation coefficient), correntropy, centered correntropy, correntropy coefficient, correntropy induced metric, centered correntropy induced metric, multivariate extension of Blomqvist's beta (medial correlation coefficient), multivariate conditional version of Spearman's rho, lower/upper tail dependence via conditional Spearman's rho,
- `cross quantities (C)`: cross-entropy,
- `kernels on distributions (K)`: expected kernel (summation kernel, mean map kernel, set kernel, multi-instance kernel, ensemble kernel; special convolution kernel), Bhattacharyya kernel (Bhattacharyya coefficient, Hellinger affinity), probability product kernel, Jensen-Shannon kernel, exponentiated Jensen-Shannon kernel, exponentiated Jensen-Renyi kernel(s), Jensen-Tsallis kernel, exponentiated Jensen-Tsallis kernel(s).

ITE offers 

- solvers for `Independent Subspace Analysis (ISA) and its extensions` to different linear-, controlled-, post nonlinear-, complex valued-, partially observed models, as well as to systems with nonparametric source dynamics, 
- several `consistency tests` (analytical vs estimated value),
- illustrations for `information theoretical image registration` and `distribution regression` (supervised entropy learning, and aerosol prediction based on multispectral satellite images).

* * *

**Citing**: If you use the ITE toolbox in your research, please cite [.bib](http://www.gatsby.ucl.ac.uk/~szabo/ITE.bib).

**Download** the latest release: 

- code: [zip](https://bitbucket.org/szzoli/ite/downloads/ITE-0.62_code.zip), [tar.bz2](https://bitbucket.org/szzoli/ite/downloads/ITE-0.62_code.tar.bz2), 
- documentation: [pdf](https://bitbucket.org/szzoli/ite/downloads/ITE-0.62_documentation.pdf).

**Note**: the evolution of the ITE code is briefly summarized in CHANGELOG.txt.

* * *

**ITE mailing list**: You can [sign up](https://groups.google.com/d/forum/itetoolbox) here.

**Follow ITE**: on [Bitbucket](https://bitbucket.org/szzoli/ite/follow), on [Twitter](https://twitter.com/ITEtoolbox).

**Share your ITE application** (reference/link): using [Wiki](https://bitbucket.org/szzoli/ite/wiki).

