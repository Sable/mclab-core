function topt = e4preest(theta, din, z)
% e4preest   - Computes a fast estimate of the parameters of a model in THD format.
%     theta2 = e4preest(theta, din, z)
% theta  > parameter vector.
% din    > matrix which stores a description of the model dynamics.
% z      > matrix of observable variables.
% topt   < vector of estimates.
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

[H_D, mtype, m, r, s, n, np, userflag, userf, innov, szpriv] = e4gthead(din);

if mtype < 100  % standard model
   topt = e4prees1(theta, din, z);
   return;
end

ptr1 = H_D+szpriv(2)+1;
[H_D, type, m, r, s, n, np, userflag, userf, innov, szpriv] = e4gthead(din(ptr1:size(din,1)));

if fix(mtype/100) == 1 % GARCH 
   theta1 = theta(1:np,:);
   din1 = din(ptr1:ptr1+H_D+szpriv(1)-1);
   topt   = e4prees1(theta1, din1, z(:,1:m+r));
   [f e]  = lffast(topt,din1,z(:,1:m+r));
   ptr1 = ptr1 + H_D + szpriv(1);
   din2   = din(ptr1:size(din,1));
   [H_D, type, mg, rg, s, n, npg, userflag, userf, innov, szpriv] = e4gthead(din2);
   theta2 = theta(np+1:size(theta,1),:);
   is_diag = rem(mtype,100);
   if is_diag
      ze = e.^2;
   else
      ze = zeros(size(e,1),mg);
      k = 1;
      for i=1:m
          for j=i:m
              ze(:,k) = e(:,i).*e(:,j);
              k=k+1;
          end
      end
   end
   if rg, ze = [ze z(:,m+r+1:m+r+rg)]; end
   topt = [topt; e4prees1(theta2, din2, ze)];
   topt = topt(1:size(theta,1),:);
   return;
end

if fix(mtype/100) == 2 % SC
   ptr1 = ptr1 + H_D + szpriv(1);
   if ptr1 <= size(din,1)
      din2   = din(ptr1:size(din,1));
      [H_D, type, m2, r2] = e4gthead(din2);
      theta2 = theta(np+1:size(theta,1),:);

      if r2 >= m
         if sum(sum(z(:,2:m+1) ~= z(:,m+2:2*m+1)))
            z(:,1) = z(:,1) - z(:,2:m+1)*(z(:,2:m+1)\z(:,1));
         end
      else
         z(:,1) = z(:,1) - z(:,2:m+1)*(z(:,2:m+1)\z(:,1));
      end

      topt = theta;
      topt(:,1) = zeros(size(theta,1),1);
      topt(np1+1:size(theta,1),:) = e4prees1(theta2, din2, z(:,[1 m+r+2:size(z,2)]));
   end
end
