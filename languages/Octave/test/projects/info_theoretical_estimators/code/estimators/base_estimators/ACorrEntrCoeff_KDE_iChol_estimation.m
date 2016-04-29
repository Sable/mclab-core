function [A] = ACorrEntrCoeff_KDE_iChol_estimation(Y,ds,co)
%function [A] = ACorrEntrCoeff_KDE_iChol_estimation(Y,ds,co)
%Estimates the correntropy coefficient (A) using Gaussian KDE (kernel density estimation) + incomplete Cholesky decomposition.
%
%We use the naming convention 'A<name>_estimation' to ease embedding new association measure estimator methods.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  ds: subspace dimensions. ds(m) = dimension of the m^th subspace, m=1,...,M (M=length(ds)).
%  co: association measure estimator object.
%
%REFERENCE:
%   Murali Rao, Sohan Seth, Jianwu Xu, Yunmei Chen, Hemant Tagare, and Jose C. Principe. A test of independence based on a generalized correlation function. Signal Processing, 91:15-27, 2011.
%   Sohan Seth and Jose C. Principe. On speeding up computation in information theoretic learning. In International Joint Conference on Neural Networks (IJCNN), pages 2883-2887, 2009.

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
    if ~one_dimensional_problem(ds) || length(ds)~=2
        error('There must be 2 pieces of one-dimensional subspaces (coordinates) for this estimator.');
    end

A = correncoef(Y(1,:).',Y(2,:).',co.sigma);
