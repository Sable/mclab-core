%function [] = quick_test_HPhi()
%Quick test for Phi-entropy estimators: analytical expression vs estimated value as a function of the sample number. In the test, uniform variables are considered. See also 'analytical_value_HPhi.m'.

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
    %Phi (in the Phi-entropy):
        c_H = 2; %>=1; c_H is also used in the analytical expression: 'H = ...'
        Phi_H = @(t)(t.^c_H);
    num_of_samples_v = [1000:1000:100*1000]; %sample numbers used for estimation
    %estimator, base:
        cost_name = 'Phi_spacing'; %d=1
    
%initialization:  
    d = 1;%dimension of the distribution
    num_of_samples_max = num_of_samples_v(end);
    L = length(num_of_samples_v);
    co = H_initialization(cost_name,1,{'Phi',Phi_H}); %{'Phi',Phi_H}: we also set the Phi function here
    H_hat_v = zeros(L,1); %vector of estimated entropies
    
%distr -> samples (Y), analytical value (H):
    switch distr 
        case 'uniform'
            %U[a,b]:
                a = rand; b = a + 4*rand;%guaranteed that a<=b
            %generate samples:
                Y =  (b-a) * rand(1,num_of_samples_max) + a;
            par.a = a; par.b = b;
            Hpar.c_H = c_H;
        otherwise
            error('Distribution=?');
    end  
    %analytical value:
        H = analytical_value_HPhi(distr,Hpar,par);
            
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
    ylabel('\Phi-entropy');
