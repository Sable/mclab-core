function [Dtemp2] = estimate_Dtemp2(X,Y,co)
%function [Dtemp2] = estimate_Dtemp2(X,Y,co)
%Estimates Dtemp2 = \int p^a(u)q^b(u)p(u)du; for example, the Hellinger distance and the Bhattacharyya distance are simple functions of this quantity.
%
%INPUT:
%   X: X(:,t) is the t^th sample from the first distribution (X~p).
%   Y: Y(:,t) is the t^th sample from the second distribution (Y~q).
%  co: cost object (structure).
%
%Note: co.k - co.a and co.k - co.b should not be in {0,-1,-2,...}.

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
    a = co.a;
    b = co.b;
    k = co.k;

squared_distancesXX = kNN_squared_distances(X,X,co,1);
squared_distancesYX = kNN_squared_distances(Y,X,co,0);
dist_k_XX = sqrt(squared_distancesXX(end,:));
dist_k_YX = sqrt(squared_distancesYX(end,:));

c = volume_of_the_unit_ball(d);

%B:
    %I:
        B = c^(-(a+b)) * gamma(k)^2 / (gamma(k-a)*gamma(k-b));
    %II (if k is 'extreme large', say co.k=180 [=>gamma(co.k)=Inf], then use this alternative form of 'B'):
        %B = c^(-(a+b)) * exp( 2*gammaln(k) - gammaln(k-a) -gammaln(k-b) ); %identity used: gamma(a)^2 / (gamma(b) * gamma(c)) = exp(2*gammaln(a)-gammaln(b)-gammaln(c))
    
Dtemp2 = (num_of_samplesX-1)^(-a) * num_of_samplesY^(-b) * B * mean(dist_k_XX.^(-d*a).*dist_k_YX.^(-d*b));

