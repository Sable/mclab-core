function [D] = DL2_kNN_k_estimation(Y1,Y2,co)
%function [D] = DL2_kNN_k_estimation(Y1,Y2,co)
%Estimates the L2 divergence (D) of Y1 and Y2 using the kNN method (S={k}).
%
%We use the naming convention 'D<name>_estimation' to ease embedding new divergence estimation methods.
%
%INPUT:
%  Y1: Y1(:,t) is the t^th sample from the first distribution.
%  Y2: Y2(:,t) is the t^th sample from the second distribution. Note: the number of samples in Y1 [=size(Y1,2)] and Y2 [=size(Y2,2)] can be different.
%  co: divergence estimator object.
%
%REFERENCE: 
%   Barnabas Poczos, Zoltan Szabo, Jeff Schneider: Nonparametric divergence estimators for Independent Subspace Analysis. European Signal Processing Conference (EUSIPCO), pages 1849-1853, 2011.
%   Barnabas Poczos, Liang Xiong, Jeff Schneider. Nonparametric Divergence: Estimation with Applications to Machine Learning on Distributions. Uncertainty in Artificial Intelligence (UAI), 2011.

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

[dY2,num_of_samplesY2] = size(Y2);
[dY1,num_of_samplesY1] = size(Y1);

%verification:
    if dY1~=dY2
        error('The dimension of the samples in Y1 and Y2 must be equal.');
    end

d = dY1;
c = volume_of_the_unit_ball(d);%volume of the d-dimensional unit ball

squared_distancesY1Y1 = kNN_squared_distances(Y1,Y1,co,1);
squared_distancesY2Y1 = kNN_squared_distances(Y2,Y1,co,0);
dist_k_Y1Y1 = sqrt(squared_distancesY1Y1(end,:));
dist_k_Y2Y1 = sqrt(squared_distancesY2Y1(end,:));

term1 = mean(dist_k_Y1Y1.^(-d)) * (co.k-1) / ((num_of_samplesY1-1)*c);
term2 = mean(dist_k_Y2Y1.^(-d)) * 2 * (co.k-1) / (num_of_samplesY2*c);
term3 = mean((dist_k_Y1Y1.^d) ./ (dist_k_Y2Y1.^(2*d))) *  (num_of_samplesY1 - 1) * (co.k-2) * (co.k-1) / (num_of_samplesY2^2*c*co.k);
L2 = term1-term2+term3;
%D = sqrt(L2);%theoretically
D = sqrt(abs(L2));%due to the finite number of samples L2 can be negative. In this case: sqrt(L2) is complex; to avoid such values we take abs().
