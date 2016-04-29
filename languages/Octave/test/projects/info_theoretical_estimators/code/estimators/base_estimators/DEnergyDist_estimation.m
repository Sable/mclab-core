function [D] = DEnergyDist_estimation(Y1,Y2,co)
%function [D] = DEnergyDist_estimation(Y1,Y2,co)
%Estimates the energy distance (D) using pairwise distances of the sample points.
%
%We use the naming convention 'D<name>_estimation' to ease embedding new divergence estimation methods.
%
%INPUT:
%  Y1: Y1(:,t) is the t^th sample from the first distribution.
%  Y2: Y2(:,t) is the t^th sample from the second distribution.
%  co: divergence estimator object.
%
%REFERENCE:
%   Gabor J. Szekely and Maria L. Rizzo. A new test for multivariate normality. Journal of Multivariate Analysis, 93:58-80, 2005. (metric space of negative type)
%   Gabor J. Szekely and Maria L. Rizzo. Testing for equal distributions in high dimension. InterStat, 5, 2004. (R^d)

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
    [dY1,num_of_samplesY1] = size(Y1);
    [dY2,num_of_samplesY2] = size(Y2);

    if dY1~=dY2
        error('The dimension of the samples in Y1 and Y2 must be equal.');
    end
    
%Euclidean distance:
distances_Y1Y1 = sqrt(sqdistance(Y1));
distances_Y2Y2 = sqrt(sqdistance(Y2));
distances_Y1Y2 = sqrt(sqdistance(Y1,Y2));

D =  2 * sum(sum(distances_Y1Y2)) / (num_of_samplesY1*num_of_samplesY2) -  sum(sum(distances_Y1Y1)) / (num_of_samplesY1^2) -  sum(sum(distances_Y2Y2)) / (num_of_samplesY2^2);
D = abs(D); %abs(): numerically very small negative values might appear
