function [X,err]  = sylvest(A, B, C)
% sylvest  - Solves the Sylvester equation A*X + X*B = C using complex Schur
% decompositions. Only for real matrices
%    X = sylvest(A, B, C)
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

zeps = eps*1000;
[Ua, A] = schur(A);
[Ua, A] = rsf2csf(Ua,A);
[Ub, B] = schur(B');
[Ub, B] = rsf2csf(Ub,B);
B = B';
C = Ua'*C*Ub;

m = size(A,1);
n = size(B,2);
err = 0;

X = zeros(m,n);

for j = n:-1:1
    for i = m:-1:1
    %
        d =  A(i,i) + B(j,j);
        suma = C(i,j) - A(i,i:m) * X(i:m,j) - X(i,j:n)*B(j:n,j);

        if (abs(suma) < zeps)
           X(i,j) = 0;
        elseif (abs(d) < zeps)
           if nargout < 2
              e4error(19);
           else
              err = 1;
              return;
           end
        else
           X(i,j) = suma / d;
        end
    %
    end
%
end

X = real(Ua * X * Ub');
