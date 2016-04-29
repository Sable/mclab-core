function [F_transformed] = basis_transformation_AR(F,A,invA)
%function [F_transformed] = basis_transformation_AR(F,A,invA)
%Basis transformation of AR processes: [F_1,...,F_L] -> [A*F_1*A^{-1},...,A*F_L*A^{-1}]

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

D = size(F,1);%dimension of the AR process
L = size(F,2)/D;%AR order

%F[z] -> A * F[z] * A^{-1}:
    F_transformed = A * F;
    for k = 1 : L
       F_transformed(:,(k-1)*D+1:k*D) =  F_transformed(:,(k-1)*D+1:k*D) * invA;
    end