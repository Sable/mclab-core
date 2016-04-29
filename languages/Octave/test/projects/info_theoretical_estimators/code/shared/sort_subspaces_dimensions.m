function [ds_sorted,per] = sort_subspaces_dimensions(ds)
%function [ds_sorted,per] = sort_subspaces_dimensions(ds)
%Sort the subspace dimensions (ds) in increasing order.
%
%INPUT:
%   ds: subspace dimensions (column vector).
%OUTPUT:
%  ds_sorted: sorted subspace dimensions.
%  per: permutation of the individual coordinates.

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
if sorted(ds)
    ds_sorted = ds;
    per = [1:D].';
    disp('Subspace dimensions: ordering is OK/not needed.');
else
    %initialization:
        num_of_comps = length(ds);
        per = zeros(D,1);%preallocation
    [ds_sorted,I] = sort(ds);
    cum_ds_sorted = cumsum([1;ds_sorted(1:end-1)]);%1,d_1+1,d_1+d_2+1,...,d_1+...+d_{M-1}+1 = starting indices of the subspaces (M=number of subspaces).
    cum_ds = cumsum([1;ds(1:end-1)]);%1,d_1+1,d_1+d_2+1,...,d_1+...+d_{M-1}+1 = starting indices of the subspaces (M=number of subspaces).
    for m = 1 : num_of_comps
        ind_sorted = [cum_ds_sorted(m):cum_ds_sorted(m)+ds_sorted(m)-1];
        ind = [cum_ds(I(m)):cum_ds(I(m))+ds(I(m))-1]; %unsorted
        per(ind_sorted) = ind;
    end
    disp('Subspace dimensions have been ordered, increasingly.');
end

%-------------------------
function [s] = sorted(ds)
%Returns whether the subspace dimensions (ds) are in increasing order.
%
%OUTPUT:
%   s:'true' means 'yes', 'false' is 'no'.

s = (sum(diff(ds)>=0) == (length(ds)-1));
