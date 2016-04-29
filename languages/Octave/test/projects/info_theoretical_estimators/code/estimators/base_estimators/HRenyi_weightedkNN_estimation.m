function [H] = HRenyi_weightedkNN_estimation(Y,co)
%function [H] = HRenyi_weightedkNN_estimation(Y,co)
%Estimates the Renyi entropy (H) of Y using the weighted k-nearest neighbor method.
%
%We use the naming convention 'H<name>_estimation' to ease embedding new entropy estimation methods.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  co: entropy estimator object.
%
%REFERENCE: 
%   Kumar Sricharan and Alfred. O. Hero. Weighted k-NN graphs for Renyi entropy estimation in high dimensions. IEEE Workshop on Statistical Signal Processing (SSP), pages 773-776, 2011. 
%
%Note: This code has been written on the basis of the implementation kindly provided by Kumar Sricharan.

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

%initialization:
    [d,T] = size(Y);
    k1 = ceil(0.1*(sqrt(T)));
    k2 = ceil(2*sqrt(T));

%calculate optimal weight (if it does not exist) => wo:
    FN = strcat('wo_weightedkNN_T',num2str(T),'_d',num2str(d),'.mat');
    if ~exist(FN)
        disp(strcat('Computing weights for the weighted kNN method: T=',num2str(T),', d=',num2str(d),'.'));
        dp = d;
        wo = calculateweight(T,d,k1,k2,dp);
        save(FN,'wo');
    else
        load(FN,'wo');
    end

cdunitball = volume_of_the_unit_ball(d); %Volume of unit ball in d dimensions
kvec = k1:k2; 
N = floor(T/2);
M = T - N;

%weighted estimator:
    Yn = Y(:,1:N);
    Ym = Y(:,(N+1):T);
    
    co.k = k2;
    [dists,nnidx] = kNN_squared_distances(Ym,Yn,co,0);
    kdist = sqrt(dists(kvec,:)).';
    
    %note gamma(kvec)./gamma(kvec+1-alpha) = beta(kvec,1-alpha)./gamma(1-alpha)
    etahatvec = (beta(kvec,1-co.alpha)./gamma(1-co.alpha)).*mean((((M).*cdunitball.*(kdist.^d))).^(1-co.alpha));
    H = (sum(wo .* etahatvec));
    H = log(H) / (1-co.alpha);
