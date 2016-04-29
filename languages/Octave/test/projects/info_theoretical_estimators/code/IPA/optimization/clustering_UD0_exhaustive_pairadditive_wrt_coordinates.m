function [p_min] = clustering_UD0_exhaustive_pairadditive_wrt_coordinates(S,ds)
%function [p_min] = clustering_UD0_exhaustive_pairadditive_wrt_coordinates(S,ds)
%Clusters the 'ICA elements' to subspaces of given dimensions (ds) using (i) 'exhaustive' clustering and
%cost which is pair-additive with respect to the coordinates (see cost_type
%= 'Ipairwise1d'). This function is an exhaustive variant of
%'clustering_UD0_greedy_pairadditive_wrt_coordinates.m'.
%
%INPUT:
%   S: have non-negative elements and symmetric (S^T=S), i.e., S = [I((s_ICA)_i,(s_ICA)_j)].
%   ds: subspace dimensions.
%OUTPUT:
%  p_min: permutation of the ICA elements (in matrix form).

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
    
disp('Clustering of the ICA elements (exhaustive search): started.');    

%optimization:
    D = sum(ds);
    %mask (corresponding to coordinate pairs from different subspaces):
        mask = ds_mask(ds);
    p_universe = perms(1:D);%all possible permutations; we do not take into account subspace invariances.
    cost_min = Inf;
    for k = 1 : size(p_universe,1)
        p = p_universe(k,:);
        cost = sum(sum(S(p,p).*mask));
        if cost < cost_min
            cost_min = cost;
            p_min = p;
        end
    end
    
disp('Clustering of the ICA elements: ready.');