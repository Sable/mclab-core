function [D] = analytical_value_DChiSquare(distr1,distr2,par1,par2)
%function [D] = analytical_value_DChiSquare(distr1,distr2,par1,par2)
%Analytical value (D) of the chi^2 divergence for the given distributions. See also 'quick_test_DChiSquare.m'.
%
%INPUT:
%   distr1 : name of distribution1.
%   distr2 : name of distribution2.
%   par1   : parameters of distribution1 (structure).
%   par2   : parameters of distribution2 (structure).
%
%If (distr1,distr2) = ('uniform', 'uniform'): par1.a = a in U[0,a], par2.a = b in U[0,b],
%                     ('normalI', 'normalI')  : par1.mean = mean1, par2.mean = mean2.

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
    D = prod(b)/prod(a) - 1;
elseif strcmp(distr1,'normalI') && strcmp(distr2,'normalI') %Frank Nielsen and Richard Nock. On the chi square and higher-order chi distances for approximating f-divergence. IEEE Signal Processing Letters, 2:10-13, 2014. 
    m1 = par1.mean;
    m2 = par2.mean;
    diffm = m2 - m1;
    D = exp(diffm.' * diffm) - 1;  
else
    error('Distribution=?'); 
end


