function index = e4ds(theta, din)
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


[H_D, type, m, r, s, n, np, userflag, userf, innov, szpriv] = e4gthead(din);
index = zeros(np,2);

if type < 4
   ptr = H_D + szpriv(2) + 1;
   ptr2 = 0;
   k = [0;0;1;1];
   for i=1:4
       index(ptr2+1:ptr2+din(ptr),1) = k(i)*ones(din(ptr),1);
       ptr2 = ptr2 + din(ptr);
       ptr = ptr + din(ptr) + 1;
   end
   ptr = H_D+innov(2);
   v = din(ptr);
   if v, index(np-v+1:np,:) = ones(v,2); end
%
elseif type == 4 % echelon
   index = e4ds(theta,din(H_D+szpriv(2)+1:size(din,1)));
elseif type == 7
   ptr = H_D + szpriv(2) + 1;
   ptr2 = 0;
   k = [0 0;0 0;1 0;0 0;0 0;1 0; 1 1; 1 1; 1 1];
   if innov(1), nm = 7; else, nm = 9; end
   for i=1:nm
       index(ptr2+1:ptr2+din(ptr),:) = ones(din(ptr),1)*k(i,:);
       ptr2 = ptr2 + din(ptr);
       ptr = ptr + din(ptr) + 1;
   end
else % composite models
   n = size(din,1);
   ptr = H_D + szpriv(2) + 1;
   ptr2 = 1;
   while ptr < n
         [H_D, type, m2, r2, s2, n2, np2, userflag, userf, innov2, szpriv] = e4gthead(din(ptr:n));
         index(ptr2:ptr2+np2-1,:) = e4ds(theta(ptr2:ptr2+np2-1,:), din(ptr:ptr+szpriv(1)+H_D-1));
         ptr = ptr + H_D + szpriv(1);
         ptr2 = ptr2 + np2;
   end   
end 