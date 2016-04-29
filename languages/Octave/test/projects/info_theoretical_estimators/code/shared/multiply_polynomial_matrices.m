function [H] = multiply_polynomial_matrices(varargin)
%function [H] = multiply_polynomial_matrices(varargin)
%Multiplies polynomial matrices, i.e., H[z]=H_1[z]*...*H_N[z], where H_i[z] is in cell array format (varargin) similarly to
%the output H. For example, if H[z]=H_0+H_1*z^{-1}+...+H_N*z^{-N} then H={H_0,H_1,...H_N}.
%Assumption: inputs can be multiplied (=sizes are verified).

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

N = length(varargin);
%((1*2)*3...)*N:
    H = varargin{1};
    for n = 2 : N
        H = multiply_two_polynomial_matrices(H,varargin{n});
    end

%-----------------------
function [C] = multiply_two_polynomial_matrices(A,B)
%C[z]=A[z]*B[z]

%initialization:
    degA = length(A)-1;
    degB = length(B)-1;
    %C initialization:
        sizeA1 = size(A{1},1);
        sizeB2 = size(B{1},2);
        C = cell(1,degA+degB+1);
        for k = 1 : length(C)
            C{k} = zeros(sizeA1,sizeB2);
        end

for ndegA = 0 : degA
    for ndegB = 0 : degB
        C{ndegA+ndegB+1} = C{ndegA+ndegB+1} + A{ndegA+1} * B{ndegB+1};
    end
end
