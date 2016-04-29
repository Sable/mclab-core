%function [] = estimate_HRenyi_constant()
%Estimates the additive constant of the Renyi entropy estimator 'Renyi_MST' for a fixed dimension (d below). In certain cases these constants can also be important. Here, they are precomputed via Monte-Carlo simulations; the result is saved and becomes available in 'HRenyi_MST_estimation.m'.

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
    clear all;

%Parameters:
    %name of the Renyi entropy estimator:
        cost_name = 'Renyi_MST';
    %dimension:
        d = 1;
    num_of_samples = 1000;%number of samples for a fixed run
    num_of_MC_runs = 50;   %number of Monte-Carlo runs
        
%initialize cost object:
    mult = 1;
    co = H_initialization(cost_name,mult);%the value of 'mult' is irrelevant (=not used) below.
    function_handle = eval(strcat('@compute_length_H',co.name));
switch cost_name
    case {'Renyi_MST'}
        %initialization:
            alp = co.alpha; 
            sum_of_normalizedL = 0;      

        for k = 1 : num_of_MC_runs
            Y = rand(d,num_of_samples);%uniform distribution, U([0,1]^d)
            %L(Y):
                L = function_handle(Y,co);
                %examples:
                    %L = compute_length_HRenyi_MST(Y,co);
            sum_of_normalizedL = sum_of_normalizedL + L / (num_of_samples^alp); 
            if mod(k,10)==1
                disp(strcat('k=',num2str(k),'(/',num2str(num_of_MC_runs),')'));
            end
        end

        constant = sum_of_normalizedL / num_of_MC_runs;
        FN = filename_of_HRenyi_constant(d,co);
        save(FN,'constant','num_of_samples','num_of_MC_runs','co');
    otherwise
        error('cost name=?');
end
