function [e,de] = sample_subspaces_IFS(num_of_comps,num_of_samples)
%function [e,de] = sample_subspaces_IFS(num_of_comps,num_of_samples)
%Sampling from the 'IFS' dataset; number of components: num_of_comps; number of samples: num_of_samples.
%
%OUTPUT:
%   e: e(:,t) is the t^th sample. size(e,2) = num_of_samples.
%   de: subspace dimensions.
%EXAMPLE:
%   e = sample_subspaces_IFS(9,10000);
%
%REFERENCE:
%   Zoltan Szabo, Barnabas Poczos, Gabor Szirtes, and Andras Lorincz. Post Nonlinear Independent Subspace Analysis. International Conference on Artificial Neural Networks (ICANN), pp. 677-686, 2007.

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

max_num_of_comps = 9;

if num_of_comps > max_num_of_comps 
    error(strcat('The number of components must be <=',num2str(max_num_of_comps),'.'));
else
    e = zeros(2*num_of_comps,num_of_samples);%preallocation
    for k = 1 : num_of_comps
        switch k
            case 1
                e_temp = sample_subspaces_IFS_Durer(num_of_samples,[5]);
            case 2
                e_temp = sample_subspaces_IFS_Durer(num_of_samples,[8]);
            case 3
                e_temp = sample_subspaces_IFS_Durer(num_of_samples,[11]);                
            case 4
                e_temp = sample_subspaces_IFS_Fern(num_of_samples);
            case 5
                e_temp = sample_subspaces_IFS_Koch(num_of_samples);                
            case 6
                e_temp = sample_subspaces_IFS_Sierpinski(num_of_samples,[3]);                
            case 7
                e_temp = sample_subspaces_IFS_Sierpinski(num_of_samples,[5]);                                
            case 8
                e_temp = sample_subspaces_IFS_Sierpinski(num_of_samples,[6]);
            case 9
                e_temp = sample_subspaces_IFS_christmas_tree(num_of_samples);                
        end
        e(2*k-1:2*k,:) = e_temp;
    end
    de = ones(num_of_comps,1) * 2;
end
