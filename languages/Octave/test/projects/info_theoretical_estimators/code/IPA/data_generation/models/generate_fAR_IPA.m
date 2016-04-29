function [x,A,s,e,de,num_of_comps] = generate_fAR_IPA(data_type,num_of_comps,num_of_samples,L)
%function [x,A,s,e,de,num_of_comps] = generate_fAR_IPA(data_type,num_of_comps,num_of_samples,L)
%Generates a fAR-IPA model.
%
%INPUT:
%   data_type: name(s) of the ISA source(s), see 'sample_subspaces.m'.
%   num_of_comps: number of ISA subspaces, see 'sample_subspaces.m'.
%   num_of_samples: number of samples.
%   L: fAR order.
%OUTPUT:
%   x: x(:,t) is the observation at time t; size(x,2) = num_of_samples.
%   A: mixing matrix, random orthogonal (without loss of generality).
%   s: s(:,t) is the source at time t, size(s,2) = num_of_samples.
%   e: e(:,t) is the driving noise at time t, size(e,2) = num_of_samples.
%   de: subspace dimensions.

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

NDiscard = 50; %="burn-in period"

%driving noise (e):
    [e,de] = sample_subspaces(data_type,num_of_comps,num_of_samples+L+NDiscard);
    D = sum(de);
    
%dynamics (F):
    F = rand(D,D*L);
    f = 'sin';
    
%source (s):
    s = zeros(D,L+NDiscard+num_of_samples); %initial values of the AR process: s(1)=...=s(L)=0    
    for t = (L+1) : (L+NDiscard+num_of_samples)%t<->s(t)
        s_past = s(:,[t-L:t-1]);
        s(:,t) = feval(f,F*s_past(:)) + e(:,t);
    end
    s = s(:,L+NDiscard+1:end);%discard the first L+NDiscard samples
    e = e(:,L+NDiscard+1:end);%discard the first L+NDiscard samples
    
%mixing matrix (A), observation (x):
    A = random_orthogonal(D);%without loss of generality
    x = A * s;


