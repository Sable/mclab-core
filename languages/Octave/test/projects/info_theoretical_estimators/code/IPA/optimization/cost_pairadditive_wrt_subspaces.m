function [cost, costs] = cost_pairadditive_wrt_subspaces(Y,ds,co)
%function [cost, costs] = cost_pairadditive_wrt_subspaces(Y,ds,co)
%Computes the cost (cost; costs--upper triangular) of signal Y given the subspace dimensions (ds), a pair-additive (with respect to the subspaces) cost co (cost_type='Ipairwise').
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

num_of_comps = length(ds);
cum_ds = cumsum([1;ds(1:end-1)]);%1,d_1+1,d_1+d_2+1,...,d_1+...+d_{M-1}+1 = starting indices of the subspaces (M=number of subspaces).
cost = 0;
costs = zeros(num_of_comps);
for k1 = 1 : num_of_comps-1
for k2 = k1+1 : num_of_comps
    %c = I(y^{k1},y^{k2}), where k1<k2:
        c = I_estimation([Y(cum_ds(k1):cum_ds(k1)+ds(k1)-1,:);Y(cum_ds(k2):cum_ds(k2)+ds(k2)-1,:)],[ds(k1);ds(k2)],co);
    cost = cost + c;
    costs(k1,k2) = c;
end
end
cost = 2 * cost; %because of symmetry: I(y^{k1},y^{k2}) = I(y^{k2},y^{k1})
