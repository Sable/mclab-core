function [x_innovation_hat,Fx_hat] = estimate_AR_E4(x,L,method)
%function [x_innovation_hat,Fx_hat] = estimate_AR_E4(x,L,method)
%AR estimation on observation x via the 'subspace', 'subspace-LL', or 'LL' method.
%
%INPUT:
%   x: x(:,t) is the observation at time t.
%   L: AR order.
%   method: 'subspace', 'subspace-LL', 'LL'. Here, 
%      'subspace': nonlinear least squares estimator based on the subspace representation of the system.
%      'LL': exact maximum likelihood optimization using the BFGS (or the Newton-Raphson) technique.
%      'subspace-LL': 'LL' initialized with 'subspace'.
%OUTPUT:
%   x_innovation_hat: estimated innovation of x, at time t it is x_innovation_hat(:,t).
%   Fx_hat: estimated dynamics of x.
%
%REFERENCE:
%   Miguel Jerez, Jose Casals, Sonia Sotoca. Signal Extraction for Linear State-Space Models. Lambert Academic Publishing GmbH & Co. KG, Saarbrucken (Germany), 2011.

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
    D = size(x,1);
    cov_e = eye(D);
    AR1toL = zeros(D,D*L);
    [theta0, din0, lab0] = arma2thd(AR1toL,[],[],[],cov_e,1);
    disp(strcat(['AR(',num2str(L),')-fit, method: ',method,'; started.']));
    
%fit:    
    switch method
        case 'subspace'
            maxiter_subspace = 1000;
            sete4opt('maxiter',maxiter_subspace); %set the optimization parameters: the iteration number
            [theta_opt] = e4preest(theta0,din0,x.'); 
        case 'subspace-LL'
            %subspace:
                maxiter_subspace = 1000;
                sete4opt('maxiter',maxiter_subspace); %set the optimization parameters: the iteration number
                [theta_temp] = e4preest(theta0,din0,x.'); 
            %LL:
                maxiter_LL = 100;
                sete4opt('maxiter',maxiter_LL); %set the optimization parameters: the iteration number
                [theta_opt] = e4min('lffast',theta_temp,'',din0,x.');
        case 'LL'
            maxiter_LL = 100;
            sete4opt('maxiter',maxiter_LL);%set the optimization parameters: the iteration number
            [theta_opt] = e4min('lffast',theta0,'',din0,x.');
    end
    
%residual (x_innovation_hat), dynamics(Fx_hat):
    %smoothed residual:
        [temp,x_innovation_hat] = residual(theta_opt, din0, x.');%[residual, smoothed_residual]=...
        x_innovation_hat = x_innovation_hat.';
        Fx_hat_temp = thd2arma(theta_opt,din0);%[I,F_1,...,F_L]
    %[I,F_1,...,F_L]->[F_L,...,F_1]:
        Fx_hat_temp = -Fx_hat_temp(:,D+1:end); %'-':other side
        Fx_hat = zeros(size(Fx_hat_temp));
        for k = 1 : L
            k2 = L+1-k;
            Fx_hat(:,(k-1)*D+1:k*D) = Fx_hat_temp(:,(k2-1)*D+1:k2*D);
        end
        
disp(strcat(['AR(',num2str(L),')-fit, method: ',method,'; ready.']));
