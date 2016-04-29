function [e_whitened,C_whiten] = complex_whiten(e)    
%function [e_whitened,C_whiten] = complex_whiten(e)    
%Transforms variable e to have zero expectation and identity covariance.
%
%INPUT:
%   e: e(:,t) is the sample at time t.
%OUTPUT:
%   e_whitened: whitened version of e. e_whitened(:,t) is the sample at time t.
%   C_whiten: whitening transformation.

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

num_of_samples = size(e,2);

%E=0:
    E = repmat(mean(e,2),1,num_of_samples);
    e_whitened = e - E;
    
%Cov=I:    
    C = complex_cov(e_whitened);
	C_whiten = inv(sqrtm(C));
    e_whitened = C_whiten * e;
    
disp('Complex whitening: ready.');

%-------------
function [C] = complex_cov(u)
%Returns the complex covariance of signal u.
%
%INPUT:
%   u: u(:,t) is the t^th sample.

C = u * u' / (size(u,2)-1); %': conjugation, not only .' transposition