function [M_R] = C2R_matrix(M_C)
%function [M_R] = C2R_matrix(M_C)
%Transforms the complex L_1 x L_2 sized complex matrix (M_C) to the real (2L_1) x (2L_2) sized matrix (M_R) applying the c->[real(c) -imag(c); imag(c) real(c)] transformation to the elements of M_C.

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

[s1,s2] = size(M_C);
M_R = zeros(2*s1,2*s2);

for i1 = 1 : s1
    for i2 = 1 : s2  
        c = M_C(i1,i2);
        M_R(2*i1-1:2*i1,2*i2-1:2*i2) = [real(c) -imag(c); imag(c) real(c)];
    end
end
