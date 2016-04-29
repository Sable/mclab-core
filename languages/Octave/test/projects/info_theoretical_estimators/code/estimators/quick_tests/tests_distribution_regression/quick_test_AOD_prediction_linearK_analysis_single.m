%function [] = quick_test_AOD_prediction_linearK_analysis_single()
%Analysis of the RMSE results obtained by 'quick_test_AOD_prediction_linearK.m', using a single kernel.

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
    dataset = 'MISR1'; %fixed
    %parameters of the randomization in the experiments:
        CV = 5; %CV-fold cross validation: (CV-2),1,1 part used for training, validation, testing 
        v_v = [1:2]; %[1:10]; indices of the random runs; number of random runs = length(v_v); '_v': vector
    %distribution embedding:
        cost_name = 'expected'; %name of the used embedding
            kernel = 'Matern3p2'; %'RBF', 'exponential', 'Cauchy', 'student', 'Matern3p2', 'Matern5p2','poly2', 'poly3','ratquadr', 'invmquadr'
            %kernel parameter (RBF width/...):
                base_kp = 10; %fine-grainedness of the parameter scan; smaller base_kp = finer scan, 'kp': kernel parameter
    %regularization parameter (lambda):
        base_rp = 10;  %fine-grainedness of the parameter scan; smaller base_rp = finer scan; 'rp': regularization parameter

%initialization: 
    num_of_random_runs = length(v_v);
    RMSE_v = zeros(num_of_random_runs*CV,1);        
    k = 0;        
    
%RMSE_v:    
    for v = v_v
    for nCV = 1 : CV
        %load RMSE_opt:
            FN = FN_surfs_optspars_k(dataset,cost_name,kernel,CV,nCV,v,base_kp,base_rp);
            load(FN,'RMSE_opt');
        k = k + 1;
        RMSE_v(k) = RMSE_opt;
    end
    end

%statistics of RMSE_v:    
    m = mean(RMSE_v);
    s = std(RMSE_v);
    %rounding:
        dec = 2; %decimals used in rounding
        m = round(100 * m * 10^dec)/10^dec; %100xRMSE, and
        s = round(100 * s * 10^dec)/10^dec; %its std
    
%disp:    
    disp(strcat(['Kernel: ',kernel,'; 100xRMSE: mean=',num2str(m),', std=',num2str(s)]));
