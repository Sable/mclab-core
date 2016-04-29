function [per] = clustering_UD0_greedy_general(s_ICA,ds,cost_type,co)
%function [per] = clustering_UD0_greedy_general(s_ICA,ds,cost_type,co)
%Clusters the ICA (s_ICA) elements to subspaces of given dimensions (ds)
%using (i) 'greedy' clustering and (ii) a general ISA cost. 
%
%Note: for special ISA objectives (e.g., pairwise additive with respect to the
%subspaces), more efficient optimizations are also available (see 'clustering_UD0_greedy_additive_wrt_subspaces.m', 
%'clustering_UD0_greedy_pairadditive_wrt_subspaces.m', 'clustering_UD0_greedy_pairadditive_wrt_coordinates.m').
%
%INPUT:
% s_ICA: s_ICA(:,t) is the t^th estimated realization of the ICA sources.
%    ds: subspace dimensions.
%    co: is the initialized cost object (entropy/mutual information).
%OUTPUT:
%   per: permutation of the ICA elements.

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
    D = sum(ds); %dim(source)
    per = [1:D]; %the actual permutation vector
    cum_ds = cumsum([1;ds(1:end-1)]);%1,d_1+1,d_1+d_2+1,...,d_1+...+d_{M-1}+1 = starting indices of the subspaces (M=number of subspaces).
    %cost:
         cost = cost_general(s_ICA,ds,cost_type,co);
    it_max = 30; %maximal number of iterations        
    it = 1;      
    
disp('Clustering of the ICA elements (greedy optimization): started.'); 
while (it <= it_max)
    cost_changed = 0;
    %p and q are coordinates belonging to different subspaces; change them if it decreases the cost:
        for p = 1  : D - ds(end)
            ind_of_p_subspace = sum(cum_ds<=p);
            for q =  cum_ds(ind_of_p_subspace+1) : D
                %candidate:
                    per_candidate = per;
                    per_candidate([p,q]) = per_candidate([q,p]);
                    cost_candidate = cost_general(s_ICA(per_candidate,:),ds,cost_type,co);
                if cost_candidate < cost%the (p,q) change seems to be useful
                    per = per_candidate;
                    cost_changed = 1;
                    cost = cost_candidate;
                end
            end
        end
     disp(strcat(['Iteration ',num2str(it),': ready.']));      
     it = it + 1;%this iteration is coming             
     if (~cost_changed) %<->it is superfluous to iterate more, no coordinate change improves the cost function
        break;
     end
end
    
disp('Clustering of the ICA elements: ready.');
