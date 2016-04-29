%function [] = quick_test_CCE()
%Quick test for cross-entropy estimators: analytical expression vs estimated value as a function of the sample number. In the test, normal variables are considered. See also 'analytical_value_CCE.m'.

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
    %estimator, base:
        cost_name = 'CE_kNN_k'; %d>=1
        %cost_name = 'CE_expF'; %d>=1
    
%initialization:
    num_of_samples_max = num_of_samples_v(end);
    L = length(num_of_samples_v);
    co = C_initialization(cost_name,1);    
    C_hat_v = zeros(L,1); %vector of estimated cross-entropies

%distr, d -> samples (Y1,Y2), analytical value (C):
    switch distr
        case 'normal'
            %mean:
                m2 = rand(d,1);
                m1 = m2;            
            %(random) linear transformation applied to the data:
                A2 = rand(d);
                A1 = rand * A2; %(e2,A2) => (e1,A1) choice guarantees Y1<<Y2 (in practise, too)                
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
        C = analytical_value_CCE(distr,distr,par1,par2);
    
%estimation:
    Tk = 0;%index of the sample number examined
    for num_of_samples = num_of_samples_v
        Tk = Tk + 1;
        C_hat_v(Tk) = C_estimation(Y1(:,1:num_of_samples),Y2(:,1:num_of_samples),co);
        disp(strcat('Tk=',num2str(Tk),'/',num2str(L)));
    end
    
%plot:
    plot(num_of_samples_v,C_hat_v,'r',num_of_samples_v,C*ones(L,1),'g');
    legend({'estimation','analytical value'});
    xlabel('Number of samples');
    ylabel('Cross-entropy');
