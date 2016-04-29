function [kn, Phi, Gam, E, H, D, Q] = thd2ssp(theta, din)
% thd2ssp  - Returns the SS matrices from periodic model in THD format.
%    [kn, Phi, Gam, E, H, D, Q] = thd2ssp(theta, din)
% The SS model formulation is:
%    x(t+1) = Phi(k)·x(t) + Gam(k)·u(t) + E(k)·w(t)
%    z(t)   = H(k)·x(t)   + D(k)·u(t)   + w(t)
%    V(w(t)) = Q(k)
% Matrices A(k), k=1..s, are stacked in A, where A can be any of the
% system matrices. kn is a (sx1) vector containing the numbers of rows
% of Phi(k), k=1..s
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

if fix(type/100)~=3, e4error(14); end

if userflag
   [kn, Phi, Gam, E, H, D, Q] = per2own(theta, din, userf(1,:));
   return;
end

nmod = din(H_D+1);
asg = din(H_D+2:H_D+1+s);
k = din(H_D+2+s:H_D+nmod+s+1);
kmax = max(max(k),1);

F = zeros(m*nmod,m*(kmax+1)); A = zeros(m*nmod,m*(kmax+1));
G = zeros(m*nmod,r*(kmax+1)); V = zeros(m*nmod,m);
iA0 = zeros(m*nmod,m);
   
ptr = H_D+szpriv(2)+1;
ptr2 = 1;
j = 1:m:s*m;

for i=1:nmod
    [H_D, type, m, r, ss, n, np, userflag, userf, isinnov, szpriv] = e4gthead(din(ptr:size(din,1)));
    [F(j(i):j(i)+m-1,1:(k(i)+1)*m), A(j(i):j(i)+m-1,1:(k(i)+1)*m), V(j(i):j(i)+m-1,:), G(j(i):j(i)+m-1,1:(k(i)+1)*r)] = thd2fr(theta(ptr2:ptr2+np-1,1), din(ptr:ptr+H_D+szpriv(1)-1));
    iA0(j(i):j(i)+m-1,:) = pinv(A(j(i):j(i)+m-1,1:m));
    ptr = ptr + H_D + szpriv(1);
    ptr2 = ptr2 + np;
end

k(~k) = ones(sum(~k),1);
ksum = sum(k(asg(1:s)));

Phi = zeros(ksum*m,kmax*m);
E   = zeros(ksum*m,m);
Q   = zeros(m*s,m);
C   = zeros(m*s,m);

if r
   Gam = zeros(ksum*m,r);
   D   = zeros(m*s,r);
else
   Gam = []; D = [];
end

kr = 0; kr2 = 0;
kc = m;
kn = zeros(s,1);
Im = eye(m);

for i=1:s
    kc = m;
    for h=1:kmax
        idx = rem(i+h-1,s)+1;
        if h <= k(asg(idx))
        %
              kr = kr+m;
              Phi(kr-m+1:kr,1:m) = -F(j(asg(idx)):j(asg(idx))+m-1,h*m+1:(h+1)*m);
              E(kr-m+1:kr,:) =  A(j(asg(idx)):j(asg(idx))+m-1,h*m+1:(h+1)*m);
              if r
                 Gam(kr-m+1:kr,:) =  G(j(asg(idx)):j(asg(idx))+m-1,h*r+1:(h+1)*r);
              end
              if h < k(asg(idx))
                 kc = kc+m;
                 Phi(kr-m+1:kr,kc-m+1:kc) = Im;
              end
        %
        end
    end
    kn(i) = kr - kr2;
    E(kr2+1:kr,:) = E(kr2+1:kr,:)*iA0(j(asg(i)):j(asg(i))+m-1,1:m)+Phi(kr2+1:kr,1:m);
    if r
       D(j(i):j(i)+m-1,:) = G(j(asg(i)):j(asg(i))+m-1,1:r);
       Gam(kr2+1:kr,:) = Gam(kr2+1:kr,:)+Phi(kr2+1:kr,1:m)*D(j(i):j(i)+m-1,:);
    end
    Q(j(i):j(i)+m-1,:) = A(j(asg(i)):j(asg(i))+m-1,1:m)*V(j(asg(i)):j(asg(i))+m-1,:)*A(j(asg(i)):j(asg(i))+m-1,1:m)';
    kr2 = kr;
end
   
H = [kron(ones(s,1),Im) zeros(s*m,max(kn)-m)];
Phi = Phi(:,1:max(kn));