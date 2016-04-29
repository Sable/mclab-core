%function [] = quick_test_KEJR1()
%Quick test for Jensen-Renyi kernel-1 estimators: analytical expression vs estimated value as a function of the sample number. In the test, normal variables are considered. See also 'analytical_value_KEJR1.m'.

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
    alpha_K = 2; %parameter of the Jensen-Renyi kernel; fixed; for alpha_K =2 we have explicit formula for the Renyi-entropy, and hence to the Jensen-Renyi kernel.
    u_K = 0.8;%>0, parameter of the Jensen-Renyi kernel
    num_of_samples_v = [1000:2000:50*1000]; %sample numbers used for estimation
    %estimator, meta:
        cost_name = 'EJR1_HR'; %d>=1
    
%initialization:
    num_of_samples_max = num_of_samples_v(end);
    L = length(num_of_samples_v);
    %initialize cost object, set the alpha and the  parameter:
        co = K_initialization(cost_name,1,{'alpha',alpha_K,'u',u_K}); %{'alpha',alpha_K,'u',u_K}: set the 'alpha' and 'u' fields
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
        K = analytical_value_KEJR1(distr,distr,u_K,par1,par2);    
    
%estimation:
    Tk = 0;%index of the sample number examined   
    for num_of_samples = num_of_samples_v
        Tk = Tk + 1;
        K_hat_v(Tk) = K_estimation(Y1(:,1:num_of_samples),Y2(:,1:num_of_samples),co);
        disp(strcat('Tk=',num2str(Tk),'/',num2str(L)));
    end
    
%plot:
    plot(num_of_samples_v,K_hat_v,'r',num_of_samples_v,K*ones(L,1),'g');
    legend({'estimation','analytical value'});
    xlabel('Number of samples');
    ylabel('Jensen-Renyi kernel-1'); 
