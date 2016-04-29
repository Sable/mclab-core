%function [] = quick_test_IShannon()
%Quick test for Shannon mutual information estimators: analytical expression vs estimated value as a function of the sample number. In the test, normal variables are considered.

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
    %clear all; close all;
    
%parameters:
    distr = 'normal'; %fixed
    %ds = [2;2]; %subspace dimensions. ds(m) = dimension of the m^th subspace, m=1,...,M (M=length(ds)); M>=2
    ds = [1;1]; %subspace dimensions. ds(m) = dimension of the m^th subspace, m=1,...,M (M=length(ds)); M>=2
    num_of_samples_v = [1000:1000:20*1000]; %sample numbers used for estimation
    %num_of_samples_v = [100:100:2*1000]; %sample numbers used for estimation (for smaller estimators)
    %estimator:
        %base:
            %cost_name = 'Shannon_vME';
        %meta:
            cost_name = 'Shannon_HShannon'; %d_m>=1, M>=2
            %cost_name = 'Shannon_DKL';     %d_m>=1, M>=2
            %cost_name = 'Shannon_AP2';     %d_m =1, M =2
            %cost_name = 'Shannon_AP';      %d_m =1, M>=2
    
%initialization:
    num_of_samples_max = num_of_samples_v(end);
    L = length(num_of_samples_v);
    co = I_initialization(cost_name,1);
    I_hat_v = zeros(L,1); %vector of the estimated mutual information values
    
%distr, d -> samples (Y), analytical value (I):
    switch distr
        case 'normal'
            d = sum(ds); %dimension of the joint distribution
            %mean:
                m = rand(d,1);
                %m = zeros(d,1);
            %(random) linear transformation applied to the data:                
                A = rand(d); 
                %A = eye(d);  %do not transform the data
            %covariance matrix:
                C = A * A.';
            %generate samples:
                Y = A * randn(d,num_of_samples_max) + repmat(m,1,num_of_samples_max); %AxN(0,I)+m
            par.ds = ds;
            par.cov = C;
        otherwise
            error('Distribution=?');            
    end
    %analytical value:
        I = analytical_value_IShannon(distr,par);
                
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
    ylabel('Shannon mutual information');
