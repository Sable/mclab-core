function [F, dF, A, dA, V, dV, G, dG] = str_dv(theta,din,di)
% STR_DV  - Computes the partial derivatives of the matrices in a (ESTR) 
% structural model with respect to the i-th parameter of theta vector.
%   [F, dF, A, dA, V, dV, G, dG] = str_dv(theta, din, i)
%
% 5/3/97
% Copyright (c) Jaime Terceiro, 1997

global E4OPTION

if (di < 1) | (di > size(theta,1)), e4error(2); end

[FR, FS, AR, AS, V, G0] = thd2arm2(theta, din, 1);
dtheta = zeros(size(theta,1),1);
dtheta(di) = 1;
[dFR, dFS, dAR, dAS, dV, dG0] = thd2arm2(dtheta, din, 1);

[H_D, type, m, r, s, n, np, userflag, userf, isinnov, szpriv] = e4gthead(din);
if type ~= 2, e4error(14); end
p = din(H_D+2); P = din(H_D+3); q = din(H_D+4); Q = din(H_D+5); g = din(H_D+6);

k = n/m;
F  = zeros(m, (1+k)*m);
A  = zeros(m, (1+k)*m);
G  = zeros(m, (1+k)*r);
dF  = zeros(m, (1+k)*m);
dA  = zeros(m, (1+k)*m);
dG  = zeros(m, (1+k)*r);

if r, G(:,1:r*(g+1)) = G0; dG(:,1:r*(g+1)) = dG0; end

if isempty(FS), FS = eye(m); dFS = zeros(m); P = 0; end
if isempty(FR), FR = eye(m); dFR = zeros(m); p = 0; end

for i=0:P
    for j=0:p
      F(:,(i*s+j)*m+1:(i*s+j+1)*m) = F(:,(i*s+j)*m+1:(i*s+j+1)*m) + FR(:,j*m+1:(j+1)*m)*FS(:,i*m+1:(i+1)*m);
      dF(:,(i*s+j)*m+1:(i*s+j+1)*m) = dF(:,(i*s+j)*m+1:(i*s+j+1)*m) + dFR(:,j*m+1:(j+1)*m)*FS(:,i*m+1:(i+1)*m) + FR(:,j*m+1:(j+1)*m)*dFS(:,i*m+1:(i+1)*m);
    end
end

if isempty(AS), AS = eye(m); dAS = zeros(m); Q = 0; end
if isempty(AR), AR = eye(m); dAR = zeros(m); q = 0; end

for i=0:Q
    for j=0:q
      A(:,(i*s+j)*m+1:(i*s+j+1)*m) = A(:,(i*s+j)*m+1:(i*s+j+1)*m) + AR(:,j*m+1:(j+1)*m)*AS(:,i*m+1:(i+1)*m);
      dA(:,(i*s+j)*m+1:(i*s+j+1)*m) = dA(:,(i*s+j)*m+1:(i*s+j+1)*m) + dAR(:,j*m+1:(j+1)*m)*AS(:,i*m+1:(i+1)*m) + AR(:,j*m+1:(j+1)*m)*dAS(:,i*m+1:(i+1)*m);
    end
end

if ~E4OPTION(5)
   if min(eig(V)) <= 0
      Vu = cholp(V);
      V  = Vu'*Vu;
   end
else
    dV = dV * V' + V * dV';
    V = V*V';  
end

