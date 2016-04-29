function [e_hat,W_hat]  = estimate_complex_ICA(x,ICA)
%function [e_hat,W_hat]  = estimate_complex_ICA(x,ICA)
%Performs complex ICA on signal x.
%
%INPUT:
%   x: x(:,t) is the t^th sample.
%   ICA: the method to perform independent component analysis. Possibilities: 
%      1)ICA.opt_type = 'fastICA'.
%      2)ICA.opt_type = 'EASI'.
%OUTPUT:
%   e_hat: estimated ICA elements; e_hat(:,t) is the t^th sample.
%   W_hat: estimated ICA demixing matrix.

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

disp('Complex ICA estimation: started.');

switch ICA.opt_type
    case 'fastICA'
            [x,W_whiten] = complex_whiten(x);
            defl = 1;%1:deflation, 0:no deflation.
            [e_hat,W_ICA] = cfastica_public(x,defl);
            W_hat = W_ICA * W_whiten;
    case 'EASI'
        [e_hat,W_hat] = estimate_ICA_EASI(x,size(x,1));
    otherwise
        error('Complex ICA method=?');
end

disp('Complex ICA estimation: ready.');
