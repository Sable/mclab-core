function [x,H,e,de,num_of_comps] = generate_MA_IPA(data_type,num_of_comps,num_of_samples,undercompleteness,MA)
%function [x,H,e,de,num_of_comps] = generate_MA_IPA(data_type,num_of_comps,num_of_samples,undercompleteness,MA)
%Generates an uMA-IPA/complete MA-IPA model.
%
%INPUT:
%   data_type: name(s) of the source(s), see 'sample_subspaces.m'.
%   num_of_comps: number of subspaces, see 'sample_subspaces.m'.
%   num_of_samples: number of samples.
%   undercompleteness: dim(observation) = ceil((undercompleteness+1) x dim(source)), >=0.
%   MA: parameters of the convolution (MA.L: length of the convolution; H_0,...,H_{L}: L+1 H_j matrices), see 'MA_polynomial.m'.
%OUTPUT:
%   x: x(:,t) is the observation at time t; size(x,2) = num_of_samples.
%   H: polynomial matrix corresponding to the convolution (=MA).
%   e: e(:,t) is the source at time t, size(e,2) = num_of_samples.
%   de: subspace dimensions.
%   num_of_comps: number of components, see 'generate_ISA.m'.

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

%length of the convolution (L):
    L = MA.L;

%driving noise/source (e) and subspace dimensions (de):
    [e,de] = sample_subspaces(data_type,num_of_comps,num_of_samples+L);
    h = plot_subspaces(e,data_type,'hidden subspaces (e^m), m=1,...,M');
    De = sum(de);

%convolution (H):
    Dx = ceil((undercompleteness+1) * De); %undercomplete problem (Dx > De)
    H = MA_polynomial(Dx,De,MA);
    
%observation (x):
    E_temp = concatenation(e,L+1);
    x = H * E_temp;
