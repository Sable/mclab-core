function [cost,costs] = cost_additive_wrt_subspaces(Y,ds,cost_type,co)
%function [cost,costs] = cost_additive_wrt_subspaces(Y,ds,cost_type,co)
%Computes the cost [cost; and the individual cost terms of the subspaces as well (costs)] of signal Y given 
%the subspace dimensions (ds), an additive (with respect to the subspaces) cost type (cost_type) and a cost object (co).
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.

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

cum_ds = cumsum([1;ds(1:end-1)]);%1,d_1+1,d_1+d_2+1,...,d_1+...+d_{M-1}+1 = starting indices of the subspaces (M=number of subspaces).
num_of_comps = length(ds);
costs = zeros(num_of_comps,1);
for k = 1 : num_of_comps
    costs(k) = cost_additive_wrt_subspaces_one_subspace(Y(cum_ds(k):cum_ds(k)+ds(k)-1,:),cost_type,co);
end
cost = sum(costs);
