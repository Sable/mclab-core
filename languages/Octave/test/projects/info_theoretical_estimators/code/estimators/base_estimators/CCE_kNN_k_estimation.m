function [CE] = CCE_kNN_k_estimation(Y1,Y2,co)
%function [CE] = CCE_kNN_k_estimation(Y1,Y2,co)
%Estimates the cross-entropy (CE) of Y1 and Y2 using the kNN method (S={k}).
%
%We use the naming convention 'C<name>_estimation' to ease embedding new cross quantity estimation methods.
%
%INPUT:
%  Y1: Y1(:,t) is the t^th sample from the first distribution.
%  Y2: Y2(:,t) is the t^th sample from the second distribution. Note: the number of samples in Y1 [=size(Y1,2)] and Y2 [=size(Y2,2)] can be different.
%  co: cross quantity estimator object.
%
%REFERENCE: 
%   Nikolai Leonenko, Luc Pronzato, and Vippal Savani. A class of Renyi information estimators for multidimensional densities. Annals of Statistics, 36(5):2153-2182, 2008.

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

d1 = size(Y1,1);
[d2,num_of_samples2] = size(Y2);

%verification:
    if d1~=d2
        error('The dimension of the samples in Y1 and Y2 must be equal.');
    end

d = d1;
V = volume_of_the_unit_ball(d); 
squared_distancesY1Y2 = kNN_squared_distances(Y2,Y1,co,0);
dist_k_Y1Y2 = sqrt(squared_distancesY1Y2(end,:));
CE = log(V) + log(num_of_samples2) - psi(co.k) + d * mean(log(dist_k_Y1Y2));

