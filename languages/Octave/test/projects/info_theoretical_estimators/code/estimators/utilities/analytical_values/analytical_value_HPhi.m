function [H] = analytical_value_HPhi(distr,Hpar,par)
%function [H] = analytical_value_HPhi(distr,Hpar,par)
%Analytical value (H) of the Phi-entropy for the given distribution. See also 'quick_test_HPhi.m'.
%
%INPUT:
%   distr  : name of the distribution.
%   par    : parameters of the distribution (structure).
%   Hpar   : parameter of the Phi-entropy. 
%            In case of Phi = @(t)(t.^c_H): Hpar.c_H.
%
%If distr = 'uniform': par.a, par.b in U[a,b].

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
           a = par.a; b = par.b;        
           c_H = Hpar.c_H;
           H = 1 / (b-a)^c_H; 
    otherwise
        error('Distribution=?');                 
end
