function [H] = HRenyi_MST_estimation(Y,co)
%function [H] = HRenyi_MST_estimation(Y,co)
%Estimates the Renyi entropy (H) of Y using the minimum spanning tree (MST).
%
%We use the naming convention 'H<name>_estimation' to ease embedding new entropy estimation methods.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  co: entropy estimator object.
%
%REFERENCE: 
%   Joseph E. Yukich. Probability Theory of Classical Euclidean Optimization Problems, Lecture Notes in Mathematics, 1998, vol. 1675.

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

[d,num_of_samples] = size(Y);%dimension, number of samples

%length (L):
    L = compute_length_HRenyi_MST(Y,co);

%estimation:
    if co.additive_constant_is_relevant %the additive constant is relevant in the Renyi entropy estimation
        FN = filename_of_HRenyi_constant(d,co);
        if exist(FN)
            load(FN,'constant');
            H = log( L / (constant*num_of_samples^co.alpha) ) / (1-co.alpha);
        else
            error('The file containing the additive constant does not exist. You can precompute the additive constant via estimate_HRenyi_constant.m.');
        end
    else %estimation up to an additive constant
        H = log( L / num_of_samples^co.alpha ) / (1-co.alpha);
    end
