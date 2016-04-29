function [e_hat,W_hat,de_hat] = estimate_uMA_IPA_LPA(x,AR,ICA,ISA,unknown_dimensions,de,De)
%function [e_hat,W_hat,de_hat] = estimate_uMA_IPA_LPA(x,AR,ICA,ISA,unknown_dimensions,de,De)
%Estimates the uMA-IPA model. Method: LPA (Linear Predictive Approximation, =AR fit) + ISA on the estimated innovation.
%
%INPUT:
%   x: x(:,t) is the observation at time t.
%   AR: AR estimator, see 'estimate_AR.m'.
%   ICA: solver for independent component analysis, see 'estimate_ICA.m'.
%   ISA: solver for independent subspace analysis (=clustering of the ICA elements). ISA.cost_type, ISA.cost_name, ISA.opt_type: cost type, cost name, optimization type. Example: ISA.cost_type = 'sumH', ISA.cost_name = 'Renyi_kNN_1tok', ISA.opt_type = 'greedy' means that we use an entropy sum ISA formulation ('sumH'), where the entropies are estimated Renyi entropies via kNN methods ('Renyi_kNN_1tok') and the optimization is greedy; see also 'demo_ISA.m'
%   unknown_dimensions: '0' means 'the subspace dimensions are known'; '1' means 'the number of the subspaces are known' (but the individual dimensions are unknown).
%   de: 
%       1)in case of 'unknown_dimensions = 0': 'de' contains the subspace dimensions.
%       2)in case of 'unknown_dimensions = 1': the length of 'de' must be equal to the number of subspaces, but the coordinates of the vector can be arbitrary.
%   De: dimension of the source (e).
%OUTPUT:
%   e_hat: e_hat(:,t) is the estimated source at time t.
%   W_hat: estimated demixing matrix.
%   de_hat: in case of known subspace dimensions ('unknown_dimensions = 0') de_hat = de; else it contains the estimated subspace dimensions; ordered increasingly.
%
%REFERENCE:
%   Zoltan Szabo, Barnabas Poczos, and Andras Lorincz. Undercomplete Blind Subspace Deconvolution via Linear Prediction. European Conference on Machine Learning (ECML), pages 740-747, 2007.

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
   
%AR fit to x:
   [x_innovation_hat,Fx_hat,SBCs] = estimate_AR(x,AR);
   
%ISA on the estimated innovation of x:
   [e_hat,W_hat,de_hat] = estimate_ISA(x_innovation_hat,ICA,ISA,unknown_dimensions,de,De);

