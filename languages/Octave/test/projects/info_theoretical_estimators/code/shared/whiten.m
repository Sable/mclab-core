function [e_whitened,C] = whiten(e,dim_reduction)
%function [e_whitened,C] = whiten(e,dim_reduction)
%Transforms variable e to have zero expectation, then applies PCA based dimension reduction and linearly transforms the data to have identity covariance.
%
%INPUT:
%   e: e(:,t) is the t^th sample.
%   dim_reduction: dim(e)=size(e,1) >= dim_reduction.
%OUTPUT:
%   e_whitened: whitened(and PCA-reduced) e (E=0,cov=I); e_whitened(:,t) is the t^th sample.
%   C: C * e = e_whitened.

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

[De,num_of_samples] = size(e);

%E=0:
    e_whitened = whiten_E0(e);
    
%[PCA,]cov=I:    
    if De > dim_reduction  %dimension reduction by PCA, +cov=I -- PCA can change coordinates of the subspaces,...:
        [W_PCA,D] = pcamat(e_whitened,1,dim_reduction);
        invsqrtdiagD = 1./sqrt(diag(D));%column vector
        C = diag(invsqrtdiagD) * W_PCA.';
        e_whitened =  C * e_whitened;
    else %cov=I
        cov_mtx = e_whitened * e_whitened.' / (num_of_samples-1);%E=0 is exploited
        C = cov_mtx^(-1/2);
        C = real(C); %real: because of rounding some imaginary part (in order (10^{-n})) needs to be removed
        e_whitened =  C * e_whitened; 
    end
    
disp('Whitening: ready.');