function [per] = clustering_UD0_greedy_pairadditive_wrt_subspaces(s_ICA,ds,co)
%function [per] = clustering_UD0_greedy_pairadditive_wrt_subspaces(s_ICA,ds,co)
%Clusters the ICA (s_ICA) elements to subspaces of given dimensions (ds)
%using (i) 'greedy' clustering, and (ii) a cost which is pairadditive with respect 
%to the subspaces (see cost_type = 'Ipairwise'). The initialized cost object is co.
%
%INPUT:
% s_ICA: s_ICA(:,t) is the t^th estimated realization of the ICA sources.
%    ds: subspace dimensions.
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
    [cost_temp, S] = cost_pairadditive_wrt_subspaces(s_ICA,ds,co);%S = [S_{ij}] = [I(y^i,y^j)]
    
disp('Clustering of the ICA elements (greedy optimization): started.'); 
while (it <= it_max)
    cost_changed = 0;
    %p and q are coordinates belonging to different subspaces; change them if it decreases the cost:
        for p = 1  : D - ds(end)
            ind_of_p_subspace = sum(cum_ds<=p);
            for q =  cum_ds(ind_of_p_subspace+1) : D
                ind_of_q_subspace = sum(cum_ds<=q);
                %--------------------
                cost_before = sum(S(1:ind_of_p_subspace-1,ind_of_p_subspace)) +...
                              sum(S(ind_of_p_subspace,[ind_of_p_subspace+1:ind_of_q_subspace-1,ind_of_q_subspace+1:num_of_comps]))+...
                              sum(S([1:ind_of_q_subspace-1],ind_of_q_subspace))+...
                              sum(S(ind_of_q_subspace,[ind_of_q_subspace+1:num_of_comps]));
                per_after = per; per_after([p,q]) = per_after([q,p]);
                [cost_after,S1,S2,S3,S4] = cost_pairadditive_wrt_subspaces_restricted(s_ICA(per_after,:),ind_of_p_subspace,ind_of_q_subspace,ds,co);
                if cost_after < cost_before
                    per = per_after;
                    cost_changed = 1;
                    %S update (at the upper triangular part):
                        S(1:ind_of_p_subspace-1,ind_of_p_subspace) = S1;
                        S(ind_of_p_subspace,[ind_of_p_subspace+1:ind_of_q_subspace-1,ind_of_q_subspace+1:num_of_comps]) = S2;
                        S([1:ind_of_q_subspace-1],ind_of_q_subspace) = S3;
                        S(ind_of_q_subspace,[ind_of_q_subspace+1:num_of_comps]) = S4;
                end
                %--------------------
            end
        end
     disp(strcat(['Iteration ',num2str(it),': ready.'])); 
     it = it + 1;%this iteration is coming             
     if (~cost_changed) %<->it is superfluous to iterate more, no coordinate change improves the cost function
        break;
     end
end
    
disp('Clustering of the ICA elements: ready.');                
