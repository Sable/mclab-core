function [Phi, H, T, E, Gam, F, Th, G] = echelon(ki, A, C, E, G, D)
%
%  [Phi, H, T, E, Gam, F, Th, G] = echelon(ki, A, C, E, G, D)
%  Transforms the realization (A,E,C,G,D) to the canonical Luenberger
%  observable form (Phi,E,H,Gam,D), given the observability indexes in ki.
%  The input parameters E,G,D are optional
%  Optionally, it returns the VARMAX echelon model in F = [F0 F1...Fk],
%  Th = [F0 Th1 ... Thk] and G = [G0 G1...Gk] la representación Varmax

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

      n = size(A,1);
      m = size(C,1);

      O = e4obsv(A,C);

      kimax = max(ki);
      ki2   = ki(find(ki > 0));
      mb    = size(ki2,1);

      M = zeros(n);
      k = 1; k2 = 0;

      for i=1:m
          k2 = k2 + ki(i);
          M(k:k2,:) = O(i:m:(i+ki(i)*m-1),:);
          k = k2+1;
      end
      M = inv(M);
      k2 = 0;
      U = zeros(n,mb);

      for i=1:mb
          k2 = k2 + ki2(i);
          U(:,i) = M(:,k2);
      end

      k = 1;
      T = zeros(n);

      for i=1:kimax
          for j=1:mb
              if i <= ki2(j)
                 T(:,k) = A^(ki2(j)-i)*U(:,j);
                 k = k+1;
              end
          end
      end

      iT = inv(T);         
       
      Phi = iT * A * T;
	  H = C * T;

	if nargin > 3, E = iT * E; end
    if nargin > 4
       Gam = iT * G;
       r = size(Gam,2);
    else, Gam = []; r = 0; end

      if nargout > 5
         ik = find(ki > 0);
         F0 = eye(m);
         F0(:,ik) = H(:,1:mb);
         F0 = inv(F0);
         F0r = F0(ik,ik);
         F = -Phi(:,1:mb)*F0r;
         Th = E;
         Th(:,ik) = Th(:,ik) + F;
         if r
            G = Gam + F*D(ik,:);
            G0 = F0*D;
         else, G = []; G0 = []; end

        [F,Th,G] = sstoech(ki,F0,F,F0,Th,G0,G);
      end
        
