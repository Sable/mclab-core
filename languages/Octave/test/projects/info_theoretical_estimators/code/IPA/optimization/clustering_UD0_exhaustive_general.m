function [p_min] = clustering_UD0_exhaustive_general(s_ICA,ds,cost_type,co)
%function [p_min] = clustering_UD0_exhaustive_general(s_ICA,ds,cost_type,co)
%Clusters the ICA (s_ICA) elements to subspaces of given dimensions (ds) using (i) 'exhaustive' clustering and
%(ii) general cost function.
%
%INPUT:
%   s_ICA: s_ICA(:,t) is the t^th sample, estimated source at time t.
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
    
disp('Clustering of the ICA elements (exhaustive optimization): started.');    

%optimization:
    D = sum(ds);
    p_universe = perms(1:D);%all possible permutations; we do not take into account subspace invariances.
    cost_min = Inf;
    for k = 1 : size(p_universe,1)
        p = p_universe(k,:);
        cost = cost_general(s_ICA(p,:),ds,cost_type,co);
        if cost < cost_min
            cost_min = cost;
            p_min = p;
        end
    end
    
disp('Clustering of the ICA elements: ready.');