function [ys, yd, yid, thetas, dins] = detcomp(theta, din, y, adj)
% detcomp   - Decomposes a series into additive input-related and error-related components.
%    [ys, yd, yid, ts, ds] = detcomp(theta, din, y, adj)
% theta     > parameter vector.
% din     > matrix which stores a description of the model dynamics.
% y      > matrix of observable variables.
% adj    > logical flag:
%             adj=1 (default), applies the marginal adjustment to compute the contribution of each input  
%             adj=0 applies the alternative aliquot split criterion 
% ys     < smoothed estimate of the error-related component.
% yd     < smoothed estimate of the total input-related components.
% yid    < estimates for the individual effects of each input.
% thetas < parameter vector of the model for the error-related component.
% dins   < matrix which stores a description of the error-related model dynamics.
%
% 7/4/2006
%
% Copyright (C) 2006 José Casals, Miguel Jerez, Sonia Sotoca
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

zeps = .00001*1e-3;
scaleb  = E4OPTION(2);

if nargin < 4, adj = 1; end

 if nargin < 3, e4error(4); end
 [H_D, type, m, r, s, n, np, userflag, userf, innov, szpriv] = e4gthead(din);
 if r == 0, e4error(4); end
  
 [Phi, Gam, E, H, D, C, Q, S, R] = thd2ss(theta, din);
 l = size(Phi,1);  
  
 % 2) Obtains the minimal realization corresponding to the low-frequency SS model
 [Phid,Gamd,Hd,Td,Kd,nd] = e4ctrbf(Phi,Gam,H,zeps,1);
 [Phis,Es,Hs,Ts,Ks,ns] = e4ctrbf(Phi,E,H,zeps,1);
 
 Td = Td(1:nd,:);
 Ts = Ts(1:ns,:);
 
 if ns
    [ign, Sigm, iSigm, nonstat] =  djccl(Phis, Es*Q*Es', 0);
    if ~nonstat, iSigm = inv(Sigm); end
    [thetas, dins] = ss2thd(Phis, [], Es, Hs, [], C, Q, S, R,s);
 else
    [thetas, dins] = arma2thd([], [], [], [], C*R*C',s);     
 end    
 
 x0 = zeros(l,1);
 P0 = zeros(l);
   
 n = size(y,1);
 EQEt=E*Q*E';
 H0 = H;   C0 = C;   D0 = D;
 Phibb0 = eye(l,l);
 WW  = zeros(l,l); WZ = zeros(l,1);
 
 for t=1:n
 
       trueobs = find(~isnan(y(t,1:m)'));
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
             y1s  = y(t,trueobs)' - D*y(t,m+1:m+r)' - H*x0;
             x0  = Phi*x0 + Gam*y(t,m+1:m+r)' + K1*y1s;
          else
             y1s  = y(t,trueobs)' - H*x0;
             x0  = Phi*x0 + K1*y1s;
          end

          Phib= Phi - K1*H; 
          P0  = Phib*P0*Phib' + EQEt + K1*C*R*C'*K1' - K1*C*S'*E' - E*S*C'*K1';
          HPhi= H*Phibb0;
          WW  = WW + HPhi'*iB1*HPhi;
          WZ  = WZ + HPhi'*iB1*y1s;
       %
       else
          Phib= Phi; 
          if r, x0  = Phi*x0 + Gam*y(t,m+1:m+r)';
          else  x0  = Phi*x0; end
          P0 = Phi*P0*Phi' + EQEt;
       end

       Phibb0 = Phib*Phibb0;
  %
 end % t

WWdd = Td*WW*Td';   
WWss = Ts*WW*Ts';
WWds = Td*WW*Ts';
WZd = Td*WZ;
WZs = Ts*WZ;

if ns & nd
   x1d = pinv(WWdd - WWds*inv(iSigm + WWss)*WWds')*(WZd - WWds*inv(iSigm + WWss)*WZs);
elseif nd
   x1d = pinv(WWdd)*WZd;
else
   x1d = 0;
end

if ~nd
   yd = y(:,m+1:m+r)*D';
   yid = [];
   if r > 1
      for i = 1:r, yid = [yid y(:,m+i)*D(:,i)']; end
   end   
else
   yd = propaga(Phid, Gamd, Hd, D, x1d,y(:,m+1:m+r));
   yid = [];
   if r > 1
      ki = getkron(Phid', Gamd');
      [Phid, Gamd, U, Hd] = echelon(ki, Phid', Gamd', Hd');
      Phid = Phid';
      Gamd = Gamd';
      Hd = Hd';
      C = e4obsv(Phid', Gamd');
      C = C';
      kncero = ~ones(nd,r);
      for i=1:r
          kncero(:,i) = sum(abs(C(:,i:r:size(C,2)))')' > zeps;
      end
      x1d = U'*x1d;
      x1di = zeros(nd,r);
      x1dc = x1d*ones(1,r);
      x1di(kncero) = x1dc(kncero);      
      x1dia = zeros(nd,r);
      if adj == 1  % marginal adjustment
         k = (1:r)';
         I = eye(nd);
         for i=1:r
             [U,S,V] = svd(x1di(:,k~=i),0);
             snz = sum(diag(S) > 0);
             if ~snz, M = I; else, M = I - U(:,1:snz)*U(:,1:snz)'; end
             x1dia(:,i) = M*x1di(:,i);
         end    
         xe = x1d - sum(x1dia')';
      else
         x1da = sum(x1di')';
         x1dia = x1di.*((x1d./x1da)*ones(1,r));
      end         
         
      for i = 1:r, yid = [yid propaga(Phid, Gamd(:,i), Hd, D(:,i), x1dia(:,i),y(:,m+i))]; end
      if adj == 1, yid = [yid propaga(Phid,[],Hd,[],xe,zeros(n,1))]; end
   end
end   


ys = fismiss(theta, din, y) - yd;
