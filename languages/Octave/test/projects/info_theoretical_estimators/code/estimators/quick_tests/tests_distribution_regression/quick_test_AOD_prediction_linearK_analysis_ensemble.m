%function [] = quick_test_AOD_prediction_linearK_analysis_ensemble()
%Analysis of the RMSE results obtained by 'quick_test_AOD_prediction_linearK.m', using ensemble of kernels.

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
            kernel_c = {'RBF','exponential','Matern3p2','invmquadr'}; %{'RBF', 'exponential', 'Cauchy', 'student', 'Matern3p2', 'Matern5p2','poly2', 'poly3','ratquadr','invmquadr'}; '_c'_ cell array
            %kernel parameter (RBF width/...):
                base_kp = 10; %fine-grainedness of the parameter scan; smaller base_kp = finer scan, 'kp': kernel parameter
    %regularization parameter (lambda):
        base_rp = 10;  %fine-grainedness of the parameter scan; smaller base_rp = finer scan; 'rp': regularization parameter
        
%initialization:  
    num_of_random_runs = length(v_v);
    RMSE_mean_opt = Inf;
    combi_all = combn([0 1],length(kernel_c)).';

nc = 0;    
for combi = combi_all(:,2:end)%'2:end' <-> delete "0,0,...,0"
    nc = nc + 1;
    %RMSE_fixed_combi:
        RMSE_fixed_combi = zeros(num_of_random_runs,CV);
        nv = 0;
        for v = v_v
        nv = nv + 1;
        for nCV = 1 : CV
            %RMSE_fixed_combi(v,nCV):
                %Y_predicted:
                    Y_predicted = 0;
                    for nkernel = 1 : length(kernel_c)
                        if combi(nkernel)==1
                            kernel = kernel_c{nkernel};
                            %load Y_predicted:
                                FN = FN_surfs_optspars_k(dataset,cost_name,kernel,CV,nCV,v,base_kp,base_rp);
                                load(FN,'Y_predicted_test','Y_test');
                                Y_predicted = Y_predicted + Y_predicted_test;
                        end
                    end
                    Y_predicted = Y_predicted / sum(combi); %sum->mean
                RMSE_fixed_combi(nv,nCV) = RMSE(Y_predicted,Y_test);
        end
        end
    %RMSE_mean_candidate, RMSE_std_candidate:    
        RMSE_mean_candidate = mean(RMSE_fixed_combi(:));
        RMSE_std_candidate = std(RMSE_fixed_combi(:));
    %if the actual combination is better than the best one seen so far, then update the best:    
        if RMSE_mean_candidate < RMSE_mean_opt
            RMSE_mean_opt = RMSE_mean_candidate;
            %disp:
                disp(strcat(['Best mean RMSE seen so far: ',num2str(RMSE_mean_opt),'.']));
            RMSE_std_opt = RMSE_std_candidate;
            combi_opt = combi;
        end
    %disp:    
        if mod(nc,100)==1
            disp(strcat('nc=',num2str(nc)));
        end

end
disp('--------------');

%RMSE_mean_opt, RMSE_std_opt, combi_opt, best kernel combination:
    %rounding:
        dec = 2;
        m_opt = round(100 * RMSE_mean_opt * 10^dec)/10^dec, %100xRMSE, and
        s_opt = round(100 * RMSE_std_opt * 10^dec)/10^dec,  %is std
    combi_opt,
    kernel_c{logical(combi_opt)},        
