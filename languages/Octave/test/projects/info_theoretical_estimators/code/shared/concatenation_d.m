function [Z] = concatenation_d(z,d,K)
%function [Z] = concatenation_d(z,d,K)
%Performs the same as 'concatenation.m', but for each d-dimensional z^k component (z=[z^1;...;z^K]) of signal z separately.

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

D = d * K;
[Dz,T] = size(z); %dim(z), number of samples in signal z
num_of_comps = Dz / d;
Z = zeros(num_of_comps*D,T-(K-1)); %preallocation

for k = 1 : num_of_comps %k^th component
    zk = z((k-1)*d+1:k*d,:);
    Z((k-1)*D+1:k*D,:) = concatenation(zk,K);
end
