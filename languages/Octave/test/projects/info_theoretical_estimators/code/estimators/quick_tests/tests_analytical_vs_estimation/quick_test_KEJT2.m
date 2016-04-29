%function [] = quick_test_KEJT2()
%Quick test for exponentiated Jensen-Tsallis kernel-2 estimators: analytical expression vs estimated value as a function of the sample number. In the test, normal variables are considered. See also 'analytical_value_KEJT2.m'.

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
    distr = 'normal'; %fixed
    d = 1; %dimension of the distribution
    num_of_samples_v = [100:500:12*1000]; %sample numbers used for estimation
    u_K = 0.8; %>0, parameter of the Jensen-Tsallis kernel
    %estimator, meta:    
    	cost_name = 'EJT2_DJT'; %d>=1
        
%initialization:
    L = length(num_of_samples_v);
    co = K_initialization(cost_name,1,{'alpha',2,'u',u_K}); %{'alpha',2,'u',u_K}:set the 'alpha' and 'u' fields. Note: there exist explicit formula in case of alpha = 2 for Jensen-Tsallis divergence => for the Jensen-Renyi kernel too.
    num_of_samples_max = num_of_samples_v(end);
    K_hat_v = zeros(L,1); %vector of the estimated kernel values
    
%distr, d -> samples (Y1,Y2), analytical value (K):
    switch distr    
        case 'normal'
            %generate samples (Y1,Y2):
                %Y1~N(m1,s1^2xI), Y2~N(m2,s2^2xI):
                   m1 = randn(d,1);  s1 = rand;
                   m2 = randn(d,1);  s2 = rand;
                Y1 = randn(d,num_of_samples_max) * s1 + repmat(m1,1,num_of_samples_max);
                Y2 = randn(d,num_of_samples_max) * s2 + repmat(m2,1,num_of_samples_max);
            par1.mean = m1; par1.std = s1;
            par2.mean = m2; par2.std = s2;
                
        otherwise
            error('Distribution=?');                 
    end
    %analytical value:
        K = analytical_value_KEJT2(distr,distr,u_K,par1,par2);
    
%estimation:
    Tk = 0;%index of the sample number examined
    for num_of_samples = num_of_samples_v
        Tk = Tk + 1;
        K_hat_v(Tk) = K_estimation(Y1(:,1:num_of_samples),Y2(:,1:num_of_samples),co);
        disp(strcat('Tk=',num2str(Tk),'/',num2str(L)));
    end
    
%plot:
    plot(num_of_samples_v,K_hat_v,'r',num_of_samples_v,ones(L,1)*K,'g');
    legend({'estimation','analytical value'});
    xlabel('Number of samples');
    ylabel('Jensen-Tsallis kernel-2');
    