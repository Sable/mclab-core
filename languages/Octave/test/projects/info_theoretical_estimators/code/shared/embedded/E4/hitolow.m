function [tl,dl,ll] = hitolow(th,dh, idxagg, typeagg, s)
%
% hitolow - Obtains the low-frequency VARMAX model corresponding to a high-frequency model in THD format.
%    [thetalow,dinlow,lablow] = hitolow(thetahigh,dinhigh, idxagg, typeagg, seasons)
% thetahigh > high-frequency model parameter vector.
% dinhigh   > din matrix which stores a description of the high-frequency model dynamics.
% idxagg    > (optional) mx1 vector. If idxagg(i) == 1, then endogenous
%             variable i is aggregated. Default value: idxagg = ones(m,1);
% typeagg   > (optional) type of aggregation: 1 stock, 0 flow (default)
% seasons   > (optional) number of high-frequency periods that add up to a low frequency sampling period,
%                e.g., s=4 if an annual data model will be obtained from a quarterly data model. 
%                If this parameter is not specified, hitolow assumes that s is the seasonal
%                frequency as specified in dinhigh 
% thetalow < low-frequency model parameter vector.
% dinlow   < din matrix which stores a description of the low-frequency model dynamics.
% lablow   < string matrix containing the parameter labels of the low-frequency model.
%
% 
% 22/7/04

% Copyright (C) 2004 José Casals, Miguel Jerez, Sonia Sotoca
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


 if nargin < 2, e4error(4); end
  [H_D, type, m, r, s0, n, np, userflag, userf, innov, szpriv] = e4gthead(dh);
  if nargin < 5
     s = s0;
     if nargin < 4
        typeagg = 0;
        if nargin < 3
           idxagg = ones(m,1);           
        end
     end   
  end
  
  idxagg = idxagg(:) ~= 0;
  magg = sum(idxagg);
  mnoagg = m-magg;

  if size(idxagg,1) ~= m, e4error(11); end

  if s == 1
     [Phil, Gaml, El, Hl, Dl, Cl, Ql, Sl, Rl] = thd2ss(th,dh);
  else   
     dinl = e4sthead(type, magg+mnoagg*s, r*s, s, n, np, userflag, userf, [0;0;0], [size(dh,1)+m+1;m+1]);
     dinl = [dinl;[typeagg;idxagg];dh];
  
     % 1) Low frequecy SS model
     [Phil, Gaml, El, Hl, Dl, Cl, Ql, Sl, Rl,Hli,Cli] = ufaggr(th, dinl);
     m = size(Hl,1); 
  end
   % 2) Obtains the minimal realization corresponding to the low-frequency SS model
  if n
     if r, B = [Gaml, El]; else, B = El; end 
     [Phil,B,Hl] = e4minreal(Phil,B,Hl,zeps);
     if r
        Gaml = B(:,1:r*s);
        El = B(:,r*s+1:size(B,2));
     else
        El = B;
     end
  end      
   
   % 3) Obtains the SS innovations model corresponding to the minimal
   % realization
  n = min(size(Phil,1),n);

  if n
     [El,Ql,UU,IUU,P] = sstoinn(Phil, El, Hl, Cl, Ql, Sl, Rl);      
     if r, B = [Gaml, El]; else, B = El; end
     [Phil,B,Hl] = e4minreal(Phil,B,Hl,zeps);
     n = size(Phil,1);
     if r
        Gaml = B(:,1:r*s);
        El = B(:,r*s+1:size(B,2));
     else
        El = B;
     end
  else
     Ql = Cl*Ql*Cl';
  end

% Check for FACTOR option
if E4OPTION(5) == 1, Ql = cholp(Ql)'; end

% 4) Obtains the varmax echelon model corresponding to the SS innovations model

if n
   if m > 1
      O = e4obsv(Phil,Hl);
      ki = zeros(m,1);
      km = 1:m;
      ko = 0;
      rangoa = 0;
      for i=1:n
          ikm = ones(1,size(km,2));
          for j=1:size(km,2)
              rango = rank(O(1:(ko+km(j)),:));
              if rango > rangoa
                 ki(km(j)) = ki(km(j))+1;
                 rangoa = rango;
              else
                 ikm(j) = 0;
              end
          end
          km = km(~(~ikm));
          if isempty(km)
             if sum(ki) ~= n
                error('A numerical error occured when approximating the Kronecker indexes');
             end
             break;
          end   
          ko = i*m;          
      end
   else
      ki = n;
   end
   if r
      [Phil, Hl, T, El, Gaml, F, Th, G] = echelon(ki, Phil, Hl, El, Gaml, Dl);
   else
      [Phil, Hl, T, El, Gaml, F, Th] = echelon(ki, Phil, Hl, El);
      G = [];
   end       
   k0 = find(G < zeps & G > -zeps);
   if ~isempty(k0),G(k0) = NaN*ones(size(k0)); end
   k0 = find(F < zeps & F > -zeps);
   if ~isempty(k0),F(k0) = NaN*ones(size(k0)); end
   k0 = find(Th < zeps & Th > -zeps);
   if ~isempty(k0),Th(k0) = NaN*ones(size(k0)); end
      
   if m > 1
      if ~r
        [tl,dl,ll] = ech2thd(ki,F,Th(:,m+1:size(Th,2)),Ql,1);
      else
        [tl,dl,ll] = ech2thd(ki,F,Th(:,m+1:size(Th,2)),Ql,1,G,r*s);
      end
   else
      if ~r
        [tl,dl,ll] = arma2thd(F(:,m+1:size(F,2)),[],Th(:,m+1:size(Th,2)),[],Ql,1);
      else
        [tl,dl,ll] = arma2thd(F(:,m+1:size(F,2)),[],Th(:,m+1:size(Th,2)),[],Ql,1,G,r*s);
      end
   end
else
   [tl,dl,ll] = arma2thd([],[],[],[],Ql,1,Dl,r*s);
end
  
