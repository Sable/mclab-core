function [t,d] = per2thd(t,d,asign,s)
% per2thd  - Converts a stacked model (see stackthd.m) in a periodic model
% in THD format.
%    [theta, din] = per2thd(t,d,asign,s)
% asign > (sx1) vector asigning stacked models to seasonal periods.
% t,d   > stacked models (VARMAX, ESTR or TF) in THD representation.
% s     > cicle (may be different to seasonal period)
% theta, din < THD representation of the periodic model.
%
% 18/9/97

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

if nargin < 2, e4error(3); end
[H_D, type, m, r, ss, n, np, userflag, userf, innov, szpriv] = e4gthead(d);

if type > 4, e4error(5); end

if nargin < 4
   s = ss;
   if nargin < 3, asign = 1:s; end
end
if isempty(asign), asign = 1:s; end

asign = asign(:);
if size(asign,1) ~= s, e4error(11); end
nmod = max(asign);
if nmod > s | min(asign) < 1, e4error(11); end

ptr = H_D+szpriv(1)+1;
k = zeros(nmod,1);
if type == 4, k(1) = max(din(H_D+1:H_D+m)); else, k(1) = n/m; end

for i=2:nmod
    [H_D, type, m2, r2, s2, n2, np, userflag, userf, innov, szpriv] = e4gthead(d(ptr:size(d,1)));
    if type > 4 | m2 ~= m | r2 ~= r, e4error(5); end
    if type == 4, k(i) = max(din(H_D+1:H_D+m2)); else, k(i) = n2/m2; end
    n = max(n,n2);
    ptr = ptr + H_D + szpriv(1);
end

din0 = e4sthead(300, m, r, s, n, size(t,1), 0, [], 0, [size(d,1)+s+nmod+1;s+nmod+1]);
d = [din0;nmod;asign;k;d];
