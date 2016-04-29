function [X] = sample_subspaces_IFS_iteration(f,p,T,x1)
%function [X] = sample_subspaces_IFS_iteration(f,p,T,x1)
%Generates T points (X) from a self-similar structure described by the IFS
%f, a probability distribution p and initial point x1.
%
%INPUT:
%   f:f{k}.A, f{k}.b, k=1..K
%   p=[p_1;...;p_K]: probability distribution on f{1},...,f{K}.
%   T: number of iterations.
%   x1: initial point.
%OUTPUT:
%   X(:,t): t^th IFS point. size(X,2)=T.
%EXAMPLE: see 'sample_subspaces_IFS_Durer.m', 'sample_subspaces_IFS_Fern.m', 'sample_subspaces_IFS_Koch.m', 'sample_subspaces_IFS_Sierpinski.m', 'sample_subspaces_IFS_christmas_tree.m'

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

X = zeros(2,T); %[x_v;y_v]
X(:,1) = x1;

for t = 2 : T
    k = discrete_nonuniform_sampling(p,1,1); %pick randomly a transformation
    X(:,t) = f{k}.A * X(:,t-1) + f{k}.b; %Ax+b
end
