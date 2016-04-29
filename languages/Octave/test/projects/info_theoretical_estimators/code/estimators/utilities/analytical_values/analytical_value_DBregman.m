function [D] = analytical_value_DBregman(distr1,distr2,alpha_D,par1,par2)
%function [D] = analytical_value_DBregman(distr1,distr2,alpha_D,par1,par2)
%Analytical value (D) of the Bregman divergence for the given distributions. See also 'quick_test_DBregman.m'.
%
%INPUT:
%   distr1 : name of distribution-1.
%   distr2 : name of distribution-2.
%   alpha_D: parameter of the Bregman divergence.
%   par1   : parameters of distribution-1 (structure).
%   par2   : parameters of distribution-2 (structure).
%
%If (distr1,distr2) = ('uniform','uniform'): par1.a = a in U[0,a], par2.a = b in U[0,b].

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

if strcmp(distr1,'uniform') && strcmp(distr2,'uniform')
    a = par1.a;
    b = par2.a;
    D = -1/(alpha_D-1)*prod(b)^(1-alpha_D) + 1/(alpha_D-1) * prod(a)^(1-alpha_D);
else
    error('Distribution=?');                 
end
