function [ClusterIndicators] = estimate_clustering_UD1_S(S,num_of_comps,opt_type)
%function [ClusterIndicators] = estimate_clustering_UD1_S(S,num_of_comps,opt_type)
%Spectral clustering on the S similarity matrix.
%
%INPUT:
%   S: similarity matrix.
%   num_of_comps: number of components/groups (in ISA: subspaces).
%   opt_type: optimization method. Possibilities: 'NCut' (normalized cut), 'SP1' (unnormalized cut; purely Matlab),'SP2'(normalized cut; purely Matlab 'NCut'), 'SP3' (normalized cut; purely Matlab).
%OUTPUT:
%   ClusterIndicators: cluster indicator matrix; ClusterIndicators(i,j)=1 <=> the i^th coordinate belongs to the j^th group; 0 otherwise.  
%          
%REFERENCE:
%   'NCut':
%       Jianbo Shi and Jitendra Malik. Normalized cuts and image segmentation. IEEE Transactions on Pattern Analysis and Machine Intelligence, 22 (8), 888-905, 2000.
%       Normalized Cut Segmentation Code, Timothee Cour, Stella Yu, Jianbo Shi. Copyright 2004 University of Pennsylvania, Computer and Information Science Department.
%   'SP1,2,3': Ulrike von Luxburg. A Tutorial on Spectral Clustering. Statistics and Computing 17 (4), 2007.
%   'SP2': Jianbo Shi and Jitendra Malik. Normalized cuts and image segmentation. IEEE Transactions on Pattern Analysis and Machine Intelligence, 22 (8), 888-905, 2000.
%   'SP3': Andrew Y. Ng, Michael I. Jordan, and Yair Weiss. On spectral clustering: analysis and an algorithm. Advances in Neural Information Processing Systems (NIPS), pp. 849-856, 2002.

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

switch opt_type
    case 'NCut'
        %query for the current working environment:
            environment_Matlab = working_environment_Matlab;
        if environment_Matlab%Matlab
            ClusterIndicators = ncutW(S,num_of_comps);
        else%Octave
            error('We are working in Octave environment. => NCut: not available.');
        end
    case {'SP1','SP2','SP3'}%'SP2'='NCut' but they are different implementations ('SP2':purely Matlab)
        method = str2num(opt_type(3));%'SP1'->1; 'SP2'->2; 'SP3'->3
        ClusterIndicators = SpectralClustering(S,num_of_comps,method);
    otherwise
        error('optimization type=?');
end
