function [e,de,Fs,Bs,A,Fx,Bx,Ae,du] = generate_ARX_IPA_parameters(data_type,num_of_comps,num_of_samples,Ls,F_lambda,Lu,du)
%function [e,de,Fs,Bs,A,Fx,Bx,Ae,du] = generate_ARX_IPA_parameters(data_type,num_of_comps,num_of_samples,Ls,F_lambda,Lu,du)
%Generates an ARX-IPA model.
%
%INPUT:
%   data_type: name(s) of the ISA source(s), see 'sample_subspaces.m'.
%   num_of_comps: number of ISA subspaces, see 'sample_subspaces.m'.
%   num_of_samples: number of samples.
%   Ls: AR order.
%   F_lambda: stability parameter of the AR process, see 'generate_AR_polynomial.m'.
%   Lu: control ('X') order.
%   du: control dimension.
%OUTPUT:
%   e: e(:,t) is the driving noise at time t, size(e,2) = num_of_samples.
%   de: subspace dimensions.
%   Fs: matrix describing the AR evolution of source s.
%   Bs: matrix describing the effect of the control on source s.
%   A: mixing matrix, random orthogonal (without loss of generality).
%   Fx: matrix describing the AR evolution of observation x.
%   Bx: matrix describing the effect of the control on observation x.
%   Ae: driving noise of observation x, =A*e.
%   du: control dimension.
%EXAMPLE:
%   [e,Fs,Bs,A,Fx,Bx,Ae,du] = generate_ARX_IPA_parameters('3D-geom',2,1000,1,0.7,1,4);

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

%driving noise (e), dimension of the control (du):
    [e,de] = sample_subspaces(data_type,num_of_comps,num_of_samples);
    D = sum(de);  
    
%AR dynamics of source s (Fs):
    Fs = AR_polynomial(D,Ls,F_lambda);
    
%control dynamics of source s(Bs):
    Bs = .5*(2*randn(D,du*Lu)-1); 
    
%mixing matrix(A):
    A = random_orthogonal(D);%without loss of generality

%basis transformation (s->x=As,e->Ae)    
    [Fx,Bx] = basis_transformation_ARX(Fs,Bs,A,A.');%A.'=inv(A) since A is orthogonal
    Ae = A * e;

