function [dPhi, dGam, dE, dH, dD, dC, dQ, dS, dR] = ss_dvn(theta, din, i)
% ss_dvn   - Computes the partial derivatives of the matrices of a nested SS model
% with respect to the i-th parameter in theta.
%    [dPhi, dGam, dE, dH, dD, dC, dQ, dS, dR] = ss_dvn(theta, din, i)
% theta > parameter vector.
% din   > matrix which stores a description of the model dynamics.
% i     > index of the parameter in the denominator of the partial derivative.
%
% 30/4/98

% Copyright (C) 1998 Jaime Terceiro
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

[H_D, type, m, r, s, n, np, userflag, userf, isinnov, szpriv] = e4gthead(din);

if fix(type/10) ~= 2, e4error(14); end

if userflag
   if userflag < 2, e4error(36); end
   [dPhi,dGam,dE,dH,dD,dC,dQ,dS,dR] = ss_dvown(theta,din,i, userf(2,:));
   return;
end
   mt = m; nt = n; rt = r;
   mode = rem(type,20);

   dPhi = zeros(n);
   if r, dGam = zeros(n,r); end
   dH = zeros(m, n);

   ptr = H_D+szpriv(2)+1;
   ptr2 = 1;
   sd = size(din,1);
   if mode
      dE = [];
      dC = [];
      dGam = []; dD = [];
      dQ = []; dR = []; dS = [];
      mc1 = 1;
      ec1 = 1;
      cc1 = 1;
   else
      dE = zeros(n,m);
      dE0 =[];
      if r, dGam0 = []; dD = 0; else, dGam = []; dD = []; end
      dC = 1;
   end
   nc1 = 1;

   upd = inf;
   j = 1;

   while ptr < sd
       [H_D, type, m, r, s, n, np, userflag, userf, isinnov, szpriv] = e4gthead(din(ptr:sd));
       if i >= ptr2 & i < ptr2+np
          [dPhi1, dGam1, dE1, dH1, dD1, dC1, dQ1, dS1, dR1] = ss_dv(theta(ptr2:ptr2+np-1,:), din(ptr:ptr+H_D+szpriv(1)-1), i-ptr2+1);
          upd = j;
       else
          [Phi1, Gam1, E1, H1, D1, C1, dQ1, dS1, dR1] = thd2ss(theta(ptr2:ptr2+np-1,:), din(ptr:ptr+H_D+szpriv(1)-1));
       end

       if mode % nesting observable inputs
          nc2 = nc1+n-1;
          mc2 = mc1+m-1;
          
          if nc1 > 1
             if upd == j
                dGamdH1 = dGam*dH1; dGamdC1 = dGam*dC1; 
                if r, dGamdD1 = dGam*dD1; else, dGamdD1 = []; end
             else
                dGamH1 = dGam*H1; dGamC1 = dGam*C1; 
                if r, dGamD1 = dGam*D1; else, dGamD1 = [];  end                 
             end
          else
             dGamH1 = []; dGamC1 = []; dGamD1 = [];
             dGamdH1 = []; dGamdC1 = []; dGamdD1 = [];
          end
          if upd == j
             if ~isempty(dD)
                dDdH1 = dD*dH1; dDdC1 = dD*dC1;
                if r, dDdD1 = dD*dD1; else, dDdD1 = []; end
             else
                dDdH1 = []; dDdC1 = []; dDdD1 = [];
             end
          else
             if ~isempty(dD)
                dDH1 = dD*H1; dDC1 = dD*C1;
                if r, dDD1 = dD*D1; else, dDD1 = [];  end
             else
                dDH1 = []; dDC1 = []; dDD1 = [];
             end
          end
         
          if isinnov(1)
             ec2 = ec1+size(dQ1,1)-1;
          else
             if n, ec2 = ec1+size(dQ1,1)+size(dR1,1)-1; else, ec2 = ec1+size(dR1,1)-1; end
          end
          cc2 = cc1 + size(dR1,1)-1;          
          if upd > j
             if r, dGam = [dGamD1;Gam1(1:n,:)]; else, dGam = []; end
             dD = [dDD1;D1];
          elseif upd == j
             if n
                dPhi(1:nc2, nc1:nc2) = [dGamdH1;dPhi1];
                dH(1:mc2,nc1:nc2) = [dDdH1;dH1];
             end
             if isinnov(1)
                dE(1:nc2,ec1:ec2) = [dGamdC1; dE1(1:n,:)];
                dQ(ec1:ec2,ec1:ec2) = dQ1;
                dS(ec1:ec2,cc1:cc2) = dS1;
             else
                if n
                   dE(1:nc2,ec1:ec2) = [dGamdC1 zeros(size(dGam,1),size(dE1,2)); zeros(n,size(dC1,2)) dE1(1:n,:)];
                   dQ(ec1:ec2,ec1:ec2)= [dR1 zeros(size(dR1,1),size(dQ1,2));zeros(size(dQ1,1), size(dR1,2)) dQ1];
                   dS(ec1:ec2,cc1:cc2) = [dR1;dS1];                    
                else
                   dE(1:nc2,ec1:ec2) = dGamdC1;
                   dQ(ec1:ec2,ec1:ec2) = dR1;
                   dS(ec1:ec2,cc1:cc2) = dR1;
                end
             end
             if r, dGam = [dGamdD1;dGam1(1:n,:)]; else, dGam = []; end
             dC(1:mc2,cc1:cc2) = [dDdC1;dC1];
             dR(cc1:cc2,cc1:cc2) = dR1;
             dD = [dDdD1;dD1];
             ncf = nc2; mcf = mc2;
          else             
             if n
                dPhi(1:ncf,nc1:nc2) = [dGamH1];
                dH(1:mcf,nc1:nc2) = dDH1;
             end
             if isinnov(1)
                dE(1:ncf,ec1:ec2) = [dGamC1];
             else
                dE(1:ncf,ec1:ec2) = [dGamC1 zeros(ncf,size(dE1,2))];
             end
             if r, dGam = dGamD1; else, dGam = []; end
             dC(1:mcf,cc1:cc2) = dDC1;
             dD = dDD1;
          end
          nc1 = nc2+1;
          mc1 = mc2+1;
          ec1 = ec2+1; cc1 = cc2+1;
      else
          nc2 = nc1+n-1;
          if nc1 > 1
             if upd == j
                dE0dH1 = dE0*dH1; dE0dC1 = dE0*dC1;
                if r, dE0dD1 = dE0*dD1; end
             else
                dE0H1 = dE0*H1; dE0C1 = dE0*C1;
                if r, dE0D1 = dE0*D1; end
             end
          else
             dE0dH1 = []; dE0H1 = []; dE0dC1 = []; dE0C1 = []; dE0dD1 = []; dE0D1 = [];
          end

          if upd > j
             dE(1:nc2,:) = [dE0C1;E1(1:n,:)];
             dC = dC*C1;
             dE0 = dE(1:nc2,:);
          elseif upd == j
             if n
                dPhi(1:nc2,nc1:nc2) = [dE0dH1;dPhi1];
                dH(:,nc1:nc2) = dC*dH1;
             end
             dE(1:nc2,:) = [dE0dC1;dE1(1:n,:)];

             if r
                dGam(1:nc2,:) = [dE0dD1;dGam1(1:n,:)];
                dD = dC*dD1;
                dGam0 = dGam(1:nc2,:);
             end
             dC = dC*dC1;
             ncf = nc2;
             dE0 = dE(1:nc2,:);
          else
             if n
                dPhi(1:ncf,nc1:nc2) = dE0H1;
                dH(:,nc1:nc2) = dC*H1;
             end
             if nc1 > 1, dE(1:ncf,:) = dE0C1; end

             if r
                if nc1 > 1, dGam(1:ncf,:) = dGam0+dE0D1; end
                dD = dD+dC*D1;
                dGam0 = dGam(1:ncf,:);
             end
             dC = dC*C1;          
             dE0 = dE(1:ncf,:);
          end
          nc1 = nc2+1;
      end

      ptr = ptr + H_D + szpriv(1);
      ptr2 = ptr2 + np;
      j = j+1;
   end

   if ~mode
      if upd == j-1, dQ = dQ1; else  dQ = zeros(size(dQ1)); end
      dS = dQ; dR = dQ;
   else
      dE = [dE zeros(size(dE,1),ec2-size(dE,2)); zeros(nc2-size(dE,1), ec2)];
      dQ = [dQ zeros(size(dQ,1),ec2-size(dQ,2)); zeros(ec2-size(dQ,1), ec2)];
      dR = [dR zeros(size(dR,1),cc2-size(dR,2)); zeros(cc2-size(dR,1), cc2)];
      dS = [dS zeros(size(dS,1),cc2-size(dS,2)); zeros(ec2-size(dS,1), cc2)];
      dC = [dC; zeros(mc2-size(dC,1), cc2)];
      dGam = [dGam;zeros(nc2-ncf,r)]; dD = [dD;zeros(mc2-mcf,r)];
   end
   
   if ~nt
      dPhi = 0; dH = zeros(mt,1);
      if r, dGam = zeros(1,rt); end
      dE = zeros(1,size(dQ,1));
   end