function [perm_ICA] = clustering_UD0(s_ICA,ds,opt_type,cost_type,cost_name)
%function [perm_ICA] = clustering_UD0(s_ICA,ds,opt_type,cost_type,cost_name)
%Clusters the ICA (s_ICA) elements to subspaces of given dimensions (ds).
%
%INPUT:
%   s_ICA: s_ICA(:,t) is the t^th sample.
%   opt_type: optimization type; 'greedy','CE'(cross-entropy),'exhaustive'
%   cost_type: 
%       i)Values: 'I','sumH', 'sum-I','Irecursive', 'Ipairwise' or 'Ipairwise1d'.
%       ii)Special cost types/schemes allow for more efficient optimization procedures. 
%   cost_name: depends on the applied ISA formulation [see below A)-F)]. For the name (cost_name) of the possible entropy/mutual information estimators, please, see the accompanying documentation.
%--------
%   Introduction: The ISA problem consists of the minimization: J(W) = I(y^1,...,y^M) ->
%   min_W, where W is orthogonal (the latter can be assumed w.l.o.g.=whitening). Provided that the ISA separation theorem holds
%   the ISA solution is a permutation of the ICA elements (W=W_ICA(perm_ICA,:), where W_ICA is the
%   ICA demixing matrix, p:=perm_ICA is a permutation). The ISA optimization
%   is carried out making use of this principle.
%--------
%   Let y=[y^1,...,y^M]=s_ICA(p,:) denote the estimated ISA source with d_m-dimensional components y^m; H and I denotes Shannon differential entropy and mutual information, respectively.
%   Possible combinations for opt_type, cost_type and cost_name:
%       A)cost_type = 'I': 
%           Example: J(p) = I(y^1,...,y^M) -> min_p.
%       B)cost_type = 'sumH' 
%           Example: J(p) = \sum_{m=1}^M H(y^m) -> min_p, 
%       C)cost_type = 'sum-I' (sum minus I)
%                    J(p) = -\sum_{m=1}^M I(y_1^m,...,y_{d_m}^M) -> min_p.
%       D)cost_type = 'Irecursive':
%           Example: J(p) = \sum_{m=1}^{M-1} I(y^m,[y^{m+1},...,y^M]) -> min_p.
%       E)cost_type = 'Ipairwise'
%           Example: J(p) = \sum_{m1,m2: different subspace indices} I(y^{m1},y^{m2}) -> min_p.
%       F)cost_type = 'Ipairwise1d':
%           Example: J(p) = \sum_{m1,m2,i1,i2; m1,m2:different subspace indices; i1,i2: coordinates of different (m1\ne m2) subspaces} I(y_{i1}^{m1},y_{i2}^{m2}) -> min_p.
%   Note: 
%       A),B),C),D): ISA is *equivalent* to A),B),C),D) in case of Shannon H and I.
%       E): The goal of E) is to make the estimated ISA subspaces pairwise independent.
%       F): The goal of F) is to make the estimated ISA subspaces pairwise independent; pairwise independence of the subspaces is estimated by the pairwise independende of their coordinates.
%       E) and F) are *necessary* conditions of ISA demixing, which also work often efficiently in practice.
%OUTPUT:
%   perm_ICA: permutation of the ICA elements.

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

%cost object initialization:
    %cost_type,ds => mult:
        mult = set_mult(cost_type,ds);
    co = co_initialization(cost_type,cost_name,mult);

switch opt_type
    case 'greedy'
        switch cost_type
            case {'I','Irecursive'}
                perm_ICA = clustering_UD0_greedy_general(s_ICA,ds,cost_type,co);
            case {'sumH','sum-I'}
                perm_ICA = clustering_UD0_greedy_additive_wrt_subspaces(s_ICA,ds,cost_type,co);
            case 'Ipairwise'
                perm_ICA = clustering_UD0_greedy_pairadditive_wrt_subspaces(s_ICA,ds,co);
            case 'Ipairwise1d'
                S = I_similarity_matrix(s_ICA,co); %similarity matrix
                perm_ICA = clustering_UD0_greedy_pairadditive_wrt_coordinates(S,ds);
            otherwise 
                error('cost type=?');
        end
    case 'CE'
        switch cost_type
            case {'I','sumH','sum-I','Irecursive','Ipairwise'}
               perm_ICA = clustering_UD0_CE_general(s_ICA,ds,cost_type,co);
            case 'Ipairwise1d'%'global JBD'
                S = I_similarity_matrix(s_ICA,co); %similarity matrix
                perm_ICA = clustering_UD0_CE_pairadditive_wrt_coordinates(S,ds);
            otherwise 
               error('optimization type=?');
        end   
    case 'exhaustive' %can be useful for small dimensions (small sum(ds)), or for cost verification
        switch cost_type
            case {'I','Irecursive','sumH','sum-I','Ipairwise'}
                perm_ICA = clustering_UD0_exhaustive_general(s_ICA,ds,cost_type,co);
            case 'Ipairwise1d'%'exhaustive JBD (joint block diagonalization)'
                S = I_similarity_matrix(s_ICA,co); %similarity matrix
                perm_ICA = clustering_UD0_exhaustive_pairadditive_wrt_coordinates(S,ds);
            otherwise 
                error('cost type=?');
        end
    otherwise 
        error('optimization type=?');
end
