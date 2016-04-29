function [e_real_hat,W_real_hat,de_real_hat,e_hat] = estimate_complex_ISA(x,ICA,ISA,unknown_dimensions,de,dim_reduction)
%function [e_real_hat,W_real_hat,de_real_hat,e_hat] = estimate_complex_ISA(x,ICA,ISA,unknown_dimensions,de,dim_reduction)
%Estimates the complex ISA model. Method: complex -> real transformation, +real ISA.
%
%INPUT:
%   x: x(:,t) is the observation at time t.
%   ICA: solver for independent component analysis, see 'estimate_ICA.m'.
%   ISA: solver for independent subspace analysis (=clustering of the ICA elements). ISA.cost_type, ISA.cost_name, ISA.opt_type: cost type, cost name, optimization type. Example: ISA.cost_type = 'sumH', ISA.cost_name = 'Renyi_kNN_1tok', ISA.opt_type = 'greedy' means that we use an entropy sum ISA formulation ('sumH'), where the entropies are estimated Renyi entropies via kNN methods ('Renyi_kNN_1tok') and the optimization is greedy; see also 'demo_ISA.m'
%   unknown_dimensions: '0' means 'the subspace dimensions are known'; '1' means 'the number of the subspaces are known' (but the individual dimensions are unknown).
%   de: 
%       1)in case of 'unknown_dimensions = 0': 'de' contains the subspace dimensions.
%       2)in case of 'unknown_dimensions = 1': the length of 'de' must be equal to the number of subspaces, but the coordinates of the vector can be arbitrary.
%   dim_reduction: dim(x) = size(x,1) >= dim_reduction; if '>' holds, perform dimension reduction to dimension dim_reduction, too.
%OUTPUT: 
%   e_real_hat: e_real_hat(:,t) is the estimated (real) ISA source at time t.
%   W_real_hat: estimated (real) ISA demixing matrix.
%   de_real_hat: in case of known subspace dimensions ('unknown_dimensions = 0') de_real_hat = 2*de; else it contains the estimated (real) subspace dimensions; ordered increasingly.
%   e_hat: e_hat(:,t) is the estimated (complex) ISA source at time t.
%
%REFERENCE:
%   Zoltan Szabo and Andras Lorincz. Complex Independent Process Analysis. Acta Cybernetica 19:177-190, 2009.

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

%complex -> problem real transformation (x_real,de_real,dim_reduction_real):
    x_real = C2R_vector(x);
    de_real = 2 * de;
    dim_reduction_real = 2 * dim_reduction;
    
%ISA on the transformed data:    
    [e_real_hat,W_real_hat,de_real_hat] = estimate_ISA(x_real,ICA,ISA,unknown_dimensions,de_real,dim_reduction_real);
    
%real->complex transformation:    
    e_hat = R2C_vector(e_real_hat);
