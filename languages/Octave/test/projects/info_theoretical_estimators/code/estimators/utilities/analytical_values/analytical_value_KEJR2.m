function [K] = analytical_value_KEJR2(distr1,distr2,u_K,par1,par2)
%function [K] = analytical_value_KEJR2(distr1,distr2,u_K,par1,par2)
%Analytical value (K) of the Jensen-Renyi kernel-2 for the given distributions. See also 'quick_test_KEJR2.m'.
%
%INPUT:
%   distr1 : name of the distribution-1.
%   distr2 : name of the distribution-2.
%   u_K    : parameter of the Jensen-Renyi kernel-2 (alpha_K = 2: fixed).
%   par1   : parameters of the distribution (structure).
%   par2   : parameters of the distribution (structure).
%
%If (distr1,distr2) = ('normal','normal'): par1.mean, par1.std <- N(m1,s1^2xI); par2.mean, par2.std <- N(m2,s2^2xI).

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

if strcmp(distr1,'normal') && strcmp(distr2,'normal')
    w_D = [1/2;1/2];%parameter of Jensen-Renyi divergence, fixed
    D = analytical_value_DJensen_Renyi(distr1,distr2,w_D,par1,par2)
    K = exp(-u_K*D);
else
    error('Distribution=?');                 
end
            
