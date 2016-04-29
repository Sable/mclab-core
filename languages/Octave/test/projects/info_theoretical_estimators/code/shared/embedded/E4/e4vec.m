function [theta, din, thetalab] = e4vec(M, rootnam, sym, blocks, init)
% e4vec    - Transforms a system matrix to the corresponding THD format.
%    [theta, din, lab] = e4vec(M, rootnam, sym, blocks, init)
% This is an internal function.
%
% 5/3/97

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

if nargin < 2, rootnam = ''; end
if nargin < 3, sym = 0;      end
if nargin < 4, blocks = 0; end
if nargin < 5, init = 1; end

nr = size(M,1);
nc = size(M,2);

if sym == 0 | nc == 1
   din = find(~isnan(M(:)));
else
   M = tril(M) + triu(ones(size(M))*NaN,1);
   din = find(~isnan(M(:)));
end

theta = M(din);
theta = theta(:);

if nargout > 2
   thetalab = [];
   for i=1:size(din,1)
       col1 = floor((din(i)-1)/nr);
       if ~blocks
           col1 = floor((din(i)-1)/nr);
           labl = [rootnam '(' int2str(din(i)-col1*nr) ',' int2str(col1+1) ')  '];
       else
           block = floor(col1/blocks);
           col2 = rem(col1,blocks);
           labl = [rootnam,int2str(block+init), '(' int2str(din(i)-col1*nr) ',' int2str(col2+1) ')  '];
       end
       thetalab = e4strmat(thetalab, labl);
   end
   thetalab = thetalab(2:size(thetalab,1), :);
end