function [z1, vT, wT, bvect, vvT, vwT] = residual(theta, din, z, stand)
% residual   - residuals.
%    [z1, vT, wT, vz1, vvT, vwT] = residual(theta, din, z, stand)
% theta > parameter vector.
% din   > matrix which stores a description of the model dynamics.
% z     > matrix of observable variables.
% stand > flag for standarized innovations
% z1    < innovations (standarized if stand == 1)
% vT    < E[v(t)/T] smoothed observation error ([] for GARCH models)
% wT    < E[w(t)/T] smoothed state error ([] for GARCH models)
% vz1   < Var(z1)
% vvT   < Var(v(t) - v(t/T)) (not for GARCH models)
% vwT   < Var(w(t) - w(t/T)) (not for GARCH models)
%
% 14/1/98

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

% check model

[H_D, mtipo] = e4gthead(din);

if nargin < 4, stand = 0; end

if fix(mtipo/100) == 1  %% Garch model
%
   [f, z1, zh, ign, bvect] = lfgarch(theta, din, z);
   if stand, z1 = zh; end
   vT = []; wT = []; vvT = []; vwT = [];
   return
%
elseif fix(mtipo/200) == 1  %% SC model
%
   [z1, vT, wT, bvect, vvT, vwT] = fisrsc(theta, din, z);
%
else
%
   if sum(sum(isnan(z)))
      [z1, vT, wT, bvect, vvT, vwT] = fisriss(theta, din, z);
      if stand
         m = size(z1,2);
         for t=1:size(z1,1)
             trueobs = find(~isnan(z1(t,:)));
             if size(trueobs,1)
                B1 = bvect((t-1)*m+trueobs,trueobs);
                [U S U] = svd(B1);
                z1(t,trueobs) = z1(t,trueobs)*U*sqrt(pinv(S));
             end
         end
      end
      return;
   end
   [z1, vT, wT, bvect, vvT, vwT] = fisres(theta, din, z);
%
end

if stand
   m = size(z1,2);
   for t=1:size(z1,1)
       B1 = bvect((t-1)*m+1:t*m,:);
       [U S U] = svd(B1);
       z1(t,:) = z1(t,:)*U*sqrt(pinv(S));
   end
end
