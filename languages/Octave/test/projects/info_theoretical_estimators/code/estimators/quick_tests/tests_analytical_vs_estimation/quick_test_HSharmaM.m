%function [] = quick_test_HSharmaM()
%Quick test for Sharma-Mittal entropy estimators: analytical expression vs estimated value as a function of the sample number. In the test, normal variables are considered. See also 'analytical_value_HSharmaM.m'.

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
    alpha_H = 0.8; %parameter of the Sharma-Mittal entropy; alpha: >0, not 1.
    beta_H = 0.6; %parameter of the Sharma-Mittal entropy; beta: not 1.
    
    num_of_samples_v = [1000:1000:30*1000]; %sample numbers used for estimation
    %estimator, base:
        cost_name = 'SharmaM_kNN_k';   %d>=1
        %cost_name = 'SharmaM_expF';   %d>=1
    
%initialization:
    num_of_samples_max = num_of_samples_v(end);
    L = length(num_of_samples_v);
    %initialize cost object, set the alpha parameter:
        co = H_initialization(cost_name,1,{'alpha',alpha_H,'beta',beta_H}); %{'alpha',alpha_H,'beta',beta_H}: set the 'alpha' and 'beta' fields
    H_hat_v = zeros(L,1); %vector of estimated entropies

%distr, d -> samples (Y), analytical value (H):
    switch distr
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
        H = analytical_value_HSharmaM(distr,alpha_H,beta_H,par);
                
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
    ylabel('Sharma-Mittal entropy'); 
