function [Phim, Bm, Hm, T, nc] = e4minreal(Phi, B, H, tol)
% e4minreal - Minimal realization
%   [Phim, Bm, Hm, T, nc] = e4minreal(Phi, B, H, tol)
%
%   Phi, B, H > SS realitation
%   tol > tolerance (optional)
%   Phim, Bm, Hm < Minimal realization (controllable and observable)
%   T < transformation matrix
%   nc < number or controllable states. The difference between nc and
%   size(Phi,1) is the number of controllable but non observable states.
%
% 10/01/12

% Copyright (C) 2004 José Casals, Miguel Jerez, Sonia Sotoca
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


if nargin < 3, e4error(3); end
if nargin < 4, tol = []; end

[Phim, Bm, Hm, T, k, nc] = e4ctrbf(Phi,B,H,tol,1);
[Phim, Hm, Bm, T2] = e4ctrbf(Phim',Hm',Bm',tol,1);
Phim = Phim';
Hm = Hm';
Bm = Bm';
T(1:nc,:) = T2*T(1:nc,:);
