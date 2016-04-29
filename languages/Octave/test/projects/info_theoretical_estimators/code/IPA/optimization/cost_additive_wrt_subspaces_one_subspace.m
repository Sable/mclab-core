function [cost] = cost_additive_wrt_subspaces_one_subspace(Y,cost_type,co)
%function [cost] = cost_additive_wrt_subspaces_one_subspace(Y,cost_type,co)
%Computes the cost (cost) of subspace Y given an additive (with respect to the subspace) cost type (cost_type) and a cost
%object (co). 
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
    case 'sumH'
        cost = H_estimation(Y,co);
    case 'sum-I'
        cost = -I_estimation(Y,ones(size(Y,1),1),co); %I(y_1,...,y_d)
    otherwise
        error('cost type=?');
end
