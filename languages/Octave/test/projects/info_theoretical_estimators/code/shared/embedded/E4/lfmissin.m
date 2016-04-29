function [x0,P0,iP0,nonstat] = lfmissin(Phi,Gam,E,H,D,C,Q,S,R,z,MV)
% lfmissin - Computes initial conditions for filtering with missing data.
%    [x0,P0,iP0,nonstat] = lfmissin(Phi,Gam,E,H,D,C,Q,S,R,z,MV)
% Phi, Gam, E, H, D, C, Q, S, R > SS model matrices.
% z       > matrix of observable variables.
% MV      > MV=1 if the ML estimation of x0 will be done during the
%             likelihood function evaluation.
% x0      < initial state vector.
% P0      < covariance of the initial state vector.
% iP0     < inverse of P0 (can be rank-defficient if P0 ->INF).
% nonstat < nonstat= 0 if the system is stationary, nonstat= 1 if the
%           system is partially nonstationary and nonstat= 2 if the
%           system is nonstationary.
%
% 27/3/97

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

if nargin < 11, MV = 0; end
econd = E4OPTION(4);

if econd == 5
   r = size(Gam,2);
   if r, econd = 2; else, econd = 3; end;
end

if econd == 2 & MV == 0
%
   zeps = E4OPTION(15);
   scaleb  = E4OPTION(2); 

   n = size(z,1); l = size(Phi,1);
   m = size(H,1); r = size(Gam, 2);

   Phibb0 = eye(l,l);
   P0  = zeros(l,l);
   x0s = zeros(l,1);
   H0 = H;   C0 = C;   D0 = D;
   WW  = zeros(l,l); WZ = zeros(l,1);
   EQEt=E*Q*E';

   for t=1:n
   %
     % Construct H(t)
       trueobs = find(~isnan(z(t,1:m)'));
       nobs = size(trueobs,1); nmiss = m - nobs; Ht = eye(m); 
       if nmiss & nobs, Ht = Ht(trueobs,:); end
       H = Ht*H0; C = Ht*C0; if r, D = Ht*D0; end

       if nobs
       %
          B1  = H*P0*H' + C*R*C';
          if scaleb, U = cholp(B1, abs(B1));
          else       U = cholp(B1); end
          iB1 = (eye(size(U))/U)/U';
          K1  = (Phi*P0*H' + E*S*C')*iB1;

          if r
             z1s  = z(t,trueobs)' - D*z(t,m+1:m+r)' - H*x0s;
             x0s  = Phi*x0s + Gam*z(t,m+1:m+r)' + K1*z1s;
          else
             z1s  = z(t,trueobs)' - H*x0s;
             x0s  = Phi*x0s + K1*z1s;
          end

          Phib= Phi - K1*H; 
          P0  = Phib*P0*Phib' + EQEt + K1*C*R*C'*K1' - K1*C*S'*E' - E*S*C'*K1';
          HPhi= H*Phibb0;
          WW  = WW + HPhi'*iB1*HPhi;
          WZ  = WZ + HPhi'*iB1*z1s;
       %
       else
          Phib= Phi; 
          if r, x0s  = Phi*x0s + Gam*z(t,m+1:m+r)';
          else  x0s  = Phi*x0s; end
          P0 = Phi*P0*Phi' + EQEt;
       end

       Phibb0 = Phib*Phibb0;
   %
   end
   if any(isnan([WW WZ])) | any(isinf([WW WZ])), , e4error(25); end
   x0 = pinv(WW,zeps)*WZ;
   [ign,ign,ign,ign,ign,P0,iP0, nonstat] = lfmodini(Phi,Gam,E,H0,D0,C0,Q,S,R,z,1,1);
%
else
   [x0,ign,ign,ign,ign,P0,iP0, nonstat] = lfmodini(Phi,Gam,E,H,D,C,Q,S,R,z,1,1);
end