%function [] = quick_test_Aindependence()
%Quick test for 'A(y^1,...,y^M)=0 if y^m-s are independent'. See also 'quick_test_Aequality.m'. In the test, normal/uniform variables are considered.

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
    M = 2; %number of components
    num_of_samples_v = [1000:1000:30*1000]; %sample numbers used for estimation
    %estimator (of association):
        %base:
            cost_name = 'Spearman1';  %M>=2
            %cost_name = 'Spearman2'; %M>=2
            %cost_name = 'Spearman3'; %M>=2   
            %cost_name = 'Spearman4'; %M>=2          
            %cost_name = 'CCorrEntr_KDE_iChol'; %M=2
            %cost_name = 'CCorrEntr_KDE_Lapl';  %M=2
            %cost_name = 'Blomqvist';   %M>=2
            %cost_name = 'Spearman_lt'; %M>=2
            %cost_name = 'Spearman_ut'; %M>=2
        %meta:
            %cost_name = 'Spearman_L'; %M>=2
            %cost_name = 'Spearman_U'; %M>=2
            
%initialization:
    num_of_samples_max = num_of_samples_v(end);
    L = length(num_of_samples_v);
    co = A_initialization(cost_name,1);
    A_hat_v = zeros(L,1); %estimated association values
    ds = ones(M,1);
    
%generate samples (Y), analytical value (A): 
    %Y:
       switch distr
           case 'uniform'
               Y = rand(M,num_of_samples_max);
           case 'normal'
               Y = randn(M,num_of_samples_max);
           otherwise
               error('Distribution=?');            
       end
    A = 0;

%estimation:
    Tk = 0;%index of the sample number examined
    for num_of_samples = num_of_samples_v
        Tk = Tk + 1;
        A_hat_v(Tk) = A_estimation(Y(:,1:num_of_samples),ds,co);
        disp(strcat('Tk=',num2str(Tk),'/',num2str(L)));        
    end

%plot:
    plot(num_of_samples_v,A_hat_v,'r',num_of_samples_v,A*ones(L,1),'g');
    legend({'estimation','analytical value'});
    xlabel('Number of samples');
    ylabel('Association');    

