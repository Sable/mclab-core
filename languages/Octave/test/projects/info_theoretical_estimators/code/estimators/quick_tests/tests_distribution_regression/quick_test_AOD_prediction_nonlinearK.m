%function [] = quick_test_AOD_prediction_nonlinearK()
%Quick test for aerosol optical depth (AOD) prediction. Method: MERR (Mean Embedding based Ridge Regression), using non-linear kernel (K) on mean embeddings.
%Note: 
%   1)The precomputed Gram matrices are saved (see 'dir_G').
%   2)The code also supports 'Matlab: parallel computing', see below (matlabpool open/close, parfor-s ['precomputation_of_Gram_matrices_kK', 'compute_slices.m']).
%
%REFERENCE: Zoltan Szabo, Arthur Gretton, Barnabas Poczos, and Bharath Sriperumbudur. Consistent, two-stage sampled distribution regression via mean embedding. Technical report, Gatsby Unit, University College London, 2014. (http://arxiv.org/abs/1402.1754).

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
        CV = 5; %CV-fold cross validation: (CV-2),1,1 part used for training, validation, testing. 
        v_v = [1:2]; %[1:10]; indices of the random runs; number of random runs = length(v_v); '_v': vector
    %distribution embedding:
        cost_name = 'expected'; %name of the used embedding
            kernel_k = 'RBF'; %kernel defining the mean embedding of the distributions. Possibilites: 'RBF', 'exponential', 'Cauchy', 'student', 'Matern3p2', 'Matern5p2', 'poly2', 'poly3', 'ratquadr', 'invmquadr'; see 'Kexpected_initialization.m' if cost_name = 'expected'
            kernel_K = 'Matern3p2'; %kernel on the mean embedded distributions.         Possibilites: 'RBF', 'exponential', 'Cauchy', 'student', 'Matern3p2', 'Matern5p2', 'ratquadr', 'invmquadr'; see 'precomputation_of_Gram_matrices_kK.m'
            %kernel parameter:
                base_kp = 10; %fine-grainedness of the parameter scan; smaller base_kp = finer scan, 'kp': k(ernel) p(arameter)
                kp_v = base_kp.^[-4:3]; %candidate kernel parameters ('RBF widths'); '_v': vector
                base_Kp = 10; %fine-grainedness of the parameter scan; smaller base_Kp = finer scan, 'Kp': K(ernel) p(arameter)
                Kp_v = base_Kp.^[-4:3]; %candidate kernel parameters ('RBF widths'); '_v': vector
    %regularization parameter (lambda):
        base_rp = 10;  %fine-grainedness of the parameter scan; smaller base_rp = finer scan; 'rp': regularization parameter
        rp_v = base_rp.^[-10:-1]; %candidate regularization parameters (lambda)
    %experiment generation, precomputing, plot:
        load_generated_experiment = 0; %if you would like to generate a new experiment (and study the efficiency of multiply kernels, ...), then load_generated_experiment = 0; else you just load the already generated dataset (=1).
        load_precomputed_Gram_matrices = 0; %0/1; if you have already precomputed the Gram matrices set precompute_Gram_matrices = 1; else =0.
        plot_needed = 1; %plot the results: 0/1

%matlabpool open; %use this line with 'Matlab: parallel computing'
%-------------------------------
%generate dataset:
   if ~load_generated_experiment %If you are experimenting with multiple kernels, to be able to study the efficiency of different kernels under the same conditions, call this generating function only _once_:
        [X,Y] = generate_AOD_dataset(dataset,v_v);
   else %if you have already generated the dataset just load 'X' and 'Y'):
        FN = strcat(dataset,'_dataset.mat');
        load(FN,'X','Y');
   end
    
 %create Gram matrices (given the AOD random runs, we precompute the Gram matrices):
    if ~load_precomputed_Gram_matrices
        precomputation_of_Gram_matrices_kK(cost_name,kernel_k,kp_v,base_kp,kernel_K,Kp_v,base_Kp,dataset,X); %the function supports 'Matlab: parallel computing' if you activate 'parfor' in it
    end    
    
 %load idx_train,idx_val,idx_test-s:
    indices = cross_validation_indices(length(Y),CV);
    
for v = v_v
    %p:
        FN = strcat(dataset,'_permutation_v',num2str(v),'.mat');%the random experiments are encoded by permutations of the elements
        load(FN,'p');
    for nCV = 1 : CV
        %idx_train,idx_val,idx_test): nCV-dependent, see 'p'
            idx_train = p(indices{nCV}.train);
            idx_val = p(indices{nCV}.val);
            idx_test = p(indices{nCV}.test);
        %output values (Y_train, Y_val, Y_test), number of training bags (L_train):
            Y_train = Y(idx_train);
            Y_val = Y(idx_val);
            Y_test = Y(idx_test); 
            L_train = length(idx_train);  
        %validation surface, test surface:
            %initialization:
                validation_surface = zeros(length(rp_v),length(kp_v));
                test_surface = zeros(length(rp_v),length(kp_v));
            for nkp = 1 : length(kp_v)
            kp = kp_v(nkp);                
            for nKp = 1 : length(Kp_v)                
                Kp = Kp_v(nKp);                
                %G_train:
                    %load G => G_train:
                        FN = FN_Gram_matrix_kK(dataset,cost_name,kernel_k,kp,base_kp,kernel_K,Kp,base_Kp);
                        load(FN,'G');
                        G_train = G(idx_train,idx_train); %Gram matrix of the training distributions  
                %validation_slice, test_slice:
                    [validation_slice,test_slice] = compute_slices(rp_v,G,G_train,L_train,Y_train,Y_val,Y_test,idx_train,idx_val,idx_test); %the function supports 'Matlab: parallel computing' if you activate 'parfor' in it
                    validation_surface(:,nKp,nkp) = validation_slice;            
                    test_surface(:,nKp,nkp) = test_slice;            
            end
            end
        %optimal parameters (regularization-, kernel parameter):
            [rp_idx,Kp_idx,kp_idx,minA] = min_3D(validation_surface);
            Kp_opt = Kp_v(Kp_idx);
            kp_opt = kp_v(kp_idx);
            rp_opt = rp_v(rp_idx);    
            RMSE_opt = test_surface(rp_idx,Kp_idx,kp_idx); %test error for the chosen parameters
                disp(strcat(['Experiment:', num2str(v),', fold:',num2str(nCV),' -> RMSE-optimal: ',num2str(RMSE_opt),'.']));
        
        %Y_test_predicted_test:
            %load G => G_train:
                FN = FN_Gram_matrix_kK(dataset,cost_name,kernel_k,kp_opt,base_kp,kernel_K,Kp_opt,base_Kp);
                load(FN,'G');
                G_train = G(idx_train,idx_train); %Gram matrix of the training distributions  
            %left:
                A = real(inv(G_train + L_train * rp_opt * eye(L_train))); %real(): 'eps' imaginary values may appear
                left = Y_train.' * A; %left hand side of the inner product; row vector            
            Y_predicted_test = ( left * G(idx_train,idx_test) ).';%column vector
            if plot_needed
                figure; plot([1:length(Y_test)],Y_test,'b',[1:length(Y_test)],Y_predicted_test,'g'); legend({'true','predicted'});
            end
        %save:
            save_dir = strcat(dataset,'_results_kK');
            dir_present = create_and_cd_dir(save_dir);
            FN = FN_surfs_optspars_kK(dataset,cost_name,kernel_k,kernel_K,CV,nCV,v,base_kp,base_Kp,base_rp);
            save(FN,'validation_surface','test_surface','Kp_v','kp_v','rp_v','Kp_idx','kp_idx','rp_idx','Kp_opt','kp_opt','rp_opt','RMSE_opt','base_Kp','base_kp','base_rp', 'Y_predicted_test','Y_test');
            cd(dir_present);
    end%nCV
end%v
%-------------------------------
%matlabpool close; %use this line with 'Matlab: parallel computing'
