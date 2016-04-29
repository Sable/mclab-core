function [L] = compute_MST(W,method)
%function [L] = compute_MST(W,method)
%Computes the minimum spanning tree on the weight matrix 'W' with the
%specified 'method'. 
%
%INPUT:
%   W: similarity matrix, square.
%   method: MST method. Possibilities: 'pmtk3_Prim', 'pmtk3_Kruskal'.
%OUTPUT:
%   L: total cost of the optimal tree.
%EXAMPLE:
%   A = rand(5); A = A+A.';  L = compute_MST(A,'pmtk3_Prim'), L = compute_MST(A,'pmtk3_Kruskal'),

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

switch method
    case 'pmtk3_Prim'
        [temp,L] = minSpanTreePrim(W);
    case 'pmtk3_Kruskal'
        [temp,L] = minSpanTreeKruskal(W);
    otherwise
         error('MST method=?');
end
