function [] = ITE_install(ITE_code_dir)
%function [] = ITE_install(ITE_code_dir)
%function [] = ITE_install
%Installs the ITE toolbox.
%
%INPUT:
%   ITE_code_dir: directory containing the ITE package, e.g., 'C:\ITE\code'. When the function is called without arguments, ITE_code_dir is set to the current directory (ITE_code_dir = pwd).
%EXAMPLE:
%   A typical usage is to first 'cd' to the 'code' directory in Matlab/Octave, and call 'ITE_install(pwd)', or just simply 'ITE_install'.

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

%parameters:
	%compile (if you do not want to use the embedded C/C++ accelerations, set all the 'compile_...' variables to zero: compile_NCut = 0; compile_ANN = 0; ...):
		compile_NCut = 1;%1=compile, 0=do not compile; -> NCut spectral clustering.
		compile_ANN = 1;%1=compile, 0=do not compile; -> approximate nearest neighbor computation.
		compile_knn = 1;%1=compile, 0=do not compile; --> kNN search.
		compile_TCA = 1;%1=compile, 0=do not compile (chol_gauss); not necessary, but can further speed-up the computations; the package also contains the purely Matlab/Octave 'chol_gauss.m'; --> KCCA/KGV estimator, incomplete Cholesky decomposition.
		compile_SWICA = 1; %1=compile, 0=do not compile; not necessary, but can accelerate computations; the package also contains the purely Matlab/Octave 'SW_kappa.m' and 'SW_sigma.m'. -->  Schweizer-Wolff's sigma and kappa estimation.
		compile_Hoeffding_term1 = 1; %1=compile, 0=do not compile; not necessary, but can be more ecomical in terms of memory used + accelerate computations; the package also contains the purely Matlab/Octave 'Hoeffding_term1.m'; --> Hoeffding's Phi estimation.
		compile_Edgeworth_t1_t2_t3 = 1; %1=compile, 0=do not compile; not necessary, but can accelerate computations; the ITE package also contains the purely Matlab/Octave 'Edgeworth_t1_t2_t3.m'; --> Edgeworth expansion based entropy estimation.
		compile_CDSS = 1; %1=compile, 0=do not compile; not necessary, but can speed up the computations; the ITE package also contains the purely Matlab/Octave 'compute_CDSS.m'; -->  a Renyi quadratic entropy estimator.
        compile_KDP = 1; %1=compile, 0=do not compile; --> adaptive partitioning based Shannon entropy estimation.
        compile_MI_AP = 1; %1=compile, 0=do not compile; not necessary, but can speed up the computations; the ITE package also contains the purely Matlab/Octave 'mutin.m'. --> mutual information estimation based on adaptive partitioning.
    %download:
		download_ARfit = 1;%1=download+extract,0=do not download; -> autoregressive fit
        download_MISR1 = 1;%1=download+extract,0=do not download; -> aerosol optical depth (AOD) prediction: MISR1 dataset
	delete_Ncut_in_Octave = 1; %1=yes, 0=no

disp('Installation: started.');

if nargin == 0 %the function was called without arguments, ITE_code_dir is set to the current directory
    ITE_code_dir = pwd;
end

%query for the current working environment (Matlab/Octave):
  addpath(strcat(ITE_code_dir,'/shared/'));%to be able to call 'working_environment_Matlab'
  environment_Matlab = working_environment_Matlab;

%store the current working directory, cd to ITE_code_dir:
     pd = pwd; 
     cd(ITE_code_dir);
  
%delete the not necessary (depending on the working environment) ann wrappers:
    if environment_Matlab
        if exist('shared/embedded/ann_wrapperO/')
            rmdir('shared/embedded/ann_wrapperO/','s');%delete 'ann_wrapperO' with subfolders.    
            disp('We are working in Matlab environment => ann_wrapper for Octave: deleted.');%the 'ann' file may otherwise cause conflicts
        else
            disp('Directory ann_wrapperO: already deleted.');
        end
    else
        if exist('shared/embedded/ann_wrapperM/')
            rmdir('shared/embedded/ann_wrapperM/','s');%delete 'ann_wrapperM' with subfolders.    
            disp('We are working in Octave environment => ann_wrapper for Matlab: deleted.');%the 'ann' file may otherwise cause conflicts
        else
            disp('Directory ann_wrapperM: already deleted.');
        end
    end
     
%download and extract the ARfit package to '/shared/downloaded/ARfit' (and create the directory), if needed:     
    if download_ARfit %create 'shared/downloaded/ARfit', if needed; download arfit.zip, extract, delete .zip:
        if ~exist('shared/downloaded/ARfit')
            %mkdir:
                %mkdir('shared/downloaded/ARfit');%Matlab
                if ~mkdir('shared/downloaded') || ~mkdir('shared/downloaded/ARfit');%Octave
                    error('Making directory for ARfit: failed.');
                end
            %download arfit.zip, extract, delete .zip:
                disp('ARfit package: downloading, extraction: started.');
                    %[FN,status] = urlwrite('http://www.gps.caltech.edu/~tapio/arfit/arfit.zip','arfit.zip');
                    %[FN,status] = urlwrite('http://clidyn.ethz.ch/arfit/arfit.zip','arfit.zip'); %new ARfit url
                    [FN,status] = urlwrite('https://clidyn.ethz.ch/arfit/arfit.zip','arfit.zip'); %new2 ARfit url
                    if status %downloading: successful
                        unzip(FN,strcat(ITE_code_dir, '/shared/downloaded/ARfit'));
                        delete(FN);%delete the .zip file    
                    else
                        error('The ARfit package could not been downloaded.');
                    end    
                disp('ARfit package: downloading, extraction: ready.');   
        else
            disp('Directory ARfit: already exists.');
        end
    end
    
%download and extract the AOD: MISR1 dataset to '/shared/downloaded/MISR1' (and create the directory), if needed:  
    if download_MISR1 %create 'shared/downloaded/MISR1', if needed; download MIR_datasets.zip, extract, delete .zip:
        if ~exist('shared/downloaded/MISR1')
            %mkdir:
                if ~exist('shared/downloaded')
                    if ~mkdir('shared/downloaded')
                        error('Making directory for AOD:MISR1 failed.');
                    end
                end
                if ~mkdir('shared/downloaded/MISR1');
                    error('Making directory for AOD:MISR1 failed.');
                end
            %download MIR_datasets.zip, extract, delete .zip:
                disp('AOD:MISR1 dataset: downloading, extraction: started.');
                    [FN,status] = urlwrite('http://www.dabi.temple.edu/~vucetic/data/MIR_datasets.zip','MIR_datasets.zip');
                    if status %downloading: successful
                        unzip(FN,strcat(ITE_code_dir, '/shared/downloaded/MISR1'));
                        delete(FN);%delete the .zip file    
                        %delete the not used parts ('MISR1.mat', 'README.txt'):
                            delete(strcat(ITE_code_dir, '/shared/downloaded/MISR1/MIR datasets/Gaussian.m'));
                            delete(strcat(ITE_code_dir, '/shared/downloaded/MISR1/MIR datasets/MISR2.mat'));
                            delete(strcat(ITE_code_dir, '/shared/downloaded/MISR1/MIR datasets/MODIS.mat'));
                            delete(strcat(ITE_code_dir, '/shared/downloaded/MISR1/MIR datasets/Outlier1.m'));
                            delete(strcat(ITE_code_dir, '/shared/downloaded/MISR1/MIR datasets/Outlier2.m'));
                        %copy 'MISR1.mat' and 'README.txt' to '/MISR1', delete 'MIR datasets/':
                            movefile(strcat(ITE_code_dir, '/shared/downloaded/MISR1/MIR datasets/MISR1.mat'),strcat(ITE_code_dir, '/shared/downloaded/MISR1/'));
                            movefile(strcat(ITE_code_dir, '/shared/downloaded/MISR1/MIR datasets/README.txt'),strcat(ITE_code_dir, '/shared/downloaded/MISR1/'));
                            rmdir(strcat(ITE_code_dir, '/shared/downloaded/MISR1/MIR datasets/'));
                    else
                        error('The AOD:MISR1 dataset could not been downloaded.');
                    end    
                disp('AOD:MISR1 dataset: downloading, extraction: ready.');   
        else
            disp('Directory MISR1: already exists.');
        end
    end
    
%add 'ITE_code_dir' with its subfolders to the Matlab PATH:
    addpath(genpath(ITE_code_dir));
    if environment_Matlab
        env = 'Matlab';
    else
        env = 'Octave';
    end
    disp(strcat(['ITE code directory: added with subfolders to the ',env,' PATH.']));
 
       
if environment_Matlab
    %compile 'ANN':
        if compile_ANN%if needed
			cd(strcat(ITE_code_dir,'/shared/embedded/ann_wrapperM')); %cd 'ann_wrapperM'
            disp('ANN compilation: started.');
            ann_class_compile; %compile the ANN package
            disp('ANN compilation: ready.'); %Note: it is sufficient if 'mex_w_times_x_symmetric.cpp', 'sparsifyc.cpp' and 'spmtimesd.cpp' compile successfully.
        end	
    %compile 'NCut':
        if compile_NCut%if needed
			cd(strcat(ITE_code_dir,'/shared/embedded/NCut')); %cd 'NCut'
            disp('NCut compilation: started.');
            compileDir_simple;%compile the NCut package
            disp('NCut compilation: ready.');
        end    
else
	if delete_Ncut_in_Octave
		rmdir(strcat(ITE_code_dir,'/shared/embedded/NCut/'),'s');%delete NCut with subfolders.
		disp('We are working in Octave environment => NCut: deleted.');
    end
end

%compile TCA (chol_gauss.c), if needed:	
	if compile_TCA
        cd(strcat(ITE_code_dir,'/shared/embedded/TCA'));%cd 'TCA'
        disp('TCA (chol_gauss.c) compilation: started.');
        mex chol_gauss.c;
        disp('TCA (chol_gauss.c) compilation: ready.');
    end
    
%compile SWICA (SW_kappa.cpp, SW_sigma.cpp), if needed:    
    if compile_SWICA
        cd(strcat(ITE_code_dir,'/shared/embedded/SWICA'));%cd 'SWICA'
        disp('SWICA (SW_kappa.cpp, SW_sigma.cpp) compilation: started.');
        build_SWICA;
        disp('SWICA (SW_kappa.cpp, SW_sigma.cpp) compilation: ready.');
    end

%compile 'Hoeffding_term1.cpp', if needed:
    if compile_Hoeffding_term1    
        cd(strcat(ITE_code_dir,'/estimators/utilities'));
        disp('Hoeffding_term1.cpp compilation: started.');
        mex Hoeffding_term1.cpp;
        disp('Hoeffding_term1.cpp compilation: ready.');
    end

%compile 'Edgeworth_t1_t2_t3.cpp', if needed:
    if compile_Edgeworth_t1_t2_t3
        cd(strcat(ITE_code_dir,'/estimators/utilities'));
        disp('Edgeworth_t1_t2_t3.cpp compilation: started.');
        mex Edgeworth_t1_t2_t3.cpp;
        disp('Edgeworth_t1_t2_t3.cpp compilation: ready.');
    end

%compile 'compute_CDSS.cpp', if needed:
    if compile_CDSS
        cd(strcat(ITE_code_dir,'/estimators/utilities'));
        disp('compute_CDSS.cpp compilation: started.');
        mex compute_CDSS.cpp;
        disp('compute_CDSS.cpp compilation: ready.');
    end

%compile 'knn', if needed:
    if compile_knn%if needed
        cd(strcat(ITE_code_dir,'/shared/embedded/knn'));
        disp('knn (top.cpp) compilation: started.');
        build; %compile the knn package (top.cpp)
        disp('knn (top.cpp) compilation: ready.');
    end
    
%compile 'KDP', if needed:    
    if compile_KDP	%if needed
        cd(strcat(ITE_code_dir,'/shared/embedded/KDP/mat_oct'));
        disp('KDP (kdpee.c, kdpeemex.c) compilation: started.');
        mexme;
        disp('KDP (kdpee.c, kdpeemex.c) compilation: ready.');        
    end
    
%compile adaptive partitioning based MI estimation:
    if compile_MI_AP %if needed   
        cd(strcat(ITE_code_dir,'/shared/embedded/MI_AP'));
        disp('Adaptive partitioning based MI (mutin.cpp) compilation: started.');
        mex mutin.cpp;
        disp('Adaptive partitioning based MI (mutin.cpp) compilation: ready.');
    end
    
%change back to the stored working directory:
    cd(pd);
    
%quick installation tests:
    disp('-------------------');
    disp('Installation tests:');
    if environment_Matlab
	    %ANN:
			if compile_ANN%verify only in case of compilation
				Y = rand(3,100); Q = rand(3,200); k = 3; epsi = 0.1;
				ann_object = ann(Y);
				[indices, squared_distances] = ksearch(ann_object, Q, k, epsi,0);%'0': nearest neighbors do not include the points
				disp('ANN quick installation test: successful.');
			end
		%NCut:			
			if compile_NCut%verify only in case of compilation
				A = rand(10); A = A + A.'; num_of_comps = 2;
				ClusterIndicators = ncutW(A,num_of_comps);
				disp('NCut quick installation test: successful.');
			end
    end        
    %ARfit:
		if download_ARfit %verify only in case of downloading
			d = 2; A = 0.9 * random_orthogonal(d); T = 1000;   Ls = 1;
			v = arsim(zeros(d,1),A,eye(d),T);
			[w_temp, Fx_hat, C, SBCs] = arfit(v, Ls, Ls);
			disp('ARfit quick installation test: successful.');
		end
	%knn:
		if compile_knn%verify only in case of compilation
			Y = rand(3,100); Q = rand(3,200);
			knn(Q,Y,1);
			disp('knn quick installation test: successful.');
        end
    %KDP:
        if compile_KDP %verify only in case of compilation
            Y = rand(1,1000);
            kdpee(Y.');
            disp('KDP quick installation test: successful.');
        end
    %adaptive partitioning based MI estimation:
        if compile_MI_AP %verify only in case of compilation
            Y = rand(2,1000);
            mutin(Y.');
            disp('Adaptive partitioning based MI quick installation test: successful.');
        end