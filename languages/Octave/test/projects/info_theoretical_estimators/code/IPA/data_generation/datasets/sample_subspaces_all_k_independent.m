function [e,de] = sample_subspaces_all_k_independent(num_of_comps,num_of_samples,k)
%function [e,de] = sample_subspaces_all_k_independent(num_of_comps,num_of_samples,k)
%Sampling from the 'all-k-independent' dataset (k>=2); number of subspaces: num_of_comps; number of samples: num_of_samples.
%Every subspace is d=k+1 - dimensional; each k-tuple of the coordinates of a given subspace are independent, but for a given subspace its
%coordinates are jointly (=the k+1-dimensional vector) dependent.
%
%OUTPUT:
%   e: e(:,t) is the t^th sample. size(e,2) = num_of_samples.
%   de: subspace dimensions.
%EXAMPLE:
%   e = sample_subspaces_all_k_independent(num_of_comps,num_of_samples,k);
%
%REFERENCE:
%   Zoltan Szabo, Barnabas Poczos, and Andras Lorincz. Cross-Entropy  Optimization for Independent Process Analysis. International Conference on Independent Component Analysis and Blind Source Separation (ICA 2006), pp. 909-916, 2006. (general k)
%   Barnabas Poczos, Andras Lorincz. Independent Subspace Analysis Using Geodesic Spanning Trees. International Conference on Machine Learning (ICML), pp. 673-680, 2005. (k=2)

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

d = k + 1;%dimension of a subspace
e = zeros(d*num_of_comps,num_of_samples); %preallocation
for m = 1 : num_of_comps
    e(d*m -(d-1):d*m,:) =  sample_subspaces_all_k_independent_one_comp(num_of_samples,k);
end
de = ones(num_of_comps,1) * d;

%-------------------------------
function [e] = sample_subspaces_all_k_independent_one_comp(num_of_samples,k)
%Sample from one all-k-independent subspace.
%
%Construction:
%   1,...k^th coordinate is disributed independently as e_i := Uniform(0,...,k-1)
%   e_k+1 = mod(e_1 +...+ e_k,k)

P = ones(1,k) / k; %uniform weights
e = zeros(k+1,num_of_samples); %preallocation
for e_ind = 1 : k
    e(e_ind,:) = discrete_nonuniform_sampling(P,1,num_of_samples)-1; %reason of '-1': [1..k] -> [0..k-1]
end

if (k==2) %epsilon noise is added.
    e(1:k,:) = e(1:k,:) + 0.0001*rand(size(e(1:k,:)));
end

e(k+1,:) = mod(sum(e(1:k,:)),k);
