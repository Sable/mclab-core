function [Dtemp1] = estimate_Dtemp1(X,Y,co)
%function [Dtemp1] = estimate_Dtemp1(X,Y,co)
%Estimates Dtemp1 = \int p^{\alpha}(u)q^{1-\alpha}(u)du, the Renyi and the Tsallis divergences are simple functions of this quantity.
%
%INPUT:
%   X: X(:,t) is the t^th sample from the first distribution (X~p).
%   Y: Y(:,t) is the t^th sample from the second distribution (Y~q).
%  co: cost object (structure).
%
%Note: co.k + 1 - co.alpha should not be in {0,-1,-2,...}.

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

%initialization:
    [dY,num_of_samplesY] = size(Y);
    [dX,num_of_samplesX] = size(X);

%verification:
    if dX~=dY
        error('The dimension of the samples in X and Y must be equal.');
    end

%initialization - continued:
    d = dX; %=dY

squared_distancesXX = kNN_squared_distances(X,X,co,1);
squared_distancesYX = kNN_squared_distances(Y,X,co,0);
dist_k_XX = sqrt(squared_distancesXX(end,:));
dist_k_YX = sqrt(squared_distancesYX(end,:));

%B:
    %I:
        B = gamma(co.k)^2 / (gamma(co.k-co.alpha+1)*gamma(co.k+co.alpha-1));
    %II (if k is 'extreme large', say co.k=180 [=>gamma(co.k)=Inf], then use this alternative form of 'B'):
        %B = exp( 2*gammaln(co.k) - gammaln(co.k-co.alpha+1) -gammaln(co.k+co.alpha-1)); %identity used: gamma(a)^2 / (gamma(b) * gamma(c)) = exp(2*gammaln(a)-gammaln(b)-gammaln(c))

Dtemp1 = mean( ((num_of_samplesX-1)/num_of_samplesY * (dist_k_XX./dist_k_YX).^d).^(1-co.alpha))  * B;
