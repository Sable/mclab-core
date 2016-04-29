function [F, dF, A, dA, V, dV, G, dG] = fr_dv(theta, din, di)
% fr_dv    - Computes the partial derivatives of the reduced form matrices
% of any simple model with respect to the i-th parameter in theta. Also
% return the reduced form of the model.
%   [F, dF, A, dA, V, dV, G, dG] = fr_dv(theta, din, i)
% This function does not work with composite models.
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

if (type<0) | (type>3), e4error(14); end
if np ~= size(theta,1), e4error(1); end

if type == 0 | type == 1
   [F, dF, A, dA, V, dV, G, dG] = arma_dv(theta, din, di);
elseif type == 2
   [F, dF, A, dA, V, dV, G, dG] = str_dv(theta, din, di);
   iF0 = inv(F(1:m,1:m)); 
   dF0 = dF(1:m,1:m);
   diF0= -iF0*dF0*iF0;
   F   = iF0*F; F(1:m,1:m) = eye(m,m);
   dF  = diF0*F + iF0*dF;
   A   = iF0*A; dA = diF0*A + iF0*dA;
   G   = iF0*G; dG = diF0*G + iF0*dG;
elseif type == 3
   [F, dF, A, dA, V, dV, W, dW, D, dD] = tf_dv(theta, din, di);
   arp = F; 
   darp = dF;
   for i=1:r
       dF = conv(dF, D(i,:)) + conv(F, dD(i,:));
       dA = conv(dA, D(i,:)) + conv(A, dD(i,:));
       F = conv(F, D(i,:)); A = conv(A, D(i,:));
   end
   wp = zeros(r,n+1);
   dwp = zeros(r,n+1);
   for i=1:r
     wpd = conv(arp, W(i,:));
     dwpd = conv(arp, dW(i,:)) + conv(darp, W(i,:));
     for j=1:r
       if i~=j
         dwpd = conv(wpd, dD(j,:)) + conv(dwpd, D(j,:));
         wpd = conv(wpd, D(j,:));
       end
     end
     dwp(i,1:min(size(dwpd,2),n+1)) = dwpd(1:min(size(dwpd,2),n+1));
     wp(i,1:min(size(wpd,2),n+1)) = wpd(1:min(size(wpd,2),n+1));
   end
   
   G = wp(:)';
   dG = dwp(:)';

   A = [A(1:min(size(A,2),n+1)) zeros(1,n-size(A,2)+1)];
   F = [F(1:min(size(F,2),n+1)) zeros(1,n-size(F,2)+1)];
   dA = [dA(1:min(size(dA,2),n+1)) zeros(1,n-size(dA,2)+1)];
   dF = [dF(1:min(size(dF,2),n+1)) zeros(1,n-size(dF,2)+1)];
else
   e4error(14);
end
