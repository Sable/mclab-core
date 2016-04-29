function [bandwith] = median_heuristic(X)
%function [bandwidth] = median_heuristic(X)
%Estimates RBF bandwith based on the median heuristic.
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

num_of_samples = size(X,2);
num_of_samples_used = 100; % if contains more samples, then X is subsampled to this cardinality

%subsample X (if necessary):
    if num_of_samples > num_of_samples_used %select '100' random X columns
        p=randperm(num_of_samples);
        p = p(1:num_of_samples_used);
        X = X(:,p);
    end
    
dist_vector = pdist(X.'); %pairwise Euclidean distances
bandwith = median(dist_vector(dist_vector>0)) / sqrt(2);
