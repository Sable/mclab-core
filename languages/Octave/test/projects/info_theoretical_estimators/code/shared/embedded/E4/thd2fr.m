function [F, A, V, G] = thd2fr(theta, din)
% thd2fr   - Converts a THD model to the reduced form of a simple model.
%    [F, A, V, G] = thd2fr(theta, din)
% This function does not work with composite models.
% The output matrices are:
% F = [I  F1 F2 ... Fk], A = [I  A1 A2 ... Ak], G = [G0 G1 G2 ... Gk],
% V = V(e(t))
% The procedure also checks that V is positive definite.
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

[H_D, type, m, r, s, n, np, userflag, userf, innov, szpriv] = e4gthead(din);
if (type<0) | (type>3), e4error(14); end
if np ~= size(theta,1), e4error(1); end
if type == 0 | type == 1
   [F, A, V, G] = thd2arma(theta, din);
elseif type == 2
   [F, A, V, G] = thd2str(theta, din);
   iF0 = inv(F(1:m,1:m));
   F   = iF0*F; F(1:m,1:m) = eye(m,m);
   A   = iF0*A; G = iF0*G; 
elseif type == 4
   [F, A, V, G] = thd2ech(theta, din);
   iF0 = inv(F(1:m,1:m));
   F   = iF0*F; F(1:m,1:m) = eye(m,m);
   A   = iF0*A; A(1:m,1:m) = eye(m,m); G = iF0*G; 
elseif type == 3
   [F, A, V, W, D] = thd2tf(theta, din);
   arp = F; 
   % phi(B)*PHI(B)*d1(B)*...*dr(B)
   for i=1:r, F = conv(F, D(i,:)); A = conv(A, D(i,:)); end
   % wbi(B) = phi(B)*PHI(B)*wi(B)*dj(B) for all j <> i
   wp = zeros(r,n+1);
   for i=1:r
     wpd = conv(arp, W(i,:));
     for j=1:r
       if i~=j
         wpd = conv(wpd, D(j,:));
       end
     end
     wp(i,1:min(size(wpd,2),n+1)) = wpd(1:min(size(wpd,2),n+1));
   end
   G = wp(:)';
   A = [A(1:min(size(A,2),n+1)) zeros(1,n-size(A,2)+1)];
   F = [F(1:min(size(F,2),n+1)) zeros(1,n-size(F,2)+1)];
else
   e4error(14);
end