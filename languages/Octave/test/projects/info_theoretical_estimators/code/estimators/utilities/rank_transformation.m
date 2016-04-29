function [Z] = rank_transformation(X)
%function [Z] = rank_transformation(X)
%Computes the rank transformation (Z) of signal X.
%
%INPUT:
%   X: X(:,t) is the t^th sample.

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

[d,num_of_samples] = size(X);
[temp,J] = sort(X,2);
Z = zeros(size(J));%preallocation

%I.:
    for k = 1 : d
        Z(k,J(k,:)) = [1:num_of_samples]; %inverse of permutation J(k,:)
    end
    
%II.:
    %I = repmat([1:d].',1,num_of_samples);
    %Z(I+(J-1)*d) = repmat([1:num_of_samples],d,1);
