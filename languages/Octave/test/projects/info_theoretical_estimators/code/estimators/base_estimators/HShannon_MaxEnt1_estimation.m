function [H] = HShannon_MaxEnt1_estimation(Y,co)
%function [H] = HShannon_MaxEnt1_estimation(Y,co)
%Estimates the Shannon entropy (H) of Y using the maximum entropy distribution method. The used Gi functions are G1(x) = x exp(-x^2/2) and G2(x) = abs(x).
%
%We use the naming convention 'H<name>_estimation' to ease embedding new entropy estimation methods.
%
%INPUT:
%   Y: Y(:,t) is the t^th sample.
%  co: entropy estimator object.
%
%REFERENCE: 
%   Aapo Hyvarinen. New approximations of differential entropy for independent component analysis and projection pursuit. In Advances in Neural Information Processing Systems (NIPS), pages 273-279, 1997. (entropy approximation based on the maximum entropy distribution)
%   Thomas M. Cover and Joy A. Thomas. Elements of Information Theory. John Wiley and Sons, New York, USA, 1991. (maximum entropy distribution)

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

[d,num_of_samples] = size(Y);

%verification:
    if d~=1
        error('The samples must be one-dimensional for this estimator.');
    end
    
%normalize Y to have zero mean and unit std:
    Y = whiten_E0(Y);%E=0, this step does not change the Shannon entropy of the variable
    %std(Y)=1:
        s = sqrt(sum(Y.^2)/(num_of_samples-1));%= std(Y,[],2)
        Y = Y / s;
        H_whiten = log(s);%we will take this scaling into account via the entropy transformation rule [ H(wz) = H(z)+log(|w|) ] at the end
        
%H1,H2 -> H:
    H1 = ( 1+log(2*pi) ) / 2; %=H[N(0,1)]
    %H2:
        k1 = 36 / ( 8*sqrt(3) - 9 );
        k2a = 1 / ( 2 - 6/pi );
        H2 = k1 * mean(Y .* exp(-Y.^2/2))^2 + k2a * (mean(abs(Y)) - sqrt(2/pi))^2;
    H = H1 - H2;
    
%take into account the 'std=1' pre-processing:
    H = H + H_whiten;


