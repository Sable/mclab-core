function [per] = clustering_UD0_greedy_pairadditive_wrt_coordinates(S,ds)
%function [per] = clustering_UD0_greedy_pairadditive_wrt_coordinates(S,ds)
%Clusters the ICA (s_ICA) elements to subspaces of given dimensions (ds)
%using (i) 'greedy' clustering and (ii) similarity matrix S. In this case the cost is pair-additive with respect to the coordinates (cost_type = 'Ipairwise1d').
%
%Namely, the clustering/optimization cost is |||N.*S||_1, where ||*||_1 =\sum_ij |(*)_ij 
%N selects the elements outside the block-diagonal, which is determined by the
%subspace dimensions (ds); N = E_D - blockdiag(E_1,E_2,...,E_M), where E_d = ones(d), 
%M = number of the subspaces = length(ds);
%
%Assumption: S: have non-negative elements and symmetric (S^T=S), i.e., S = [I((s_ICA)_i,(s_ICA)_j)].
%
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
    it_max = 30; %maximal number of iterations
    D = sum(ds); %dim(source)
    per = [1:D]; %the actual permutation vector
    num_of_comps = length(ds); %number of subspaces
    cum_ds = cumsum([1;ds(1:end-1)]);%1,d_1+1,d_1+d_2+1,... = starting indices of the subspaces.
    it = 1;
    
disp('Clustering of the ICA elements (greedy optimization): started.'); 
while (it <= it_max)
    cost_changed = 0;
    %p and q are coordinates belonging to different subspaces; change them if it decreases the cost:
        for p = 1  : D - ds(end)
            ind_of_p_subspace = sum(cum_ds<=p);
            for q =  cum_ds(ind_of_p_subspace+1) : D
                %---------------------
                %start/end indices of the subspaces of p and q:
                    ind_p_start = cum_ds(ind_of_p_subspace);
                    ind_p_end = ind_p_start + ds(ind_of_p_subspace)-1;
                    ind_of_q_subspace = sum(cum_ds<=q);                    
                    ind_q_start = cum_ds(ind_of_q_subspace);
                    ind_q_end = ind_q_start + ds(ind_of_q_subspace)-1;
                 cost_before = sum( S(p,[ind_q_start:q-1,q+1:ind_q_end]) ) + sum( S([ind_p_start:p-1,p+1:ind_p_end],q) ); 
                 cost_after = sum(S(ind_p_start:p-1,p)) + sum(S(p,p+1:ind_p_end)) + sum(S(ind_q_start:q-1,q)) + sum(S(q,q+1:ind_q_end));
                 if cost_after < cost_before%the (p,q) change seems to be useful
                      per([p,q]) = per([q,p]);
                      cost_changed = 1;
                      S([p,q],:) = S([q,p],:);
                      S(:,[p,q]) = S(:,[q,p]);  
                 end
                 %---------------------
            end
        end
     disp(strcat(['Iteration ',num2str(it),': ready.'])); 
     it = it + 1;%this iteration is coming             
     if (~cost_changed) %<->it is superfluous to iterate more, no coordinate change improves the cost function
        break;
     end
end
    
disp('Clustering of the ICA elements: ready.');