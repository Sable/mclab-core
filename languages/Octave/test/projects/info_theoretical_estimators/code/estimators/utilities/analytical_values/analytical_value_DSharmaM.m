function [D] = analytical_value_DSharmaM(distr1,distr2,alpha_D,beta_D,par1,par2)
%function [D] = analytical_value_DSharmaM(distr1,distr2,alpha_D,beta_D,par1,par2)
%Analytical value (D) of the Sharma-Mittal divergence for the given distributions. See also 'quick_test_DSharmaM.m'.
%
%INPUT:
%   distr1  : name of the distribution1.
%   distr2  : name of the distribution2.
%   par1    : parameters of distribution1 (structure).
%   par2    : parameters of distribution2 (structure).
%   alpha_D: parameter of the Sharma-Mittal divergence; > 0, not 1.
%   beta_D : parameter of the Sharma-Mittal divergence; not 1.
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

if strcmp(distr1,'normal') && strcmp(distr2,'normal') %Frank Nielsen and Richard Nock. A closed-form expression for the Sharma-Mittal entropy of exponential families. Journal of Physics A: Mathematical and Theoretical, 45:032003, 2012.
    %covariance matrices, expectations:
        m1 = par1.mean; C1 = par1.cov;
        m2 = par2.mean; C2 = par2.cov;
    C = inv(alpha_D * inv(C1) + (1-alpha_D) * inv(C2));
    diffm = m1 - m2;
    
    %Jensen difference divergence, C:
        J = ( log( abs(det(C1))^alpha_D * abs(det(C2))^(1-alpha_D) / abs(det(C)) ) + alpha_D * (1-alpha_D) * diffm.' * inv(C) * diffm ) / 2;
        C = exp(-J);
        
    D = ( C^((1-beta_D)/(1-alpha_D)) - 1 )/ (beta_D - 1);
    
 else
    error('Distribution=?'); 
end
