function [theta, din, labtheta] = sc2thd(theta,din,t2,d2, lab1, lab2)
% sc2thd   - Generates the THD representation of a time-varying parameters model.
%    [theta, din, lab] = sc2thd(t1,d1, t2,d2, lab1,lab2)
% This function only supports models with one endogenous variable.
% t1, d1 > THD representation of the parametric change law.
% t2, d2 > THD representation of the fixed parameter output model. This
%          parameters are not required [See Swamy and Tavlas (1994)].
% theta, din < THD representation of the whole model.
%
% 30/4/97

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

if nargin  < 2, e4error(3); end
if nargin < 6, lab2 = []; end
if nargin < 5, lab1 = []; end
[H_D, type, m, r, s, n, np, userflag, userf, isinnov, szpriv] = e4gthead(din);
if isinnov(1)
   v = m; if n, q = m; else, q = 0; end
else
   v = isinnov(3); if n, q = isinnov(2); else, q = 0; end
end
if nargin == 3, labtheta = t2;
elseif nargin >= 4
%
   [H_D, typec, mc, rc, sc, nc, npc, userflagc, userfc, isinnovc, szprivc] = e4gthead(d2);
   if mc > 1, e4error(29); end
   if nargin == 6 & nargout > 2, lab2 = [ones(size(lab2,1),1)*'c' lab2]; end
   [theta, din, labtheta] = stackthd(theta,din,t2,d2,lab1,lab2);
   
   r = r + rc;
   n = n + nc;
   np = np + npc;
   if isinnov(1)
      v = v + mc; if nc, q = q + mc; end
   else
      v = v + isinnovc(3); if nc, q = q + isinnovc(2); end
   end
%
end

din0 = e4sthead(200, 1, r, s, n, np, 0, [], [0 q v], size(din,1));
din = [din0;din];
