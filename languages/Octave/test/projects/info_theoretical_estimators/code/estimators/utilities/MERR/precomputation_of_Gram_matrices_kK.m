function [] = precomputation_of_Gram_matrices_kK(cost_name,kernel_k,kp_v,base_kp,kernel_K,Kp_v,base_Kp,dataset,X)
%function [] = precomputation_of_Gram_matrices_kK(cost_name,kernel_k,kp_v,base_kp,kernel_K,Kp_v,base_Kp,dataset,X)
%
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

save_dir = strcat(dataset,'_Gram_kK');
dir_present = create_and_cd_dir(save_dir);
for nkp = 1 : length(kp_v)    %use this line without 'Matlab: parallel computing'
%parfor nkp = 1 : length(kp_v) %use this line with 'Matlab: parallel computing'
    %GD:
        %initialize co (=k(ernel object)):
            kp = kp_v(nkp);
            co = co_D_initialization(kernel_k,kp);
        %compute G:
            GD = compute_Gram_matrix_D(X,co);
    %GD -> G=G(GD,K):
        for nKp = 1 : length(Kp_v)
            %G:
                Kp = Kp_v(nKp); 
                switch kernel_K
                    case 'RBF'
                        G = exp(-GD.^2/(2*Kp^2));
                    case 'exponential'
                        G = exp(-GD/(2*Kp^2));
                    case 'Cauchy'
                        G = 1 ./ (1+GD.^2/Kp^2);
                    case 'student'           
                        G = 1 ./ (1+GD.^Kp);
                    case 'Matern3p2'
                        G = (1+sqrt(3)*GD/Kp) .* exp(-sqrt(3)*GD/Kp);
                    case 'Matern5p2'
                        G = (1+sqrt(5)*GD/Kp + 5*GD.^2/(3*Kp^2)) .* exp(-sqrt(5)*GD/Kp);
                    case 'ratquadr'
                        G = 1 - GD.^2 ./ (GD.^2 + Kp);
                    case 'invmquadr'
                        G =  1 ./ sqrt(GD.^2 + Kp^2);
                    otherwise
                        error('Kernel K = ?');
                end                    
            %save G:
                FN = FN_Gram_matrix_kK(dataset,cost_name,kernel_k,kp,base_kp,kernel_K,Kp,base_Kp);
                save_Gram_matrix(FN,G);                    
        end
end
cd(dir_present);


