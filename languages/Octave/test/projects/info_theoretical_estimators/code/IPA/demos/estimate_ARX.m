function [x_innov_hat,Fx_hat,Bx_hat,obs] = estimate_ARX(Fx,Bx,Ae,Dx,Du,u_size,ARX)
%function [x_innov_hat,Fx_hat,Bx_hat,obs] = estimate_ARX(Fx,Bx,Ae,Dx,Du,u_size,ARX)
%ARX estimation on observation. The observation is generated online.
%
%INPUT:
%   Fx: matrix describing the AR evolution of observation x.
%   Bx: matrix describing the effect of the control on observation x.
%   Ae: driving noise of observation x, =A*e.
%   Dx: dimension of the observation.
%   Du: dimension of the control.
%   u_size: size of the control; box constraint, i.e., |u_i| <= u_size
%   ARX: ARX estimator,
%       ARX.method: ARX identification method. Possibilities: 'NIW' (online Bayesian technique with normal-inverted Wishart prior).
%       ARX.Lx: AR order.
%       ARX.Lu: control ('X') order.
%OUTPUT:
%   x_innov_hat: estimated innovation process of x.
%   Fx_hat: estimated matrix describing the AR evolution of observation x.
%   Bx_hat: estimated matrix describing the effect of the control on observation x.
%   obs: observations, obs(:,t): observation at time t.
%
%REFERENCE:
%   Barnabas Poczos, Andras Lorincz. Identification of Recurrent Neural Networks by Bayesian Interrogation Techniques. Journal of Machine Learning Research 10 (2009) 515-554. [NIW method]
%NOTE: 
%   Fx,Bx, and Ae are used for observation generation only.

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

%ARX -> method, Lx, Lu:
    method = ARX.method;
    Lx = ARX.Lx;
    Lu = ARX.Lu;
    
    disp(strcat(['ARX identification: started. Method: ', method,'.']));
    switch method
        case 'NIW'
            %initialization:
                num_of_samples = size(Ae,2);
                m =  Dx * Lx + Du * Lu;
                FBx = [Fx,Bx];
                %M,K,invK:
                    M = 100*(2*rand(Dx,m)-1); %you may would like to play with the initialization
                    K = 0.0001*eye(m,m); %you may would like to play with the initialization
                    invK = inv(K);
                %type of the control:        
                    control_code_t = [1*ones(1,floor(num_of_samples/2)), 2*ones(1,floor(num_of_samples/2))];
                    control_type_list = {'parameter-optimal','noise-optimal'};
                    control_type_t = control_type_list(control_code_t);        
                %first observations, controls (obs_pre,u_pre):
                    obs_pre = randn(Dx*Lx,1) ; %the first Lx observations
                    u_pre = zeros(Du*(Lu-1),1); %the first Lu-1 controls, the Lu^th is optimized
                %observations (obs) and their predictors (obs_predictors) -- used at the end for innovation estimation:
                    obs = zeros(Dx,num_of_samples);
                    obs_predictors = zeros(m,num_of_samples); 
                for t = 1 : num_of_samples
                    %u=u(control_code,...):
                        obs_u_pre = [obs_pre; u_pre];
                        control_type = control_type_t{t};
                        u = control(control_type,invK,m,Du,obs_u_pre,u_size);
                    %new observation:            
                        x = [obs_u_pre;u];
                        obs_new = FBx * x + Ae(:,t);
                        obs(:,t) = obs_new;
                        obs_predictors(:,t) = x;
                    %Bayesian update (invK,M,K):   
                        invK = invK - (invK*x*x.'*invK)/(1+x.'*invK*x); %recursive, update by the inverse of a _number_ (<= matrix inversion lemma)                    
                        M = (M*K+obs_new*x.')*invK; %We don't use M to update K => replace the actual value of M.
                        K = x*x.'+K;
                    %obs_pre,u_pre update:            
                        obs_pre = [obs_pre(Dx+1:end);obs_new]; 
                        if Lu>1, u_pre = [u_pre(Du+1:end);u]; end
                    %x_innov_hat (online estimation):
                        %x_innov_hat(:,t) = obs_new - M * x;
                    %disp(t):
                        if mod(t,100)==1, disp(strcat('t=',num2str(t),'(/',num2str(num_of_samples),')')); end
                end
                x_innov_hat = obs - M * obs_predictors;
                Fx_hat = M(:,1:Dx*Lx);
                Bx_hat = M(:,Dx*Lx+1:end);
        otherwise
            error('ARX method=?');
    end
    disp('ARX identification: ready.');
