function [I] = analytical_value_IShannon(distr,par)
%function [I] = analytical_value_IShannon(distr,par)
%Analytical value (I) of the Shannon mutual information for the given distribution. See also 'quick_test_IShannon.m'.
%
%INPUT:
%   distr  : name of the distribution.
%   par    : parameters of the distribution (structure).
%
%If distr = 'normal': par.ds = vector of component dimensions; par.cov = (joint) covariance matrix.

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
    
switch distr
    case 'normal'
        ds = par.ds;
        C = par.cov;
        
        cum_ds = cumsum([1;ds(1:end-1)]);%1,d_1+1,d_1+d_2+1,...,d_1+...+d_{M-1}+1 = starting indices of the subspaces (M=number of subspaces).
        I = 1;
        for m = 1 : length(ds)
            iv = [cum_ds(m):cum_ds(m)+ds(m)-1];
            I = I * det(C(iv,iv));
        end
        I = log(I/det(C)) / 2;        
    otherwise
        error('Distribution=?');            
end