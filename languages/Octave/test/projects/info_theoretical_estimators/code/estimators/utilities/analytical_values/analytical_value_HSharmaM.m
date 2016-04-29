function [H] = analytical_value_HSharmaM(distr,alpha_H,beta_H,par)
%function [H] = analytical_value_HSharmaM(distr,alpha_H,beta_H,par)
%Analytical value (H) of the Sharma-Mittal entropy for the given distribution. See also 'quick_test_HSharmaM.m'.
%
%INPUT:
%   distr  : name of the distribution.
%   par    : parameters of the distribution (structure).
%   alpha_H: parameter of the Sharma-Mittal entropy; alpha: >0, not 1.
%   beta_H : parameter of the Sharma-Mittal entropy; beta: not 1.
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
    case 'normal' %Frank Nielsen and Richard Nock. A closed-form expression for the Sharma-Mittal entropy of exponential families. Journal of Physics A: Mathematical and Theoretical, 45:032003, 2012.
        C = par.cov;
        d = size(C,1); %=size(C,2)
        H = ( ( (2*pi)^(d/2) * sqrt(abs(det(C))) )^(1-beta_H) / alpha_H^( d*(1-beta_H) / (2*(1-alpha_H)) )  - 1 ) / (1-beta_H);
    otherwise
        error('Distribution=?');                 
end
