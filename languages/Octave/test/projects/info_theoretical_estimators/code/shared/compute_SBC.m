function [SBC] = compute_SBC(L,Lmax,num_of_samples,innovation_hat)
%function [SBC] = compute_SBC(L,Lmax,num_of_samples,innovation_hat)
%Computes the Schwarz's Bayesian Criterion (SBC) for AR processes.
%
%INPUT:
%   L: order of the estimated model is p [AR(p)].
%   Lmax: maximal order estimated.
%   num_of_samples: number of samples in the observation.
%   innovation_hat: estimated innovation_hat.
%OUTPUT:
%   SBC: computed Schwarz's Bayesian Criterion.
%REFERENCE:
%	Arnold Neumaier, Tapio Schneider. Estimation of parameters and eigenmodes of multivariate autoregressive models. ACM Transactions on Mathematical Software volume 27, issue 1, pages 27-57, 2001.

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

D = size(innovation_hat,1); %dimension of the AR process
SBC_1 = log(det(innovation_hat * innovation_hat.')) / D;

N = num_of_samples - Lmax;%number of effective samples
np = D * L ;%number of parameters estimated
SBC_2 = log(N) * (N-np)/N;
    
SBC = SBC_1 - SBC_2;

