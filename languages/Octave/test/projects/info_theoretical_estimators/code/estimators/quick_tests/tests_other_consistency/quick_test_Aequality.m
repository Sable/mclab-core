%function [] = quick_test_Aequality()
%Quick test for 'A(y^1,y^2)=0/1 if y^1=y^2'. Here 'y^1=y^2'is meant in distribution/realization. See also 'quick_test_Aindependence.m'. In the test, normal/uniform variables are considered.

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
    distr = 'uniform'; %possibilities: 'uniform', 'normal'
    num_of_samples_v = [100:500:3*1000]; %sample numbers used for estimation
    %estimator (of association):
        %base:
            cost_name = 'CorrEntr_KDE_direct';        
            %cost_name = 'CorrEntrCoeff_KDE_direct'; %computationally intensive
            %cost_name = 'CorrEntrCoeff_KDE_iChol';
        %meta:
            %cost_name = 'CCIM'; %metric
            %cost_name = 'CIM';  %metric

%initialization:    
    num_of_samples_max = num_of_samples_v(end);
    L = length(num_of_samples_v);
    ds = [1;1];
    co = A_initialization(cost_name,1);
    A_hat_v = zeros(L,1);%estimated association values

%generate samples (Y=[Y1,Y2]), analytical value (A):
    %Y1:
        switch distr
            case 'uniform'
                Y1 = rand(1,num_of_samples_max);
            case 'normal'
                Y1 = randn(1,num_of_samples_max);
            otherwise
                error('Distribution=?');
        end
    %Y2:
        switch cost_name
            case {'CIM','CCIM'}
                Y2 = 0.99 * Y1;
                %Y2 = 1 * Y1;
                A = 0; %if Y1=Y2
            case 'CorrEntr_KDE_direct'
                Y2 = 0.9 *Y1;
                %Y2 = Y1;
                A = 1; %if Y1=Y2 
            case {'CorrEntrCoeff_KDE_direct','CorrEntrCoeff_KDE_iChol'}
                switch distr
                    case 'uniform'
                        Y2 = rand(1,num_of_samples_max);
                    case 'normal'
                        Y2 = randn(1,num_of_samples_max);
                end
                A = 0; %if Y1=distr=Y2
            otherwise
                error('Method=?');
        end
    Y = [Y1;Y2];
    
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
