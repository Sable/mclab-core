function [D] = DEnergyDist_DMMD_estimation(Y1,Y2,co)
%function [D] = DEnergyDist_DMMD_estimation(Y1,Y2,co)
%Estimates the energy distance (D) according to the relation: D(f_1,f_2;rho) = 2 [MMD(f_1,f_2;k)]^2, where MMD denotes maximum mean discrepancy and k is a kernel that generates rho, a semimetric of negative type.
%
%Note:
%   1)We use the naming convention 'D<name>_estimation' to ease embedding new divergence estimation methods.
%   2)This is a meta method: the MMD estimator can be arbitrary.
%
%INPUT:
%  Y1: Y1(:,t) is the t^th sample from the first distribution.
%  Y2: Y2(:,t) is the t^th sample from the second distribution.
%  co: divergence estimator object.
%
%REFERENCE:
%   Dino Sejdinovic, Arthur Gretton, Bharath Sriperumbudur, and Kenji Fukumizu. Hypothesis testing using pairwise distances and associated kernels. International Conference on Machine Learning (ICML), pages 1111-1118, 2012. (semimetric space; energy distance <=> MMD, with a suitable kernel)
%   Russell Lyons. Distance covariance in metric spaces. Annals of Probability, 41:3284-3305, 2013. (energy distance, metric space of negative type; pre-equivalence to MMD).
%   Gabor J. Szekely and Maria L. Rizzo. A new test for multivariate normality. Journal of Multivariate Analysis, 93:58-80, 2005. (energy distance; metric space of negative type)
%   Gabor J. Szekely and Maria L. Rizzo. Testing for equal distributions in high dimension. InterStat, 5, 2004. (energy distance; R^d)

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
    if size(Y1,1)~=size(Y2,1)
        error('The dimension of the samples in Y1 and Y2 must be equal.');
    end

D =  2 * ( D_estimation(Y1,Y2,co.member_co) )^2;
