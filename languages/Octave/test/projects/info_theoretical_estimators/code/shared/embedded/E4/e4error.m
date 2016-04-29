function ercod = e4error(code, P1, P2, P3, P4, P5)
% e4error  - Displays an error message and aborts the program.
%    ercod = e4error(code, P1, P2, P3, P4, P5)
%
% 11/3/97

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

% Internal use only

global ERRORSTR

str1  = ['ERROR ' ERRORSTR(code,:)];
if nargin > 1
    eval(['str1 = sprintf(str1 ' getparms(nargin-1) ');']);
end
error(str1);