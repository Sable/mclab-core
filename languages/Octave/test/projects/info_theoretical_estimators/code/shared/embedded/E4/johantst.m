function [joht,lambda, A, B, C, S] = johantst(y, p, tits);
% johantst   - Computes de Johanssen cointegration test.
%           [joht] = johantst(y, p, trend);
% For each series y(t) in y matrix following vector autoregressions
% are computed by OLS (Hamilton, 19XX, pp. 529):
%    y(t) = rho*y(t-1) + alpha + b(1)*z(t-1) + ... + b(p-1)*z(t-p+1) + u(t)
% and
%    z(t) = rho*y(t-1) + alpha + b(1)*z(t-1) + ... + b(p-1)*z(t-p+1) + v(t)
% where z(t) = y(t) - (yt-1).
% The resulting statistics are:
%  - 
%  - 
%  - 
%
% If an output argument is specied, no display of results is given.
%

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

if nargin < 1, e4error(7); end
if any(size(y,1) <= 0), e4error(7); end
if any(any(isnan(y))), e4error(16); end

[n, m] = size(y);
if nargin == 1
   p = fix(sqrt(n));
end
if p <= 0, e4warn(2); p = fix(sqrt(n)); end
if p > round(n/3), e4warn(2); p = fix(sqrt(n)); end

yt    = y(p+1:n,:);
yt1   = y(p:n-1,:);
dy    = transdif(y, 1, 1);
if p > 1
   [dyl, dyc] = lagser(dy, 1:p-1);
else
   dyc = dy;
   dyl = [];
end
X     = [ones(size(dyc,1),1) dyl];
%  [yt X]
beta1  = X\yt1;
u      = yt1 - X*beta1;
beta2  = X\dyc;
v      = dyc - X*beta2;

[U S V] = svd((v'*v)^(-1/2)*(v'*u)*(u'*u)^(-1/2));
diag(S^2)
A = (v'*v)^(1/2)*U*S
B = V'*(u'*u)^(-1/2)
%eig(A(:,1)*B(1,:)+eye(2))
%plot([v(:,2) u*B(1,:)'*A(2,1) v(:,2)-u*B(1,:)'*A(2,1)])
S = v-u*B(1,:)'*A(:,1)';
S = S'*S/n;
C = X \ (dyc - yt1*B(1,:)'*A(:,1)');

Suu = (u'*u)/n;
Svv = (v'*v)/n;
Suv = (u'*v)/n;

M = inv(Svv)*Suv'*inv(Suu)*Suv;
[eigvec, eigval] = eig(M);
%[diag(eigval)'; eigvec]
[eigval, indx] = sort(diag(eigval));
lambda  = flipud(eigval);
eigvec  = fliplr(eigvec(:,indx'));
% [lambda'; eigvec]
% eigvec
% eigvec'*Svv*eigvec
% diag(eigvec'*Svv*eigvec)
nd       = sqrt(diag(eigvec'*Svv*eigvec));
neigvec  = eigvec./(ones(m,1)*nd');
neigvec1 = neigvec./(ones(m,1)*neigvec(1,:));
%neigvec
%neigvec1


joht  = log(1-lambda); % H0: h=i, H1: h=i-1
joht1 = -n*cumsum(joht);     % H0: h=0, H1: h=i
joht2 = -n*flipud(cumsum(flipud(joht)));  % H0: h=i, H1: h=m

joht = -n*joht;

if ~nargout
    disp(' ');
    disp('********************* Johansen cointegration test *********************');
    disp(sprintf('Johansen cointegration test, p = %3d', p ));
    disp('h: cointegration rank (h=0 no cointegration, h=m stationarity)');
    disp(sprintf('                   H0: h=0     H0: h=i-1     H0: h=i-1'));
    disp(sprintf('   i   eigenval    H1: h=i     H1: h=i       H1: h=m'));

    for i=1:m
        disp(sprintf('  %2d   %8.4f    %8.4f    %8.4f    %8.4f ', ...
            i, lambda(i), joht1(i), joht(i), joht2(i) ));
    end
    disp(' ');
    disp('Normalized cointegration vector estimates');
    for i=1:m
        disp(sprintf('%8.4f ',neigvec1(i,:)));
    end
    disp('***********************************************************************');
    disp(' ');
end