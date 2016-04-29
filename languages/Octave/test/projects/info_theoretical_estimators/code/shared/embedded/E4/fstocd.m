function [g] = fstocd(x,index,fcn,sx,rnoise,P1,P2,P3,P4,P5,P6,P7,P8)
% FSTOCD   - Computes the numerical gradient of FCN by central differences.
%    g = fstocd(x, index, fcn, sx, rnoise, P1,P2,P3,P4,P5,P6,P7,P8)
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

n      = size(x,1);
rthird = rnoise^(1.0/3.0);
parms  = getparms(nargin-4);
g      = zeros(n,1);
stepsz = rthird.*max(abs(x), 1.0./sx);
for i = 1:n
    xtempi= x(index(i),1);
    x(index(i))  = xtempi + stepsz(i);
    fplus = eval([fcn '(x' parms ')']);
    x(index(i))  = xtempi - stepsz(i);
    fminus= eval([fcn '(x' parms ')']);
    x(index(i))  = xtempi;
    g(index(i))  = (fplus - fminus)/(stepsz(i)+stepsz(i));
end
