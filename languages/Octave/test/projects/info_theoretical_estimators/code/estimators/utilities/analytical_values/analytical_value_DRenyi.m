function [D] = analytical_value_DRenyi(distr1,distr2,alpha_D,par1,par2)
%function [D] = analytical_value_DRenyi(distr1,distr2,alpha_D,par1,par2)
%Analytical value (D) of the Renyi divergence for the given distributions. See also 'quick_test_DRenyi.m'.
%
%INPUT:
%   distr1 : name of distribution-1.
%   distr2 : name of distribution-2.
%  alpha_D : parameter of Renyi divergence.
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
        
    mixC = alpha_D * C2 + (1-alpha_D) * C1;
    diffm = m1 - m2;
    %Renyi divergence (Manuel Gil. On Renyi Divergence Measures for Continuous Alphabet Sources. Phd Thesis, Queen’s University, 2011):
        D = alpha_D * ( 1/2 *diffm.' * inv(mixC) * diffm  - 1 / (2*alpha_D*(alpha_D-1)) * log( abs(det(mixC)) / (det(C1)^(1-alpha_D) * det(C2)^(alpha_D)) )); 
    %Kullback-Leibler divergence (verification, in the alpha_D->1 limit: KL div. = Renyi div.):
        %d = size(C1,1);
        %D = 1/2 * ( log(det(C2)/det(C1)) + trace(inv(C2)*C1)  + diffm.' * inv(C2) * diffm - d);
else
    error('Distribution=?'); 
end




