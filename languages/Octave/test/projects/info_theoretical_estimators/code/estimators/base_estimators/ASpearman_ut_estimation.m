function [A] = ASpearman_ut_estimation(Y,ds,co)
%function [A] = ASpearman_ut_estimation(Y,ds,co)
%Estimates the multivariate conditional version of Spearman's rho; the measure weights the upper tail of the copula. The estimation is based on the empirical copula.
%
%We use the naming convention 'A<name>_estimation' to ease embedding new association measure estimator methods.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  ds: subspace dimensions. ds(m) = dimension of the m^th subspace, m=1,...,M (M=length(ds)).
%  co: association measure estimator object.
%
%REFERENCE:
%   Friedrich Schmid and Rafael Schmidt. Multivariate conditional versions of Spearman's rho and related measures of tail dependence. Journal of Multivariate Analysis, 98:1123-1140, 2007.
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

p = co.p;

c = mean(prod(1 - max(U,1-p)));
c1 = (p*(2-p)/2)^d;
c2 = p^d * (d+1-p*d) / (d+1);

A = (c-c1) / (c2-c1);

