function [T, U, iU, eigen]  = bkdiag(A)
% bkdiag - Computes the decomposition A = U*T*inv(U)
%    [T, U, iU, eigen]  = bkdiag(A)
% T is a block diagonal matrix
% iU = inv(U)
% eigen are the eigenvalues vector

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

zeps = 1e-4;
zeps2 = 100*eps;
[U T] = schur(A);
[Uc Tc] = rsf2csf(U, T);
l = diag(Tc);
n = size(A,1);
i = n;
iU = U';

while i >= 1
    if i > 1 
       if abs(T(i,i-1)) > zeps2, inci = 1; else inci = 0; end
    else
       inci = 0;
    end
    j = i+1;

    while j <= n
       if j < n
          if abs(T(j+1,j)) > zeps2, incj = 1; else incj = 0; end
       else
          incj = 0;
       end

       if inci > 0  | incj > 0
          [S, err] = sylvest(T(i-inci:i,i-inci:i), -T(j:j+incj,j:j+incj), -T(i-inci:i,j:j+incj));
       elseif abs(T(i,i)-T(j,j)) > zeps
           S = -T(i,j)/(T(i,i)-T(j,j)); % in the scalar case sylvest function calling is avoided for speed
           err = 0;
       else, err = 1; end
       if ~err
           U(:,j:j+incj) = U(:,i-inci:i)*S+U(:,j:j+incj);
           iU(i-inci:i,:) = iU(i-inci:i,:)-S*iU(j:j+incj,:);
           T(i-inci:i,j+incj+1:n) = T(i-inci:i,j+incj+1:n)-S*T(j:j+incj,j+incj+1:n);
           T(1:i-inci-1,j:j+incj) = T(1:i-inci-1,i-inci:i)*S+T(1:i-inci-1,j:j+incj);
           T(i-inci:i,j:j+incj) = zeros(inci+1,incj+1);
       end
       j = j + incj + 1;
    end
    i = i - inci - 1;
end
[ign,idx] = sort(-real(l));
U = U(:,idx);
T = T(idx,idx);
iU = iU(idx,:);
eigen = l(idx);