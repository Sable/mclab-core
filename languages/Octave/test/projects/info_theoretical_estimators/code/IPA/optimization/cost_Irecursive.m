function [cost] = cost_Irecursive(Y,ds,co)
%function [cost] = cost_Irecursive(Y,ds,co)
%Computes the cost (cost) of signal Y given the subspace dimensions (ds) and a cost object (co); in case of cost_type='I_recursive' ISA formulation.
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
D = sum(ds);
num_of_comps = length(ds);
cost = 0;
for m = 1 : num_of_comps-1
    %cost = cost + I(y^m,[y^{m+1},...,y^M]):
        idx_m = [cum_ds(m) : cum_ds(m)+ds(m)-1];
        idx_tail = [cum_ds(m)+ds(m) : D];
        cost = cost + I_estimation(Y([idx_m,idx_tail],:),[ds(m);length(idx_tail)],co);
end


