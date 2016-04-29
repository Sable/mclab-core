function [Z] = concatenation(z,K)
%function [Z] = concatenation(z,K)
%Gives the K-time concatenated version (Z) of signal z.
%
%z=[z_1,...,z_T] ->
%Z = [z(K)  , ... , z(T);
%     z(K-1), ... , z(T-1);
%     ...
%     z(1)  , ... , z(T-K+1)].
%z_t = z(:,t): vector.

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

[d,T] = size(z);
Z = zeros(K*d,T-(K-1));%preallocation
for k = 1 : K  %k^th blockrow in Z
    s_start_ind = (K+1) - k;
    s_end_ind = (T+1) - k;
    Z((k-1)*d+1:k*d,:) = z(:,s_start_ind:s_end_ind);
end