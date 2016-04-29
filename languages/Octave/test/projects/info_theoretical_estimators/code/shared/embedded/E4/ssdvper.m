function [dPhi,dGam,dE,dH,dD,dQ] = ssdvper(theta, din, i)
% ssdvper  - Computes the partial derivatives of the matrices of a periodic
%    model with respect to the i-th parameter in theta.
%    [dPhi, dGam, dE, dH, dD, dQ] = ssdvper(theta, din, i)
% theta > parameter vector.
% din   > matrix which stores a description of the model dynamics.
% i     > index of the parameter in the denominator of the partial derivative.
%
% 5/3/97

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
   if userflag < 2, e4error(36); end
   [dPhi,dGam,dE,dH,dD,dQ] = ss_dvown(theta,din,i,userf(2,:));
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

for l=1:nmod
    [H_D, type, m, r, ss, n, np, userflag, userf, isinnov, szpriv] = e4gthead(din(ptr:size(din,1)));

    if i >= ptr2 & i < ptr2+np
       [F(j(l):j(l)+m-1,1:(k(l)+1)*m), dF, A(j(l):j(l)+m-1,1:(k(l)+1)*m), dA, V(j(l):j(l)+m-1,:), dV, ...
         G(j(l):j(l)+m-1,1:(k(l)+1)*r), dG] = fr_dv(theta(ptr2:ptr2+np-1,1), din(ptr:ptr+H_D+szpriv(1)-1), i-ptr2+1);
       iA0(j(l):j(l)+m-1,:) = pinv(A(j(l):j(l)+m-1,1:m));
       diA0 = -iA0(j(l):j(l)+m-1,:)*dA(:,1:m)*iA0(j(l):j(l)+m-1,:);
       im = l;
    else
       [F(j(l):j(l)+m-1,1:(k(l)+1)*m), A(j(l):j(l)+m-1,1:(k(l)+1)*m), V(j(l):j(l)+m-1,:), G(j(l):j(l)+m-1,1:(k(l)+1)*r)] = thd2fr(theta(ptr2:ptr2+np-1,1), din(ptr:ptr+H_D+szpriv(1)-1));
       iA0(j(l):j(l)+m-1,:) = pinv(A(j(l):j(l)+m-1,1:m));
    end
    ptr = ptr + H_D + szpriv(1);
    ptr2 = ptr2 + np;
end

k(~k) = ones(sum(~k),1);
ksum = sum(k(asg(1:s)));

Phi = zeros(ksum*m,m);
E   = zeros(ksum*m,m);

dPhi = zeros(ksum*m,kmax*m);
dE   = zeros(ksum*m,m);
dQ   = zeros(m*s,m);
dC   = zeros(m*s,m);

if r
   Gam = zeros(ksum*m,r);
   D   = zeros(m*s,r);
   dGam = zeros(ksum*m,r);
   dD   = zeros(m*s,r);
else
   dGam = [];
   dD = [];
end

kr = 0; kr2 = 0;
kc = m;
kn = zeros(s,1);
Im = eye(m);

idm = find(asg == im);

for i=1:s
    for h=1:kmax
        idx = rem(i+h-1,s)+1;
        if h <= k(asg(idx))
        %
           kr = kr+m;
           Phi(kr-m+1:kr,:) = -F(j(asg(idx)):j(asg(idx))+m-1,h*m+1:(h+1)*m);
           E(kr-m+1:kr,:) =  A(j(asg(idx)):j(asg(idx))+m-1,h*m+1:(h+1)*m);
           if r
              Gam(kr-m+1:kr,:) =  G(j(asg(idx)):j(asg(idx))+m-1,h*r+1:(h+1)*r);
           end

           if asg(idx) == im
              dPhi(kr-m+1:kr,1:m) = -dF(:,h*m+1:(h+1)*m);
              dE(kr-m+1:kr,:)     =  dA(:,h*m+1:(h+1)*m);
              if r
                 dGam(kr-m+1:kr,:) = dG(:,h*r+1:(h+1)*r);
              end
           end
        end
    end
    kn(i) = kr - kr2;
    if asg(i) == im
       dE(kr2+1:kr,:) = dE(kr2+1:kr,:)*iA0(j(asg(i)):j(asg(i))+m-1,1:m) + E(kr2+1:kr,:)*diA0 + dPhi(kr2+1:kr,1:m);
       if r
          dD(j(i):j(i)+m-1,:) = dG(:,1:r);
          dGam(kr2+1:kr,:) = dGam(kr2+1:kr,:)+dPhi(kr2+1:kr,1:m)*G(j(asg(i)):j(asg(i))+m-1,1:r)+Phi(kr2+1:kr,:)*dG(:,1:r);
       end
       dQ1 = dA(:,1:m)*V(j(asg(i)):j(asg(i))+m-1,:)*A(j(asg(i)):j(asg(i))+m-1,1:m)';
       dQ(j(i):j(i)+m-1,:) = dQ1 + dQ1' + A(j(asg(i)):j(asg(i))+m-1,1:m)*dV*A(j(asg(i)):j(asg(i))+m-1,1:m)';
    else
       dE(kr2+1:kr,:) = dE(kr2+1:kr,:)*iA0(j(asg(i)):j(asg(i))+m-1,1:m) + dPhi(kr2+1:kr,1:m);
       if r
          dGam(kr2+1:kr,:) = dGam(kr2+1:kr,:)+dPhi(kr2+1:kr,1:m)*G(j(asg(i)):j(asg(i))+m-1,1:r);
       end
    end
    kr2 = kr;
end   

dH = zeros(s*m,max(kn));
dPhi = dPhi(:,1:max(kn));