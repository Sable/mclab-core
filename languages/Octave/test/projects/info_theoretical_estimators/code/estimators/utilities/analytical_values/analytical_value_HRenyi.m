function [H] = analytical_value_HRenyi(distr,alpha_H,par)
%function [H] = analytical_value_HRenyi(distr,alpha_H,par)
%Analytical value (H) of the Renyi entropy for the given distribution. See also 'quick_test_HRenyi.m'.
%
%INPUT:
%   distr  : name of the distribution.
%   alpha_H: parameter of the Renyi entropy.
%   par    : parameters of the distribution (structure).
%
%If distr = 'uniform': par.a, par.b, par.A <- AxU[a,b],
%           'normal' : par.cov = covariance matrix.

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

switch distr
    case 'uniform' %we also applied the transformation rule of Renyi entropy
        a = par.a; b = par.b; A = par.A;
        H = log(prod(b-a))  + log(abs(det(A)));
    case 'normal' %Kai-Sheng Song. Renyi information, loglikelihood and an intrinsic distribution measure. Journal of Statistical Planning and Inference 93 (2001) 51-69.
        C = par.cov;
        d = size(C,1);
        
        H = log( (2*pi)^(d/2) * sqrt(abs(det(C))) ) - d * log(alpha_H) / 2 / (1-alpha_H);
    otherwise
        error('Distribution=?');                 
end            

