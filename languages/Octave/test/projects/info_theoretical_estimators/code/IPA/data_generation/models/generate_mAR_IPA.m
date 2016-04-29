function [y,x,A,s,e,F,de,Fx] = generate_mAR_IPA(data_type,num_of_comps,num_of_samples,L,F_lambda,p)
%function [y,x,A,s,e,F,de,Fx] = generate_mAR_IPA(data_type,num_of_comps,num_of_samples,L,F_lambda,p)
%Generates a mAR-IPA model.
%
%INPUT:
%   data_type: name(s) of the ISA source(s), see 'sample_subspaces.m'.
%   num_of_comps: number of ISA subspaces, see 'sample_subspaces.m'.
%   num_of_samples: number of samples.
%   L: AR order.
%   F_lambda: stability parameter of the AR process, see 'generate_AR_polynomial.m'.
%   p: p=P(x(i,t) is not observable).
%OUTPUT: 
%   y: y(:,t) is the observation at time t; size(y,2) = num_of_samples; x(i,t) is not observable <=> y(i,t)=NaN.
%   x: x(:,t) is the observation before masking at time t; size(x,2) = num_of_samples.
%   A: mixing matrix, random orthogonal (without loss of generality).
%   s: s(:,t) is the source at time t, size(s,2) = num_of_samples.
%   e: e(:,t) is the driving noise at time t, size(e,2) = num_of_samples.
%   F: AR matrix describing the evolution of s.
%   Fx: AR matrix describing the evolution of x.
%   de: subspace dimensions.
%EXAMPLE:
%   [y,x,A,s,e,F,de,Fx] = generate_mAR_IPA('3D-geom',6,5000,2,0.95,0.01);

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
    
%AR dynamics (F):
    F = AR_polynomial(D,L,F_lambda);
    
%AR generation (F,e->s):
    s = zeros(D,L+NDiscard+num_of_samples); %initial values of the AR process: s(1)=...=s(L)=0    
    for t = (L+1) : (L+NDiscard+num_of_samples)%t:<->s(t)
        s_past = s(:,t-1-L+1:t-1); %s_past(:) = [s(t-1-L+1);...;s(t-1)]
        s(:,t) = F * s_past(:) + e(:,t);
    end
    s = s(:,L+NDiscard+1:end);    

%mixing matrix (A), observation before masking (x) and its dynamics (Fx):
    A = random_orthogonal(D);%without loss of generality
    x = A * s;
    %Fx = A * F * A^{-1}:
        invA = A.'; %A is orthogonal
        Fx = basis_transformation_AR(F,A,invA);
%x,p->observation(y):
    %missing values:
        I = find(rand(size(x))<p);
    y = x;
    y(I) = NaN;%linear indexing
