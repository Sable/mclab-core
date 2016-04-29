%function [] = quick_test_Iindependence()
%Quick test for 'I(y^1,...,y^M)=0 if y^m-s are independent'. In the test, uniform variables are considered.

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

%clear start:
    clear all; close all;

%parameters:
    distr = 'uniform'; %fixed
    %ds:
        M = 2; %>=2, number of subspaces; y^1,..., y^M
        dm = 1; %dimension of the components, dm=dim(y^1)=...=dim(y^M)
        ds = dm * ones(M,1);
    num_of_samples_v = [1000:500:5*1000]; %sample numbers used for estimation
    %estimator (of mutual information):
        %base:
            cost_name = 'GV';     %dm =1,M =2
            %cost_name = 'HSIC';  %dm>=1,M>=2
            %cost_name = 'KCCA';  %dm>=1,M>=2
            %cost_name = 'KGV';   %dm>=1,M>=2
            %cost_name = 'Hoeffding'; %dm=1,M>=2
            %cost_name = 'SW1';       %dm=1,M =2
            %cost_name = 'SWinf';     %dm=1,M =2
            %cost_name = 'QMI_CS_KDE_direct'; %dm =1, M =2; computationally intensive
            %cost_name = 'QMI_CS_KDE_iChol';  %dm>=1, M =2
            %cost_name = 'QMI_ED_KDE_iChol';  %dm>=1, M =2
            %cost_name = 'dCov';              %dm>=1, M =2
            %cost_name = 'dCor';              %dm>=1, M =2
            %cost_name = '3way_Lancaster';    %dm>=1, M =3
            %cost_name = '3way_joint';        %dm>=1, M =3
            %cost_name = 'Shannon_AP2';        %dm =1, M =2
            %cost_name = 'Shannon_AP';         %dm =1, M>=2
            
        %meta:
            %cost_name = 'complex';      %dm>=1,M>=2
            %cost_name = 'L2_DL2';       %dm>=1,M>=2;
            %cost_name = 'Renyi_DRenyi'; %dm>=1,M>=2
            %cost_name = 'MMD_DMMD';     %dm =1,M>=2        
            %cost_name = 'Renyi_HRenyi'; %dm =1,M>=2
            %cost_name = 'Shannon_HShannon'; %dm>=1,M>=2
            %cost_name = 'Tsallis_DTsallis'; %dm>=1,M>=2
            %cost_name = 'dCov_IHSIC';       %dm>=1,M =2
            %cost_name = 'ApprCorrEntr';     %dm =1,M =2
            %cost_name = 'ChiSquare_DChiSquare'; %dm>=1,M>=2
            %cost_name = 'Shannon_DKL';          %dm>=1, M=2

%initialization:
    num_of_samples_max = num_of_samples_v(end);
    L = length(num_of_samples_v);
    co = I_initialization(cost_name,1);
    I_hat_v = zeros(L,1);%vector of the estimated mutual information values

%distr, ds -> samples (Y), analytical value (I):
    %Y:
        switch distr 
            case 'uniform'
                if strcmp(cost_name,'complex') %measures over complex variables
                    Y = rand(sum(ds),num_of_samples_max) + i * rand(sum(ds),num_of_samples_max);
                    %introduce dependence within the subspaces:
                        cum_ds = cumsum([1;ds(1:end-1)]);%1,d_1+1,d_1+d_2+1,...,d_1+...+d_{M-1}+1 = starting indices of the subspaces (M=number of subspaces).
                        for m = 1 : M
                            Am = random_unitary(ds(m));
                            idxm = [cum_ds(m):cum_ds(m)+ds(m)-1];
                            Y(idxm,:) = Am * Y(idxm,:);
                        end
                else
                    Y = rand(sum(ds),num_of_samples_max);
                    cum_ds = cumsum([1;ds(1:end-1)]);%1,d_1+1,d_1+d_2+1,...,d_1+...+d_{M-1}+1 = starting indices of the subspaces (M=number of subspaces).                
                    %introduce dependence within the subspaces:
                        for m = 1 : M
                            Am = random_orthogonal(ds(m));
                            idxm = [cum_ds(m):cum_ds(m)+ds(m)-1];
                            Y(idxm,:) = Am * Y(idxm,:);
                        end
                end
            otherwise
                error('Distribution=?');
        end 
    I = 0; %upon independence the joint mutual information is zero
    
%estimation:
    Tk = 0;%index of the sample number examined
    for num_of_samples = num_of_samples_v
        Tk = Tk + 1;
        I_hat_v(Tk) = I_estimation(Y(:,1:num_of_samples),ds,co);
        disp(strcat('Tk=',num2str(Tk),'/',num2str(L)));
    end

%plot:    
    plot(num_of_samples_v,I_hat_v,'r',num_of_samples_v,I*ones(L,1),'g');
    legend({'estimation','analytical value'});
    xlabel('Number of samples');
    ylabel('Mutual information');    
 
