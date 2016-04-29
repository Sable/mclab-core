function [H] = HRenyi_kNN_1tok_estimation(Y,co)
%function [H] = HRenyi_kNN_1tok_estimation(Y,co)
%Estimates the Renyi entropy (H) of Y using the kNN method (S={1,...,k}).
%
%We use the naming convention 'H<name>_estimation' to ease embedding new entropy estimation methods.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  co: entropy estimator object.
%
%REFERENCE: 
%   Barnabas Poczos, Andras Lorincz. Independent Subspace Analysis Using k-Nearest Neighborhood Estimates. International Conference on Artificial Neural Networks (ICANN), pages 163-168, 2005.

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
    L = compute_length_HRenyi_kNN_1tok(Y,co);

%estimation:
    cS = compute_constS(co.alpha,[1:co.k],d,num_of_samples);
    H = log( L / (cS * num_of_samples^co.alpha) ) / (1-co.alpha);
    
  
    






