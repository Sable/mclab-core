function [] = precomputation_of_Gram_matrices_k(cost_name,kernel,kp_v,base_kp,dataset,X)
%function [] = precomputation_of_Gram_matrices_k(cost_name,kernel,kp_v,base_kp,dataset,X)
%Precomputation of the Gram matrices given embedding (cost_name), and the 'kernel' defining it, for different kernel parameters (kp_v), which were defined by base 'base_kp'. Matrix 'X' represents samples (each column of X is a sample) from 'dataset'. 
%Note: The code supports 'Matlab: parallel computing', see below (parfor).

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

save_dir = strcat(dataset,'_Gram_k');
dir_present = create_and_cd_dir(save_dir);
for nkp = 1 : length(kp_v)    %use this line without 'Matlab: parallel computing'
%parfor nkp = 1 : length(kp_v) %use this line with 'Matlab: parallel computing'
    %initialize co (=kernel object):
        kp = kp_v(nkp);
        co = co_K_initialization(cost_name,kernel,kp);
    %compute G:
        G = compute_Gram_matrix(X,co);
    %save G:
        FN = FN_Gram_matrix_k(dataset,cost_name,kernel,kp,base_kp);
        save_Gram_matrix(FN,G);
end
cd(dir_present);
