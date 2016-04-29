function [M_real] = C2R_vector(M_complex)
%function [M_real] = C2R_vector(M_complex)
%Transforms the k^th column of matrix M_complex to the k^th column of
%matrix M_real applying the  c->[real(c) imag(c)] transformation ([x1 + i * y1;...;xD + i * yD] -> [x1;y1;...;xD;yD]) coordinate-wise. This function is the inverse of 'R2C_vector.m'.
%
%INPUT:
%  M_complex: DxT sized complex matrix.
%OUTPUT:
%  M_real: (2D)xT sized real matrix.

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

[D,T] = size(M_complex);%dimension, number of samples

M_real = zeros(2*D,T);%preallocation
M_real(1:2:2*D-1,:) = real(M_complex);
M_real(2:2:2*D,:) = imag(M_complex);
