function [dts, corrm, varm, Im] = igarch(theta, din, z)
% igarch - Computes the information matrix of a model with GARCH structure
% in the error term. It uses the approximation by Watson and Engle (1983).
% [dts, corrm, varm, Im] = igarch(theta, din, z)
% theta > parameter vector.
% din > matrix which stores a description of the model dynamics.
% z > matrix of observable variables.
% dts < vector of standard deviations.
% corrm < correlation matrix.
% varm < covariance matrix.
% Im < information matrix.
% If Im is not positive-definite, it is perturbed before computing
% the rest of the matrices.
%
% 29/4/97

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

if nargin < 3, e4error(3); end

[H_D, type] = e4gthead(din);
is_diag = rem(type,100); % uncorrelated errors
oldfil = E4OPTION(1); E4OPTION(1) = 1;
scaleb = E4OPTION(2);
zeps   = E4OPTION(15);

[Phi, Gam, E, H, D, C, Q, Phig, Gamg, Eg, Hg, Dg] = garch2ss(theta, din);
n  = size(z,1);
m  = size(H,1);
r  = max([size(Gam,2), size(D,2)]);
rg = max([size(Gamg,2), size(Dg,2)]);

if size(z,2) ~= m+r+rg, e4error(11); end

if is_diag
   Q0 = diag(Q);
   Q00 = Q - diag(Q0);
else, Q0 = vech(Q); end

x0g = zeros(size(Phig,1),1);

[x0, P0] = lfmodini(Phi,Gam,E,H,D,C,Q,Q,Q,z, 0);

if size(theta,2) > 1  % look for fixed parameters in theta
   index = find(theta(:,2) == 0);
else
   index = (1:size(theta,1))';
end

np = size(index,1);
p = size(P0,1);
Im  = zeros(np, np);

dPhii = zeros(p*np, p);
sGam  = size(Gam,1);
dGami = zeros(sGam*np, size(Gam,2));
sE    = size(E,1);
dEi   = zeros(sE*np, size(E,2));
dHi   = zeros(m*np, p);
sD    = size(D,1);
dDi   = zeros(sD*np, size(D,2));
sC    = size(C,1);
dCi   = zeros(sC*np, size(C,2));
sQ    = size(Q,1);
dQi   = zeros(sQ*np, size(Q,2));
sPhig = size(Phig,1);
dPhigi= zeros(sPhig*np, sPhig);
sGamg = size(Gamg,1);
dGamgi= zeros(sGamg*np, size(Gamg,2));
sEg   = size(Eg,1);
dEgi  = zeros(sEg*np, size(Eg,2));
mg    = size(Hg,1);
dHgi  = zeros(mg*np, sPhig);
sDg   = size(Dg,1);
dDgi  = zeros(sDg*np, size(Dg,2));

dP0i  = zeros(p*np,p);
dB1i  = zeros(m*np,m);
dx0i  = zeros(p*np,1);
dz1i  = zeros(m*np,1); 
dx0gi = zeros(sPhig*np,1);
dQ0   = zeros(mg*np,1);
dQ00   = zeros(sQ*np,sQ);


for i = 1:np
%
    j = i-1;

    [dPhii(j*p+1:i*p,:),dGam,dEi(j*sE+1:i*sE,:),...
     dHi(j*m+1:i*m,:),dD,dCi(j*sC+1:i*sC,:),...
     dQi(j*sQ+1:i*sQ,:),dPhigi(j*sPhig+1:i*sPhig,:),...
     dGamg,dEgi(j*sEg+1:i*sEg,:),...
     dHgi(j*mg+1:i*mg,:),dDg] = garch_dv(theta,din,index(i));

    if is_diag
       dQ0(j*mg+1:i*mg,:) = diag(dQi(j*sQ+1:i*sQ,:));
       dQ00(j*sQ+1:i*sQ,:) = dQi(j*sQ+1:i*sQ,:) - diag(dQ0(j*mg+1:i*mg,:));
    else
       dQ0(j*mg+1:i*mg,:) = vech(dQi(j*sQ+1:i*sQ,:));
    end

    if r
       dGami(j*sGam+1:i*sGam,:) = dGam;
       dDi(j*sD+1:i*sD,:) = dD;
    end

    if rg
       dGamgi(j*sGamg+1:i*sGamg,:) = dGamg;
       dDgi(j*sDg+1:i*sDg,:) = dDg;
    end

    [dx0i(j*p+1:i*p,:), dP0i(j*p+1:i*p,:)] = ...
       gmodini(Phi,dPhii(j*p+1:i*p,:),...
               Gam, dGami(j*sGam+1:i*sGam,:),E,dEi(j*sE+1:i*sE,:),...
               H,dHi(j*m+1:i*m,:),D,dDi(j*sD+1:i*sD,:),C,dCi(j*sC+1:i*sC,:),...
               Q,dQi(j*sQ+1:i*sQ,:),Q,dQi(j*sQ+1:i*sQ,:),...
               Q,dQi(j*sQ+1:i*sQ,:),z,0);
%
end

for t = 1:n  % main loop
%
    if rg
       Q1 = Q0 + Hg*x0g + Dg*z(t,m+r+1:m+r+rg)';
    else
       Q1 = Q0 + Hg*x0g;
    end

    if is_diag
       Q = diag(Q1) + Q00;
    else
       Q = vech2m(Q1,m);
    end
    CRCt = C*Q*C'; ESCt = E*Q*C';

    B1   = H*P0*H' + CRCt;
    if scaleb, U = cholp(B1, abs(B1));
    else       U = cholp(B1); end
    iB1 = (eye(size(U))/U)/U';

    K1  = (Phi*P0*H' + ESCt)*iB1;
    Phib  = Phi - K1*H;
    if r
       Gamb   = Gam  - K1*D;
       z1 = z(t,1:m)' - H*x0 - D*z(t,m+1:m+r)';
    else
       z1 = z(t,1:m)' - H*x0;
    end
    if is_diag
       z1g  = diag(z1*z1') - Q1;
    else
       z1g  = vech(z1*z1') - Q1;
    end

    for i = 1:np
    %
      j = i-1;

      if rg
         dQ1 = dQ0(j*mg+1:i*mg,:) + dHgi(j*mg+1:i*mg,:)*x0g + Hg*dx0gi(j*sPhig+1:i*sPhig,:) + dDgi(j*sDg+1:i*sDg,:)*z(t,m+r+1:m+r+rg)';
      else
         dQ1 = dQ0(j*mg+1:i*mg,:) + dHgi(j*mg+1:i*mg,:)*x0g + Hg*dx0gi(j*sPhig+1:i*sPhig,:);
      end

      if is_diag
         dQ = diag(dQ1) + dQ00(j*sQ+1:i*sQ,:);
      else
         dQ = vech2m(dQ1,m);
      end

      dB1i(j*m+1:i*m,:) = dHi(j*m+1:i*m,:)*P0*H'+ H*dP0i(j*p+1:i*p,:)*H'+ H*P0*dHi(j*m+1:i*m,:)' + ...
                          dCi(j*sC+1:i*sC,:)*Q*C' + C*dQ*C' + C*Q*dCi(j*sC+1:i*sC,:)';

      dK1i= (dPhii(j*p+1:i*p,:)*P0*H' + Phi*dP0i(j*p+1:i*p,:)*H' + ...
               Phi*P0*dHi(j*m+1:i*m,:)' + ...
               dEi(j*sE+1:i*sE,:)*Q*C' + E*dQ*C' + ...
               E*Q*dCi(j*sC+1:i*sC,:)' - K1*dB1i(j*m+1:i*m,:))*iB1;

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
                  dEi(j*sE+1:i*sE,:)*Q*E' + E*dQ*E' + ...
                  E*Q*dEi(j*sE+1:i*sE,:)' + dK1i*CRCt*K1' + ...
                  K1*dCi(j*sC+1:i*sC,:)*Q*C'*K1' + K1*C*dQ*C'*K1' + ...
                  K1*C*Q*dCi(j*sC+1:i*sC,:)'*K1' + K1*CRCt*dK1i' - ...
                  dK1i*ESCt' - K1*dCi(j*sC+1:i*sC,:)*Q'*E' - ...
                  K1*C*dQ'*E' - K1*C*Q'*dEi(j*sE+1:i*sE,:)' - ...
                  dEi(j*sE+1:i*sE,:)*Q*C'*K1' - E*dQ*C'*K1' - ...
                  E*Q*dCi(j*sC+1:i*sC,:)'*K1' - ESCt*dK1i';
         
      for k=1:i
          l = k-1;
          Im(i,k) = Im(i,k) + 0.5*trace( (dB1i(j*m+1:i*m,:)'*iB1)*(dB1i(l*m+1:k*m,:)'*iB1) ) + ...
                                         (dz1i(j*m+1:i*m,:)'*iB1*dz1i(l*m+1:k*m,:));
      end

      if is_diag
         dz1g  = diag(dz1i(j*m+1:i*m,:)*z1' + z1*dz1i(j*m+1:i*m,:)') - dQ1;
      else
         dz1g  = vech(dz1i(j*m+1:i*m,:)*z1' + z1*dz1i(j*m+1:i*m,:)') - dQ1;
      end

      if rg
         dx0gi(j*sPhig+1:i*sPhig,:) = dPhigi(j*sPhig+1:i*sPhig,:)*x0g + ...
                         Phig*dx0gi(j*sPhig+1:i*sPhig,:) + ...
                         dGamgi(j*sGamg+1:i*sGamg,:)*z(t,m+r+1:m+r+rg)' +...
                         dEgi(j*sEg+1:i*sEg,:)*z1g + Eg*dz1g;
      else
         dx0gi(j*sPhig+1:i*sPhig,:) = dPhigi(j*sPhig+1:i*sPhig,:)*x0g + ...
                         Phig*dx0gi(j*sPhig+1:i*sPhig,:) + ...
                         dEgi(j*sEg+1:i*sEg,:)*z1g + Eg*dz1g;
      end
    %  
    end

    if r
       x0 = Phib*x0 + Gamb*z(t,m+1:m+r)' + K1*z(t,1:m)';
    else
       x0 = Phi*x0 + K1*(z(t,1:m)' - H*x0);
    end

    if rg
       x0g  = Phig*x0g + Gamg*z(t,m+r+1:m+r+rg)' + Eg*z1g;
    else
       x0g  = Phig*x0g + Eg*z1g;
    end

    P0 = Phib*P0*Phib' + E*Q*E' + K1*CRCt*K1' - ...
         K1*ESCt' - ESCt*K1';
%
end % t

Im = Im + tril(Im,-1)';

if min(eig(Im)) <= 0, e4warn(8); end
varm = pinv(Im);

dts   = sqrt(diag(varm));
corrm = diag(1./dts)*varm*diag(1./dts);
E4OPTION(1) = oldfil;
