function [e] = sample_subspaces_IFS_Koch(num_of_samples)
%function [e] = sample_subspaces_IFS_Koch(num_of_samples)
%Sampling from the Koch curve. Number of samples: num_of_samples.
%
%OUTPUT:
%   e: e(:,t) is the t^th sample. size(e,2) = num_of_samples.
%EXAMPLE:
%   e = sample_subspaces_IFS_Koch(10000);

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

[f,p,x1] = f_Koch;
e = sample_subspaces_IFS_iteration(f,p,num_of_samples,x1);

%----------------
function [f,p,x1] = f_Koch()
%Returns IFS (f), probability distribution (p) and initial point (x1) of a Koch curve.

%p:
    p = [1, 1, 1, 1]/4;
%f:
    %T=[A b; 0 0 1]:
        T1 = [1/3 0 0; 0 1/3 0; 0 0 1];
        T2 = [1/3*cos(pi/3) -1/3*sin(pi/3) 1/3; 1/3*sin(pi/3) 1/3*cos(pi/3) 0; 0 0 1];
        T3 = [1/3*cos(pi/3)  1/3*sin(pi/3) 1/2; -1/3*sin(pi/3) 1/3*cos(pi/3) sqrt(3)/6; 0 0 1];
        T4 = [1/3 0 2/3; 0 1/3 0; 0 0 1];
    %T->A,b:
        f{1}.A = T1(1:2,1:2);
        f{1}.b = T1(1:2,3);
        f{2}.A = T2(1:2,1:2);
        f{2}.b = T2(1:2,3);
        f{3}.A = T3(1:2,1:2);
        f{3}.b = T3(1:2,3);
        f{4}.A = T4(1:2,1:2);
        f{4}.b = T4(1:2,3);
%x1:
    x1 = [.5;.5];   