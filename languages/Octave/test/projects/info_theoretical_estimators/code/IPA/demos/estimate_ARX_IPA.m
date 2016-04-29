function [e_hat,de_hat,Fs_hat,Bs_hat,W_hat,Fx_hat,Bx_hat,s_hat] = estimate_ARX_IPA(Fx,Bx,Ae,Dx,Du,u_size,ICA,ISA,unknown_dimensions,de,ARX)
%function [e_hat,de_hat,Fs_hat,Bs_hat,W_hat,Fx_hat,Bx_hat,s_hat] = estimate_ARX_IPA(Fx,Bx,Ae,Dx,Du,u_size,ICA,ISA,unknown_dimensions,de,ARX)
%Estimates the ARX-IPA model. Method: ARX identification + ISA on the estimated innovation.
%
%INPUT:
%   Fx: matrix describing the AR evolution of observation x.
%   Bx: matrix describing the effect of the control on observation x.
%   Ae: driving noise of observation x, =A*e.
%   Dx: dimension of the observation.
%   Du: dimension of the control.
%   u_size: size of the control; box constraint, i.e., |u_i| <= u_size
%   ICA: solver for independent component analysis, see 'estimate_ICA.m'.
%   ISA: solver for independent subspace analysis (=clustering of the ICA elements). ISA.cost_type, ISA.cost_name, ISA.opt_type: cost type, cost name, optimization type. Example: ISA.cost_type = 'sumH', ISA.cost_name = 'Renyi_kNN_1tok', ISA.opt_type = 'greedy' means that we use an entropy sum ISA formulation ('sumH'), where the entropies are estimated Renyi entropies via kNN methods ('Renyi_kNN_1tok') and the optimization is greedy; see also 'demo_ISA.m'
%   unknown_dimensions: '0' means 'the subspace dimensions are known'; '1' means 'the number of the subspaces are known' (but the individual dimensions are unknown).
%   de: 
%       1)in case of 'unknown_dimensions = 0': 'de' contains the subspace dimensions.
%       2)in case of 'unknown_dimensions = 1': the length of 'de' must be equal to the number of subspaces, but the coordinates of the vector can be arbitrary.
%   ARX: ARX estimator, see 'estimate_ARX.m'.
%OUTPUT:
%   e_hat: e_hat(:,t) is the estimated driving noise at time t.
%   de_hat: in case of known subspace dimensions ('unknown_dimensions = 0') de_hat = de; else it contains the estimated subspace dimensions; ordered increasingly.
%   Fs_hat: estimated matrix describing the AR evolution of source s.
%   Bs_hat: estimated matrix describing the effect of the control on source s.
%   W_hat: estimated inverse of the mixing matrix.
%   Fx_hat: estimated matrix describing the AR evolution of observation x.
%   Bx_hat: estimated matrix describing the effect of the control on observation x.
%   s_hat: estimated source.
%
%REFERENCE:
%   Zoltan Szabo and Andras Lorincz. Towards Independent Subspace Analysis in Controlled Dynamical Systems. ICA Research Network International Workshop (ICARN), pages 9-12, 2008.
%NOTE: 
%   Fx,Bx, and Ae are used for observation generation only.

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

%ARX-identification = estimate dynamics (Fx_hat, Bx_hat) and innovation (x_innov_hat):
    [x_innov_hat,Fx_hat,Bx_hat,observation] = estimate_ARX(Fx,Bx,Ae,Dx,Du,u_size,ARX);%the observation is generated online
        
%ISA on the estimation innovation:    
    [e_hat,W_hat,de_hat] = estimate_ISA(x_innov_hat,ICA,ISA,unknown_dimensions,de,size(x_innov_hat,1));    
    
%basis tranformation (x->s): Fs_hat,Bs_hat, s_hat:
    %Fs_hat,Bs_hat:
        invW_hat = inv(W_hat);
        [Fs_hat,Bs_hat] = basis_transformation_ARX(Fx_hat,Bx_hat,W_hat,invW_hat);
    s_hat = W_hat * observation;
