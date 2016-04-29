function [e_hat,W_hat]  = estimate_ICA(x,ICA,dim_reduction)
%function [e_hat,W_hat]  = estimate_ICA(x,ICA,dim_reduction)
%Performs ICA on signal x.
%
%INPUT:
%   x: x(:,t) is the t^th sample.
%   ICA: the method to perform independent component analysis. Possibilities: 
%      1)ICA.opt_type = 'fastICA'.
%      2)ICA.opt_type = 'EASI'.
%   dim_reduction: <=dim(x)[= size(x,1)]; in case of '<', dimension reduction is also carried out.
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

disp('ICA estimation: started.');

switch ICA.opt_type
    case 'fastICA'
        %whitening:
            %1)If (i) e is white and (ii) A is orthogonal, then this step can be discarded.
            %2)Alternative: perform whitening in ICA directly. It is often
            %advantageous to carry out whitening separately since it reduces the number
            %of parameters to be optimized (approximately) to the half. [A dxd sized 'W' demixing matrix has d^2 parameters, a dxd sized ortogonal matrix can be described by only dx(d+1)/2 parameters.]
            [x,W_whiten] = whiten(x,dim_reduction);
        [e_hat,A_ICA,W_ICA] = fastica(x, 'whiteSig',x,'whiteMat',eye(dim_reduction),'dewhiteMat',eye(dim_reduction), ...
                                     'approach', 'symm', 'g', 'tanh','displayMode', 'off','verbose','off',  ...
                                     'stabilization','on','maxNumIterations',3000); 
        W_hat = W_ICA * W_whiten;
    case 'EASI'
        [e_hat,W_hat] = estimate_ICA_EASI(x,dim_reduction);
    case 'Jacobi1'
        %whitening: see the note at 'fastICA' above.
            [x,W_whiten] = whiten(x,dim_reduction);
        [e_hat,W_ICA] = estimate_ICA_Jacobi1(x,ICA);
        W_hat = W_ICA * W_whiten;
    case 'Jacobi2'
        %whitening: see the note at 'fastICA' above.
            [x,W_whiten] = whiten(x,dim_reduction);
        [e_hat,W_ICA] = estimate_ICA_Jacobi2(x,ICA);
        W_hat = W_ICA * W_whiten;
    otherwise
        error('ICA method=?');
end

disp('ICA estimation: ready.');
