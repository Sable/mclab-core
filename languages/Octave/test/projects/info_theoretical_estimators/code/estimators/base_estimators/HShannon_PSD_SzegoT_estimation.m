function [H] = HShannon_PSD_SzegoT_estimation(Y,co)
%function [H] = HShannon_PSD_SzegoT_estimation(Y,co)
%Estimates the Shannon entropy (H) of Y using power spectral density representation and Szego's theorem.
%
%We use the naming convention 'H<name>_estimation' to ease embedding new entropy estimation methods.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  co: entropy estimator object.
%
%REFERENCE: 
%   David Ramirez, Javier Via, Ignacio Santamaria, and Pedro Crespo. Entropy and Kullback-Leibler divergence estimation based on Szego's theorem. In European Signal Processing Conference (EUSIPCO), pages 2470-2474, 2009.
%   Robert M. Gray. Toeplitz and circulant matrices: A review. Foundations and Trends in Communications and Information Theory, 2:155-239, 2006. (Szego's theorem)
%   Ulf Grenander and Gabor Szego. Toeplitz forms and their applications. University of California Press, 1958. (Szego's theorem)

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

%co.mult:OK. The information theoretical quantity of interest can be (and is!) estimated exactly [co.mult=1]; the computational complexity of the estimation is essentially the same as that of the 'up to multiplicative constant' case [co.mult=0]. In other words, the estimation is carried out 'exactly' (instead of up to 'proportionality').

%verification:
    if size(Y,1)~=1
        error('The samples must be one-dimensional for this estimator.');
    end

H = entropy_est_szego_ar(Y,co.p_max,co.K) / log2(exp(1)); %'/ log2(exp(1))': log_2 -> log_e definition

