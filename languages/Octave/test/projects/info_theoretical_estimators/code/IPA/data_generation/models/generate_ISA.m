function [x,A,e,de,num_of_comps] = generate_ISA(data_type,num_of_comps,num_of_samples)
%function [x,A,e,de,num_of_comps] = generate_ISA(data_type,num_of_comps,num_of_samples)
%Generates an ISA model.
%
%INPUT:
%   data_type: name(s) of the ISA source(s), see 'sample_subspaces.m'.
%   num_of_comps: number of ISA subspaces, see 'sample_subspaces.m'.
%   num_of_samples: number of samples.
%OUTPUT: 
%   x: x(:,t) is the observation at time t; size(x,2) = num_of_samples.
%   A: mixing matrix, random orthogonal (without loss of generality).
%   e: e(:,t) is the source at time t, size(s,2) = num_of_samples.
%   de: subspace dimensions.
%   num_of_comps: number of components; num_of_comps = length(de).
%EXAMPLE:
%   [x,A,e,de,num_of_comps] = generate_ISA('3D-geom',6,1000);
%   [x,A,e,de,num_of_comps] = generate_ISA({'3D-geom','ABC'},[2,3],1000);

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

%source(e):
    [e,de] = sample_subspaces(data_type,num_of_comps,num_of_samples);
    h = plot_subspaces(e,data_type,'hidden subspaces (e^m), m=1,...,M');
    
%mixing matrix(A):
    D = sum(de);%dimension of the hidden source
    A = random_orthogonal(D);%without loss of generality
    
%observation(x):
    x = A * e;
    h = plot_subspaces(x,data_type,'linear mixture (x=Ae)');

num_of_comps = sum(num_of_comps); %number of components/subspaces; until this point num_of_comps could be a vector; see 'demo_ISA.m'