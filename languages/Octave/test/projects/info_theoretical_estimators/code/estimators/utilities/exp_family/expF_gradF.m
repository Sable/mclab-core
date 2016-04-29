function [gradF] = expF_gradF(distr,np) 
%function [gradF] = expF_gradF(distr,np) 
%Computes the gradient of the log-normalizer (gradF) at a given natural parameter value (np) for the input exponential family.
%
%INPUT:
%   distr: 'normal', or 'normalI'.
%   np   : natural parameters.
%          distr = 'normal': np.t1 = C^{-1}*m, np.t2 = 1/2*C^{-1}, where mis the mean, C is the covariance matrix,
%                  'normalI': np.m = m, where m is the mean. 

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
        I = inv(np.t2);
        s = I * np.t1;
        gradF.t1 = s / 2;
        gradF.t2 = -I/2 - s * s.' / 4;
    case 'normalI'
	gradF.m = np.m;
    otherwise
       error('Distribution=?');   
end
