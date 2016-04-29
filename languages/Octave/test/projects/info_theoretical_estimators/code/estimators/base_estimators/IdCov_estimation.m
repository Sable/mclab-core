function [I] = IdCov_estimation(Y,ds,co)
%function [I] = IdCov_estimation(Y,ds,co)
%Estimates distance covariance (I) using pairwise distances of the sample points. 
%
%We use the naming convention 'I<name>_estimation' to ease embedding new mutual information estimation methods.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  ds: subspace dimensions. ds(m) = dimension of the m^th subspace, m=1,...,M (M=length(ds)).
%  co: mutual information estimator object.
%
%REFERENCE: 
%   Gabor J. Szekely and Maria L. Rizzo. Brownian distance covariance. The Annals of Applied Statistics, 3:1236-1265, 2009.
%   Gabor J. Szekely, Maria L. Rizzo, and Nail K. Bakirov. Measuring and testing dependence by correlation of distances. The Annals of Statistics, 35:2769-2794, 2007.

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

[d,num_of_samples] = size(Y); %dimension, number of samples

%verification:
    if sum(ds)~=d;
        error('The subspace dimensions are not compatible with Y.');
    end
    if length(ds)~=2
        error('There must be two subspaces for this estimator.');
    end
    
A = compute_dCov_dCor_statistics(Y(1:ds(1),:),co.alpha);
B = compute_dCov_dCor_statistics(Y(ds(1)+1:ds(1)+ds(2),:),co.alpha);
I = sqrt(sum(sum(A.*B))) / num_of_samples;
