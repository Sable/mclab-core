function [K] = analytical_value_KPP(distr1,distr2,rho_K,par1,par2)
%function [K] = analytical_value_KPP(distr1,distr2,rho_K,par1,par2)
%Analytical value (K) of the probability product kernel for the given distributions. See also 'quick_test_KPP.m'.
%
%INPUT:
%   distr1 : name of the distribution-1.
%   distr2 : name of the distribution-2.
%   rho_K  : parameter of the probability product kernel.
%   par1   : parameters of the distribution (structure).
%   par2   : parameters of the distribution (structure).
%
%If (distr1,distr2) = ('normal','normal'). par1.mean = mean1, par1.cov = covariance matrix1; par2.mean = mean2, par2.cov = covariance matrix2.

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

if strcmp(distr1,'normal') && strcmp(distr2,'normal') %Tony Jebara, Risi Kondor, and Andrew Howard. Probability product kernels. Journal of Machine Learning Research, 5:819-844, 2004
    m1 = par1.mean; C1 = par1.cov;
    m2 = par2.mean; C2 = par2.cov;
    d = length(m1);
    
    %inv12:
        inv1 = inv(C1);
        inv2 = inv(C2);
        inv12 = inv(inv1+inv2);
    m12 = inv1 * m1 + inv2 * m2;
    K = (2*pi)^((1-2*rho_K)*d/2) * rho_K^(-d/2) * abs(det(inv12))^(1/2) * abs(det(C1))^(-rho_K/2) * abs(det(C2))^(-rho_K/2) * exp(-rho_K/2*(m1.'*inv1*m1 + m2.'*inv2*m2 - m12.'*inv12*m12));
else
    error('Distribution=?');                 
end
