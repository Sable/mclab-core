function [theta, din, labtheta] = garc2thd(t1,d1,t2,d2, lab1, lab2)
% garc2thd - Converts a simple model with GARCH structure in the error term
% to the THD format.
%    [theta, din, lab] = garc2thd(t1,d1,t2,d2,lab1,lab2)
% t1,d1 > THD representation of the mean model (VARMAX, ESTR or TF).
% t2,d2 > THD representation of the variance model (VARMAX).
% theta, din < THD representation of the whole model.
%
% 29/4/97

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

if nargin < 4, e4error(3); end
if nargin < 6, lab1 = []; lab2 = []; end

[H_D, type, m, r, s, n, np, userflag, userf, innov, szpriv] = e4gthead(d1);
[H_D, typeg, mg, rg, sg, ng, npg, userflagg, userfg, innovg] = e4gthead(d2);

if ~innov(1) | ~innovg(1), e4error(12); end
if mg ~= m
   if mg ~= (m+1)*m/2, e4error(12); end
   gtype = 0;
else
   gtype = 1;
end

[t2,d2,lab2] = e4trunc(t2,d2,lab2);
if nargin == 7 & nargout > 2, lab2 = [ones(size(lab2,1),1)*'g' lab2]; end
[theta, din, labtheta] = stackthd(t1,d1,t2,d2,lab1,lab2);
din0 = e4sthead(100+gtype, m, r+rg, s, n, size(theta,1), 0, [], innov, size(din,1));
din = [din0;din];