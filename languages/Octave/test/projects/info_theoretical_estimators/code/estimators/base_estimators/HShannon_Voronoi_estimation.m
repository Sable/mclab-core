function [H] = HShannon_Voronoi_estimation(Y,co)
%function [H] = HShannon_Voronoi_estimation(Y,co)
%Estimates the Shannon entropy (H) of Y using Voronoi regions.
%
%We use the naming convention 'H<name>_estimation' to ease embedding new entropy estimation methods.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  co: entropy estimator object.
%
%REFERENCE: 
%   Erik Miller. A new class of entropy estimators for multi-dimensional densities. International Conference on Acoustics, Speech, and Signal Processing (ICASSP), pp. 297-300, 2003. 

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

[d,num_of_samples] = size(Y);
%verification:
    if d==1
        error('The dimension of the samples must be >=2 for this estimator.');
    end

[V,C] = voronoin(Y.');
num_of_unbounded_regions = 0;
H = 0;
for k = 1 : length(C)
    if isempty(find(C{k}==1)) %bounded region
        %[c,volume] = convhulln(V(C{k},:)); 
        [c,volume] = convhulln(V(C{k},:),{'Qx'}); 
        H = H + log(num_of_samples * volume);
    else %unbounded region
	num_of_unbounded_regions = num_of_unbounded_regions + 1;
    end
end
H = H / (num_of_samples-num_of_unbounded_regions);  

