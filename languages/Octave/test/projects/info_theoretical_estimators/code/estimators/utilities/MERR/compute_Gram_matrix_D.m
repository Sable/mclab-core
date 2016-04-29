function [G] = compute_Gram_matrix_D(X,co)
%function [G] = compute_Gram_matrix_D(X,co)
%Computes 'Gram' matrix, given the cell array X (X{k}: samples from the k^th distribution) and divergence (co). G_{ij} = D(X{i},X{j}).

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

disp('GD computation: started.');

num_of_distributions = length(X);
G = zeros(num_of_distributions);
for k1 = 1 : num_of_distributions %k1^th distribution
    for k2 = k1 : num_of_distributions %k2^th distribution
        %if k1==k2 %to guarantee independence
        %    num_of_samples_half = floor(size(X{k1},2)/2);
        %    G(k1,k2) = D_estimation(X{k1}(:,1:num_of_samples_half),X{k1}(:,num_of_samples_half+1:end),co); %D(X{k1}(first half),X{k1}(second half))
        %else
            G(k1,k2) = D_estimation(X{k1},X{k2},co); %D(X{k1},X{k2})
        %end
        G(k2,k1) = G(k1,k2);%assumption: symmetry
    end
    %disp:
        if mod(k1,10)==1
            disp(strcat('k1=',num2str(k1),'/',num2str(num_of_distributions),': ready.'));
        end
end
