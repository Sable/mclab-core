function g = ggarch(theta, din, z)
% ggarch   - Computes the analytical gradient of LFGARCH.
%    g = ggarch(theta, din, z)
% theta  > parameter vector.
% din    > matrix which stores a description of the model dynamics.
% z      > matrix of observable variables.
% g      < gradient of the likelihood function for GARCH models.
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

if nargin < 3,  e4error(3); end
if nargin < 4,  userf = []; end

scaleb = E4OPTION(2);
vcond  = E4OPTION(3);
econd  = E4OPTION(4);
zeps   = E4OPTION(15);
deps   = 1e-8;

[H_D, type] = e4gthead(din);
is_diag = rem(type,100); % uncorrelated errors

[Phi, Gam, E, H, D, C, Q, Phig, Gamg, Eg, Hg, Dg] = garch2ss(theta, din);
% C matrix ignored
n  = size(z,1); l  = size(Phi,1); m  = size(H,1);
r  = size(Gam,2);
rg = size(Gamg,2);
if econd == 5
   if r, econd = 2; else, econd = 3; end;
end

if size(z,2) ~= m+r+rg, e4error(11); end

if is_diag
   Q0 = diag(Q);
   Q0d = Q - diag(Q0);
else, Q0 = vech(Q); end
Q00 = Q;

if size(theta,2) > 1  % look for fixed parameters in theta
   index = find(theta(:,2) == 0);
else
   index = (1:size(theta,1))';
end

np = size(index,1);
g  = zeros(np,1);

if r & (econd == 1 | econd == 4)
%
   if econd == 1
      u0 = mean(z(:,m+1:m+r))';
   else
      u0 = z(1,m+1:m+r)';
   end
   [x00, Sigm, iSigm, nonstat] =  djccl(Phi, E*Q*E', 0, Gam*u0);
%
else
   [x00, Sigm, iSigm, nonstat] =  djccl(Phi, E*Q*E', 0);
end

for i = 1:np
%
    x0 = x00;
    x0g = zeros(size(Phig,1),1);
    dx0g = zeros(size(Phig,1),1);

    [dPhi,dGam,dE,dH,dD,dC,dQ,dPhig,dGamg,dEg,dHg,dDg] = garch_dv(theta,din,index(i));

    if is_diag
       dQ0 = diag(dQ);
       dQ0d = dQ - diag(dQ0);
    else
       dQ0 = vech(dQ);
    end

    dSigm = zeros(l);
    dx0   = zeros(l,1);

    if any(any(dPhi)) | any(any(dE)) | any(any(dQ)) | any(any(dGam))
    %
       % numerical difference step
     
       if r & (econd == 1 | econd == 4)     
          [x1, Sigm1] =  djccl((Phi+dPhi*deps), (E+dE*deps)*(Q00+dQ*deps)*(E+dE*deps)', 0, (Gam+dGam*deps)*u0);
          dx0 = (x1-x0)/deps;
       else
          [x1, Sigm1] =  djccl((Phi+dPhi*deps), (E+dE*deps)*(Q00+dQ*deps)*(E+dE*deps)', 0);
       end
       dSigm = (Sigm1-Sigm)/deps;
    %
    end  

    gg = 0.0;

    Phib  = Phi - E*H;
    dPhib = dPhi - dE*H - E*dH;
    Phibb0 = eye(l,l);
    dPhibb0 = zeros(l,l);
    WW  = zeros(l,l); WZ = zeros(l,1);
    dWW = zeros(l,l); dWZ = zeros(l,1);

    for t=1:n
    %
        if r
           z1  = z(t,1:m)' - H*x0 - D*z(t,m+1:m+r)';
           dz1 = -dH*x0 -H*dx0 - dD*z(t,m+1:m+r)';
           dx0  = Phi*dx0 + dPhi*x0 + dGam*z(t,m+1:m+r)' + E*dz1 + dE*z1;
           x0  = Phi*x0 + Gam*z(t,m+1:m+r)' + E*z1;
        else
           z1  = z(t,:)' - H*x0;
           dz1 = -dH*x0 -H*dx0;
           dx0  = Phi*dx0 + dPhi*x0 + E*dz1 + dE*z1;
           x0  = Phi*x0 + E*z1;
        end

        if rg
        %
           Q1 = Q0 + Hg*x0g + Dg*z(t,m+r+1:m+r+rg)';
           dQ1 = dQ0 + dHg*x0g + Hg*dx0g + dDg*z(t,m+r+1:m+r+rg)';

           if is_diag
              z1g  = diag(z1*z1') - Q1;
              dz1g = diag(dz1*z1' + z1*dz1') - dQ1;
           else
              z1g  = vech(z1*z1') - Q1;
              dz1g = vech(dz1*z1' + z1*dz1') - dQ1;
           end

           dx0g = dPhig*x0g + Phig*dx0g + dGamg*z(t,m+r+1:m+r+rg)' +...
                  dEg*z1g + Eg*dz1g;
           x0g  = Phig*x0g + Gamg*z(t,m+r+1:m+r+rg)' + Eg*z1g;
        %
        else
        %
           Q1 = Q0 + Hg*x0g;
           dQ1 = dQ0 + dHg*x0g + Hg*dx0g;

           if is_diag
              z1g  = diag(z1*z1') - Q1;
              dz1g = diag(dz1*z1' + z1*dz1') - dQ1;
           else
              z1g  = vech(z1*z1') - Q1;
              dz1g = vech(dz1*z1' + z1*dz1') - dQ1;
           end
           dx0g = dPhig*x0g + Phig*dx0g + dEg*z1g + Eg*dz1g;
           x0g  = Phig*x0g + Eg*z1g;
        %
        end

        if is_diag
           Q = diag(Q1) + Q0d;
           dQ = diag(dQ1) + dQ0d;
        else
           Q = vech2M(Q1,m);
           dQ = vech2M(dQ1,m);
        end

        if scaleb, U = cholp(Q, abs(Q));
        else       U = cholp(Q); end
        iQ = (eye(size(U))/U)/U';

        HPhi= H*Phibb0;
        dHPhi = dH*Phibb0 + H*dPhibb0;
        dWW = dWW + dHPhi'*iQ*HPhi - HPhi'*iQ*dQ*iQ*HPhi + HPhi'*iQ*dHPhi;
        dWZ = dWZ + dHPhi'*iQ*z1 - HPhi'*iQ*dQ*iQ*z1 + HPhi'*iQ*dz1;
        WW  = WW + HPhi'*iQ*HPhi;
        WZ  = WZ + HPhi'*iQ*z1;
        dPhibb0 = dPhib*Phibb0 + Phib*dPhibb0;
        Phibb0 = Phib*Phibb0;
 
        z1i = z1'*iQ;
        gg  = gg + 0.5*trace(iQ*dQ) + z1i*dz1 - 0.5*(z1i*dQ*z1i');
    %
    end  % t

    if any(isnan([WW WZ])) | any(isinf([WW WZ])), e4error(25); end
    iWW = pinv(WW, zeps);

    if ~nonstat  % stationary system
    %
       [Ns S Ns] = svd(Sigm);
       k = find(diag(S) < zeps);
       if size(k,1) < l
          if size(k,1)
          %   
             N = Ns(:,1:k(1)-1);
             Sigm = S(1:k(1)-1,1:k(1)-1);
             WW = N'*WW*N;
             WZ = N'*WZ;
             dWW = N'*dWW*N;
             dWZ = N'*dWZ;
          %
          end
          gg = gg + .5*trace(pinv(eye(l)+Sigm*WW)*(dSigm*WW+Sigm*dWW));

          if econd == 2
             WZ = iWW*WZ;
             gg = gg - dWZ'*WZ + 0.5*WZ'*dWW*WZ;
          else
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

       if econd == 2
          WZ = iWW*WZ;
          gg = gg - dWZ'*WZ + 0.5*WZ'*dWW*WZ;
       else
          iWWdWZ = iSigWW*WZ;
          gg = gg  - dWZ'*iWWdWZ + 0.5*iWWdWZ'*(dWW - iSigm*dSigm*iSigm)*iWWdWZ;
       end
    %
    end

    g(i) = gg;
%
end  % i
