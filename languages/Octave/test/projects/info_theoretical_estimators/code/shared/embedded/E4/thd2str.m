function [F, A, V, G] = thd2str(theta, din)
% thd2str - Converts a THD model to STRUCTURAL notation.
%    [F, A, V, G] = thd2str(theta, din)
% For the model:
%   F(B)y(t) = G(B)u(t) + A(B)e(t)
% this function returns the matrices:
%   F = [F0 F1 F2 ... Fk], A = [A0 A1 A2 ... Ak],
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
if type ~= 2, e4error(14); end
p = din(H_D+2); P = din(H_D+3); q = din(H_D+4); Q = din(H_D+5); g = din(H_D+6);

k = n/m;
F  = zeros(m, (1+k)*m);
A  = zeros(m, (1+k)*m);
G  = zeros(m, (1+k)*r);
if r, G(:,1:r*(g+1)) = G0; end

if isempty(FS), FS = eye(m); P = 0; end
if isempty(FR), FR = eye(m); p = 0; end

for i=0:P
    for j=0:p
      F(:,(i*s+j)*m+1:(i*s+j+1)*m) = F(:,(i*s+j)*m+1:(i*s+j+1)*m) + FR(:,j*m+1:(j+1)*m)*FS(:,i*m+1:(i+1)*m);
    end
end

if isempty(AS), AS = eye(m); Q = 0; end
if isempty(AR), AR = eye(m); q = 0; end

for i=0:Q
    for j=0:q
      A(:,(i*s+j)*m+1:(i*s+j+1)*m) = A(:,(i*s+j)*m+1:(i*s+j+1)*m) + AR(:,j*m+1:(j+1)*m)*AS(:,i*m+1:(i+1)*m);
    end
end


% Force V positive definite
if ~E4OPTION(5)
   if min(eig(V)) <= 0
      Vu = cholp(V);
      V  = Vu'*Vu;
   end
else
   V = V*V';  
end
