function [e_hat,W_hat,de_hat,s_hat] = estimate_fAR_IPA(x,ICA,ISA,unknown_dimensions,de,fAR)
%function [e_hat,W_hat,de_hat,s_hat] = estimate_fAR_IPA(x,ICA,ISA,unknown_dimensions,de,fAR)
%Estimates the fAR-IPA model. Method: fAR identification + ISA on the estimated innovation.
%
%INPUT:
%   x: x(:,t) is the t^th observation from the fAR model.
%   ICA: solver for independent component analysis, see 'estimate_ICA.m'.
%   ISA: solver for independent subspace analysis (=clustering of the ICA elements). ISA.cost_type, ISA.cost_name, ISA.opt_type: cost type, cost name, optimization type. Example: ISA.cost_type = 'sumH', ISA.cost_name = 'Renyi_kNN_1tok', ISA.opt_type = 'greedy' means that we use an entropy sum ISA formulation ('sumH'), where the entropies are estimated Renyi entropies via kNN methods ('Renyi_kNN_1tok') and the optimization is greedy; see also 'demo_ISA.m'
%   unknown_dimensions: '0' means 'the subspace dimensions are known'; '1' means 'the number of the subspaces are known' (but the individual dimensions are unknown).
%   de: 
%       1)in case of 'unknown_dimensions = 0': 'de' contains the subspace dimensions.
%       2)in case of 'unknown_dimensions = 1': the length of 'de' must be equal to the number of subspaces, but the coordinates of the vector can be arbitrary.
%   fAR: fAR estimator, see 'estimate_fAR.m'.
%OUTPUT:
%   e_hat: e_hat(:,t) is the estimated driving noise at time t.
%   W_hat: estimated demixing matrix.
%   de_hat: in case of known subspace dimensions ('unknown_dimensions = 0') de_hat = de; else it contains the estimated subspace dimensions; ordered increasingly.
%   s_hat: s_hat(:,t) is the estimated source at time t.
%
%REFERENCE:
%   Zoltan Szabo and Barnabas Poczos. Nonparametric Independent Process Analysis. European Signal Processing Conference (EUSIPCO), pages 1718-1722, 2011.

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

%fAR identification, estimated innovation (x_innovation_hat):
    x_innovation_hat = estimate_fAR(x,fAR);
    
%ISA on the estimated innovation:    
    [e_hat,W_hat,de_hat] = estimate_ISA(x_innovation_hat,ICA,ISA,unknown_dimensions,de,size(x_innovation_hat,1));
    
%estimated source (s_hat):    
    s_hat = W_hat * x;    
