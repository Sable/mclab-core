function [K] = analytical_value_KEJT1(distr1,distr2,u_K,par1,par2)
%function [K] = analytical_value_KEJT1(distr1,distr2,u_K,par1,par2)
%Analytical value (K) of the Jensen-Tsallis kernel-1 for the given distributions. See also 'quick_test_KEJT1.m'.
%
%INPUT:
%   distr1 : name of the distribution-1.
%   distr2 : name of the distribution-2.
%   u_K    : parameter of the Jensen-Tsallis kernel-1 ((w = [1/2;1/2]: fixed).
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
    m1 = par1.mean; s1 = par1.std;
    m2 = par2.mean; s2 = par2.std;
    %analytical value of Renyi entropy (Fei Wang, Tanveer Syeda-Mahmood, Baba C. Vemuri, David Beymer, and Anand Rangarajan. Closed-Form Jensen-Renyi Divergence for Mixture of Gaussians and Applications to Group-Wise Shape Registration. Medical Image Computing and Computer-Assisted Intervention, 12: 648–655, 2009.):
        ms = [m1,m2];
        ss = [s1,s2];
        HR = compute_H2([1/2,1/2],ms,ss);%quadratic Renyi entropy
    %quadratic Renyi entropy -> quadratic Tsallis entropy:
        HT = 1 - exp(-HR);
    K = exp(-u_K*HT);
else
    error('Distribution=?');                 
end

    

