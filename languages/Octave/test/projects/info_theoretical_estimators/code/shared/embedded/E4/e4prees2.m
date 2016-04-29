function [f, NB] = e4prees2(theta, din, R, ix, ij)
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

[Phi, Gam, E, H, D, C, Q] = thd2ss(theta, din);

n = size(Phi,1);
m = size(H,1);
r = max([size(Gam,2), size(D,2)]);

iC = pinv(C); E = E*iC;

i = ij(1);
j = ij(2);
pond = ij(3);
N = ij(4);

O = [H; zeros((j-1)*m,n)];

Phi2 = Phi;

for k=1:j-1
    O(k*m+1:(k+1)*m,:) = H*Phi2;
    Phi2 = Phi2*Phi;
end

iO = pinv(O);
O1 = O(1:(j-1)*m,:);

if ~r
%
   X = [iO*R(ix(2,1):ix(3,2),ix(1,1):ix(1,2)) zeros(n,m)];
   if pond
      e = R(ix(3,1):ix(3,2),ix(3,1):ix(3,2)) \ [R(ix(3,1):ix(3,2),ix(1,1):ix(2,2))-O1*((Phi-E*H)*X+E*R(ix(2,1):ix(2,2),ix(1,1):ix(2,2)))];
   else
      e = R(ix(3,1):ix(3,2),ix(1,1):ix(2,2))-O1*((Phi-E*H)*X+E*R(ix(2,1):ix(2,2),ix(1,1):ix(2,2)));
   end

   f = N*trace(e*e');

   if nargout > 1
      e = [R(ix(2,1):ix(2,2),ix(1,1):ix(2,2))-H*X];
      NB = iC*e*e'*iC';
   end
%
else
%
   Hid = [[D;O1*Gam] zeros(j*m,(j-1)*r)];
   for k=2:j
       Hid((k-1)*m+1:j*m,(k-1)*r+1:k*r) = Hid(1:(j-k+1)*m,1:r);
   end

   A = (eye(j*m)-O*iO)*Hid;
   Di = A(1:m,:);
   Gami = A(m+1:m*j,:);

   X = [iO*R(ix(4,1):ix(5,2),ix(1,1):ix(3,2)) zeros(n,m)];
   if pond
      e = R(ix(5,1):ix(5,2),ix(5,1):ix(5,2)) \ [R(ix(5,1):ix(5,2),ix(1,1):ix(4,2))-O1*((Phi-E*H)*X+E*R(ix(4,1):ix(4,2),ix(1,1):ix(4,2)))-(Gami-O1*E*Di)*R(ix(1,1):ix(1,2),ix(1,1):ix(4,2))];
   else
      e = R(ix(5,1):ix(5,2),ix(1,1):ix(4,2))-O1*((Phi-E*H)*X+E*R(ix(4,1):ix(4,2),ix(1,1):ix(4,2)))-(Gami-O1*E*Di)*R(ix(1,1):ix(1,2),ix(1,1):ix(4,2));
   end      
   f = N*trace(e*e');

   if nargout > 1
      e = [R(ix(4,1):ix(4,2),ix(1,1):ix(4,2))-H*X-Di*R(ix(1,1):ix(1,2),ix(1,1):ix(4,2))];
      NB = iC*e*e'*iC';
   end
%
end
