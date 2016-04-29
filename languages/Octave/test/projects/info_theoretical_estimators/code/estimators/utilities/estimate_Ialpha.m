function [I_alpha] = estimate_Ialpha(Y,co)
%function [I_alpha] = estimate_Ialpha(Y,co)
%Estimates I_alpha = \int p^{\alpha}(y)dy, the Renyi and the Tsallis entropies are simple functions of this quantity. Here, alpha:=co.alpha.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample from the distribution having density p.
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

[d,num_of_samples] = size(Y);
squared_distances = kNN_squared_distances(Y,Y,co,1);

V = volume_of_the_unit_ball(d);

%C:
    %I:
        C = ( gamma(co.k)/gamma(co.k+1-co.alpha) )^(1/(1-co.alpha));
    %II (if k is 'extreme large', say co.k=180 [=>gamma(co.k)=Inf], then use this alternative form of 'C'):
        %C = exp( gammaln(co.k) - gammaln(co.k+1-co.alpha) )^(1/(1-co.alpha)); %used identity: gamma(a) / gamma(b) = exp( gammaln(a) - gammaln(b) )
        
s = sum( squared_distances(co.k,:).^(d*(1-co.alpha)/2) ); %'/2' <= squared distances
I_alpha = (num_of_samples-1) / num_of_samples * V^(1-co.alpha) * C^(1-co.alpha) * s / (num_of_samples-1)^(co.alpha);
