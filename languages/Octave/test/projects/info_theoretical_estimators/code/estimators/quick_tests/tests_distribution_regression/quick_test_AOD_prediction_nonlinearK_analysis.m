%function [] = quick_test_AOD_prediction_nonlinearK_analysis()
%Analysis of the RMSE results obtained by 'quick_test_AOD_prediction_nonlinearK.m'.

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
            kernel_k_c = {'RBF','Matern3p2'}; %kernel defining the mean embedding of the distributions. Possibilites: 'RBF', 'exponential', 'Cauchy', 'student', 'Matern3p2', 'Matern5p2', 'poly2', 'poly3', 'ratquadr', 'invmquadr'; see 'Kexpected_initialization.m' if cost_name = 'expected'. '_c': cell array
            kernel_K_c = {'RBF','invmquadr'}; %kernel on the mean embedded distributions.         Possibilites: 'RBF', 'exponential', 'Cauchy', 'student', 'Matern3p2', 'Matern5p2', 'ratquadr', 'invmquadr'; see 'precomputation_of_Gram_matrices_kK.m'. '_c': cell array
        %kernel parameter:
            base_kp = 10; %fine-grainedness of the parameter scan; smaller base_kp = finer scan, 'kp': k(ernel) p(arameter)
            base_Kp = 10; %fine-grainedness of the parameter scan; smaller base_Kp = finer scan, 'Kp': K(ernel) p(arameter)
    %regularization parameter (lambda):
        base_rp = 10;  %fine-grainedness of the parameter scan; smaller base_rp = finer scan; 'rp': regularization parameter
    num_of_greedy_kK_pairs = 10; %num_of_greedy_kK_pairs number of (k,K) pairs can form the ensemble (at most)

%initialization:    
    num_of_greedy_kK_pairs = min(num_of_greedy_kK_pairs,length(kernel_k_c)*length(kernel_K_c));%it can be at most the number of (k,K) pairs, the second term in the min()
    num_of_random_runs = length(v_v);
    dec = 2; %decimals in rounding

%mRMSE_M, stdRMSE_M [goodness of different (k,K) pairs: mean, std of RMSE]:    
    %initialization:
        mRMSE_M = zeros(length(kernel_k_c),length(kernel_K_c)); %'_M': matrix
        stdRMSE_M = zeros(length(kernel_k_c),length(kernel_K_c)); %'_M': matrix
    for nkernel_K = 1 : length(kernel_K_c)
    kernel_K = kernel_K_c{nkernel_K};
    for nkernel_k = 1 : length(kernel_k_c)
    kernel_k = kernel_k_c{nkernel_k};         
    %RMSE_v:
        RMSE_v = zeros(num_of_random_runs*CV,1);        
        k = 0;        
        for v = v_v
        for nCV = 1 : CV
            %load RMSE_opt:
                FN = FN_surfs_optspars_kK(dataset,cost_name,kernel_k,kernel_K,CV,nCV,v,base_kp,base_Kp,base_rp);
                load(FN,'RMSE_opt');
            k = k + 1;
            RMSE_v(k) = RMSE_opt;
        end
        end
    %m,s:
        m = mean(RMSE_v);
        s = std(RMSE_v);
        %rounding:
            m = round(100 * m * 10^dec) / 10^dec; %100xRMSE, and 
            s = round(100 * s * 10^dec) / 10^dec; %its std
    %update mRMSE_M, stdRMSE_M:
        mRMSE_M(nkernel_k,nkernel_K) = m;
        stdRMSE_M(nkernel_k,nkernel_K) = s;
    end
    end
    
%single kernel results:
    mRMSE_M, stdRMSE_M,
    
%***************************
if num_of_greedy_kK_pairs > 1 %otherwise there in no sense to search
    %indices of the smallest mean RMSE values in 'mRMSE_M':
        [idx_k,idx_K,values] = sort_2D(mRMSE_M);
        idx_k = idx_k(1:num_of_greedy_kK_pairs);
        idx_K = idx_K(1:num_of_greedy_kK_pairs);    

    RMSE_mean_opt = Inf;
    nc = 0;
    combi_all = combn([0 1],num_of_greedy_kK_pairs).';
    for combi = combi_all(:,2:end)%'2:end' <-> delete "0,0,...,0"
        nc = nc + 1;
        %RMSE_fixed_combi:
            RMSE_fixed_combi = zeros(num_of_random_runs,CV);
            nv = 0;
            for v = v_v
            nv = nv + 1;
            for nCV = 1 : CV
                %RMSE_fixed_combi(nv,nCV):
                    %Y_predicted:
                        Y_predicted = 0;
                        for nkernel = 1 : num_of_greedy_kK_pairs
                            if combi(nkernel)==1
                                kernel_K = kernel_K_c{idx_K(nkernel)};
                                kernel_k = kernel_k_c{idx_k(nkernel)};
                                %load Y_predicted:
                                    FN = FN_surfs_optspars_kK(dataset,cost_name,kernel_k,kernel_K,CV,nCV,v,base_kp,base_Kp,base_rp);
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

    %RMSE_mean_opt, RMSE_std_opt, combi_opt, (k,K) pairs in the best ensemble:
        m_opt = round(100 * RMSE_mean_opt * 10^dec) / 10^dec, %100xRMSE, and 
        s_opt = round(100 * RMSE_std_opt * 10^dec) / 10^dec,  %its std
        combi_opt,
        %optimal (k,K)-s forming the ensemble:
            kernel_k_c{idx_k(logical(combi_opt))},
            kernel_K_c{idx_K(logical(combi_opt))},
end  
