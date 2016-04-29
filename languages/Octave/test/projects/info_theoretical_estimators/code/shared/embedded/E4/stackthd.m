function [theta,din,lab] = stackthd(t1,d1,t2,d2,l1,l2)
% stackthd - Stacks to models in THD format
%    [theta, din, lab] = stackthd(t1,d1,t2,d2,l1,l2)
% t1,d1,l1 > THD representation of first model.
% t2,d2,l2 > THD representation of second model.
% theta, din, lab < [t1;t2], [d1;d2], [l1;l2].
%
% 18/9/97

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

if nargin < 4, e4error(3); end

st1 = size(t1);
st2 = size(t2);

theta = [[t1 zeros(st1(1),st2(2)-st1(2))];[t2 zeros(st2(1), st1(2)-st2(2))]];
din = [d1;d2];

if nargin > 4
   if nargin < 6, lab = e4strmat(l1,' '*zeros(size(t2,1),1));
   else, lab = e4strmat(l1,l2); end
end