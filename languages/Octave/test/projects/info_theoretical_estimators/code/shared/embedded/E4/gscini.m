function [dx0, Ao, Bo, Co, Do, dP0] = gscini(Phi,dPhi,Gam,dGam,E,dE,Hv,dHv,Dv,dDv, ...
                    Cv,dCv,Q,dQ,S,dS,R,dR,Hf,dHf,Df,dDf,Cf,dCf, z, MV)
% gscini   - Computes initial conditions for the gradient in a time-varying
% parameters model.
%    [dx0, Ao, Bo, Co, Do, dP0] = ...
%         gscini(Phi,dPhi,Gam,dGam,E,dE,Hv,dHv,Dv,dDv, ...
%                    Cv,dCv,Q,dQ,S,dS,R,dR,Hf,dHf,Df,dDf,Cf,dCf,z,MV)
% Phi, Gam, E, Hv, Dv, Cv, R, S, Q,
%        Hf, Df, Cf   > SS model matrices of the time-varying parameters model.
% dPhi, dGam, dE, dHv, dDv, dCv, dR, dS, dQ,
%        dHf, dDf, dCf > derivatives of the SS model matrices.
% z     > matrix of observable variables.
% MV    > MV=1 if the ML estimation of x0 and dx0 will be done during
%         the likelihood function evaluation.
% dx0   < derivative of the expectation of the initial state.
% A0    < dP0
% Bo, Co, Do  < empty
% dP0   < derivative of the covariance matrix of the initial state P0.
%
% 6/4/97

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

if nargin < 26, MV = 0; end

zeps   = E4OPTION(15);
econd  = E4OPTION(4);
scaleb = E4OPTION(2);

n = size(z,1);  l = size(Phi,1);
m = size(Hv,1); r = size(Gam, 2);

if econd == 5
   if r, econd = 2; else, econd = 3; end;
end

dx0   = zeros(l,1); x0 = zeros(l,1);
dP0   = zeros(l,l); P0 = zeros(l,l);

if econd == 2 & MV == 0  % Maximum likelihood estimator of the initial state vector
%
   Phibb0 = eye(l,l); dPhibb0 = zeros(l,l);
   EQEt = E*Q*E';
   WW   = zeros(l,l); WZ   = zeros(l,1);
   dWW = zeros(l,l); dWZ = zeros(l,1);

   for t=1:n
   %
     % Construct time varying observer
       H = [z(t,2:m+1)*Hv Hf]; C = [z(t,2:m+1)*Cv Cf];
       if r, D = [z(t,2:m+1)*Dv Df]; end
       CRCt = C*R*C'; ESCt = E*S*C';
       dH = [z(t,2:m+1)*dHv dHf];
       dC = [z(t,2:m+1)*dCv dCf];
       if r, dD = [z(t,2:m+1)*dDv dDf]; end

       if r
          z1  = z(t,1)' - H*x0 - D*z(t,m+2:m+r+1)';
          dz1 = -dH*x0 -H*dx0 - dD*z(t,m+2:m+r+1)';
       else
          z1  = z(t,1)' - H*x0;
          dz1 = -dH*x0 -H*dx0;
       end

       B1  = H*P0*H' + CRCt;
       dB1 = dH*P0*H' + H*dP0*H' + H*P0*dH' + ...
              dC*R*C' + C*dR*C' + C*R*dC';
       if scaleb, U = cholp(B1, abs(B1));
       else       U = cholp(B1);  end
       iB1=(eye(size(U))/U)/U';

       K1   = (Phi*P0*H' + ESCt)*iB1;
       dK1  = (dPhi*P0*H' + Phi*dP0*H' + Phi*P0*dH' + ...
               dE*S*C' + E*dS*C' + E*S*dC' - K1*dB1)*iB1;
       Phib   = Phi  - K1*H;
       dPhib  = dPhi - dK1*H -K1*dH;

       if r
          Gamb   = Gam  - K1*D;
          dGamb  = dGam - dK1*D - K1*dD;
          x1  = Phib*x0 + Gamb*z(t,m+2:m+r+1)' + K1*z(t,1)';
          dx1 = dPhib*x0 + Phib*dx0 + dGamb*z(t,m+2:m+r+1)' + dK1*z(t,1)';
       else
          x1  = Phi*x0 + K1*z1;
          dx1 = dPhib*x0 + Phib*dx0 + dK1*z(t,1)';
       end

       P1   = Phib*P0*Phib' + EQEt + K1*CRCt*K1' - ...
                 K1*ESCt' - ESCt*K1';
       dP1  = dPhib*P0*Phib' + Phib*dP0*Phib' + Phib*P0*dPhib' + ...
                 dE*Q*E' + E*dQ*E' + E*Q*dE' + dK1*CRCt*K1' + ...
                  K1*dC*R*C'*K1' + K1*C*dR*C'*K1' + K1*C*R*dC'*K1' + ...
                   K1*CRCt*dK1' - dK1*ESCt' - K1*dC*S'*E' - ...
                    K1*C*dS'*E' - K1*C*S'*dE' - dE*S*C'*K1' - ...
                     E*dS*C'*K1' - E*S*dC'*K1' - ESCt*dK1';

       HPhi= H*Phibb0;
       dHPhi = dH*Phibb0 + H*dPhibb0;
       dWW = dWW + dHPhi'*iB1*HPhi - HPhi'*iB1*dB1*iB1*HPhi + HPhi'*iB1*dHPhi;
       dWZ = dWZ + dHPhi'*iB1*z1 - HPhi'*iB1*dB1*iB1*z1 + HPhi'*iB1*dz1;
       WW  = WW + HPhi'*iB1*HPhi;
       WZ  = WZ + HPhi'*iB1*z1;
       dPhibb0 = dPhib*Phibb0 + Phib*dPhibb0;
       Phibb0 = Phib*Phibb0;

       P0  = P1; x0  = x1; dP0 = dP1; dx0 = dx1;
   %
   end  % t

   if any(isnan([WW WZ])) | any(isinf([WW WZ])), e4error(25); end
   iWW = pinv(WW, zeps);
   x0  = iWW*WZ;
   dx0 = iWW*(dWZ - dWW*x0);
   [ign, Ao, Bo, Co, Do, dP0] = gmodini(Phi, dPhi, Gam, dGam, E, dE, Hv, dHv, ...
                                 Dv, dDv, Cv, dCv, Q, dQ, S, dS, R, dR, z, 1);
%
else
   [dx0, Ao, Bo, Co, Do, dP0] = gmodini(Phi, dPhi, Gam, dGam, E, dE, Hv, dHv, ...
                                 Dv, dDv, Cv, dCv, Q, dQ, S, dS, R, dR, z, 1);
end
