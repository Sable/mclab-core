function [I] = IMMD_DMMD_estimation(Y,ds,co)
%function [I] = IMMD_DMMD_estimation(Y,ds,co)
%Estimates mutual information (I) using the MMD (maximum mean discrepancy)
%method: I(Y_1,...,Y_d) = MMD(P_Z,P_U), where (i) Z =[F_1(Y_1);...;F_d(Y_d)] is the copula transformation of Y; F_i is the cdf of Y_i, (ii) P_U is the uniform distribution on [0,1]^d, (iii) dim(Y_1) = ... = dim(Y_d) = 1.
%
%Note:
%   1)We use the naming convention 'I<name>_estimation' to ease embedding new mutual information estimation methods.
%   2)This is a meta method: the MMD estimator can be arbitrary.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  ds: subspace dimensions. ds(m) = dimension of the m^th subspace, m=1,...,M (M=length(ds)).
%  co: mutual information estimator object.
%
%REFERENCE:
%   Barnabas Poczos, Zoubin Ghahramani, Jeff Schneider. Copula-based Kernel Dependency Measures. International Conference on Machine Learning (ICML), 2012.

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

Z = copula_transformation(Y);
U = rand(size(Z));
I = D_estimation(Z,U,co.member_co);

