function e4disp(code, P1, P2, P3, P4, P5)
% e4disp   - Displays the iterative optimizer output and other information.
%    e4disp(code, P1, P2, P3, P4, P5)
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

global E4OPTION DISPSTR

if ~E4OPTION(10), return; end

str1   = DISPSTR(code,:);
str3   = [];
argstr = getparms(nargin-1);
if nargin > 1
    if any(findstr(str1,'%'))
        str1 = eval(['sprintf(str1' argstr ')']);
    else
        for i=1:nargin-1
            str2 = [deblank(str1) eval(['sprintf('' %8.4f'', P' int2str(i) ' )'])];
            str3 = e4strmat(str3,str2);
        end
        str1 = str3(2:nargin,:);
    end      
end
disp(str1);
