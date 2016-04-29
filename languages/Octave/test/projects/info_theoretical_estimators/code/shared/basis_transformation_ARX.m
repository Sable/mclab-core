function [F_transformed,B_transformed] = basis_transformation_ARX(F,B,A,invA)
%function [F_transformed,B_transformed] = basis_transformation_ARX(F,B,A,invA)
%Basis transformation of ARX processes: 
%   [F_1,...,F_Ls] -> [A*F_1*A^{-1},...,A*F_Ls*A^{-1}]
%   [B_1,...,B_Lu] -> [A*B_1,...,A*B_Lu]

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

%F_transformed:
    F_transformed = basis_transformation_AR(F,A,invA);
    
%B_transformed:
    B_transformed = A * B;