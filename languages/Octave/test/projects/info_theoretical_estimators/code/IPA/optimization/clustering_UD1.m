function [perm_ICA,ds_hat] = clustering_UD1(s_ICA,num_of_comps,opt_type,cost_name)
%function [perm_ICA,ds_hat] = clustering_UD1(s_ICA,num_of_comps,opt_type,cost_name)
%Clusters the ICA (s_ICA) elements to given number (num_of_comps) of subspaces, using the specified
%optimization type (opt_type) and cost (mutual information) estimator.
%Here, cost_type = 'Ipairwise1d', and optimization is carried out by spectral clustering given in opt_type (see 'clustering_UD1_S.m').
%
%INPUT:
%   s_ICA: s_ICA(:,t) is the t^th sample.
%OUTPUT:
%   perm_ICA: permutation of the ICA elements.
%     ds_hat: estimated subspace dimensions; length(ds_hat) = num_of_comps.
%
%REFERENCE:
%   Barnabas Poczos, Zoltan Szabo, Melinda Kiszlinger, Andras Lorincz: Independent Process Analysis without A Priori Dimensional Information. ICA-2007, pages 252-259. (application of the NCut method in IPA)
%   Jianbo Shi and Jitendra Malik. Normalized Cuts and Image Segmentation. IEEE Transactions on Pattern Analysis and Machine Intelligence, Vol. 22, No. 8, 2000. (NCut method)

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
        co = I_initialization(cost_name,0);%mult = 0;
        
%similarity matrix (S):
        S = I_similarity_matrix(s_ICA,co); %similarity matrix
        
%S->perm_ICA,ds_hat:  
    disp(strcat('Clustering of the ICA elements (method=',opt_type,'): started.'));
    %S,num_of_comps,opt_type -> ClusterIndicators:
        ClusterIndicators = estimate_clustering_UD1_S(S,num_of_comps,opt_type);
    [perm_ICA,ds_hat] = clusterindicator2perm(ClusterIndicators);
    disp(strcat('Clustering of the ICA elements (method=',opt_type,'): ready.'));
    
