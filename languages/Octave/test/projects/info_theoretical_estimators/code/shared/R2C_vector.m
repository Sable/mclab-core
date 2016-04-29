function [M_complex] = R2C_vector(M_real)
%function [M_complex] = R2C_vector(M_real)
%This function is the inverse of 'C2R_vector.m'.
%
%INPUT:
%  M_real: (2D)xT sized real matrix.
%OUTPUT:
%  M_complex: DxT sized complex matrix.

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

M_complex = M_real(1:2:end-1,:) + i * M_real(2:2:end,:);	