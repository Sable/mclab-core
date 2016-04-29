function [indices] = cross_validation_indices(L,n)
%function [indices] = cross_validation_indices(L,n)
%Returns the indices of an n-fold cross-validation in case of L elements (=bags): indices{k}.train, indices{k}.val, indices{k}.test, k=1,...,n.

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

L = floor(L/n) * n; %guarantee that n|L
iv = [1:L];

%index_blocks:
    for k = 1 : n
        index_blocks{k} = [(k-1)*L/n+1:k*L/n];
    end
    
indices = {};
for k = 1 : n
    %idx_train, idx_val, idx_test:
        idx_train = [1:n-2] + (k-1);
        idx_val = [n-1] + (k-1);
        idx_test = [n] + (k-1);
    %correct the interval if its elements are out of [1:n]    
        idx_train = correct_interval(idx_train,n);
        idx_val = correct_interval(idx_val,n);
        idx_test = correct_interval(idx_test,n);
    indices{k}.train = union2(index_blocks(idx_train));
    indices{k}.val = union2(index_blocks(idx_val));
    indices{k}.test = union2(index_blocks(idx_test));
end
