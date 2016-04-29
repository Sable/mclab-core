function [dx0, dP0] = gmissin(Phi,dPhi,Gam,dGam,E,dE,H,dH,D,dD,C,dC,Q,dQ,S,dS,R,dR,z,MV)
% gmissin  - Computes initial conditions for the gradient.
% Allows for missing values in the endogenous variables data.
%    [dx0, dP0] = gmissin(Phi, dPhi, Gam, dGam, E, dE, H, dH, D, dD, ...
%                                          C, dC, Q, dQ, S, dS, R, dR, z, MV)
% Phi, Gam, E, H, D, C, Q, S, R          > SS model matrices.
% dPhi, dGam, dE, dH, dD, dC, dQ, dS, dR > derivatives of the SS model
%                       matrices with respect to one of the parameters.
% z     > matrix of observable variables.
% MV    > MV=1 if the ML estimation of x0 and dx0 will be done during
%            the likelihood function evaluation.
% dx0   < derivative of the expectation of the initial state vector.
% dP0   < derivative of the covariance of the initial state.
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

if nargin < 20, MV = 0; end

zeps   = E4OPTION(15);
econd  = E4OPTION(4);
scaleb = E4OPTION(2);

n = size(z,1); l = size(Phi,1);
m = size(H,1); r = size(Gam, 2);

if econd == 5
   if r, econd = 2; else, econd = 3; end;
end

dx0 = zeros(l,1); x0 = zeros(l,1);
dP0 = zeros(l,l); P0 = zeros(l,l);

if econd == 2 & MV == 0   % Maximum likelihood estimator of the initial state vector
%
     Phibb0 = eye(l,l); dPhibb0 = zeros(l,l);
     EQEt = E*Q*E';
     WW   = zeros(l,l); WZ   = zeros(l,1);
     dWW  = zeros(l,l); dWZ = zeros(l,1);
     H0 = H;   C0 = C;   D0 = D;
     dH0 = dH; dD0 = dD; dC0 = dC;

     for t=1:n
     %
     % Construct H(t)
       trueobs = find(~isnan(z(t,1:m)'));
       nobs = size(trueobs,1); nmiss = m - nobs; Ht = eye(m); 
       if nmiss & nobs, Ht = Ht(trueobs,:); end
  
       if nobs
       %
          H = Ht*H0; dH = Ht*dH0; C = Ht*C0; dC = Ht*dC0;
          if r, D = Ht*D0; dD = Ht*dD0; end

          if r
             z1  = z(t,trueobs)' - H*x0 - D*z(t,m+1:m+r)';
             dz1 = -dH*x0 -H*dx0 - dD*z(t,m+1:m+r)';
          else
             z1  = z(t,trueobs)' - H*x0;
             dz1 = -dH*x0 -H*dx0;
          end

          B1  = H*P0*H' + C*R*C';
          dB1 = dH*P0*H' + H*dP0*H' + H*P0*dH' + ...
                dC*R*C' + C*dR*C' + C*R*dC';
          if scaleb, U = cholp(B1, abs(B1));
          else       U = cholp(B1);  end
          iB1=(eye(size(U))/U)/U';
          K1   = (Phi*P0*H' + E*S*C')*iB1;
          dK1  = (dPhi*P0*H' + Phi*dP0*H' + Phi*P0*dH' + ...
             dE*S*C' + E*dS*C' + E*S*dC' - K1*dB1)*iB1;

          Phib   = Phi  - K1*H;
          dPhib  = dPhi - dK1*H -K1*dH;

          if r
             Gamb   = Gam  - K1*D;
             dGamb  = dGam - dK1*D - K1*dD;
             x1  = Phib*x0 + Gamb*z(t,m+1:m+r)' + K1*z(t,1:m)';
             dx1 = dPhib*x0 + Phib*dx0 + dGamb*z(t,m+1:m+r)' + dK1*z(t,trueobs)';
          else
             x1  = Phi*x0 + K1*z1;
             dx1 = dPhib*x0 + Phib*dx0 + dK1*z(t,trueobs)';
          end

          P1   = Phib*P0*Phib' + EQEt + K1*C*R*C'*K1' - ...
                   K1*(E*S*C')' - E*S*C'*K1';
          dP1  = dPhib*P0*Phib' + Phib*dP0*Phib' + Phib*P0*dPhib' + ...
                   dE*Q*E' + E*dQ*E' + E*Q*dE' + dK1*C*R*C'*K1' + ...
                    K1*dC*R*C'*K1' + K1*C*dR*C'*K1' + K1*C*R*dC'*K1' + ...
                     K1*C*R*C'*dK1' - dK1*(E*S*C')' - K1*dC*S'*E' - ...
                      K1*C*dS'*E' - K1*C*S'*dE' - dE*S*C'*K1' - ...
                       E*dS*C'*K1' - E*S*dC'*K1' - E*S*C'*dK1';

          HPhi= H*Phibb0;
          dHPhi = dH*Phibb0 + H*dPhibb0;
          dWW = dWW + dHPhi'*iB1*HPhi - HPhi'*iB1*dB1*iB1*HPhi + HPhi'*iB1*dHPhi;
          dWZ = dWZ + dHPhi'*iB1*z1 - HPhi'*iB1*dB1*iB1*z1 + HPhi'*iB1*dz1;
          WW  = WW + HPhi'*iB1*HPhi;
          WZ  = WZ + HPhi'*iB1*z1;
       %
       else
       %
          Phib   = Phi;
          dPhib  = dPhi;
          if r
             x1  = Phi*x0 + Gam*z(t,m+1:m+r)';
             dx1 = dPhi*x0 + Phi*dx0 + dGam*z(t,m+1:m+r)';
          else
             x1  = Phi*x0;
             dx1 = dPhi*x0 + Phi*dx0;
          end

          P1   = Phi*P0*Phi' + EQEt;
          dP1  = dPhi*P0*Phi' + Phi*dP0*Phi' + Phi*P0*dPhi' + ...
                   dE*Q*E' + E*dQ*E' + E*Q*dE';

       end

       dPhibb0 = dPhib*Phibb0 + Phib*dPhibb0;
       Phibb0 = Phib*Phibb0;
       P0  = P1; x0  = x1; dP0 = dP1; dx0 = dx1;
     %
     end  % t

     if any(isnan([WW WZ])) | any(isinf([WW WZ])), e4error(25); end
     iWW = pinv(WW,zeps);
     x0  = iWW*WZ;
     dx0 = iWW*(dWZ - dWW*x0);

     [ign, dP0] = gmodini(Phi, dPhi, Gam, dGam, E, dE, H0, dH0, ...
                              D0, dD0, C0, dC0, Q, dQ, S, dS, R, dR, z, 1);
else
     [dx0, dP0] = gmodini(Phi, dPhi, Gam, dGam, E, dE, H, dH, ...
                              D, dD, C, dC, Q, dQ, S, dS, R, dR, z, 1);
end
