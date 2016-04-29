function [Phi, Gam, E, H, D, C, Q, S, R] = thd2ssh(theta, din)
% thd2ssh  - Returns the minimal dimension SS matrices from a echelon model in THD format.
%    [Phi, Gam, E, H, D, C, Q, S, R] = thd2ssh(theta, din)
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

if type ~= 4, e4error(14); end

if userflag
   [Phi, Gam, E, H, D, C, Q, S, R] = thd2own(theta, din, userf(1,:));
   return;
end

kronecker = din(H_D+1:H_D+m);
[F, A, V, G] = thd2str(theta, din(H_D+m+1:size(din,1)));

km = (size(F,2)-m);
k = km/m;

F0 = F(1:m,1:m)+eye(m); Ab = zeros(km,m); Fb = zeros(km,m);
iF0 = inv(F0);

for i=1:k
    Fb((i-1)*m+1:i*m,:) = F(1:m,m*i+1:m*i+m);
    Ab((i-1)*m+1:i*m,:) = A(1:m,m*i+1:m*i+m);
end
if r
   G0 = G(1:m,1:r); Gb = zeros(km,r);
   for i=1:k, Gb((i-1)*m+1:i*m,:) = G(1:m,r*i+1:r*i+r); end
end

H   = [ iF0 zeros(m,km-m)];
C   = eye(m);

if n==0
   Phi = 0;  E = zeros(1,m);
   if r, Gam = zeros(1,r); D = iF0*G0; 
   else   Gam = []; D = []; end
else
  Phi = [ -Fb*iF0 eye(km,m*(k-1))]; 
  E   = [ Ab - Fb];
  if r,  D = iF0*G0; Gam = [ Gb - Fb*D];
  else   Gam = []; D = []; end
end

idx = ones(km,1);

for i=1:m
    idx(m*kronecker(i)+i:m:km) = zeros(k-kronecker(i),1);
end

idx = idx==1;

H = H(:,idx);
E = E(idx,:);
if r, Gam = Gam(idx,:); end
Phi = Phi(idx,idx);

Q = V; S = Q; R = Q;
