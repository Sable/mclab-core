function [ dts, dtsg, corrm, corrmg, varm, varmg, Im, g] = imodg(theta, din, z, aprox)
% imodg - Robustified information matrix.
% [dts, dtsg, corrm, corrmg, varm, varmg, Im, g] = imodg(theta, din,z, aprox)
% theta > parameter vector.
% din > matrix which stores a description of the model dynamics.
% z > matrix of observable variables.
% aprox > If aprox = 0, computes the approximation of Watson and Engle (1983).
% userf > contains the names of the user functions required to obtain the system
% matrices and their derivatives (only for user models).
% dts < vector of standard errors under normality.
% dtsg < vector of robustified standard errors.
% corrm < correlation matrix under normality.
% corrmg< robustified correlation matrix.
% varm < covariance matrix under normality.
% varmg < robustified covariance matrix.
% Im < information matrix under normality.
% If any of the information matrices is not positive-definite,
% it will be perturbed before computing the rest of the matrices.
%
% 11/3/97

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
econd = E4OPTION(4);
zeps = E4OPTION(15);
oldfil= E4OPTION(1); E4OPTION(1) = 1; % Siempre Kalman
scaleb= E4OPTION(2);

if nargin < 4
   aprox = 0;
elseif aprox ~= 0
   e4warn(7);
end

n=size(z,1);
[Phi, Gam, E, H, D, C, Q, S, R] = thd2ss(theta, din);
m = size(H,1);
r = size(Gam,2);

if econd == 5
   if r, econd = 2; else, econd = 3; end;
end
if econd == 2 & ~aprox, MV = 1; else, MV = 0; end

[x0, P0] = lfmodini(Phi,Gam,E,H,D,C,Q,S,R,z, MV);
CRCt=C*R*C';  ESCt=E*S*C';  EQEt=E*Q*E'; % Constantes en Kalman

% Retiramos los parámetros fijos, si los hay
if size(theta,2) > 1
%
   index = find(theta(:,2) == 0);
%
else
%
   index = (1:size(theta,1))';
%
end

np = size(index,1);
p  = size(P0,1);
Im = zeros(np, np);
gg = Im;
cc = zeros(p,p);
g  = zeros(np,1);
gc = g;

% inicializamos matrices
%
dPhii = zeros(p*np, p);
sGam =  size(Gam,1);
dGami = zeros(sGam*np, size(Gam,2));
sE =    size(E,1);
dEi =   zeros(sE*np, size(E,2));
dHi =   zeros(m*np, p);
sD =    size(D,1);
dDi =   zeros(sD*np, size(D,2));
sC =    size(C,1);
dCi =   zeros(sC*np, size(C,2));
sQ =    size(Q,1);
dQi =   zeros(sQ*np, size(Q,2));
sS =    size(S,1);
dSi =   zeros(sS*np, size(S,2));
sR =    size(R,1);
dRi =   zeros(sR*np, size(R,2));

dP0i =  zeros(p*np,p);
dB1i =  zeros(m*np,m);

if MV
   x00 = x0;
   P00 = P0;
end

dx0i =  zeros(p*np,1);
dz1i =    zeros(m*np,1);

if ~aprox
%
   dK1i =  zeros(p*np,m);
   Pc11 =  zeros(p, p);   % corregimos de acuerdo a los últimos resultados del paper  (antes = P0);
   Pc21 =  zeros(p*np,p);
   Pc23 =  zeros(p*(np+1)*np/2, p);

   if r | MV
   %
      xc =     x0;
      dxc =    zeros(p*np,1);
      dzc =    zeros(m*np,1);

      % Más adelante inicializamos dxc con los valores correctos, es decir dx0
   %
   else
   %
      Pc11 = Pc11 + x0*x0';
   %
   end
%
end

if MV
   Phibb0 = eye(p,p);
   dPhibb0 = zeros(p*np,p);
   WW  = zeros(p,p); WZ = zeros(p,1);
   dWW = zeros(p*np,p); dWZ = zeros(p*np,1);
end


for i = 1:np
%
    j = i-1;
    [dPhii(j*p+1:i*p,:),dGam,dEi(j*sE+1:i*sE,:),...
     dHi(j*m+1:i*m,:),dD,dCi(j*sC+1:i*sC,:),...
     dQi(j*sQ+1:i*sQ,:),dSi(j*sS+1:i*sS,:),dRi(j*sR+1:i*sR,:)] =...
         ss_dv(theta,din,index(i));

    if r
    %
       dGami(j*sGam+1:i*sGam,:) = dGam;
       dDi(j*sD+1:i*sD,:) = dD;
    %
    end

    [dx0i(j*p+1:i*p,:), dP0i(j*p+1:i*p,:)] = ...
       gmodini(Phi,dPhii(j*p+1:i*p,:),...
               Gam, dGami(j*sGam+1:i*sGam,:),E,dEi(j*sE+1:i*sE,:),...
               H,dHi(j*m+1:i*m,:),D,dDi(j*sD+1:i*sD,:),C,dCi(j*sC+1:i*sC,:),...
               Q,dQi(j*sQ+1:i*sQ,:),S,dSi(j*sS+1:i*sS,:),...
               R,dRi(j*sR+1:i*sR,:),z,MV);

    if ~aprox
       if ~r & ~MV
       %
             Pc21(j*p+1:i*p,:) = dx0i(j*p+1:i*p,:)*x0';

             for k=1:i
             %
                 l = k-1;
                 ptri = (j*i/2 + l)*p + 1;
                 ptrf = ptri + p - 1;

                 Pc23(ptri:ptrf,:) = dx0i(j*p+1:i*p,:)*dx0i(l*p+1:k*p,:)';
             %
             end
       %
       elseif ~MV
       %
          dxc(j*p+1:i*p,:) = dx0i(j*p+1:i*p,:);
       %
       end
    end
%
end

for t = 1:n

    % (B.5)

    % Propagación filtro de Kalman
    B1   = H*P0*H' + CRCt;
    if scaleb, U = cholp(B1, abs(B1));
    else       U = cholp(B1); end
    iB1 = (eye(size(U))/U)/U';
    K1  = (Phi*P0*H' + ESCt)*iB1;
    Phib  = Phi - K1*H;
    if r, Gamb   = Gam  - K1*D; end

    if MV, HPhi= H*Phibb0; end

    if r
       z1  = z(t,1:m)' - H*x0 - D*z(t,m+1:m+r)';
    else
       z1  = z(t,:)' - H*x0;
    end

    z1i = iB1*z1;

    for i = 1:np
    %
      j = i-1;
      dB1i(j*m+1:i*m,:) = dHi(j*m+1:i*m,:)*P0*H'+ H*dP0i(j*p+1:i*p,:)*H'+ H*P0*dHi(j*m+1:i*m,:)' + ...
                          dCi(j*sC+1:i*sC,:)*R*C' + C*dRi(j*sR+1:i*sR,:)*C' + C*R*dCi(j*sC+1:i*sC,:)';

      % sólo necesario para aproximación Engle y Watson
      if aprox
      %
         dK1i= (dPhii(j*p+1:i*p,:)*P0*H' + Phi*dP0i(j*p+1:i*p,:)*H' + ...
               Phi*P0*dHi(j*m+1:i*m,:)' + ...
               dEi(j*sE+1:i*sE,:)*S*C' + E*dSi(j*sS+1:i*sS,:)*C' + ...
               E*S*dCi(j*sC+1:i*sC,:)' - K1*dB1i(j*m+1:i*m,:))*iB1;

         dPhibi= dPhii(j*p+1:i*p,:) - dK1i*H - K1*dHi(j*m+1:i*m,:);

         if r
           dz1i(j*m+1:i*m,:) = -dHi(j*m+1:i*m,:)*x0 -H*dx0i(j*p+1:i*p,:) - ...
                                dDi(j*sD+1:i*sD,:)*z(t,m+1:m+r)';

           dGambi  = dGami(j*sGam+1:i*sGam,:) - dK1i*D - K1*dDi(j*sD+1:i*sD,:);

           dx0i(j*p+1:i*p,:) = dPhibi*x0 + Phib*dx0i(j*p+1:i*p,:) + ...
                               dGambi*z(t,m+1:m+r)' + dK1i*z(t,1:m)';
         else
           dz1i(j*m+1:i*m,:) = -dHi(j*m+1:i*m,:)*x0 -H*dx0i(j*p+1:i*p,:);

           dx0i(j*p+1:i*p,:) = dPhibi*x0 + Phib*dx0i(j*p+1:i*p,:) + ...
                               dK1i*z(t,1:m)';
         end

         dP0i(j*p+1:i*p,:) = dPhibi*P0*Phib' + Phib*dP0i(j*p+1:i*p,:)*Phib' + Phib*P0*dPhibi' + ...
                  dEi(j*sE+1:i*sE,:)*Q*E' + E*dQi(j*sQ+1:i*sQ,:)*E' + ...
                  E*Q*dEi(j*sE+1:i*sE,:)' + dK1i*CRCt*K1' + ...
                  K1*dCi(j*sC+1:i*sC,:)*R*C'*K1' + K1*C*dRi(j*sR+1:i*sR,:)*C'*K1' + ...
                  K1*C*R*dCi(j*sC+1:i*sC,:)'*K1' + K1*CRCt*dK1i' - ...
                  dK1i*ESCt' - K1*dCi(j*sC+1:i*sC,:)*S'*E' - ...
                  K1*C*dSi(j*sS+1:i*sS,:)'*E' - K1*C*S'*dEi(j*sE+1:i*sE,:)' - ...
                  dEi(j*sE+1:i*sE,:)*S*C'*K1' - E*dSi(j*sS+1:i*sS,:)*C'*K1' - ...
                  E*S*dCi(j*sC+1:i*sC,:)'*K1' - ESCt*dK1i';

         for k=1:i
         %
             l = k-1;
             Im(i,k) = Im(i,k) + 0.5*trace( (dB1i(j*m+1:i*m,:)'*iB1)*(dB1i(l*m+1:k*m,:)'*iB1) ) + ...
                                           (dz1i(j*m+1:i*m,:)'*iB1*dz1i(l*m+1:k*m,:));
         %
         end
      else

         dK1i(j*p+1:i*p,:) = (dPhii(j*p+1:i*p,:)*P0*H' + Phi*dP0i(j*p+1:i*p,:)*H' + ...
               Phi*P0*dHi(j*m+1:i*m,:)' + ...
               dEi(j*sE+1:i*sE,:)*S*C' + E*dSi(j*sS+1:i*sS,:)*C' + ...
               E*S*dCi(j*sC+1:i*sC,:)' - K1*dB1i(j*m+1:i*m,:))*iB1;

         dPhibi= dPhii(j*p+1:i*p,:) - dK1i(j*p+1:i*p,:)*H - K1*dHi(j*m+1:i*m,:);

         if r
            dz1i(j*m+1:i*m,:) = -dHi(j*m+1:i*m,:)*x0 -H*dx0i(j*p+1:i*p,:) - ...
                                 dDi(j*sD+1:i*sD,:)*z(t,m+1:m+r)';

            dGambi  = dGami(j*sGam+1:i*sGam,:) - dK1i(j*p+1:i*p,:)*D - K1*dDi(j*sD+1:i*sD,:);

            dx0i(j*p+1:i*p,:) = dPhibi*x0 + Phib*dx0i(j*p+1:i*p,:) + ...
                                dGambi*z(t,m+1:m+r)' + dK1i(j*p+1:i*p,:)*z(t,1:m)';
         else
            dz1i(j*m+1:i*m,:) = -dHi(j*m+1:i*m,:)*x0 -H*dx0i(j*p+1:i*p,:);

            dx0i(j*p+1:i*p,:) = dPhibi*x0 + Phib*dx0i(j*p+1:i*p,:) + ...
                                dK1i(j*p+1:i*p,:)*z(t,1:m)';
         end

         dP0i(j*p+1:i*p,:) = dPhibi*P0*Phib' + Phib*dP0i(j*p+1:i*p,:)*Phib' + Phib*P0*dPhibi' + ...
                  dEi(j*sE+1:i*sE,:)*Q*E' + E*dQi(j*sQ+1:i*sQ,:)*E' + ...
                  E*Q*dEi(j*sE+1:i*sE,:)' + dK1i(j*p+1:i*p,:)*CRCt*K1' + ...
                  K1*dCi(j*sC+1:i*sC,:)*R*C'*K1' + K1*C*dRi(j*sR+1:i*sR,:)*C'*K1' + ...
                  K1*C*R*dCi(j*sC+1:i*sC,:)'*K1' + K1*CRCt*dK1i(j*p+1:i*p,:)' - ...
                  dK1i(j*p+1:i*p,:)*ESCt' - K1*dCi(j*sC+1:i*sC,:)*S'*E' - ...
                  K1*C*dSi(j*sS+1:i*sS,:)'*E' - K1*C*S'*dEi(j*sE+1:i*sE,:)' - ...
                  dEi(j*sE+1:i*sE,:)*S*C'*K1' - E*dSi(j*sS+1:i*sS,:)*C'*K1' - ...
                  E*S*dCi(j*sC+1:i*sC,:)'*K1' - ESCt*dK1i(j*p+1:i*p,:)';

         Phibi = dPhii(j*p+1:i*p,:) - K1*dHi(j*m+1:i*m,:);

         if r & ~MV
         %
            dzc(j*m+1:i*m,:) = -dHi(j*m+1:i*m,:)*xc -H*dxc(j*p+1:i*p,:) - ...
                                dDi(j*sD+1:i*sD,:)*z(t,m+1:m+r)';

            dxc(j*p+1:i*p,:) = Phibi*xc + Phib*dxc(j*p+1:i*p,:) + ...
                               (dGami(j*sGam+1:i*sGam,:) - K1*dDi(j*sD+1:i*sD,:))*z(t,m+1:m+r)';
         %
         end

         for k=1:i
         %
             l = k-1;
             ptri = (j*i/2 + l)*p + 1;
             ptrf = ptri + p - 1;

             Phibj = dPhii(l*p+1:k*p,:) - K1*dHi(l*m+1:k*m,:);

             Bij = dHi(j*m+1:i*m,:)*Pc11*dHi(l*m+1:k*m,:)' + ...
                   H * Pc21(j*p+1:i*p,:)*dHi(l*m+1:k*m,:)' + ...
                   dHi(j*m+1:i*m,:)*Pc21(l*p+1:k*p,:)'*H' + ...
                   H*Pc23(ptri:ptrf,:)*H';

             Pc23(ptri:ptrf,:) = Phibi*Pc11*Phibj' + Phib*Pc21(j*p+1:i*p,:)*Phibj' + ...
                       Phibi*Pc21(l*p+1:k*p,:)'*Phib' + Phib*Pc23(ptri:ptrf,:)*Phib' + ...
                       dK1i(j*p+1:i*p,:)*B1*dK1i(l*p+1:k*p,:)';

             if r & ~MV
             %
                Im(i,k) = Im(i,k) + 0.5*trace( (dB1i(j*m+1:i*m,:)'*iB1)*(dB1i(l*m+1:k*m,:)'*iB1) ) + ...
                                        trace(iB1*(Bij + dzc(j*m+1:i*m,:)*dzc(l*m+1:k*m,:)'));
             %
             else
             %
                Im(i,k) = Im(i,k) + 0.5*trace( (dB1i(j*m+1:i*m,:)'*iB1)*(dB1i(l*m+1:k*m,:)'*iB1) ) + ...
                                        trace(iB1*Bij);
             %
             end
         %
         end

      end
    %

      if MV    % estimación mv vector estado inicial y su derivada: acumuladores
      %
         dHPhi = dHi(j*m+1:i*m,:)*Phibb0 + H*dPhibb0(j*p+1:i*p,:);
         dWW(j*p+1:i*p,:) = dWW(j*p+1:i*p,:) + dHPhi'*iB1*HPhi - HPhi'*iB1*dB1i(j*m+1:i*m,:)*iB1*HPhi + HPhi'*iB1*dHPhi;
         dWZ(j*p+1:i*p,:) = dWZ(j*p+1:i*p,:) + dHPhi'*z1i - HPhi'*iB1*dB1i(j*m+1:i*m,:)*iB1*z1 + HPhi'*iB1*dz1i(j*m+1:i*m,:);
         dPhibb0(j*p+1:i*p,:) = dPhibi*Phibb0 + Phib*dPhibb0(j*p+1:i*p,:);
      %
      end

      gc(i) = 0.5*trace(iB1*dB1i(j*m+1:i*m,:)) + z1i'*dz1i(j*m+1:i*m,:) - 0.5*(z1i'*dB1i(j*m+1:i*m,:)*z1i);
      g(i)  = g(i) + gc(i);
    end

    gg = gg + gc*gc';

    if MV    % estimación mv vector estado inicial y su derivada: acumuladores
    %
       WW  = WW + HPhi'*iB1*HPhi;
       WZ  = WZ + HPhi'*iB1*z1;
       Phibb0 = Phib*Phibb0;
    %
    end

    if r
       x0 = Phib*x0 + Gamb*z(t,m+1:m+r)' + K1*z(t,1:m)';
    else
       x0 = Phi*x0 + K1*(z(t,:)' - H*x0);
    end

    if ~aprox
    %
       for i=1:np
       %
           j = i-1;
           Phibi = dPhii(j*p+1:i*p,:) - K1*dHi(j*m+1:i*m,:);
           Pc21(j*p+1:i*p,:) = Phibi*Pc11*Phi' + Phib*Pc21(j*p+1:i*p,:)*Phi' + dK1i(j*p+1:i*p,:)*B1*K1';
       %
       end

       Pc11 = Phi*Pc11*Phi' + K1*B1*K1';

       if r & ~MV
       %
          xc = Phi*xc + Gam*z(t,m+1:m+r)';
       %
       end
    %
    end

    P0 = Phib*P0*Phib' + EQEt + K1*CRCt*K1' - ...
         K1*ESCt' - ESCt*K1';

end % t


if MV   % calculamos los productos de medias
%
   % lo primero es calcular las condiciones iniciales.

   WW = pinv(WW, zeps);
   xc = WW*WZ;
   for i = 1:np
   %
       j = i-1;
       dxc(j*p+1:i*p,:) = WW*(dWZ(j*p+1:i*p,:)-dWW(j*p+1:i*p,:)*xc);
       g(i) = g(i) - dWZ(j*p+1:i*p,:)'*xc + 0.5*xc'*dWW(j*p+1:i*p,:)*xc;
   %
   end
   xc = xc + x00;
   P0 = P00;

   for t = 1:n
   %
       % calculamos las medias
       % Propagación filtro de Kalman
        B1   = H*P0*H' + CRCt;
        if scaleb, U = cholp(B1, abs(B1));
        else       U = cholp(B1); end
        iB1 = (eye(size(U))/U)/U';
        K1  = (Phi*P0*H' + ESCt)*iB1;
        Phib  = Phi - K1*H;
        if r, Gamb   = Gam  - K1*D; end

        for i = 1:np
        %
            j = i-1;

            Phibi = dPhii(j*p+1:i*p,:) - K1*dHi(j*m+1:i*m,:);

            if r
            %
               dzc(j*m+1:i*m,:) = -dHi(j*m+1:i*m,:)*xc -H*dxc(j*p+1:i*p,:) - ...
                                   dDi(j*sD+1:i*sD,:)*z(t,m+1:m+r)';

               dxc(j*p+1:i*p,:) = Phibi*xc + Phib*dxc(j*p+1:i*p,:) + ...
                                  (dGami(j*sGam+1:i*sGam,:) - K1*dDi(j*sD+1:i*sD,:))*z(t,m+1:m+r)';
            %
            else
            %
               dzc(j*m+1:i*m,:) = -dHi(j*m+1:i*m,:)*xc -H*dxc(j*p+1:i*p,:);

               dxc(j*p+1:i*p,:) = Phibi*xc + Phib*dxc(j*p+1:i*p,:);
            %
            end

            for k = 1:i
            %
                l = k-1;
                Im(i,k) = Im(i,k) + dzc(l*m+1:k*m,:)'*iB1*dzc(j*m+1:i*m,:);
            %
            end
        %
        end

        if r
        %
           xc = Phi*xc + Gam*z(t,m+1:m+r)';
        %
        else
        %
           xc = Phi*xc;
        %
        end

        P0 = Phib*P0*Phib' + EQEt + K1*CRCt*K1' - ...
             K1*ESCt' - ESCt*K1';
   %
   end
%
end

Im = Im + tril(Im,-1)';

if min(eig(Im)) <= 0, e4warn(8); end

varm = pinv(Im);

varmg  = varm*gg*varm;
dts    = sqrt(diag(varm));
dtsg   = sqrt(diag(varmg));
corrm  = diag(1./dts)*varm*diag(1./dts);
corrmg = diag(1./dtsg)*varmg*diag(1./dtsg);
E4OPTION(1) = oldfil;
