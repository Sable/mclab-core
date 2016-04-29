function [x_innovation_hat,Fx_hat,SBCs] = estimate_AR(x,AR)
%function [x_innovation_hat,Fx_hat,SBCs] = estimate_AR(x,AR)
%AR estimation on observation x.
%
%INPUT:
%   x: x(:,t) is the observation at time t.
%   AR: AR estimator,
%      AR.L: AR order; can be vector, too; in that case the 'best' AR order is chosen according to SBC, see 'estimate_AR.m'.
%      AR.method: AR estimation method. Possibilities: 'NIW' (see 'estimate_AR_NIW.m'), 'subspace', 'subspace-LL', 'LL' (see 'estimate_AR_E4.m')  and 'stepwiseLS' (see 'arfit.m').  
%NOTE: the 'stepwiseLs' method corresponds to the stepwise least squares estimator of the ARfit Matlab package ("http://www.gps.caltech.edu/~tapio/arfit/"). The ARfit
%   toolbox has NOT been included in the ITE package because its license is NOT compatible with 'GPLv3 or later'. However, we recommend downloading and 
%   installing it, and use it as the DEFAULT AR estimator; it is quite efficient, and ITE is ready to call it.   
%OUTPUT:
%   x_innovation_hat: estimated innovation of x, at time t it is x_innovation_hat(:,t).
%   Fx_hat: estimated dynamics of x.
%   SBCs: computed SBC values if AR.L is a vector, else SBCs = [].

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
    method = AR.method;
    Ls = AR.L;
    [D,num_of_samples] = size(x);
    disp(strcat(['ARfit: started (method: ',method,').']));
    switch method
        case {'NIW', 'subspace', 'subspace-LL', 'LL'}
            if strcmp(method,'subspace') || strcmp(method,'subspace-LL') || strcmp(method,'LL')
                %initialize E4, if it is needed:
                    global E4OPTION;
                    if isempty(E4OPTION)%test whether the E4 package has been initialized
                        e4init; %E4-initialization
                    end
            end
            num_of_Ls = length(Ls);
            %SBC:
                if num_of_Ls>1 %compute and store SBC values
                    Lmax = max(Ls);
                    SBCs = zeros(num_of_Ls,1);
                    SBC_min = Inf;
                else 
                    SBCs = [];
                end
        for kL = 1 : num_of_Ls
            L = Ls(kL);
            %AR(L) fit to x (x_innovation_hat,Fx_hat):
                switch method
                    case 'NIW'
                        [x_innovation_hat,Fx_hat] = estimate_AR_NIW(x,L);
                    case {'subspace','subspace-LL','LL'}
                        [x_innovation_hat,Fx_hat] = estimate_AR_E4(x,L,method);
                end
            if num_of_Ls>1%compute SBC, and store the estimation if it is the best seen so far
                SBC_L = compute_SBC(L,Lmax,num_of_samples,x_innovation_hat);
                SBCs(kL) = SBC_L;
                if SBC_L < SBC_min
                    x_innovation_hat_opt = x_innovation_hat;
                    Fx_hat_opt = Fx_hat;
                    SBC_min = SBC_L;
                end
            end
        end

        %return the best estimation (in SBC sense) found:
            if num_of_Ls>1
                x_innovation_hat = x_innovation_hat_opt;
                Fx_hat = Fx_hat_opt;
            end
        case 'stepwiseLS'%ARfit Matlab package
            %Fx_hat:
                [w_temp, Fx_hat, C, SBCs] = arfit(x.', min(Ls), max(Ls),'sbc','zero');
            %x_innovation_hat:
                [siglev,x_innovation_hat] = arres(w_temp, Fx_hat, x.');
                x_innovation_hat = x_innovation_hat.';
        otherwise
            error('AR identification method=?');
    end
disp('ARfit: ready.');    
