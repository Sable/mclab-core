function [str] = getparms(np);
% getparms - Returns the strings 'P1,P2, ...,Pnp' to evaluate the
% functions with optional parameters using the MATLAB eval procedure.
%    str = getparms(np)
%
% 5/4/97

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

if np > 0, str = sprintf(',P%1d', 1:np);
else       str = [];
end
      
