function [S] = discrete_nonuniform_sampling(p,num_of_rows,num_of_columns)
%function [S] = discrete_nonuniform_sampling(p,num_of_rows,num_of_columns)
%Generates samples (S) from a given distribution (p; \sum_ip_i=1,p_i>=0), i.e, P(S_ij=1)=p(1), P(S_ij=2)=p(2),...
%
%OUTPUT:
%   S: samples, of size num_of_rows x num_of_columns.
%EXAMPLE:
%    [S] = discrete_nonuniform_sampling([0.3,0.7],1,5);%the result is a row vector

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

S = zeros(num_of_rows,num_of_columns);%preallocation
U = rand(num_of_rows,num_of_columns);
cump = cumsum(p); cump = cump(1:end-1);%p_1,p_1+p_2,....,p_1+...+p_M-1

for k1 = 1 : num_of_rows
for k2 = 1 : num_of_columns
    S(k1,k2) = sum(U(k1,k2)>cump) + 1;
end
end






