function [K] = analytical_value_Kexpected(distr1,distr2,sigma_K,par1,par2)
%function [K] = analytical_value_Kexpected(distr1,distr2,sigma_K,par1,par2)
%Analytical value (K) of the expected kernel for the given distributions. See also 'quick_test_Kexpected.m'.
%
%INPUT:
%   distr1 : name of the distribution-1.
%   distr2 : name of the distribution-2.
%   sigma_K: parameter of the expected kernel.
%   par1   : parameters of the distribution (structure).
%   par2   : parameters of the distribution (structure).
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

if strcmp(distr1,'normal') && strcmp(distr2,'normal') %Krikamol Muandet, Kenji Fukumizu, Francesco Dinuzzo, and Bernhard Scholkopf. Learning from distributions via support measure machines. In Advances in Neural Information Processing Systems (NIPS), pages 10-18, 2011.
    m1 = par1.mean; C1 = par1.cov;
    m2 = par2.mean; C2 = par2.cov;
    d = length(m1);
    
    gam = 1/sigma_K^2;
    diffm = m1 - m2; 
    K = exp(-1/2*(diffm.'*inv(C1+C2+eye(d)/gam)*diffm)) / sqrt(abs(det(gam*C1+gam*C2+eye(d))));
else
    error('Distribution=?');                 
end
