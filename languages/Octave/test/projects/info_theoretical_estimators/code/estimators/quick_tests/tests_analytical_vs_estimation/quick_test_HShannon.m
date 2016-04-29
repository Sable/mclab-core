%function [] = quick_test_HShannon()
%Quick test for Shannon entropy estimators (over the real field): analytical expression vs estimated value as a function of the sample number. In the test, normal/uniform variables are considered. See also 'analytical_value_HShannon.m'.

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
    distr = 'normal'; %possibilities: 'uniform', 'normal' 
    d = 1; %dimension of the distribution
    num_of_samples_v = [1000:1000:50*1000]; %sample numbers used for estimation
    %num_of_samples_v = [100:100:2*1000]; %sample numbers used for estimation (for slower estimators)
    %estimator:
        %base:
            cost_name = 'Shannon_kNN_k';      %d>=1     
            %cost_name = 'Shannon_Edgeworth';  %d>=1; expansion around the normal distribution...
            %cost_name = 'Shannon_Voronoi';    %d>=2; assumption: uniform distribution in each Voronoi region; computationally intensive
            %cost_name = 'Shannon_spacing_V';  %d=1
            %cost_name = 'Shannon_spacing_Vb'; %d=1
            %cost_name = 'Shannon_spacing_Vpconst';%d=1
            %cost_name = 'Shannon_spacing_Vplin';  %d=1
            %cost_name = 'Shannon_spacing_Vplin2'; %d=1
            %cost_name = 'Shannon_spacing_LL';     %d=1
            %cost_name = 'Shannon_KDP';            %d>=1
            %cost_name = 'Shannon_MaxEnt1';    %d=1; approximation around the normal distribution...
            %cost_name = 'Shannon_MaxEnt2';    %d=1; approximation around the normal distribution...
            %cost_name = 'Shannon_PSD_SzegoT'; %d=1
            %cost_name = 'Shannon_expF';       %d>=1; distr = 'normal'
            %cost_name = 'Shannon_spacing_VKDE'; %d=1
            %cost_name = 'Shannon_vME';            %d>=1
        %meta:
            %cost_name = 'Shannon_DKL_N';  %d>=1
            %cost_name = 'Shannon_DKL_U';  %d>=1          
            %cost_name = 'ensemble';       %d>=1
            %cost_name = 'RPensemble';     %d>=1
    
%initialization:    
    num_of_samples_max = num_of_samples_v(end);
    L = length(num_of_samples_v);
    co = H_initialization(cost_name,1);
    H_hat_v = zeros(L,1); %vector of estimated entropies
    
%distr, d -> samples (Y), analytical value (H):
    switch distr 
        case 'uniform'
            %U[a,b]:
                a = -rand(d,1); b = rand(d,1);%guaranteed that a<=b (coordinate-wise)
                %a = zeros(d,1); b = ones(d,1); %U[0,1]
            %(random) linear transformation applied to the data:
                A = rand(d);
                %A = eye(d);%do not transform the data
            %generate samples:
                Y =  A * (rand(d,num_of_samples_max) .* repmat(b-a,1,num_of_samples_max) + repmat(a,1,num_of_samples_max));
            par.a = a; par.b = b; par.A = A;
        case 'normal'
            %mean:
                m = rand(d,1);
            %random linear transformation applied to N(0,I):
                A = rand(d); 
                %A = eye(d); %do not transform the data
            %covariance matrix:
                C = A * A.';
            %generate samples:
                Y = A * randn(d,num_of_samples_max) + repmat(m,1,num_of_samples_max); %AxN(0,I)+m
            par.cov = C;
        otherwise
            error('Distribution=?');
    end  
    %analytical value:
        H = analytical_value_HShannon(distr,par);
            
%estimation:
    Tk = 0;%index of the sample number examined
    for num_of_samples = num_of_samples_v
        Tk = Tk + 1;
        H_hat_v(Tk) = H_estimation(Y(:,1:num_of_samples),co);
        disp(strcat('Tk=',num2str(Tk),'/',num2str(L)));
    end
    
%plot:
    plot(num_of_samples_v,H_hat_v,'r',num_of_samples_v,H*ones(L,1),'g');
    legend({'estimation','analytical value'});
    xlabel('Number of samples');
    ylabel('Shannon entropy');
