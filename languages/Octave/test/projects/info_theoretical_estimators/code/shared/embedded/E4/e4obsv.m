function O = e4obsv(Phi,H)
%e4obsv	Build the observability matrix.
%   O = e4obsv(Phi,H)
%	
%	O = [H; H*Phi; H*Phi^2 ... H*Phi^(n-1)]
%
% 09/01/12

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

if nargin < 2, e4error(3); end
m = size(H,1);
n = size(Phi,1);
if size(H,2) ~= n, e4error(11); end
O = zeros(m*n,n);
O(1:m,:) = H;

for i=1:n-1
	O(i*m+1:(i+1)*m,:) = O((i-1)*m+1:i*m,:)*Phi;
end

