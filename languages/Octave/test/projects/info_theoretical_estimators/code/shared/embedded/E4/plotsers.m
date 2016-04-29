function [ystd] = plotsers(y, modo, tit, obsnum)
% plotsers - Displays standardized graphs of a series set with +-2 bands.
%    ystd = plotsers(y, mode, tit, obsnum)
% y     > (nxm) m series of n observations each.
% mode  > type of plot.
%       0 = Each series is displayed in a single plot.
%       1 = All the series (up to seven) are displayed in a single plot.
%      -1 = Each series is displayed in a single plot,
%           but all the y-axes are scaled in the same way.
% tit   > (mx?) matrix which contains the names of the series.
% obsnum > (optional) mx1 vector containing x-values for a X-Y plot
% ystd  < (nxm) matrix of standardized values of the data.
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

if nargin == 1
   titf = 0; modo = 0;
elseif nargin == 2
   titf = 0;
   if (modo ~= 1) & (modo ~= 0) & (modo ~= -1)
      e4warn(3, 'MODE'); modo = 0;
   end
elseif nargin >= 3
   if (modo ~= 1) & (modo ~= 0) & (modo ~= -1)
      e4warn(3, 'MODE'); modo = 0;
   end
   titf = 1;
   if (size(tit,1) > 0) & (size(tit,1) ~= size(y,2))
      e4warn(1); titf = 0;
   elseif (size(tit,1) == 0)
      titf = 0;
  end
end
if size(y,1) <= 1
   e4error(7);
end
ystd = y; m = size(y,2);
if (modo == 1) & (m > 7)
   e4warn(4);
   m = 7;
end
for i=1:m
   obs = find(~isnan(y(:,i)));
   ystd(obs,i) = (y(obs,i)-mean(y(obs,i)))/std(y(obs,i));
   amax(i) = max(abs(ystd(obs,i)));
end

if nargin < 4, obsnum = 1; end  
if size(obsnum,1) ~= size(y,1)
   obsnum = 1:size(y,1);
end
unos = ones(size(y,1),1);
figure; if ~prog, whitebg('w'); end; close;
if modo == 1
  marcas = ['k- '; 'k--'; 'k-.'; 'k: '; 'k* '; 'ko '; 'k+ '];
  absmax = max(amax(i)); if absmax <= 2.0, absmax = 3.0; end
  if titf
      titstr = ['Standardized plot of: '];
      for i=1:m, titstr = [titstr deblank(tit(i,:)) ', ']; end
      titstr = titstr(1:size(titstr,2)-2);
  else     titstr = ['Standardized series plot'];  end
  figure;  
  plot(obsnum,2*unos,'r--', ...
       obsnum,-2*unos,'r--', obsnum,0*unos,'r-');
  title(titstr);  
  axis([min(obsnum), max(obsnum), -absmax, absmax]);
  hold on; legstr = 'h'; h = zeros(m,1);
  for i=1:m
     h(i) = plot(obsnum,ystd(:,i),marcas(i,:));
     if ~titf
        tit(i,:) = ['series # ' int2str(i)];
    end
    legstr = [legstr ',''' deblank(tit(i,:)) ''''];
  end
  eval(['legend(' legstr ', 0);']);
else
  for i=1:m
    if modo == -1, absmax = max(amax); 
    else           absmax = amax(i); end
    if absmax <= 2.4, absmax = 3.0; end
    if titf
       titstr = ['Standardized plot of ' deblank(tit(i,:)) ' '];
    else  
       titstr = ['Standardized plot of series # ' int2str(i) ' '];
    end
    figure;
    plot(obsnum,ystd(:,i),'k-',obsnum,2*unos,'r--', ...
          obsnum,-2*unos,'r--', obsnum,0*unos,'r-');
    title(titstr);
    axis([min(obsnum), max(obsnum), -absmax, absmax]);
  end
end
hold off
