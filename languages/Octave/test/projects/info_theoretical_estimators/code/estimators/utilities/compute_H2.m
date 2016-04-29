function [H2] = compute_H2(ws,ms,ss)
%function [H2] = compute_H2(ws,ms,ss)
%Computes the quadratic Renyi entropy (H2) for the mixture of Gaussians model with weights (ws), means (ms), and standard deviations (ss). 
%INPUT:
%   ws: weights, vector of length N.
%   ms: means, ms(:,n) = n^th mean (n=1,...,N).
%   ss: standard deviations, ss(n)=n^th std.

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

%initialization:
    N = length(ws);
    d = size(ms,1);
    I = eye(d);
    term = 0;

%without -log():    
    for n1 = 1 : N
    for n2 = 1 : N
        term = term + ws(n1) * ws(n2) * normal_density_at_zero(ms(:,n1)-ms(:,n2),(ss(n1)^2+ss(n2)^2)*I);
    end
    end

H2 = -log(term);

