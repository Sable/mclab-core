function plotperf(func,theta0,incs,tit,P1,P2,P3,P4,P5)
% plotperf - Plots the profile of a function f(x) around x.
%    plotperf(func, x, incs, tit, P1,P2,P3,P4,P5)
%  func   > name of the function to be plotted.
%  x      > (kx1) vector of values.
%  incs   > (kx3) lower and upper bounds and number of evaluations.
%           If the number of evaluations is zero, the corresponding plot
%           is omited.
%  tit    > string which contains a title for the graph.
% When plotting likelihood functions, P1 = din and P2 = data matrix.
%
% 6/3/97

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
prog  = E4OPTION(18);

if nargin < 4, e4error(3); end
if ~exist(func), e4error(33); end
if (size(theta0,1) ~= size(incs,1)) | (size(incs,2) ~= 3)
   e4error(11);
end

e_str = [func '( theta ' getparms(nargin-4) ')'];

figure; if ~prog, whitebg('w'); end; close;
incs(:,3) = round(incs(:,3));
index = find(incs(:,3) > 1);
steps = (incs(index,2)-incs(index,1))./incs(index,3);
theta = theta0;
for i = 1:size(index,1)
%
   vals = zeros(incs(index(i),3)+1,1);
   f1   = zeros(size(vals));
   for j=0:incs(index(i),3)
   %
     theta(index(i)) = incs(index(i),1) + j*steps(i);
     vals(j+1) = theta(index(i),1);
     f1(j+1)   = eval(e_str);
   %
   end
   theta = theta0;
   figure;
   plot(vals,f1);
   axvect = [min(vals), max(vals), min(f1), max(f1)];
   axis(axvect);
   title([tit ' Function ' func ', ' sprintf('x(%d)',i)] );
end
