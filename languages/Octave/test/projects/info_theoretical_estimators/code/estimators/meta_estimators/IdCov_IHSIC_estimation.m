function [I] = IdCov_IHSIC_estimation(Y,ds,co)
%function [I] = IdCov_IHSIC_estimation(Y,ds,co)
%Estimates distance covariance based on the formula: [I(y^1,y^2;rho_1,rho_2)]^2 = 4 [HSIC(y^1,y^2;k)]^2, where HSIC stands for the Hilbert-Schmidt independence criterion, y=[y^1;y^2] has density f, y^i-s have density f_i-s, and k=k_1 x k_2, where k_i-s generates rho_i-s, semimetrics of negative type used in distance covariance.
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
%   Dino Sejdinovic, Arthur Gretton, Bharath Sriperumbudur, and Kenji Fukumizu. Hypothesis testing using pairwise distances and associated kernels. International Conference on Machine Learning (ICML), pages 1111-1118, 2012. (equivalence to HSIC)
%   Russell Lyons. Distance covariance in metric spaces. Annals of Probability, 2012. (To appear. http://php.indiana.edu/~rdlyons/pdf/dcov.pdf; http://arxiv.org/abs/1106.5758; generalized distance covariance, rho_i; equivalence to HSIC).
%   Gabor J. Szekely and Maria L. Rizzo and. Brownian distance covariance. The Annals of Applied Statistics, 3:1236-1265, 2009. (distance covariance)
%   Gabor J. Szekely, Maria L. Rizzo, and Nail K. Bakirov. Measuring and testing dependence by correlation of distances. The Annals of Statistics, 35:2769-2794, 2007. (distance covariance)

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
    if length(ds)~=2
        error('There must be two subspaces for this estimator.');
    end

I = 2 * abs(I_estimation(Y,ds,co.member_co));

