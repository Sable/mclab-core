function [F,Th,G] = sstoech(ki,F0,F1,Th0,Th1,G0,G1)
%  sstoech  - Internal function called by echelon used for building the
%  matrices F, TH and G of an echelon representation from the relevant
%  components of the Luenberger Canonical form.
%   [F,Th,G] = sstoech(ki,F0,F1,Th0,Th1,G0,G1)
%
% 7/7/04

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
%
   k  = max(ki);
   ik = find(ki > 0);
   mb = sum(ki > 0);
   m  = size(F0,1);
   r  = 0;

   F  = [F0 zeros(m,k*m)];
   Th = [Th0 zeros(m,k*m)];
   if nargin > 5
      r  = size(G0,2);
      G  = [G0 zeros(m,k*r)];      
   end

   n = 0;
   for i=1:k
       ik2 = find(ki >= i);
       mb2 = sum(ki >= i);
       F(ik2,m*i+ik)         = F1(n+1:n+mb2,:);
       Th(ik2,m*i+1:m*(i+1)) = Th1(n+1:n+mb2,:);
       if r, G(ik2,r*i+1:r*(i+1))  = G1(n+1:n+mb2,:); end
       n = n+mb2;
   end