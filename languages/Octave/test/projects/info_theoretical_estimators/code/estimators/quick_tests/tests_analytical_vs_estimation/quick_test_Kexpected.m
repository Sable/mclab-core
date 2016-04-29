%function [] = quick_test_Kexpected()
%Quick test for expected kernel estimators: analytical expression vs estimated value as a function of the sample number. In the test, normal variables are considered. See also 'analytical_value_Kexpected.m'.

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
    sigma_K = 1;
    num_of_samples_v = [1000:1000:15*1000]; %sample numbers used for estimation
    %estimator, base:
        cost_name = 'expected'; %d>=1

%initialization:    
    L = length(num_of_samples_v);    
    num_of_samples_max = num_of_samples_v(end);   
    co = K_initialization(cost_name,1,{'sigma',sigma_K}); %{'sigma',sigma_K}: set the sigma field
    K_hat_v = zeros(L,1); %vector of the estimated kernel values
    
%distr, d -> samples (Y1,Y2), analytical value (K):
    switch distr
        case 'normal'   
            %mean:
                m1 = rand(d,1);
                m2 = rand(d,1);
            %(random) linear transformation applied to the data:
                A1 = rand(d);
                A2 = rand(d);
            %covariance matrix:
                C1 = A1 * A1.';
                C2 = A2 * A2.';
            %generate samples:
                Y1 = A1 * randn(d,num_of_samples_max) + repmat(m1,1,num_of_samples_max); %A1xN(0,I)+m1
                Y2 = A2 * randn(d,num_of_samples_max) + repmat(m2,1,num_of_samples_max); %A2xN(0,I)+m2
            par1.mean = m1; par1.cov = C1;
            par2.mean = m2; par2.cov = C2;
        otherwise
            error('Distribution=?');
    end 
    %analytical value:
        K = analytical_value_Kexpected(distr,distr,sigma_K,par1,par2);
    
%estimation:
    Tk = 0;%index of the sample number examined
    for num_of_samples = num_of_samples_v
        Tk = Tk + 1;   
        K_hat_v(Tk) = K_estimation(Y1(:,1:num_of_samples),Y2(:,1:num_of_samples),co);
        disp(strcat('Tk=',num2str(Tk),'/',num2str(L)));
    end
    
%plot:
    plot(num_of_samples_v,K_hat_v,'r',num_of_samples_v,K*ones(1,L),'g');
    legend({'estimation','analytical value'});
    xlabel('Number of samples');
    ylabel('Expected kernel');        
