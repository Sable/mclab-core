function [H] = analytical_value_HShannon(distr,par)
%Analytical value (H) of the Shannon entropy for the given distribution. See also 'quick_test_HShannon.m'.
%
%INPUT:
%   distr  : name of the distribution.
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
    case 'uniform' %we also applied the transformation rule of Shannon entropy
        a = par.a; b = par.b; A = par.A;
        H = log(prod(b-a)) + log(abs(det(A))); 
    case 'normal'   
        C = par.cov;
        d = size(C,1);
        
        H =  1/2 * log( (2*pi*exp(1))^d * det(C) ); %equals to: H = 1/2 * log(det(C)) + d/2*log(2*pi) + d/2
    otherwise
        error('Distribution=?');
end  
        
