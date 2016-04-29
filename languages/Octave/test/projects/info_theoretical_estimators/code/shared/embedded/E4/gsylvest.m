function [X,Y, err]  = gsylvest(A1, A2, B1, B2, C1, C2)
% gsylvest  - Solves the generalized Sylvester equation
%     A1*X + Y*B1 = C1
%     A2*X + Y*B2 = C2
%
%     [X,Y, err]  = gsylvest(A1, A2, B1, B2, C1, C2)
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

prog = E4OPTION(18); % octave or matlab
ver_prog = E4OPTION(19); % octave or matlab

zeps = eps*1000;

[A1, A2, Ua, Za] = qz(A1, A2);
[B1, B2, Ub, Zb] = qz(B1',B2');
if prog & ver_prog < 3.4
   % In matlab AA = Q*A*Z, but in octave (below ver 3.4.X) de qz works: AA = Q'*A*Z
   % Then we transpose the matrices Ua and Ub
   Ua = Ua';
   Ub = Ub';
end   
B1 = B1';
B2 = B2';
C1 = Ua*C1*Ub';
C2 = Ua*C2*Ub';

m = size(A1,1);
n = size(B1,2);
err = 0;

X = zeros(m,n);
Y = X;

if prog  % octave only supports real qz decomposition
   j = n;
   while j > 0
     if j == 1 
        j0 = 1;
     elseif B1(j-1,j) ~= 0 | B2(j-1,j) ~= 0
        j0 = j-1;
     else
        j0 = j;
     end
     nj = j-j0+1;
     Ij = eye(nj);
     
     i = m;
     while i > 0
        if i == 1 
           i0 = 1;
        elseif A1(i,i-1) ~= 0 | A2(i,i-1) ~= 0
           i0 = i-1;
        else
           i0 = i;
        end
        
        ni = i-i0+1;
        Ii = eye(ni);
        nvar = ni*nj;
   
        if nvar == 1 % scalar case
           D =  [A1(i,i) B1(j,j);A2(i,i) B2(j,j)];
           suma = [C1(i,j) - A1(i,i:m) * X(i:m,j) - Y(i,j:n)*B1(j:n,j);...
                  C2(i,j) - A2(i,i:m) * X(i:m,j) - Y(i,j:n)*B2(j:n,j)];
           if (sum(abs(suma)) < zeps)
              r = zeros(2,1);
           elseif (rank(D) < 2)
              if nargout < 3
                 e4error(19);
              else
                 err = 1;
                 return;
              end
           else         
              r = D \ suma;
           end
           X(i,j) = r(1);
           Y(i,j) = r(2);
        else
           C11 = C1(i0:i,j0:j) - A1(i0:i,i0:m) * X(i0:m,j0:j) - Y(i0:i,j0:n)*B1(j0:n,j0:j);
           C22 = C2(i0:i,j0:j) - A2(i0:i,i0:m) * X(i0:m,j0:j) - Y(i0:i,j0:n)*B2(j0:n,j0:j);
           D = [kron(Ij,A1(i0:i,i0:i)) kron(B1(j0:j,j0:j)',Ii);...
                kron(Ij,A2(i0:i,i0:i)) kron(B2(j0:j,j0:j)',Ii)];
            
           if (rank(D) < size(D,1))
              if nargout < 3
                 e4error(19);
              else
                 err = 1;
                 return;
              end
           else         
              r = D \ [C11(:);C22(:)];
           end
           X(i0:i,j0:j) = reshape(r(1:nvar),ni, nj);
           Y(i0:i,j0:j) = reshape(r(nvar+1:2*nvar),ni, nj);
        end             
        i = i0-1;
     %
     end
     j = j0-1;
  %
  end  
else    
  for j = n:-1:1
     for i = m:-1:1
     %
        D =  [A1(i,i) B1(j,j);A2(i,i) B2(j,j)];
        suma = [C1(i,j) - A1(i,i:m) * X(i:m,j) - Y(i,j:n)*B1(j:n,j);...
               C2(i,j) - A2(i,i:m) * X(i:m,j) - Y(i,j:n)*B2(j:n,j)];
        if (sum(abs(suma)) < zeps)
           r = zeros(2,1);
        elseif (rank(D) < 2)
           if nargout < 3
              e4error(19);
           else
              err = 1;
              return;
           end
        else         
           r = D \ suma;
        end
        X(i,j) = r(1);
        Y(i,j) = r(2);
     %
     end
  %
  end
end

X = Za * X * Ub;
Y = Ua' * Y * Zb';
