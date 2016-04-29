function [X,Y] = generate_AOD_dataset(dataset,v_v)
%function [X,Y] = generate_AOD_dataset(dataset,v_v)
%
%Generates the AOD:MISR1 dataset and random permutations to the experiments (so that the same permutation could be used for different kernels).
%
%Example: dataset = 'MISR1'; CV = 5; v_v = [1:10]; => 'MISR1.mat' -> MISR1_dataset.mat; 'MISR1_permutation_CV5_v1.mat', ..., 'MISR1_permutation_CV5_v10.mat';

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

%create saving directory (if it does not exist) for the generated AOD experiment:
    dir_experiment = strcat(dataset,'_experiment');
    dir_present = create_and_cd_dir(dir_experiment);

%generate dataset (X,Y):
    FN = strcat(dataset,'_dataset.mat');
    %verification:
        if exist(FN)
            error('The dataset already exists.');
        end
    %load data:    
        data = load(strcat(dataset,'.mat')); %the result is a structure
        data = data.(dataset); %extract the data from the structure
    %find the unique ID-s in the first column:    
        IDs = unique(data(:,1)).'; %row vector
        L = length(IDs);%number of bags
    %initialization:
        X = cell(L,1);  %inputs
        Y = zeros(L,1); %outputs
    %X,Y <- data:    
        k = 0;
        for ID = IDs
            idx_ID = find(data(:,1)==ID);%find the rows identified by ID
            k = k + 1;
            X{k} = data(idx_ID,2:end-1).';
            Y(k) = data(idx_ID(1),end); %assumption: each bag has the same real output label [see idx_ID(1)]
        end
    %save X,Y:   
        FN = strcat(dataset,'_dataset.mat');
        save(FN,'X','Y');

%generate random runs (p):        
    for v = v_v
        %generate permutation:
            num_of_bags = length(Y);
            p = randperm(num_of_bags);
         %save:
            FN = strcat(dataset,'_permutation_v',num2str(v),'.mat');%the random experiments are encoded by permutations of the elements
            save(FN,'p');
    end
    
cd(dir_present);%change back to the working directory
