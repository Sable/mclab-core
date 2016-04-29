function [e] = sample_subspaces_IFS_Fern(num_of_samples)
%function [e] = sample_subspaces_IFS_Fern(num_of_samples)
%Sampling from the Fern system. Number of samples: num_of_samples.
%
%OUTPUT:
%   e: e(:,t) is the t^th sample. size(e,2) = num_of_samples.
%EXAMPLE:
%   e = sample_subspaces_IFS_Fern(10000);

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

[f,p,x1] = f_Fern;
e = sample_subspaces_IFS_iteration(f,p,num_of_samples,x1);

%--------------------
function [f,p,x1] = f_Fern()
%Returns IFS (f), probability distribution (p) and initial point (x1) of a Fern system.

%p:
    p = [.01, .85, .07, .07];
%f:    
    [f{1}.A, f{1}.b] = AT(0,0,0,.16,0,0);
    [f{2}.A, f{2}.b] = AT(.85,.04,-.04,.85,0,1.6);
    [f{3}.A, f{3}.b] = AT(.2,-.26,.23,.22,0,1.6);
    [f{4}.A, f{4}.b] = AT(-.15,.28,.26,.24,0,.44);
    
%x1:
    x1 = [.5;.5];

%-----------------------
function [A,b_v] = AT(a,b,c,d,e,f)
%2x2 affine transformation

A = [a,b; c d];
b_v = [e;f];
