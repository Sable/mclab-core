function [Dtemp3] = estimate_Dtemp3(Y1,Y2,co)
%function [Dtemp3] = estimate_Dtemp3(Y1,Y2,co)
%Estimates Dtemp3 = \int p(u)q^{a-1}(u)du; used in Bregman distance computation.
%
%INPUT:
%   Y1: Y1(:,t) is the t^th sample from the first distribution (Y1~p).
%   Y2: Y2(:,t) is the t^th sample from the second distribution (Y2~q).
%  co: cost object (structure).
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
    [dY1,num_of_samplesY1] = size(Y1);
    [dY2,num_of_samplesY2] = size(Y2);
    
%verification:
    if dY1~=dY2
        error('The dimension of the samples in Y1 and Y2 must be equal.');
    end
    
d = dY1; %=dY2
a = co.alpha;
k = co.k;

squared_distancesY1Y2 = kNN_squared_distances(Y2,Y1,co,0);
V = volume_of_the_unit_ball(d);

%Ca:
    %I:
        Ca = ( gamma(k)/gamma(k+1-a) ); %C^a
    %II (if k is 'extreme large', say co.k=180 [=>gamma(co.k)=Inf], then use this alternative form of 'Ca'):
        %Ca = exp( gammaln(k) - gammaln(k+1-a) ); %used identity: gamma(a) / gamma(b) = exp( gammaln(a) - gammaln(b) ) 

Dtemp3 = num_of_samplesY2^(1-a) * Ca * V^(1-a) * mean(squared_distancesY1Y2(co.k,:).^(d*(1-a)/2)); %/2 <= squared distances


