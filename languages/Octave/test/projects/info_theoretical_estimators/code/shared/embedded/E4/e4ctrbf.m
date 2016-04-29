function [Phic, Bc, Hc, T, k, no] = e4ctrbf(Phi, B, H, tol, onlyctrb)
% e4ctrbf	 Rosenbrock staircase algorithm implementation.
%   [Phic, Bc, Hc, T, k, nc] = e4ctrbf(Phi, B, H, tol, onlyctrb)
%
%	Given the SS realitation (Phi, B, H), this function returns Phic, Bc,
%	Hc such that
%
%	Phic = T*Phi*T', Bc = T*B, Hc = H*T'
%
%	where
%
%	       | Phico  Phic12 |        |Bco|
%	Phic = |-------------- | ,  Bc =|---|  ,  Hc = [Hco | Hnco].
%	       |   0    Phinco |        | 0 |
%	                                         
%	being (Phico, Bco) the controllable subsystem.
%
%  if onlyctrb is true (default value = 0) the function returns (Phico,
%  Bco, Hco) instead of (Phic, Bc, Hc).
%  tol > is the tolerance for identifying the zero singular values. If it's
%  ommited or empty then the default value is n*norm(Phi,1)*eps, following Petkov et Al.
%  k  < is the controlable modes identified at each iteration (matlab style),
%  so that sum(k) = size(Phico,1)
%  nc < sum(k)
%
% 10/01/12

% Copyright (C) 2004 José Casals, Miguel Jerez, Sonia Sotoca
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

if nargin < 3, e4error(3); end
n = size(Phi,1);    
r = size(B,2);
if nargin < 4, tol = []; end
if isempty(tol), tol = n*norm(Phi,1)*eps; end  % following Petkov et al.
if nargin < 5, onlyctrb = 0; end

T = eye(n);
Phi2 = Phi;
B2 = B;
no = 0;
k = zeros(1,n);

for i = 1:n
    [U,S,V] = svd(B2);    
    
    if min(size(S)) < 2, S = S(1,1); end
    rk = sum(diag(S) > tol);
    k(i) = rk;
    if rk == 0, break, end
    if no + rk == n, no = n; break, end
    Phib = U' * Phi2 * U;
    Phi2   = Phib(rk+1:n-no,rk+1:n-no);
    B2   = Phib(rk+1:n-no,1:rk);
    T(no+1:n,:) = U'*T(no+1:n,:);
    no = no + rk;
end
%
if onlyctrb
   Phic = T(1:no,:) * Phi * T(1:no,:)';
   Bc = T(1:no,:) * B;
   Hc = H * T(1:no,:)';
else    
   Phic = T * Phi * T';
   Bc = T * B;
   Hc = H * T';
end

   
