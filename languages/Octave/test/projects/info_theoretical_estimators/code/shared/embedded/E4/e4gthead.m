function [H_D, type, m, r, s, n, np, userflag, userf, innov, szpriv] = e4gthead(din)
% e4gthead - Gets the common header of din.
%    [H_D, type, m, r, s, n, np, userflag, userf, innov, szpriv] = e4gthead(din)
%
% din    < din of THD format
% H_D    > common head dimension
% type   > model type
% m      > number of endogenous variables
% r      > number of exogenous variables
% s      > seasonal period
% n      > number of states
% np     > number of parameters
% usflg  > flag for user models
% innov  > innov(1) flag for innovation models,
%          innovation models: innov(2) Q matrix position in din, innov(3) void
%          non innovation models: innov(2) size of Q matrix, innov(3) size of R matrix
% szpriv > szpriv(1) size of private din, szpriv(2) size of private header

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

if nargin < 1, e4error(3); end
if size(din,1) < 17, e4error(11); end
H_D = 28;
type = din(1); m = din(2); r = din(3); s = din(4); n = din(5); np = din(6); userflag = din(7); innov = din(24:26); szpriv = din(27:28);
if userflag == 1
   userf = sprintf('%c', din(8:15));
elseif userflag == 2
   userf = [sprintf('%c', din(8:15));sprintf('%c', din(16:23))];
else
   userf = [];
end
