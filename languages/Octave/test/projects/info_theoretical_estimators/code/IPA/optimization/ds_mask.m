function [mask] = ds_mask(ds)
%function [mask] = ds_mask(ds)
%Returns the mask corresponding to coordinate pairs from different subspaces.
%
%INPUT:
%   ds:subspace dimensions.

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

D = sum(ds);
%BD = blkdiag(ones(ds(1)),ones(ds(2)),...):
    cum_ds = cumsum([1;ds(1:end-1)]);%1,d_1+1,d_1+d_2+1,...,d_1+...+d_{M-1}+1 = starting indices of the subspaces (M=number of subspaces).
    BD = zeros(D);
    for m = 1 : length(ds)
        ind = [cum_ds(m):cum_ds(m)+ds(m)-1];
        BD(ind,ind) = ones(ds(m));
    end
mask = ones(D) - BD;
