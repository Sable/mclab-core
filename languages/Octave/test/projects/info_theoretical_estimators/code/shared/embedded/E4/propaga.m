function zd = propaga(Phi, Gam, H, D, x1, u)
% propaga - Internal function called by detcomp that propagates the 
% deterministic subsystem in order to get the output of this system zd,
% characterized by (Phi, Gam, H, D), given the input u and the initial
%   zd = propaga(Phi, Gam, H, D, x1, u)
%
% 7/4/2006

% Copyright (C) 2006 José Casals, Miguel Jerez, Sonia Sotoca
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


n = size(u,1);
r = size(u,2);
if isempty(Gam), Gam = zeros(size(Phi,1),r); end
if isempty(D), D = zeros(size(H,1),r); end
zd = zeros(n,size(H,1));

for i = 1:size(u,1)
    zd(i,:) = (H*x1 + D*u(i,:)')';
    x1 = Phi*x1 + Gam*u(i,:)';
end    