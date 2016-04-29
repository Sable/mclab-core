function [D_S] = DsymBregman_DBregman_estimation(Y1,Y2,co)
%function [D_S] = DsymBregman_DBregman_estimation(Y1,Y2,co)
%Estimates the symmetric Bregman distance (D) of Y1 and Y2 using the relation: D_S = (D_NS(f1,f2) + D_NS (f2,f1)) / alpha, where D_NS is the nonsymmetric Bregman distance. 
%
%Note:
%   1)We use the naming convention 'D<name>_estimation' to ease embedding new divergence estimation methods.
%   2)This is a meta method: the (nonsymmetric) Bregman distance estimator can arbitrary.
%
%INPUT:
%  Y1: Y1(:,t) is the t^th sample from the first distribution (f1).
%  Y2: Y2(:,t) is the t^th sample from the second distribution (f2). Note: the number of samples in Y1 [=size(Y1,2)] and Y2 [=size(Y2,2)] can be different.
%  co: divergence estimator object.
%
%REFERENCE: 
%   Nikolai Leonenko, Luc Pronzato, and Vippal Savani. A class of Renyi information estimators for multidimensional densities. Annals of Statistics, 36(5):2153-2182, 2008.
%   Imre Csiszar. Generalized projections for non-negative functions. Acta Mathematica Hungarica, 68:161-185, 1995.
%   Lev M. Bregman. The relaxation method of finding the common points of convex sets and its application to the solution of problems in convex programming. USSR Computational Mathematics and Mathematical Physics, 7:200-217, 1967.

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

D_S = (D_estimation(Y1,Y2,co.member_co) + D_estimation(Y2,Y1,co.member_co)) / co.alpha;

