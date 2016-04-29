function [A] = replace_Infs_with_max(A)
%function [A] = replace_Infs_with_max(A)
%Replaces the Inf elements of matrix A with the largest non-Inf elements. If A does not contain Inf-s, then the output of the function equals to its input.

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

[I,J] = find(A==Inf);
if ~isempty(I) %max=Inf
    ind = sub2ind(size(A),I,J);
    A(ind) = -Inf; %=>they will not be maximal
    A(ind) = max(max(A));
    disp('Inf elements: changed to the maximal non-Inf ones.');
end
