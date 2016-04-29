function [H] = analytical_value_HTsallis(distr,alpha_H,par)
%function [H] = analytical_value_HTsallis(distr,alpha_H,par)
%Analytical value (H) of the Tsallis entropy for the given distribution. See also 'quick_test_HTsallis.m'.
%
%INPUT:
%   distr  : name of the distribution.
%   alpha_H: parameter of the Tsallis entropy.
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
    case 'uniform'
        a = par.a; b = par.b; A = par.A;
        
        H = log(prod(b-a))  + log(abs(det(A))); %Renyi entropy; we also applied the transformation rule of Renyi entropy
        H = ( exp((1-alpha_H)*H) - 1 ) / (1-alpha_H); %Renyi entropy -> Tsallis entropy
        %II (assumption: A=I):
           %H = (prod(b-a)^(1-alpha_H) - 1) / (1-alpha_H);
    case 'normal'
        C = par.cov;
        d = size(C,1);
        
        %I:
            H = log( (2*pi)^(d/2) * sqrt(abs(det(C))) ) - d * log(alpha_H) / 2 / (1-alpha_H); %Renyi entropy: Kai-Sheng Song. Renyi information, loglikelihood and an intrinsic distribution measure. Journal of Statistical Planning and Inference 93 (2001) 51-69.
            H = ( exp((1-alpha_H)*H) - 1 ) / (1-alpha_H); %Renyi entropy -> Tsallis entropy
        %II:
            %H = ( ( (2*pi)^(d/2) * sqrt(abs(det(C))) )^(1-alpha_H)  / (alpha_H^(d/2)) - 1 ) / (1-alpha_H);
    otherwise
        error('Distribution=?');                 
end            
        
