function  [e,de] = sample_subspaces_ikeda(num_of_comps,num_of_samples)
%function  [e,de] = sample_subspaces_ikeda(num_of_comps,num_of_samples)
%Sampling from the 'ikeda' dataset; number of subspaces: num_of_comps; number of samples: num_of_samples.
%
%OUTPUT:
%   e: e(:,t) is the t^th sample. size(e,2) = num_of_samples.
%   de: subspace dimensions.
%EXAMPLE:
%   e = sample_subspaces_ikeda(2,1000);
%
%REFERENCE:
%   Zoltan Szabo, Barnabas Poczos. Nonparametric Independent Process Analysis. European Signal Processing Conference (EUSIPCO), pp. 1718-1722, 2011.

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

%verification:
    if num_of_comps ~= 2
        error('The number of components must be 2.');
    end
    
e1 = sample_subspaces_ikeda_map(num_of_samples,20,20,0.9994);
e2 = sample_subspaces_ikeda_map(num_of_samples,-100,30,0.998);
e = [e1;e2];
de = ones(num_of_comps,1) * 2;

%--------------------
function [e] = sample_subspaces_ikeda_map(num_of_samples,x0,y0,lam)
%Simulates the ikeda map.
%
%OUTPUT:
%   e: e(:,t) is the t^th sample.

e = zeros(2,num_of_samples);%preallocation
%initial point:
    e(1,1) = x0;
    e(2,1) = y0;
for t = 2 : num_of_samples
    w = 0.4 - 6 / (1+sum(e(:,t-1).^2));
    e(:,t) = [1;0] + lam * [cos(w),-sin(w);sin(w),cos(w)] * e(:,t-1);
end
