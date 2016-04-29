function [Phi, Gam, E, Hv, Dv, Cv, Q, S, R, Hf, Df, Cf] = sc2own(theta, din, userf);
% sc2own   - Builds a call to the function which name is specified in userf.
% This function should return the system matrices.
%    [Phi, Gam, E, Hv, Dv, Cv, Q, S, R, Hf, Df, Cf] = sc2own(theta, din, userf)
% Only for models with time-varying parameters.
%
% 3/4/97

% Copyright (C) 1997 Jaime Terceiro
% 
% This program is free software; you can redistribute it and/or modify
% it under the terms of the GNU General Public License as published by
% the Free Software Foundation; either version 2, or (at your option)
% any later version.
% 
% This program is distributed in the hope that it will be useful, but
% WITHOUT ANY WARRANTY; without even the implied warranty of
% MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
% General Public License for more details. 
% 
% You should have received a copy of the GNU General Public License
% along with this file.  If not, write to the Free Software Foundation,
% 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.

if nargin < 3, e4error(13); end

userf = deblank(userf(:)');
if ~exist(userf), e4error(18,userf); end

[Phi, Gam, E, Hv, Dv, Cv, Q, S, R, Hf, Df, Cf] = eval([userf '(theta,din)']);