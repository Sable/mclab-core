function [dx0,Ao,Bo,Co,Do,dP0] = gmodini(Phi,dPhi,Gam,dGam,E,dE,H,dH,D,dD,C,dC,Q,dQ,S,dS,R,dR,z,MV)
% gmodini  - Computes initial conditions for the gradient.
%    [dx0, Ao, Bo, Co, Do, dP0] = gmodini(Phi, dPhi, Gam, dGam, E, dE, ...
%                            H, dH, D, dD, C, dC, Q, dQ, S, dS, R, dR, z, MV)
% Phi, Gam, E, H, D, C, Q, S, R          > SS model matrices.
% dPhi, dGam, dE, dH, dD, dC, dQ, dS, dR > derivatives of the SS model
%                           matrices with respect to one of the parameters.
% z     > matrix of observable variables.
% MV    > MV=1 if the ML estimation of x0 and dx0 will be done during
%         the likelihood function evaluation.
% dx0   < initial value of the state vector.
%          Kalman  Chandrasekhar
%  Ao   <   dP0        dB0
%  Bo   <    []        dK0
%  Co   <    []        dy0
%  Do   <    []        dM0
%  dP0  < derivative of the initial state covariance P0.
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

global E4OPTION

if nargin < 20, MV = 0; end

if E4OPTION(1) == 1, filtk = 1; else, filtk = 0; end
zeps   = E4OPTION(15);
vcond  = E4OPTION(3);
econd  = E4OPTION(4);
scaleb = E4OPTION(2);

n = size(z,1); l = size(Phi,1);
m = size(H,1); r = size(Gam, 2);

if econd == 5
   if r, econd = 2; else, econd = 3; end;
end

dx0   = zeros(l,1); x0 = zeros(l,1);
dP0   = zeros(l,l); P0 = zeros(l,l);

if econd == 2 & MV == 0   % Maximum likelihood estimator of the initial state vector
%
   Phibb0 = eye(l,l); dPhibb0 = zeros(l,l);
   CRCt = C*R*C'; EQEt = E*Q*E'; ESCt = E*S*C';
   WW   = zeros(l,l); WZ   = zeros(l,1);
   dWW = zeros(l,l); dWZ = zeros(l,1);

   if ~filtk
   %
      B0  = CRCt;
      dB0 = dC*R*C'+C*dR*C'+C*R*dC';
      if scaleb, U = cholp(B0, abs(B0));
      else       U = cholp(B0); end
      iB0 = ((eye(size(B0))/U)/U');
      y0  = E;
      dy0 = dE;
      K0 = E*S*C'*iB0;
      dCRCt = -iB0*dB0*iB0;
      dK0 = (dE*S*C' + E*dS*C' + E*S*dC')*iB0 + E*S*C'*dCRCt;
      M0 = Q - S*C'*iB0*C*S';
      dSCt = dS*C' + S*dC';
      dM0 = dQ - dSCt*iB0*C*S' - S*C'*dCRCt*C*S' - S*C'*iB0*(dSCt)';
   %
   end

   for t=1:n
   %
       if r
          z1  = z(t,1:m)' - H*x0 - D*z(t,m+1:m+r)';
          dz1 = -dH*x0 -H*dx0 - dD*z(t,m+1:m+r)';
       else
          z1  = z(t,:)' - H*x0;
          dz1 = -dH*x0 -H*dx0;
       end

       if filtk
          B1  = H*P0*H' + CRCt;
          dB1 = dH*P0*H' + H*dP0*H' + H*P0*dH' + ...
                 dC*R*C' + C*dR*C' + C*R*dC';
       end
       if scaleb, U = cholp(B1, abs(B1));
       else       U = cholp(B1);  end
       iB1=(eye(size(U))/U)/U';

       if filtk
          K1   = (Phi*P0*H' + ESCt)*iB1;
          dK1  = (dPhi*P0*H' + Phi*dP0*H' + Phi*P0*dH' + ...
                  dE*S*C' + E*dS*C' + E*S*dC' - K1*dB1)*iB1;
       else
          Hy0 = H*y0; yM0 = y0*M0;

          if t > 1
             K1   = K0 + ((Phi - K0*H)*yM0*Hy0')*iB1;
             dK1  = dK0 + (dPhi - dK0*H -K0*dH)*(yM0*Hy0')*iB1 + ...
                      (Phi - K0*H)*(dy0*M0*Hy0' + y0*dM0*Hy0' + ...
                       yM0*dy0'*H' + yM0*y0'*dH' - yM0*Hy0'*iB1*dB1)*iB1;

             y1   = (Phi -K1*H)*y0;
             dy1  = (dPhi -dK1*H -K1*dH)*y0 + (Phi -K1*H)*dy0;
             M1   = M0 + M0*Hy0'*iB0*Hy0*M0;
             dM1  = dM0 + (dM0*y0'*H' + M0*dy0'*H' + M0*y0'*dH')*iB0*Hy0*M0 +...
                     M0*Hy0'*iB0*(dH*y0*M0 + H*dy0*M0 + H*y0*dM0) - ...
                     (M0*Hy0'*iB0)*dB0*(iB0*Hy0*M0);
             B0 = B1;
             dB0 = dB1;
          end
       end

       Phib   = Phi  - K1*H;
       dPhib  = dPhi - dK1*H -K1*dH;
       if r
          Gamb   = Gam  - K1*D;
          dGamb  = dGam - dK1*D - K1*dD;
          x1  = Phib*x0 + Gamb*z(t,m+1:m+r)' + K1*z(t,1:m)';
          dx1 = dPhib*x0 + Phib*dx0 + dGamb*z(t,m+1:m+r)' + dK1*z(t,1:m)';
       else
          x1  = Phi*x0 + K1*z1;
          dx1 = dPhib*x0 + Phib*dx0 + dK1*z(t,1:m)';
       end
       if filtk
          P1   = Phib*P0*Phib' + EQEt + K1*CRCt*K1' - ...
                   K1*ESCt' - ESCt*K1';
          dP1  = dPhib*P0*Phib' + Phib*dP0*Phib' + Phib*P0*dPhib' + ...
                   dE*Q*E' + E*dQ*E' + E*Q*dE' + dK1*CRCt*K1' + ...
                    K1*dC*R*C'*K1' + K1*C*dR*C'*K1' + K1*C*R*dC'*K1' + ...
                     K1*CRCt*dK1' - dK1*ESCt' - K1*dC*S'*E' - ...
                      K1*C*dS'*E' - K1*C*S'*dE' - dE*S*C'*K1' - ...
                       E*dS*C'*K1' - E*S*dC'*K1' - ESCt*dK1';
       end

       HPhi= H*Phibb0;
       dHPhi = dH*Phibb0 + H*dPhibb0;
       dWW = dWW + dHPhi'*iB1*HPhi - HPhi'*iB1*dB1*iB1*HPhi + HPhi'*iB1*dHPhi;
       dWZ = dWZ + dHPhi'*iB1*z1 - HPhi'*iB1*dB1*iB1*z1 + HPhi'*iB1*dz1;
       WW  = WW + HPhi'*iB1*HPhi;
       WZ  = WZ + HPhi'*iB1*z1;
       dPhibb0 = dPhib*Phibb0 + Phib*dPhibb0;
       Phibb0 = Phib*Phibb0;

       if filtk
          P0  = P1; x0  = x1; dP0 = dP1; dx0 = dx1;
       else
          B1  = B0 + H*y1*M1*y1'*H';
          dB1 = dB0 + (dH*y1 + H*dy1)*M1*y1'*H' + ...
                 H*y1*(dM1*y1'*H' + M1*dy1'*H' + M1*y1'*dH');
          K0 = K1; dK0 = dK1; M0 = M1; dM0 = dM1; 
          y0 = y1; dy0 = dy1; x0 = x1; dx0 = dx1; iB0 = iB1;
       end
   %
   end  % t

   if any(isnan([WW WZ])) | any(isinf([WW WZ])), e4error(25); end
   iWW = pinv(WW,zeps);
   x0  = iWW*WZ;
   dx0 = iWW*(dWZ - dWW*x0);
% 
end

if r &  econd == 1
   u0 = mean(z(:,m+1:m+r))';
elseif r & econd == 4
   u0 = z(1,m+1:m+r)';
end

if vcond == 1
%
  [P0, nonstat] = lyapunov(Phi, E*Q*E');

  if nonstat > 0  % non stationary
  %
     if ~filtk, e4error(26); end
     e4warn(6);
     vcond = 3;  % Change of initial conditions
  %
  else
  %

     dPhib = dPhi*P0*Phi';  dQb = dE*Q*E';
     dP0   = lyapunov(Phi, dPhib +dPhib' +dQb +E*dQ*E' +dQb');

     if r & (econd == 1 | econd == 4)
        IPhi = pinv(eye(l)-Phi);
        dx0 = IPhi*(dPhi*IPhi*Gam + dGam)*u0;
     end
  %
  end
%
end

if vcond >= 3
%
  if any(any(dPhi)) | any(any(dE)) | any(any(dQ)) | any(any(dGam))
  %
     k = 0;

     % numerical difference step
     deps = 1e-8;
     
     if r & (econd == 1 | econd == 4)     
        [x0, P0] =  djccl(Phi, E*Q*E', k, Gam*u0);
        [x1, P1] =  djccl((Phi+dPhi*deps), (E+dE*deps)*(Q+dQ*deps)*(E+dE*deps)', k, (Gam+dGam*deps)*u0);
        dx0 = (x1-x0)/deps;
     else
        [ignore, P0] =  djccl(Phi, E*Q*E', k);
        [ignore, P1] =  djccl((Phi+dPhi*deps), (E+dE*deps)*(Q+dQ*deps)*(E+dE*deps)', k);
     end
     dP0 = (P1-P0)/deps;
  %
  end  
%
end

if filtk
  Ao = dP0; Bo = []; Co = []; Do = [];
else
%
  if vcond == 1
  %
     B0  = H*P0*H' + C*R*C';
     dB0 = dH*P0*H'+H*dP0*H'+H*P0*dH' +dC*R*C'+C*dR*C'+C*R*dC';
     if scaleb, U = cholp(B0, abs(B0));
     else       U = cholp(B0); end
     iB0 = ((eye(size(B0))/U)/U');
     y0  = (Phi*P0*H' + E*S*C');
     dy0 = dPhi*P0*H' + Phi*dP0*H' + Phi*P0*dH' + ...
             dE*S*C' + E*dS*C' + E*S*dC';
     K0  = y0*iB0; dK0 = (dy0 - K0*dB0)*iB0;
     M0  = -iB0;   dM0 = iB0*dB0*iB0;
  %
  else
  %
     CRCt = C*R*C';
     B0  = CRCt;
     dB0 = dC*R*C'+C*dR*C'+C*R*dC';
     if scaleb, U = cholp(B0, abs(B0));
     else       U = cholp(B0); end
     iB0 = ((eye(size(B0))/U)/U');
     y0  = E;
     dy0 = dE;
     K0 = E*S*C'*iB0;
     dCRCt = -iB0*dB0*iB0;
     dK0 = (dE*S*C' + E*dS*C' + E*S*dC')*iB0 + E*S*C'*dCRCt;
     M0 = Q - S*C'*iB0*C*S';
     dSCt = dS*C' + S*dC';
     dM0 = dQ - dSCt*iB0*C*S' - S*C'*dCRCt*C*S' - S*C'*iB0*(dSCt)';
  %
  end
  Ao = dB0; Bo = dK0; Co = dy0; Do = dM0;
end
