function [D] = analytical_value_DHellinger(distr1,distr2,par1,par2)
%function [D] = analytical_value_DHellinger(distr1,distr2,par1,par2)
%Analytical value (D) of the Hellinger distance for the given distributions. See also 'quick_test_DHellinger.m'.
%
%INPUT:
%   distr1 : name of distribution-1.
%   distr2 : name of distribution-2.
%   par1   : parameters of distribution-1 (structure).
%   par2   : parameters of distribution-2 (structure).
%
%If (distr1,distr2) = ('normal','normal'): par1.mean = mean1, par1.cov = covariance matrix1; par2.mean = mean2, par2.cov = covariance matrix2.

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
    %covariance matrices, expectations:
        m1 = par1.mean; C1 = par1.cov;
        m2 = par2.mean; C2 = par2.cov;
        
    %"https://en.wikipedia.org/wiki/Hellinger_distance": Examples: 
    diffm = m1 - m2;
    avgC = (C1 + C2) / 2;
    D = 1 - det(C1)^(1/4) * det(C2)^(1/4) / det(avgC)^(1/2) * exp(-(diffm.' * inv(avgC) * diffm)/8); %=D^2
    D = sqrt(D);
else
    error('Distribution=?'); 
end




