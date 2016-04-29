function [Phi, Gam, E, H, D, C, Q, S, R] = thd2ssc(theta, din)
% thd2ssc  - Returns the SS matrices from components model in THD format.
%    [Phi, Gam, E, H, D, C, Q, S, R] = thd2ssc(theta, din)
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

[H_D, type, m, r, s, n, np, userflag, userf, isinnov, szpriv] = e4gthead(din);

if fix(type/10) ~= 3, e4error(14); end

if userflag
   [Phi, Gam, E, H, D, C, Q, S, R] = thd2own(theta, din, userf(1,:));
   return;
end

n = max(n,1);

Phi = zeros(n);
if r, Gam = zeros(n,r); D = zeros(m,r); else, Gam =[]; D = []; end
H = zeros(m, n);

ptr = H_D+szpriv(2)+1;
ptr2 = 1;
sd = size(din,1);
if isinnov(1)
   q = m; v = m;
else
   q = isinnov(2); v = isinnov(3);
end

q = max(q,1);
v = max(v,1);

E = zeros(n,q); C = zeros(m,v); Q = zeros(q); R = zeros(v); S = zeros(q,v);  
nc1 = 1;
rc1 = 1;
qc1 = 1;
vc1 = 1;

while ptr < sd
      [H_D, type, m, r, s, n, np, userflag, userf, isinnov, szpriv] = e4gthead(din(ptr:sd));
      if isinnov(1)
         v = m; if n, q = m; else, q = 0; end
      else
         v = isinnov(3); if n, q = isinnov(2); else q = 0; end
      end 

      nc2 = nc1+n-1;
      rc2 = rc1+r-1;
      qc2 = qc1+q-1;
      vc2 = vc1+v-1;

      [Phi1, Gam1, E1, H1, D1, C1, Q1, S1, R1] = thd2ss(theta(ptr2:ptr2+np-1,:), din(ptr:ptr+H_D+szpriv(1)-1));

      if n
         Phi(nc1:nc2,nc1:nc2) = Phi1;
         if r, Gam(nc1:nc2,rc1:rc2) = Gam1; end
         H(:,nc1:nc2) = H1;
         if q
            E(nc1:nc2,qc1:qc2) = E1;
            Q(qc1:qc2,qc1:qc2) = Q1;
            if v, S(qc1:qc2,vc1:vc2) = S1; end
         end   
      end
      if v
         R(vc1:vc2,vc1:vc2) = R1;
         C(:,vc1:vc2) = C1;
      end
      if r, D(:,rc1:rc2) = D1; end
      nc1 = nc2+1;
      rc1 = rc2+1;
      qc1 = qc2+1;
      vc1 = vc2+1;
      ptr = ptr + H_D + szpriv(1);
      ptr2 = ptr2 + np;
end

