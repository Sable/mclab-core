function g = gmod(theta, din, z)
% GMOD     - Computes the analytical gradient of the likelihood function.
%    g = gmod(theta, din, z)
% theta  > parameter vector.
% din    > matrix which stores a description of the model dynamics.
% z      > matrix of observable variables.
% g      < gradient of the likelihood function.
%
% 7/3/97
% Copyright (c) Jaime Terceiro, 1997
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

if nargin < 3,  e4error(3); end

if E4OPTION(1) == 1, filtk = 1; else, filtk = 0; end
scaleb = E4OPTION(2);
vcond  = E4OPTION(3);
econd  = E4OPTION(4);
zeps   = E4OPTION(15);

[Phi, Gam, E, H, D, C, Q, S, R] = thd2ss(theta, din);
n = size(z,1);
m = size(H,1);
r = max([size(Gam,2), size(D,2)]);

if econd == 5
   if r, econd = 2; else, econd = 3; end;
end

if econd == 2, MV = 1; else, MV = 0; end

if size(z,2) ~= m+r, e4error(11); end

if size(theta,2) > 1  % look for fixed parameters in theta
   index = find(theta(:,2) == 0);
else
   index = (1:size(theta,1))';
end

np = size(index,1);
g  = zeros(np,1);

if filtk
   if vcond == 4
     [x00, Sigm, ign, ign, ign, P00, iSigm, nonstat] = lfmodini(Phi,Gam,E,H,D,C,Q,S,R,z,MV);
     P00 = zeros(size(Sigm));
   else
      [x00, P00] = lfmodini(Phi,Gam,E,H,D,C,Q,S,R,z,MV);
   end
   CRCt=C*R*C';  ESCt=E*S*C';  EQEt=E*Q*E';
else
   if vcond == 4
      [x00, B00, K00, y00, M00, Sigm, iSigm, nonstat] = lfmodini(Phi,Gam,E,H,D,C,Q,S,R,z,MV);
   else
      [x00, B00, K00, y00, M00] = lfmodini(Phi,Gam,E,H,D,C,Q,S,R,z,MV);
   end
end

for i = 1:np
%
    x0 = x00;
    if filtk, P0 = P00;
    else      B0 = B00; K0 = K00; y0 = y00; M0 = M00; B1 = B0; K1 = K0; M1 = M0; y1 = y0;  end
    [dPhi, dGam, dE, dH, dD, dC, dQ, dS, dR] = ss_dv(theta, din, index(i));
    if filtk
       [dx0, dP0] = gmodini(Phi,dPhi,Gam,dGam,E,dE,H,dH,D,dD,C,dC,Q,dQ, ...
                            S,dS,R,dR,z,MV);
       if vcond == 4
          dSigm = dP0;
          dP0 = zeros(size(dP0));
       end
    else
       [dx0, dB0, dK0, dy0, dM0, dSigm] = gmodini(Phi,dPhi,Gam,dGam,E,dE,H,dH, ...
               D,dD,C,dC,Q,dQ,S,dS,R,dR,z,MV);

       dB1 = dB0;
       dK1 = dK0;
       dy1 = dy0;
       dM1 = dM0;
    end

    gg = 0.0;

    if MV | vcond == 4
       l = size(Phi,1);
       Phibb0 = eye(l,l);
       dPhibb0 = zeros(l,l);
       WW  = zeros(l,l); WZ = zeros(l,1);
       dWW = zeros(l,l); dWZ = zeros(l,1);
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

              if vcond == 1
                 y1   = (Phi -K0*H)*y0;
                 dy1  = (dPhi -dK0*H -K0*dH)*y0 + (Phi -K0*H)*dy0;
                 M1   = M0 - M0*Hy0'*iB1*Hy0*M0;
                 dM1  = dM0 - (dM0*y0'*H' + M0*dy0'*H' + M0*y0'*dH')*iB1*Hy0*M0 -...
                        M0*Hy0'*iB1*(dH*y0*M0 + H*dy0*M0 + H*y0*dM0) + ...
                        (M0*Hy0'*iB1)*dB1*(iB1*Hy0*M0);
              else
                 y1   = (Phi -K1*H)*y0;
                 dy1  = (dPhi -dK1*H -K1*dH)*y0 + (Phi -K1*H)*dy0;
                 M1   = M0 + M0*Hy0'*iB0*Hy0*M0;
                 dM1  = dM0 + (dM0*y0'*H' + M0*dy0'*H' + M0*y0'*dH')*iB0*Hy0*M0 +...
                        M0*Hy0'*iB0*(dH*y0*M0 + H*dy0*M0 + H*y0*dM0) - ...
                        (M0*Hy0'*iB0)*dB0*(iB0*Hy0*M0);
              end
              B0  = B1;
              dB0 = dB1;
           end
        end
        Phib  = Phi  - K1*H;
        dPhib = dPhi - dK1*H -K1*dH;

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

        if MV | vcond == 4
           HPhi= H*Phibb0;
           dHPhi = dH*Phibb0 + H*dPhibb0;
           dWW = dWW + dHPhi'*iB1*HPhi - HPhi'*iB1*dB1*iB1*HPhi + HPhi'*iB1*dHPhi;
           dWZ = dWZ + dHPhi'*iB1*z1 - HPhi'*iB1*dB1*iB1*z1 + HPhi'*iB1*dz1;
           WW  = WW + HPhi'*iB1*HPhi;
           WZ  = WZ + HPhi'*iB1*z1;
           dPhibb0 = dPhib*Phibb0 + Phib*dPhibb0;
           Phibb0 = Phib*Phibb0;
        end

        z1i = z1'*iB1;
        gg  = gg + 0.5*trace(iB1*dB1) + z1i*dz1 - 0.5*(z1i*dB1*z1i');
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

    if MV | vcond == 4
       if any(isnan([WW WZ])) | any(isinf([WW WZ])), e4error(25); end
       iWW = pinv(WW, zeps);
    end

    if vcond == 4
    %
       if ~nonstat  % stationary system
       %
          [Ns Ss Ns] = svd(Sigm);
          k = find(diag(Ss) < zeps);
          if size(k,1) < l
             if size(k,1)
             %   
                N = Ns(:,1:k(1)-1);
                Sigm = Ss(1:k(1)-1,1:k(1)-1);
                WW = N'*WW*N;
                WZ = N'*WZ;
                dWW = N'*dWW*N;
                dWZ = N'*dWZ;
             %
             end

             gg = gg + .5*trace(pinv(eye(l)+Sigm*WW)*(dSigm*WW+Sigm*dWW));

             if MV
                WZ = iWW*WZ;
                gg = gg - dWZ'*WZ + 0.5*WZ'*dWW*WZ;
             elseif econd ~= 2
   	        iSigm = pinv(Sigm);
                iWWdWZ = pinv(iSigm + WW)*WZ;
                gg = gg  - dWZ'*iWWdWZ + 0.5*iWWdWZ'*(dWW - iSigm*dSigm*iSigm)*iWWdWZ;
             end
          end
       %
       elseif nonstat == 2  % non stationary system
       %
          WZ = iWW*WZ;
          gg = gg + 0.5*trace(iWW*dWW) - dWZ'*WZ + 0.5*WZ'*dWW*WZ;
       %
       else  % partially stationary
       %
	   iSigWW = pinv(iSigm + WW);
	   gg = gg + 0.5*(trace(iSigWW*(dWW - iSigm*dSigm*iSigm)) + trace(iSigm*dSigm));

           if MV
              WZ = iWW*WZ;
              gg = gg - dWZ'*WZ + 0.5*WZ'*dWW*WZ;
           else
              iWWdWZ = iSigWW*WZ;
              gg = gg  - dWZ'*iWWdWZ + 0.5*iWWdWZ'*(dWW - iSigm*dSigm*iSigm)*iWWdWZ;
           end
       %
       end
    %
    elseif MV
    %
       WZ = iWW*WZ;
       gg = gg - dWZ'*WZ + 0.5*WZ'*dWW*WZ;
    %
    end   
 
    g(i) = gg;
%
end  % i
