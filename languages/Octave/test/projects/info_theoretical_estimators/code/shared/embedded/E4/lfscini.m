function [x0,Ao,Bo,Co,Do,P0, iP0, nonstat] = lfscini(Phi,Gam,E,Hv,Dv,Cv,Q,S,R,Hf,Df,Cf,z,MV)
% lfscini  - Computes initial conditions for LFSC.
%    [x0, Ao, Bo, Co, Do, P0, iP0, nonstat] = ...
%                        lfscini(Phi,Gam,E,H,D,C,Q,S,R,Hf,Df,Cf,z,MV)
% Phi, Gam, E, H, D, C, Q, S, R, Hf, Df, Cf > SS model matrices.
% z       > matrix of observable variables.
% MV      > MV=1 if the ML estimation of x0 will be done during the
%             likelihood function evaluation.
% x0      < initial state vector.
%           Kalman  Chandrasekhar
% Ao      <   P0         B0
% Bo      <   []         K0
% Co      <   []         y0
% Do      <   []         M0
% P0      < covariance of the initial state vector.
% iP0     < inverse of P0 (can be rank-defficient if P0 ->INF).
% nonstat < nonstat= 0 if the system is stationary, nonstat= 1 if the
%           system is partially nonstationary and nonstat= 2 if the
%           system is nonstationary.
%
% 2/4/97

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

if nargin < 14, MV = 0; end
econd = E4OPTION(4);

if econd == 5
   r = size(Gam,2);
   if r, econd = 2; else, econd = 3; end;
end

if econd == 2 & MV == 0
%
   zeps   = E4OPTION(15);
   scaleb = E4OPTION(2); 

   n = size(z,1); l = size(Phi,1);
   m = size(Hv,1); r = size(Gam, 2);

   Phibb0 = eye(l,l);
   P0  = zeros(l,l);
   x0s = zeros(l,1);
   WW  = zeros(l,l); WZ = zeros(l,1);
   EQEt = E*Q*E';

   for t=1:n
   %
     % Construct time varying observer
       H = [z(t,2:m+1)*Hv Hf]; C = [z(t,2:m+1)*Cv Cf];
       if r, D = [z(t,2:m+1)*Dv Df]; end
       CRCt = C*R*C'; ESCt = E*S*C';

       B1  = H*P0*H' + CRCt;
       if scaleb, U = cholp(B1, abs(B1));
       else       U = cholp(B1); end
       iB1 = (eye(size(U))/U)/U';
       K1  = (Phi*P0*H' + ESCt)*iB1;

       if r
          z1s  = z(t,1)' - D*z(t,m+2:m+r+1)' - H*x0s; 
          x0s  = Phi*x0s + Gam*z(t,m+2:m+r+1)' + K1*z1s;   
       else
          z1s  = z(t,1)' - H*x0s; 
          x0s  = Phi*x0s + K1*z1s;
       end

       Phib= Phi - K1*H; 
       P0  = Phib*P0*Phib' + EQEt + K1*CRCt*K1' - K1*ESCt' - ESCt*K1';
       HPhi= H*Phibb0;
       WW  = WW + HPhi'*iB1*HPhi;
       WZ  = WZ + HPhi'*iB1*z1s;
       Phibb0 = Phib*Phibb0;
   %
   end  % t

   if any(isnan([WW WZ])) | any(isinf([WW WZ])), e4error(25); end
   x0 = pinv(WW,zeps)*WZ;
   [ign,Ao,Bo,Co,Do,P0, iP0, nonstat] = lfmodini(Phi,Gam,E,Hv,Dv,Cv,Q,S,R,z,1,1);
%
else
   [x0,Ao,Bo,Co,Do,P0, iP0, nonstat] = lfmodini(Phi,Gam,E,Hv,Dv,Cv,Q,S,R,z,1,1);
end
