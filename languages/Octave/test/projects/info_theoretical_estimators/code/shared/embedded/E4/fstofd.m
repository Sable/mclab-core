function [a] = fstofd(xpls,index,fcn,fpls,sx,rnoise,icase,P1,P2,P3,P4,P5,P6,P7,P8)
% FSTOFD   - Computes the numerical gradient of FCN by forward differences.
%    a = fstofd(xpls, index, fcn, fpls, sx, rnoise, icase, P1,P2,P3,P4,P5,P6,P7,P8)
% This function computes:
% - The gradient of FCN if there is no analytical gradient (icase = 1).
% - The hessian of FCN if there is no analytical hessian but there is
%   an analytical gradient (icase = 3).
%
% 11/3/97
% Copyright (c) Jaime Terceiro, 1997
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

% Internal use only.

parms = getparms(nargin-7);
n     = size(xpls(index),1);
m     = 1; if icase == 3; m = n; end
a     = zeros(n,m);

stepsz = sqrt(rnoise).*max(abs(xpls(index,1)), 1.0./sx);
for j=1:n
   xtmpj    = xpls(index(j),1);
   xpls(index(j),1) = xtmpj + stepsz(j);   
   fhat     = eval([fcn '(xpls' parms ')']);
   xpls(index(j),1) = xtmpj;
   a(j,1:m) = (fhat(:) - fpls(:))'/stepsz(j);
end

if (icase == 3) & (n ~= 1), a = (a + a')/2; end