function [A] = mixing_matrix_TCC(H,de,L2)
%function [A] = mixing_matrix_TCC(H,de,L2)
%Computes the mixing matrix (A) associated to the TCC based uMA-IPA method.
%
%INPUT:
%   H: polynomial matrix corresponding to the convolution (=MA).   
%   de: subspace dimensions.
%   L2: determines (i) the number times the subspaces are recovered (L+L2), (ii) the dimension of the associated ISA task (De x (L+L2)).

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

%dimension of the observation (Dx) and the source (De), number of subspaces (num_of_comps), convolution length (L;[H_0,...H_L]):
    Dx = size(H,1);
    De = sum(de);
    num_of_comps = length(de);
    L = size(H,2)/De - 1;
    
cum_de = cumsum([1;de(1:end-1)]);%1,d_1+1,d_1+d_2+1,...,d_1+...+d_{M-1}+1 = starting indices of the subspaces (M=number of subspaces).
    
A = [];
for i1 = 1 : Dx
    %A_i1:
        A_i1 = [];
        for i2 = 1 : num_of_comps
            d_i2 = de(i2);%dimension of the i2^th subspace
            %A_i1i2:
                A_i1i2 = [];
                %H_i1i2:
                    I = [cum_de(i2):cum_de(i2)+d_i2-1];%l=0
                    %I->I+0xD,I+1xD,I+2xD,...,I+LxD:
                        I = repmat(I,1,L+1) + kron([0:L],ones(1,d_i2)) * De;
                    H_i1i2 = H(i1,I);
                for k = 1 : L2
                    A_i1i2 = [A_i1i2; zeros(1,(k-1)*d_i2),H_i1i2,zeros(1,(L2-k)*d_i2)];
                end
            A_i1 = [A_i1,A_i1i2];
        end
    A = [A;A_i1];
end
    