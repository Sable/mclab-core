function [I] = analytical_value_IRenyi(distr,alpha_I,par)
%function [I] = analytical_value_IRenyi(distr,alpha_I,par)
%Analytical value (I) of the Renyi mutual information for the given distribution. See also 'quick_test_IRenyi.m'.
%
%INPUT:
%   distr  : name of the distribution.
%   alpha_I: parameter of the Renyi mutual information.
%   par    : parameters of the distribution (structure).
%
%If distr = 'normal': par.cov = covariance matrix.

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
    case 'normal'
        C = par.cov;
        t1 = -alpha_I / 2 * log(det(C));
        t2 = -(1-alpha_I) / 2 * log(prod(diag(C)));
        t3 = log( det( alpha_I*inv(C) + (1-alpha_I)*diag(1./diag(C)) ) ) / 2;
        I = 1 / (alpha_I-1) * (t1+t2-t3);            
    otherwise
        error('Distribution=?');            
end