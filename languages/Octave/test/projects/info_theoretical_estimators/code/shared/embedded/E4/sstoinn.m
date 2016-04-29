function [E,Q,U,iU,P] = sstoinn(Phi, E, H, C, Q, S, R)
%
% SSTOINN  - Transforms a general SS noise structure to an innovations structure.
%        [E,Q,U,iU,P] = sstoinn(Phi, E, H, C, Q, S, R)
%
%   U = cholp(Q)
%   iU = inv(U')
%   P  < Solution of ARE (algebraic Riccati equation)
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

   scaleb  = E4OPTION(2);
   zeps    = E4OPTION(15);
   prog  = E4OPTION(18);
   ver_prog = E4OPTION(19);
   
   tol = 1e-7;
   maxiter = 200;

   l = size(Phi,1);
   m = size(H,1);
   N = C*R*C';
   iN = pinv(C*R*C');
   ESCt = E*S*C';
   EQEt = E*Q*E';
   M = EQEt - ESCt*iN*ESCt';
   A = Phi - ESCt*iN*H;
   if trace(M) < zeps & ~sum(abs(eig(A)) > 1)
      P=zeros(l);
      maxiter = 1;
   else
      [U,S,V] = svd([N;-H']);
      T = [U(m+1:l+m,m+1:l+m)'*A' zeros(l); -M   eye(l) ];
      Y = [U(m+1:l+m,m+1:l+m)' U(1:m,m+1:l+m)'*H; zeros(l) A ];

      if prog | ver_prog < 7  % octave or old matlab
         [T,Y,Q,Z] = qz(T,Y);
         [Qt, Zt, index] = e4gdiag(T, Y, l, prog);
         Z = Z*Zt;
         Z2 = Z(1:l,index);
         Z1 = Z(l+1:2*l,index);
      else              
         eval('[T,Y,Q,Z] = qz(T,Y, ''real'');'); 
         eval('[T,Y,Q,Z] = ordqz(T,Y,Q,Z,''udi'');');
         Z2 = Z(1:l,1:l);
         Z1 = Z(l+1:2*l,1:l);
      end

      P = Z1*pinv(Z2);
      
      if (rank(Z2) < l)
          e4warn(17);
      end
      P = real((P+P')/2);
   end
   
   i = 0;
   while i < maxiter
      Q = H*P*H'+N;          
      
      if scaleb, U = cholp(Q, abs(Q));
      else       U = cholp(Q); end
      iU = eye(m)/U';
      E = (Phi*P*H' + ESCt)*iU'*iU;
      i = i + 1;      
      if i < maxiter
         Phib= Phi - E*H;
         P1  = Phib*P*Phib' + EQEt + E*N*E' - E*ESCt' - ESCt*E';
         if norm(P1-P,'fro')/max(norm(P,'fro'),tol) < tol
            break
         end
         P = P1;
      end
   end

   if maxiter > 1
      if i == maxiter
         e4warn(18, maxiter);
      elseif i > 1, e4warn(19, i); end
   end

   
