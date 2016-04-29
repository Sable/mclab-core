function [A] = ASpearman4_estimation(Y,ds,co)
%function [A] = ASpearman4_estimation(Y,ds,co)
%Estimates the fourth multivariate extension of Spearman's rho, the average of pairwise Spearman's rho using empirical copulas.
%
%We use the naming convention 'A<name>_estimation' to ease embedding new association measure estimator methods.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  ds: subspace dimensions. ds(m) = dimension of the m^th subspace, m=1,...,M (M=length(ds)).
%  co: association measure estimator object.
%
%REFERENCE:
%   Friedrich Schmid, Rafael Schmidt, Thomas Blumentritt, Sandra Gaiser, and Martin Ruppert. Copula Theory and Its Applications, Chapter Copula based Measures of Multivariate Association. Lecture Notes in Statistics. Springer, 2010.
%   Friedrich Schmid and Rafael Schmidt. Multivariate extensions of Spearman's rho and related statistics. Statistics & Probability Letters, 77:407-416, 2007.
%   Maurice G. Kendall. Rank correlation methods. London, Griffin, 1970.
%   C. Spearman. The proof and measurement of association between two things. The American Journal of Psychology, 15:72-101, 1904.

%Copyright (C) 2012- Zoltan Szabo ("http://www.gatsby.ucl.ac.uk/~szabo/", "zoltan (dot) szabo (at) gatsby (dot) ucl (dot) ac (dot) uk")
%
%This file is part of the ITE (Information Theoretical Estimators) toolbox.
%
%ITE is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by
%the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
%
%This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
%MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
%
%You should have received a copy of the GNU General Public License along with ITE. If not, see <http://www.gnu.org/licenses/>.

%co.mult:OK. The information theoretical quantity of interest can be (and is!) estimated exactly [co.mult=1]; the computational complexity of the estimation is essentially the same as that of the 'up to multiplicative constant' case [co.mult=0]. In other words, the estimation is carried out 'exactly' (instead of up to 'proportionality').

%verification:
    if sum(ds) ~= size(Y,1);
        error('The subspace dimensions are not compatible with Y.');
    end
    if ~one_dimensional_problem(ds)
        error('The subspaces must be one-dimensional for this estimator.');
    end
    
[d,num_of_samples] = size(Y); %dimension, number of samples
U = copula_transformation(Y);

M_triu = triu(ones(d),1);%upper triangular mask
b = nchoosek(d,2);
A = 12 * sum(sum(((1-U)*(1-U).').*M_triu)) / (b*num_of_samples) -3;

