function [F] = AR_polynomial(D,L,F_lambda)
%function [F] = AR_polynomial(D,L,F_lambda)
%Generates stable AR polynomial F[z] of dimension D (F[z]\in R[z]^{DxD}), length L and
%stability parameter F_lambda (0<F_lambda<1; the smaller F_lambda is the more stable the F[z] polynomial matrix is).
%
%OUTPUT:
%   F: from F[z]=\prod_{k=1}^L(I-A_kz^{-1}), where A_k = F_lambda * random orthogonal_k, the part after I is preserved.
%EXAMPLE:
%   F = AR_polynomial(3,2,0.95);

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

%F[z](F[z]s=e):
    Fz = {eye(D)};
    for k = 1 : L
        %Fznew = I - F_lambda * random orthogonal:
            Fznew = {};
            Fznew{1} = eye(D);
            Fznew{2} = -F_lambda * random_orthogonal(D);%
        Fz = multiply_polynomial_matrices(Fz,Fznew);
    end
    
%Fz->F (F = -Fz{2:end}):
    for k = 2 : length(Fz)
        F(:,:,k-1) = -Fz{end-(k-2)};
    end
    
%F->F (s(t) = F * [s(t-1-L+1);...;s(t-1)] + e(t)):
    F = reshape(F,[D,D*L]);%=[F_L,...,F_1]