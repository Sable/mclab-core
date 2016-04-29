function [mult] = set_mult(cost_type,ds)
%function [mult] = set_mult(cost_type,ds)
%Sets whether a multiplicative factor is important/needed in the cost
%estimation (entropy/mutual information) based on the applied cost type
%(cost_type) and subspace dimensions (ds).
%
%OUTPUT:
%   mult: '=1': important, '=0': not.

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
    case {'I','Ipairwise1d'}
        mult = 0;
    case {'sumH','sum-I','Ipairwise'}
        if equal_subspace_dimension(ds)
            mult = 0;
        else
            mult = 1;
        end
    case 'Irecursive'
        mult = 1;
    otherwise
        error('cost type=?');
end

%-----------------------------
function [b] = equal_subspace_dimension(ds)
%Returns whether the subspace dimensions (ds) are equal or not.
%
%OUTPUT:
%   b: '=1'(true) equal, '=0'(false) not.

b = (sum(ds==ds(1)) == length(ds));