function [FN] = FN_surfs_optspars_kK(dataset,cost_name,kernel_k,kernel_K,CV,nCV,v,base_kp,base_Kp,base_rp)
%function [FN] = FN_surfs_optspars_kK(dataset,cost_name,kernel_k,kernel_K,CV,nCV,v,base_kp,base_Kp,base_rp)
%Creates filename for the validation/test surfaces and optimal parameters in case of non-linear K (kernel on the mean embeddings).

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

FN = strcat(dataset,'_surfs_optpars','_cost', cost_name,'_k', kernel_k,'_K',kernel_K,'_CV',num2str(CV),'_nCV',num2str(nCV),'_v',num2str(v),'_logkp',num2str(base_kp),'_logKp',num2str(base_Kp),'_logrp',num2str(base_rp),'.mat');

