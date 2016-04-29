%function [] = quick_test_DBregman()
%Quick test for Bregman divergence estimators: analytical expression vs estimated value as a function of the sample number. In the test, uniform variables are considered. See also 'analytical_value_DBregman.m'.

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
    d = 1; %dimension of the distribution
    num_of_samples_v = [1000:1000:50*1000]; %sample numbers used for estimation
    alpha_D = 0.7; %parameter of the Bregman divergence
    %estimator, base:
        cost_name = 'Bregman_kNN_k'; %d>=1
        
%initialization:    
    L = length(num_of_samples_v);    
    co = D_initialization(cost_name,1,{'alpha',alpha_D});
    D_hat_v = zeros(L,1);  %vector of the estimated divergence values
    num_of_samples_max = num_of_samples_v(end);  
    
%generate samples (Y1<<Y2), analytical value (D):
   switch distr
        case 'uniform'
            b = 3*rand(d,1);
            a = b.*rand(d,1);
            Y1 = rand(d,num_of_samples_max) .* repmat(a,1,num_of_samples_max); %~U[0,a]
            Y2 = rand(d,num_of_samples_max) .* repmat(b,1,num_of_samples_max); %U[0,b], a<=b (coordinate-wise) => Y1<<Y2
            par1.a = a;
            par2.a = b;
        otherwise
            error('Distribution=?');                 
   end
   %analytical value:
       D = analytical_value_DBregman(distr,distr,alpha_D,par1,par2);
    
%estimation:
    Tk = 0;%index of the sample number examined   
    for num_of_samples = num_of_samples_v
        Tk = Tk + 1;
        D_hat_v(Tk) = D_estimation(Y1(:,1:num_of_samples),Y2(:,1:num_of_samples),co);
        disp(strcat('Tk=',num2str(Tk),'/',num2str(L)));
    end
    
%plot:
    plot(num_of_samples_v,D_hat_v,'r',num_of_samples_v,D*ones(1,L),'g');
    legend({'estimation','analytical value'});
    xlabel('Number of samples (T)');
    ylabel('Bregman divergence');        
