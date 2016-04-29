function [x_innovation_hat,Fx_hat] = estimate_AR_NIW(x,L)
%function [x_innovation_hat,Fx_hat] = estimate_AR_NIW(x,L)
%AR estimation on observation x via the 'NIW' method, which is an online Bayesian technique with normal-inverted Wishart prior.
%
%INPUT:
%   x: x(:,t) is the observation at time t.
%   L: AR order.
%OUTPUT:
%   x_innovation_hat: estimated innovation of x, at time t it is x_innovation_hat(:,t).
%   Fx_hat: estimated dynamics of x.
%
%REFERENCE:
%   Special case of 'Barnabas Poczos, Andras Lorincz. Identification of Recurrent Neural Networks by Bayesian Interrogation Techniques. Journal of Machine Learning Research 10 (2009) 515-554.'
%   K. Rao Kadiyala and Sune Karlsson. Numerical methods for estimation and inference in Bayesian-VAR models. Journal of Applied Econometrics, 12:99-132, 1997.

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

%initialization:
    [D,num_of_samples] = size(x);
    m =  D * L;
    %M0,K0,invK0:
        M = (2*rand(D,m)-1)*0.001; %you may would like to play with the initialization
        K = 0.0001*eye(m,m); %you may would like to play with the initialization
        invK = inv(K);
    obs_past = x(:,1:L); %the first L observations
    obs_past = obs_past(:);
    obs_pasts = zeros(m,num_of_samples-L);
    disp(strcat('AR(',num2str(L),')-fit, method: NIW; started.'));
    
%fit:
    for t = L+1 : num_of_samples
        %new observation:            
            obs_new = x(:,t);
            obs_pasts(:,t-L) = obs_past;%for the computation of x_innovation_hat
        %Bayesian update (invK,M,K):   
            invK = invK - (invK*obs_past*obs_past.'*invK) / (1+obs_past.'*invK*obs_past); %recursive, update with the reciprocial of a !number! (<=matrix inversion lemma)                    
            M = (M*K+obs_new*obs_past.') * invK; %We don't use M to update K => replace the actual value of M.
            K = obs_past*obs_past' + K;
        %obs_past update:            
            obs_past = [obs_past(D+1:end);obs_new]; 
        %disp(t):
            if mod(t,1000)==1, disp(strcat('t=',num2str(t),'(/',num2str(num_of_samples),')')); end
    end
    
%residual (x_innovation_hat), dynamics(Fx_hat):    
    x_innovation_hat = x(:,L+1:end) - M * obs_pasts;     
    Fx_hat = M;
    
disp(strcat('AR(',num2str(L),')-fit, method: NIW; ready.'));    
    
