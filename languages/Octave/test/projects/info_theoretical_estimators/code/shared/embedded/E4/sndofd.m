function [a] = sndofd(xpls,index,fcn,fpls,sx,rnoise,P1,P2,P3,P4,P5,P6,P7,P8)
% SNDOFD   - Computes the numerical hessian by forward differences.
%    a = sndofd(x, index, fcn, f, sx, rnoise, P1,P2,P3,P4,P5,P6,P7,P8)
% This function computes the hessian of FCN at x if there is no analytical
% gradient.
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

ov3   = 1.0/3.0;
n     = size(xpls(index),1);
a     = zeros(n,n);
anbr  = zeros(n,1);
parms = getparms(nargin-6);

stepsz = (rnoise^ov3).*max(abs(xpls(index,1)), 1.0./sx);
for i = 1:n
   xtmpi    = xpls(index(i),1);
   xpls(index(i),1)  = xtmpi + stepsz(i);
   anbr(i)  = eval([fcn '(xpls' parms ')']);
   xpls(index(i),1)  = xtmpi;
end

for i=1:n
   xtmpi   = xpls(index(i),1);
   xpls(index(i),1) = xtmpi + 2*stepsz(i);
   fhat    = eval([fcn '(xpls' parms ')']);
   a(i,i)  = ((fpls - anbr(i))+(fhat - anbr(i)))/(stepsz(i)*stepsz(i));
   if i ~= n
      xpls(index(i),1) = xtmpi + stepsz(i);
      for j = i+1:n
          xtmpj   = xpls(index(j),1);
          xpls(index(j),1) = xtmpj + stepsz(j);
          fhat    = eval([fcn '(xpls' parms ')']);
          a(j,i)  = ((fpls - anbr(i))+(fhat - anbr(j)))/(stepsz(i)*stepsz(j));
          a(i,j)  = a(j,i);
          xpls(index(j),1) = xtmpj;
      end
      xpls(index(i),1) = xtmpi;
   else
      xpls(index(i),1) = xtmpi;
   end
end