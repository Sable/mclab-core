function [perm,ds_hat] = clusterindicator2perm(clusterindicators)
%function [perm,ds_hat] = clusterindicator2perm(clusterindicators)
%Transforms the output of spectral clustering (clusterindicators) to permutation (perm) and return the estimated cluster sizes (ds_hat).

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

[D,num_of_comps] = size(clusterindicators);
start_idx = 1;
perm = zeros(D,1);%preallocation
ds_hat = zeros(num_of_comps,1);%preallocation
for k = 1 : num_of_comps
    I = find(clusterindicators(:,k));%indices of the coordinates belonging to the k^th subspace
    dk = length(I);
    ds_hat(k) = dk;        
    perm(start_idx:start_idx+dk-1) = I;
    start_idx = start_idx + dk;
end
