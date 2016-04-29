function [H] = compute_CDSS(Y_sorted)
%function [H] = compute_CDSS(Y_sorted)
%Computes the Renyi quadratic entropy (H) for the 'Renyi_CDSS' entropy estimator, see 'HRenyi_CDSS_estimation.m'.
%
%INPUT:
%   Y_sorted: vector, Y_sorted(t) is the t^th sample, Y_sorted(1)<=...<=Y_sorted(end).

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

num_of_samples = length(Y_sorted);
m = floor(sqrt(num_of_samples));%m/num_of_samples->0, m,num_of_samples->infty
H = 0;
for i = 1 : num_of_samples-m
    for j = i+1 : i+m-1
        H = H + (Y_sorted(j) - Y_sorted(i+m))^2 * (Y_sorted(j) - Y_sorted(i))^2 / (Y_sorted(i+m) - Y_sorted(i))^5;
    end
end
H = -log(30*H/(num_of_samples*(num_of_samples-m)));
   