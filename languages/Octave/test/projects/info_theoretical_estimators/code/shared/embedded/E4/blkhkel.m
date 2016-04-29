function Z = blkhkel(z, i, s, ext, inverse)

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


if nargin < 5, inverse = 0; end

if nargin < 4, ext = 0; end

if nargin < 3, s = 1; end

off = s*(i-1);

T = size(z,1);
l = size(z,2);

if ~ext
%
   Z = zeros(l*i, T-off);

   if ~inverse
       for j = 1:i
           Z(l*(j-1)+1:l*j,:) = z(s*(j-1)+1:T-s*(i-j),:)';
       end
   else
       for j = 1:i
           Z(l*(j-1)+1:l*j,:) = z(s*(i-j)+1:T-s*(j-1),:)';
       end
   end

%
else
%
   Z = zeros(l*i, T+off);

   if ~inverse
       for j = 1:i      
           Z(l*(j-1)+1:l*j,off+1:off+T) = z';
           off = off-s;
       end
   else
       for j = i:-1:1   
           Z(l*(j-1)+1:l*j,off+1:off+T) = z';
           off = off-s;
       end
   end
%
end