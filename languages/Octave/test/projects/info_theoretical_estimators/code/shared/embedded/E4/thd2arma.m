function [F, A, V, G] = thd2arma(theta, din)
% thd2arma - Converts a THD model to reduced form VARMAX notation.
%    [F, A, V, G] = thd2arma(theta, din)
% For the model:
%   F(B)y(t) = G(B)u(t) + A(B)e(t)
% this function returns the matrices:
%   F = [I  F1 F2 ... Fk], A = [I A1 A2 ... Ak],
%   G = [G0 G1 G2 ... Gk], V = V(e(t))
%   k = max(p, q, ps*s, qs*s, g)
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

global E4OPTION

[FR, FS, AR, AS, V, G0] = thd2arm2(theta, din);
[H_D, type, m, r, s, n, np, userflag, userf, isinnov, szpriv] = e4gthead(din);
if type > 1, e4error(14); end
p = din(H_D+2); P = din(H_D+3); q = din(H_D+4); Q = din(H_D+5); g = din(H_D+6);

k = n/m;
F  = eye(m, (1+k)*m);
A  = eye(m, (1+k)*m);
G  = zeros(m, (1+k)*r);
if r, G(:,1:r*(g+1)) = G0; end

F(:,m+1:m*(p+1))=FR;
for i=1:P
    F(:,(i*s)*m+1:(i*s+1)*m) = F(:,(i*s)*m+1:(i*s+1)*m) + FS(:,(i-1)*m+1:i*m);
    for j=1:p
      F(:,(i*s+j)*m+1:(i*s+j+1)*m) = F(:,(i*s+j)*m+1:(i*s+j+1)*m) + FR(:,(j-1)*m+1:j*m)*FS(:,(i-1)*m+1:i*m);
    end
end

A(:,m+1:m*(q+1))=AR;
for i=1:Q
    A(:,(i*s)*m+1:(i*s+1)*m) = A(:,(i*s)*m+1:(i*s+1)*m) + AS(:,(i-1)*m+1:i*m);
    for j=1:q
      A(:,(i*s+j)*m+1:(i*s+j+1)*m) = A(:,(i*s+j)*m+1:(i*s+j+1)*m) + AR(:,(j-1)*m+1:j*m)*AS(:,(i-1)*m+1:i*m);
    end
end
