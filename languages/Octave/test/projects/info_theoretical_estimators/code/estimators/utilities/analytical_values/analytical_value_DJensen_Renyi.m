function D = analytical_value_DJensen_Renyi(distr1,distr2,w_D,par1,par2)
%function D = analytical_value_DJensen_Renyi(distr1,distr2,w_D,par1,par2)
%Analytical value (D) of the Jensen-Renyi divergence for the given distributions. See also 'quick_test_DJensen_Renyi.m'.
%
%INPUT:
%   distr1 : name of distribution-1.
%   distr2 : name of distribution-2.
%   w_D    : weight used in the Jensen-Renyi divergence.
%   par1   : parameters of distribution-1 (structure).
%   par2   : parameters of distribution-2 (structure).
%
%If (distr1,distr2) = ('normal','normal'): par1.mean = mean1, par1.std = covariance matrix1 (s1^2xI); par2.mean = mean2, par2.std = covariance matrix2 (s2^2xI).

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

if strcmp(distr1,'normal') && strcmp(distr2,'normal') %Fei Wang, Tanveer Syeda-Mahmood, Baba C. Vemuri, David Beymer, and Anand Rangarajan. Closed-Form Jensen-Renyi Divergence for Mixture of Gaussians and Applications to Group-Wise Shape Registration. Medical Image Computing and Computer-Assisted Intervention, 12: 648–655, 2009.
    m1 = par1.mean; s1 = par1.std;
    m2 = par2.mean; s2 = par2.std;
            
    ms = [m1,m2];
	ss = [s1,s2];
	term1 = compute_H2(w_D,ms,ss);
	term2 = w_D(1) * compute_H2(1,m1,s1) + w_D(2) * compute_H2(1,m2,s2);
	D  = term1 - term2; %H2(\sum_i wi Yi) - \sum_i w_i H2(Yi), where H2 is the quadratic Renyi entropy
else
    error('Distribution=?'); 
end
