% This is a collection of MATLAB scripts complementing the paper 
% "Approximate Rank-Detecting Factorization of Low-Rank Tensors."
% 
% It contains the algorithm AROFAC, which computes rank-one-components
% of degree 3 tensors, and its rank. Furthermore example data to reproduce 
% the main results of the paper are included.    
%
% For details consult the related publication:
%
% Kiraly FJ, Ziehe A. Approximate Rank-Detecting Factorization of Low-Rank Tensors. ICASSP 2013.
% http://arxiv.org/abs/1211.7369
%
% Matlab Files:
%
% arofac.m	     computes candidates for rank-one-components of a degree 3 tensor
%
% arofac_cluster.m 
%
%                     estimates rank and rank-one-components of a degree 3 tensor by
%		      applying AROFAC and additionaly applying a clustering algorithm. 
% MeanShiftCluster.m  mean shift clustering algorithm implemented by Bryan Feldman available at
%  http://www.mathworks.com/matlabcentral/fileexchange/10161-mean-shift-clustering/content/MeanShiftCluster.m
%		     (other clustering algorithms could be tried as well)
%
% toydata_simdiag.m  generate some 3-way tensor toydata based on simultaneous diagonalization of matrices
%
% demo_arofac.m      demonstrates the usage of all programs on the toydata
%
% demo_arofac_eem.m  demo on realworld example data of fluorescence spectroscopy taken from:  
%                                      http://www.models.life.ku.dk/dorrit
%                                      see also:
%                                      http://www.mathworks.com/matlabcentral/fileexchange/1088-the-n-way-toolbox/
%                                      EEM data in file  "dorrit.mat" 
%
%
% Getting started:
% 
% copy all files to a directory in your matlab path
% 
% run demo_arofac
%
% run demo_arofac_eem 
%
%
% Copyright (c) 2013, Franz J. Kiraly, Andreas Ziehe
%
% This code is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License 
% version 3, as published by the Free Software Foundation (see license.txt).
%
% This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
% without even the implied warranty of   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.