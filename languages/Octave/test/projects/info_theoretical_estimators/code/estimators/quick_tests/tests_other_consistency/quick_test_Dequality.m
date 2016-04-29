%function [] = quick_test_Dequality()
%Quick test for 'D(p,q)=0 if p=q'. In the test, normal/uniform variables are considered.

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
    distr = 'uniform'; %possibilities: 'normal', 'uniform', 'normalI'
    d = 1; %dimension of the distribution
    num_of_samples_v = [1000:1000:30*1000]; %sample numbers used for estimation
    %method used to measure divergence: 
         %base:
             %cost_name = 'L2_kNN_k';    %d>=1
             cost_name = 'Tsallis_kNN_k';%d>=1
             %cost_name = 'Renyi_kNN_k'; %d>=1
             %cost_name = 'MMD_Ustat';   %d>=1
             %cost_name = 'MMD_Vstat';   %d>=1
             %cost_name = 'MMD_online';  %d>=1
             %cost_name = 'Hellinger_kNN_k';    %d>=1
             %cost_name = 'Bhattacharyya_kNN_k';%d>=1 
             %cost_name = 'KL_kNN_k';        %d>=1
             %cost_name = 'KL_kNN_kiTi';     %d>=1
             %cost_name = 'CS_KDE_iChol';    %d>=1
             %cost_name = 'ED_KDE_iChol';    %d>=1
             %cost_name = 'EnergyDist';      %d>=1
             %cost_name = 'Bregman_kNN_k';   %d>=1
             %cost_name = 'symBregman_kNN_k';%d>=1
             %cost_name = 'ChiSquare_kNN_k'; %d>=1
             %cost_name = 'MMD_Ustat_iChol'; %d>=1
             %cost_name = 'MMD_Vstat_iChol'; %d>=1
             %cost_name = 'KL_PSD_SzegoT';   % d=1
             %cost_name = 'KL_expF';         %d>=1
             %cost_name = 'ChiSquare_expF';  %d>=1; distr = 'normalI'

        %meta:
             %cost_name = 'Jdistance';       %d>=1 
             %cost_name = 'KL_CCE_HShannon'; %d>=1
             %cost_name = 'EnergyDist_DMMD'; %d>=1
             %cost_name = 'JensenShannon_HShannon'; %d>=1
             %cost_name = 'JensenRenyi_HRenyi';     %d>=1
             %cost_name = 'K_DKL'; %d>=1
             %cost_name = 'L_DKL'; %d>=1
             %cost_name = 'JensenTsallis_HTsallis';%d>=1
             %cost_name = 'symBregman_DBregman';   %d>=1
             %cost_name = 'BMMD_DMMD_Ustat';       %d>=1
             %cost_name = 'f_DChiSquare';          %d>=1

%initialization:
    num_of_samples_max = num_of_samples_v(end);
    L = length(num_of_samples_v);
    co = D_initialization(cost_name,1); %mult=1
    %explicitly set the kNN method and its parameters:
        %co = D_initialization(cost_name,1,{'kNNmethod','ANN','k',3,'epsi',0}); %mult=1
    D_hat_v = zeros(L,1); %vector of the estimated divergence values

%generate samples (Y1,Y2), analytical value (D):
    %Y1,Y2:
        switch distr
            case 'uniform'
                A = randn(d);%(random) linear transformation
                Y1 = A*rand(d,num_of_samples_max);
                Y2 = A*rand(d,num_of_samples_max);
            case 'normal'
                m = rand(d,1); A = rand(d);
                Y1 = A*randn(d,num_of_samples_max) + repmat(m,1,num_of_samples_max);%AxN(0,I)+m
                Y2 = A*randn(d,num_of_samples_max) + repmat(m,1,num_of_samples_max);%AxN(0,I)+m
            case 'normalI'
                m = 2*rand(d,1);
                Y1 = randn(d,num_of_samples_max) + repmat(m,1,num_of_samples_max); %N(m,I)
                Y2 = randn(d,num_of_samples_max) + repmat(m,1,num_of_samples_max); %N(m,I)
            otherwise
                error('Distribution=?');        
        end
    D = 0;
    
%estimation:
    Tk = 0;%index of the sample number examined
    for num_of_samples = num_of_samples_v
        Tk = Tk + 1;
        D_hat_v(Tk) = D_estimation(Y1(:,1:num_of_samples),Y2(:,1:num_of_samples),co); %ideally: it is zero
        disp(strcat('Tk=',num2str(Tk),'/',num2str(L)));
    end
  
%plot:
    plot(num_of_samples_v,D_hat_v,'r',num_of_samples_v,D*ones(L,1),'g');
    legend({'estimation','analytical value'});
    xlabel('Number of samples');
    ylabel('Divergence');  
