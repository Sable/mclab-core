function [cost] = cost_general(Y,ds,cost_type,co)
%function [cost] = cost_general(Y,ds,cost_type,co)
%Estimates general ISA cost (cost) of the estimation Y, given subspace
%dimensions (ds), cost type (cost_type) and cost object (co).
%Note: cost_type = 'Ipairwise1d' is handled separately (enabling more
%efficient optimization).
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

switch cost_type
    case {'sumH','sum-I'}
		cost = cost_additive_wrt_subspaces(Y,ds,cost_type,co);
    case 'Ipairwise'
        cost = cost_pairadditive_wrt_subspaces(Y,ds,co);
    case 'I'
        cost = I_estimation(Y,ds,co);
    case 'Irecursive'
        cost = cost_Irecursive(Y,ds,co);
    otherwise
        error('cost type=?');
end