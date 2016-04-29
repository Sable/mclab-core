function [Phi, Gam, E, H, D, C, Q, S, R] = thd2sss(theta, din)
% thd2sss  - Returns the SS matrices from a simple model in THD format.
%    [Phi, Gam, E, H, D, C, Q, S, R] = thd2sss(theta, din)
% This function does not work with composite models.
% The SS formulation is:
%    x(t+1) = Phi·x(t) + Gam·u(t) + E·w(t)
%    y(t)   = H·x(t)   + D·u(t)   + C·v(t)
%    V(w(t) v(t)) = [Q S; S' R];
% In models without exogenous variables Gam = [], D = [].
% In static models Phi = 0. If the perturbations are white noise E = 0.
%
% 7/3/97

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

global E4OPTION

[H_D, type, m, r, s, n, np, userflag, userf, isinnov, szpriv] = e4gthead(din);

if userflag
   [Phi, Gam, E, H, D, C, Q, S, R] = thd2own(theta, din, userf(1,:));
   return;
end

k = n / m;

[F, A, V, G] = thd2fr(theta, din);
if any(type == [0 1 2]) % W.N., VARMAX, ESTR
  F0 = F(1:m,1:m); A0 = A(1:m,1:m); Ab = zeros(n,m); Fb = zeros(n,m);
  for i=1:k
     Fb((i-1)*m+1:i*m,:) = F(1:m,m*i+1:m*i+m);
     Ab((i-1)*m+1:i*m,:) = A(1:m,m*i+1:m*i+m);
  end
  if r
    G0 = G(1:m,1:r); Gb = zeros(n,r);
    for i=1:k, Gb((i-1)*m+1:i*m,:) = G(1:m,r*i+1:r*i+r); end
  end
elseif type == 3 % TF
  F0 = F(1); A0 = A(1); G0 = G(1:r);
  Fb = F(2:k+1)'; Ab = A(2:k+1)'; Gb = [];
  for i=1:k, Gb = [Gb; G(r*i+1:r*i+r)]; end
else
  e4error(14);
end

H   = [ eye(m) zeros(m,m*(k-1))];
C   = A0;

if (type==0) | (k==0)
  Phi = 0;  E = zeros(1,m); H = H(:,1);
  if r, Gam = zeros(1,r); D = G0; 
  else   Gam = []; D = []; end
else
  Phi = [ -Fb eye(m*k,m*(k-1))]; 
  E   = [ Ab - Fb*A0];
  if r,  Gam = [ Gb - Fb*G0]; D = G0;
  else   Gam = []; D = []; end
end

Q = V; S = Q; R = Q;
