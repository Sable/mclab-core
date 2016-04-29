function prtmod(theta, din, labtheta, level);
% prtmod   - Displays information about a THD model.
%    prtmod(theta, din, lab)
%
% 10/1/97

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

if     nargin < 2, e4error(3);
elseif nargin == 2; labtheta = [];
end

if nargin < 4, level = 0; strlv = []; else, strlv = sprintf('%c', 32*ones(level,1));end

if size(labtheta,1) == 0, labtheta = e4strmat(ones(size(theta,1),14)*32); end
if size(labtheta,2) < 14
   labtheta = [labtheta e4strmat(ones(size(theta,1),14-size(labtheta,2))*32)];
end

if size(theta,1) ~= size(labtheta,1), e4error(11); end

[H_D, type, m, r, s, n, np, userflag, userf, innov, szpriv] = e4gthead(din);

mgarch = 0; mpc = 0; mnest = 0; mper = 0; mcomp = 0;
if fix(type/100) == 1, mgarch = 1; modstr = 'GARCH model';
elseif fix(type/100) == 2, mpc    = 1; modstr = 'Changing parameters model';
elseif fix(type/100) == 3, mper   = 1; modstr = 'Periodic model';
elseif type == 21, mnest  = 1;  modstr = 'Nested model in inputs';
elseif type == 20, mnest  = 1;  modstr = 'Nested model in errors';
elseif fix(type/10) == 3, mcomp  = 1;  modstr = 'Components model';
elseif  type == 0, modstr = 'White noise model';
elseif  type == 1, modstr = 'VARMAX model';
elseif  type == 2, modstr = 'Structural model';
elseif  type == 3, modstr = 'Transfer function model';
elseif  type == 4, modstr = 'VARMAX echelon model';
elseif  type == 7, modstr = 'Native SS model';
else, modstr = 'Unknown model';
end

if userflag==1, modstr = [modstr ', with user function: ' userf(1,:) ];
elseif userflag==2, modstr = [modstr ', with user functions: ' userf(1,:) userf(2,:) ]; end
if innov(1), modstr = [modstr ' (innovations model)']; end

if ~level
   disp('*************************** Model ***************************');
end

disp([strlv modstr]);
disp([strlv int2str(m) ' endogenous v., ' int2str(r) ' exogenous v.']);
disp([strlv 'Seasonality: ' int2str(s) ]);
disp([strlv 'SS vector dimension: ' int2str(n)]);

ptr = H_D + szpriv(2) + 1;
ptr2 = 1;
sd = size(din,1);

if ~mpc & ~mgarch & ~mnest & ~mcomp & ~mper
   disp([strlv 'Parameters (* denotes constrained parameter):']);
   if (size(theta,2) == 2)
      ndx = find(theta(:,2)~=0);
      if ~isempty(ndx)
         labtheta(ndx,size(labtheta,2)) = e4strmat(ones(size(ndx))*42);
      end
   end
   for i=1:size(theta,1)
       disp([strlv sprintf('%14s %10.4f', labtheta(i,:), theta(i,1))]);
   end
elseif mgarch | mpc
   if mgarch
      if type == 21, disp([strlv 'Diagonal GARCH']); end
      disp([strlv 'Endogenous variables model:']);
   else
      disp([strlv 'Stochastic coefficients model:']);
   end

   [H_D, type, m, r, s, n, np, userflag, userf, innov, szpriv] = e4gthead(din(ptr:size(din,1)));
   if isempty(labtheta)
      prtmod(theta(ptr2:ptr2+np-1,:), din(ptr:ptr+H_D+szpriv(1)-1), [], level+2);
   else
      prtmod(theta(ptr2:ptr2+np-1,:), din(ptr:ptr+H_D+szpriv(1)-1), labtheta(ptr2:ptr2+np-1,:), level+2);
   end
   ptr = ptr + H_D + szpriv(1);
   ptr2 = ptr2 + np;

   if ptr <= size(din,1)
      if mgarch
         disp([strlv 'GARCH model of noise:']);
      else
         disp([strlv 'Fixed model:']);
      end
      if isempty(labtheta)
         prtmod(theta(ptr2:size(theta,1),:), din(ptr:sd), [], level+2);
      else
         prtmod(theta(ptr2:size(theta,1),:), din(ptr:sd), labtheta(ptr2:size(theta,1),:), level+2);
      end
   end

else

   disp([strlv 'Submodels:']);
   disp([strlv '{']);
   while ptr <= sd
      [H_D, type, m, r, s, n, np, userflag, userf, innov, szpriv] = e4gthead(din(ptr:size(din,1)));
      if isempty(labtheta)
         prtmod(theta(ptr2:ptr2+np-1,:), din(ptr:ptr+H_D+szpriv(1)-1), [], level+2);
      else
         prtmod(theta(ptr2:ptr2+np-1,:), din(ptr:ptr+H_D+szpriv(1)-1), labtheta(ptr2:ptr2+np-1,:), level+2);
      end
      ptr = ptr + H_D + szpriv(1);
      ptr2 = ptr2 + np;
   end
   disp([strlv '}']);
end

if ~level
   disp('*************************************************************');
   disp(' ');
elseif ~mpc & ~mgarch & ~mnest & ~mcomp & ~mper
   disp([strlv '--------------']);
end
