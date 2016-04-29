function J = shitolow(th,dh, idxagg, typeagg, s)
% shitolow - Calculates the Jacobian matrix associated to the
% transformation performed by hitolow function. See hitolow for details
%   [J] = shitolow(th,dh, idxagg, typeagg, s)
%
% 22/7/04

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

[H_D, type, m, r, s0, n, np, userflag, userf, innov, szpriv] = e4gthead(dh);
if nargin < 5
   s = s0;
   if nargin < 4
      typeagg = 0;
      if nargin < 3
         idxagg = ones(m,1);           
      end
   end   
end

dif = 1e-8;
[tl,dl,ll] = hitolow(th,dh, idxagg, typeagg, s);

if size(th,2) < 2
   sth = size(th,1);    
   k = (1:sth)';
else
   k = find(~th(:,2));
   sth = size(k,1);
end   

J = zeros(size(tl,1),sth);

for i = 1:sth
    th2 = th;
    th2(k(i),1) = th2(k(i),1) + dif;
    J(:,i) = (hitolow(th2,dh, idxagg, typeagg, s) - tl)/dif;
end