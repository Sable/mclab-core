%function [] = quick_test_DJensen_Renyi()
%Quick test for Jensen-Renyi divergence estimators: analytical expression vs estimated value as a function of the sample number. In the test, normal variables are considered. See also 'analytical_value_DJensen_Renyi.m'.

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
    d = 2; %dimension of the distribution
    num_of_samples_v = [100:500:12*1000]; %sample numbers used for estimation
    w_D = [1/3,2/3]; %weight used in the Jensen-Renyi divergence
    %estimator, meta:    
        cost_name = 'JensenRenyi_HRenyi'; %d>=1
    
%initialization:
    L = length(num_of_samples_v);
    co = D_initialization(cost_name,1,{'alpha',2,'w',w_D}); %{'alpha',2}:set the 'alpha' field to 2, and w
    num_of_samples_max = num_of_samples_v(end);
    D_hat_v = zeros(L,1); %vector of the estimated divergence values

%distr, d -> samples (Y1,Y2), analytical value (D):
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
        D = analytical_value_DJensen_Renyi(distr,distr,w_D,par1,par2);
        
%estimation:
    Tk = 0;%index of the sample number examined
    for num_of_samples = num_of_samples_v
        Tk = Tk + 1;
        D_hat_v(Tk) = D_estimation(Y1(:,1:num_of_samples),Y2(:,1:num_of_samples),co);
        disp(strcat('Tk=',num2str(Tk),'/',num2str(L)));
    end
    
%plot:
    plot(num_of_samples_v,D_hat_v,'r',num_of_samples_v,ones(L,1)*D,'g');
    legend({'estimation','analytical value'});
    xlabel('Number of samples');
    ylabel('Jensen-Renyi divergence');
