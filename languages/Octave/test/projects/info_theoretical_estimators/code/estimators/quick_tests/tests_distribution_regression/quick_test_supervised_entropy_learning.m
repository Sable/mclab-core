%function [] = quick_test_supervised_entropy_learning()
%Quick test for distribution regression in supervised entropy learning. Method: MERR (Mean Embedding based Ridge Regression).
%Note: 
%   1)The precomputed Gram matrices are saved (see 'dir_G').
%   2)The code also supports 'Matlab: parallel computing', see below (matlabpool open/close, parfor-s).
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
    dataset = 'entropy'; %fixed
    %sample numbers:
        L = 100; %number of distributions
        N = 500; %number of samples/distribution
    %training, validation, test sample sizes (assumption: L = L_train + L_val + L_test):
        L_train = 25;  %number of training distributions from L
        L_val = 25;    %number of validation distributions from L
        L_test = 50;   %number of test distributions from L
    %embedding:        
        cost_name = 'expected'; %name of the used embedding; cost_name = 'expected' <-> mean embedding
        kernel =  'RBF';
        %kernel parameter:
            base_kp = 10; %fine-grainedness of the parameter scan; smaller base_kp = finer scan, 'kp': kernel parameter
            kp_v = base_kp.^[-4:3]; %candidate kernel parameters ('RBF widths'); '_v': vector
    %regularization parameter (lambda):
        base_rp = 10;  %fine-grainedness of the parameter scan; smaller base_rp = finer scan; 'rp': regularization parameter
        rp_v = base_rp.^[-10:-1]; %candidate regularization parameters (lambda)
    v_v = [1:1]; %[1:25]; indices of the random runs; number of random runs = length(v_v); '_v': vector
    
%matlabpool open; %use this line with 'Matlab: parallel computing'
%-------------------------------
%create saving directory (if it does not exist) for the precomputed Gram matrices:
    dir_G = strcat(dataset,'_Gram'); %directory name for the precomputed Gram matrices.
    dir_present = create_and_cd_dir(dir_G);
    
objective_values = zeros(length(v_v),1);    
for v = v_v
    
    %generate dataset:    
        [X,Y,idx_train,idx_val,idx_test,X_parameter] = generate_supervised_entropy_dataset(L,N,L_train,L_val,L_test);

    %create Gram matrices (precomputing):
        for nkp = 1 : length(kp_v) %use this line without 'Matlab: parallel computing'
        %parfor nkp = 1 : length(kp_v) %use this line with 'Matlab: parallel computing'
            %compute G:
                kp = kp_v(nkp);
                co = K_initialization(cost_name,1,{'kernel',kernel,'sigma',kp});
                G = compute_Gram_matrix(X,co);
            %save G:
                FN = FN_Gram_matrix_v(dataset,cost_name,kernel,kp,base_kp,v);
                save_Gram_matrix(FN,G);
        end

    %output values (Y_train, Y_val, Y_test):
        Y_train = Y(idx_train);
        Y_val = Y(idx_val);
        Y_test = Y(idx_test);     

    %validation surface, test surface:
        %initialization:
            validation_surface = zeros(length(rp_v),length(kp_v));
            test_surface = zeros(length(rp_v),length(kp_v));
        for nkp = 1 : length(kp_v)
            %G_train:
                kp = kp_v(nkp);
                %load G => G_train:
                    FN = FN_Gram_matrix_v(dataset,cost_name,kernel,kp, base_kp,v);
                    load(FN,'G');
                    G_train = G(idx_train,idx_train); %Gram matrix of the training distributions  
            %validation_slice, test_slice:
                validation_slice = zeros(length(rp_v),1);
                test_slice = zeros(length(rp_v),1);
                for nrp = 1 : length(rp_v)    %use this line without 'Matlab: parallel computing'
                %parfor nrp = 1 : length(rp_v) %use this line with 'Matlab: parallel computing'
                    rp = rp_v(nrp);
                    %left:
                        A = real(inv(G_train + L_train * rp * eye(L_train))); %real(): to avoid complex values due to epsilon rounding errors
                        left = Y_train.' * A; %left hand side of the inner product; row vector
                    %Y_predicted_val, Y_predicted_test:
                        Y_predicted_val = ( left * G(idx_train,idx_val) ).';  %column vector
                        Y_predicted_test = ( left * G(idx_train,idx_test) ).';%column vector
                    %update the validation and test slices:
                        %L_2 error:
                            %validation_slice(nrp) = norm(Y_val-Y_predicted_val);
                            %test_slice(nrp) = norm(Y_test-Y_predicted_test);
                        %RMSE:
                            validation_slice(nrp) = RMSE(Y_val,Y_predicted_val);
                            test_slice(nrp) = RMSE(Y_test,Y_predicted_test);
                end
                validation_surface(:,nkp) = validation_slice;            
                test_surface(:,nkp) = test_slice;            
        end

    %optimal parameters (regularization-, kernel parameter):
        [rp_idx,kp_idx,minA] = min_2D(validation_surface);
        kp_opt = kp_v(kp_idx);
        rp_opt = rp_v(rp_idx);    
        objective_opt = test_surface(rp_idx,kp_idx);
        objective_values(v) = objective_opt;
        
    %plot (validaton_surface, test_surface):
        plot_surf(validation_surface,kp_v,rp_v,base_kp,base_rp,'log(validation surface)');
        plot_surf(test_surface,kp_v,rp_v,base_kp,base_rp,'log(test surface)');
        figure;
            %X_parameter_test:    
                X_parameter_test = X_parameter(idx_test);            
            %Y_test_predicted:
                %load G => G_train:
                    FN = FN_Gram_matrix_v(dataset,cost_name,kernel,kp_opt,base_kp,v);
                    load(FN,'G');
                    G_train = G(idx_train,idx_train); %Gram matrix of the training distributions  
                %left:
                    A = real(inv(G_train + L_train * rp_opt * eye(L_train))); %real(): to avoid complex values due to epsilon rounding errors
                    left = Y_train.' * A; %left hand side of the inner product; row vector            
                Y_predicted_test = ( left * G(idx_train,idx_test) ).'; %column vector
            plot(X_parameter,Y,'b',X_parameter_test,Y_predicted_test,'*g'); 
            %decorations:
                %title:
                    kp_str = num2str(eval(strcat(['log',num2str(base_kp),'(',num2str(kp_opt),')'])));
                    rp_str = num2str(eval(strcat(['log',num2str(base_rp),'(',num2str(rp_opt),')'])));
                    title_str = strcat('Prediction with the opt. parameters: log_{',num2str(base_kp),'}(kernel par.)=',kp_str,', log_{',num2str(base_rp),'}(reg. par.)=',rp_str);
                    title(title_str);
                %labels, legend:
                    xlabel('Rotation angle (\alpha)');
                    ylabel('Entropy of the first coordinate');
                    legend({'true','predicted'});
end

objective_values,

cd(dir_present);%change back to the original directory, from 'dir_G'
%-------------------------------
%matlabpool close; %use this line with 'Matlab: parallel computing'