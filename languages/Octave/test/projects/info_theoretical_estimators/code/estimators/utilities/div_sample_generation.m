function [X,Y] = div_sample_generation(Z,ds)
%function [X,Y] = div_sample_generation(Z,ds)
%Splits samples in Z (Z(:,t) is the t^th observation) into X and Y so that
%X(:,t)-s are samples from the joint distribution and Y(:,t)-s are samples
%from the product of the ds(m)-dimensional marginals [assumption: sum(ds)=size(Z,1)].

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

%verification [sum(ds)=size(Z,1)]:
    if sum(ds)~=size(Z,1)
        error('sum(ds) should be equal to size(Z,1).');
    end
    
%initialization:
    [D,num_of_samples] = size(Z);
    cum_ds = cumsum([1;ds(1:end-1)]);%1,d_1+1,d_1+d_2+1,... = starting indices of the subspaces.
    num_of_samples = floor(num_of_samples/2);
    
%X:
    X = Z(:,1:num_of_samples);
    
%Y:
    Y = zeros(D,num_of_samples);%preallocation
    for m = 1 : length(ds)
        idx = [cum_ds(m):cum_ds(m)+ds(m)-1]; 
        Y(idx,:) = Z(idx,num_of_samples+randperm(num_of_samples));
    end



