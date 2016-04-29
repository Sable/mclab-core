function [F] = expF_F(distr,np) 
%function [F] = expF_F(distr,np) 
%Computes the log-normalizer (F) at a given natural parameter value (np) for the input exponential family.
%
%INPUT:
%   distr: 'normal', 'normalI'.
%   np   : natural parameters.
%          distr = 'normal': np.t1 = C^{-1}*m, np.t2 = 1/2*C^{-1}, where m is the mean, C is the covariance matrix.
%                  'normalI': no.m = m, where m is the mean.  

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
    case 'normal' %Ref: Frank Nielsen, Vincent Garcia. Statistical exponential families: A digest with flash cards. "http://arxiv.org/abs/0911.4863"
        d = length(np.t1);
        F = trace(inv(np.t2) * np.t1 * np.t1.') /4 - log(det(np.t2)) / 2 + d * log(pi) / 2;
    case 'normalI'
        F = sum((np.m).^2) / 2;
    otherwise
       error('Distribution=?');   
end
