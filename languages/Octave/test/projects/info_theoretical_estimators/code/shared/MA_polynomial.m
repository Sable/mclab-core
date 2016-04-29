function [H] = MA_polynomial(Dx,De,MA)
%function [H] = MA_polynomial(Dx,De,MA)
%Generates a MA polynomial H[z] of dimension Dx x De (H[z]\in R[z]^{Dx x De}) and length L, to an uMA-IPA/complete MA-IPA problem.
%
%INPUT: 
%   MA.type: 
%      1)'randn' ('rand'): the coordinates of H are i.i.d. standard normal (uniform = U[0,1]) variables.
%      2)'stable': H corresponds to the polynomail matrix H[z] = [\prod_{k=1}^L(I-lambda O_i z^{-1})]H_0: O_i,H_0:random orthogonal, |MAparameters.lambda|<1.
%   MA.L: length of the convolution; H_0,...,H_{L}: L+1 H_j matrices.
%OUTPUT:
%   H = [H_0,...,H_L], where H[z] = \sum_{l=0}^L H_l z^{-l}.
%EXAMPLE:
%   %uMA-IPA:
%      MA.type = 'randn';
%      H = MA_polynomial(4,2,3,MA);
%   %MA-IPA:
%       MA.type = 'stable';
%       MA.lambda = 0.4;
%       H = MA_polynomial(4,2,3,MA);

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

switch MA.type
    case 'randn'%uMA-IPA
        H = randn(Dx,De*(MA.L+1));
    case 'rand'%uMA-IPA
        H = rand(Dx,De*(MA.L+1));  
    case 'stable'%MA-IPA (Dx=De)
        if Dx==De
            MA_L = MA.L;
            MA_lambda = MA.lambda;
            %Hz = [\prod_{k=1}^L(I-lambda O_i z^{-1})]: O_i:random orthogonal.
                Hz = {eye(De)};            
                for k = 1 : MA_L
                    Hznew = {};
                    Hznew{1}= eye(De);
                    Hznew{2} = -MA_lambda * random_orthogonal(De);
                    Hz = multiply_polynomial_matrices(Hz,Hznew);
                end
            %xH0, H_0: orthogonal.
                H0 = random_orthogonal(De);
                H = zeros(De,De*(MA_L+1));%preallocation
                for k = 1 : length(Hz)
                    H(:,(k-1)*De+1:k*De) = Hz{k} * H0;
                end
        else
            error('dim(x) must be equal to dim(e), i.e., we are focusing on complete systems!');
        end
    otherwise 
        error('MA type=?');
end
