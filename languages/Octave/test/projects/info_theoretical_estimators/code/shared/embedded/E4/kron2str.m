function [F, Th, G] = kron2str(ki, r)
% kron2str - Gives the matrices structure for a VARMAX echelon model with ki kronecker indices
%        [F, Th, G] = kron2str(ki, r)
% In F (autoregressive), Th (moving average) and G (exogenous) 0 value means parameter
% constrained to 0, 1 value means parameter constrained to 1 and 2 means parameter not
% constrained

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

  m = size(ki,1);
  n = max(ki);

  ik = find(ki > 0);
  mb = size(ik,1);
  ki2 = ki(ik);

  F  = [2*tril(ones(m))-eye(m) zeros(m,n*m)];
  Th = [zeros(m,n*m)];
  if nargin > 1
     G  = [2*ones(m,r) zeros(m,n*r)];      
  else
     r = 0;
  end

  for k=1:m
      idx = find(ki(1:k-1) <= ki(k));
      F(k,idx) = zeros(1,size(idx,1));
  end

  for din=1:n
      ik2 = find(ki2 >= din);
      sik2 = size(ik2,1);

      Th(ik(ik2),m*(din-1)+1:m*din) = 2*ones(sik2,m);
      if r, G(ik(ik2),r*din+1:r*(din+1))  = 2*ones(sik2,r); end

      for h=1:size(ik2,1)
          idx = find(ki2 + din > ki2(ik2(h)));
          F(ik(ik2(h)),din*m+ik(idx)) = 2*ones(1,size(idx,1));
      end
  end

%