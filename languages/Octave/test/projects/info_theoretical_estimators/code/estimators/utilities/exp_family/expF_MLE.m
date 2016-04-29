function [np] = expF_MLE(Y,distr)
%function [np] = expF_MLE(Y,distr)
%Maximum likelihood estimation with the given exponential family on data Y. See also 'expF_F.m'.
%
%INPUT:
%   distr: 'normal'.
%   Y    : data, Y(:,t) is the t^{th} sample.
%OUTPUT:
%   np: estimated natural parameters.
%       distr = 'normal': np.t1 = C^{-1}*m, np.t2 = 1/2*C^{-1}, where m is the mean, C is the covariance matrix.

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
        m = mean(Y,2);
        C = cov(Y.');
        invC = inv(C);
        np.t1 = invC * m;
        np.t2 = invC / 2;
    case 'normalI' 
        np.m = mean(Y,2);
    otherwise
       error('Distribution=?');                 
end
