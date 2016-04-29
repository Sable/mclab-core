function [freqs] = histsers(y, mode, tit)
% histsers - Builds histograms of a set standardized series. Missing data 
% are excluded.
%    freqs = histsers(y, mode, tit)
%  y     > (nxm) m series of n observations each.
%  mode  > 0=relative frequencies (default), 1 = absolute frequecies
%  tit   > (mx?) matrix which contains the names of the series.
%  freqs < (2mxcats) each block 2xcats contain the class marks and the
%          frecuency of each of the intervals presented in the graph.
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

if nargin == 1
   titf = 0;
   mode = 0;
elseif nargin == 2
   titf = 0;
elseif nargin == 3
   titf = 1;
   if (size(tit,1) > 0) & (size(tit,1) ~= size(y,2))
     e4warn(1); titf = 0;
   elseif (size(tit,1) == 0)
     titf = 0;
  end
end
if any(size(y,1) <= 0)
   e4error(7);
end
m = size(y,2); nobs = size(y,1);
 
figure; whitebg('w'); close;
for k=1:m
  yi = y(:,k); obs = find(~isnan(yi));
  ystd = (yi-mean(yi(obs)))/std(yi(obs));
  absmax = max(abs(ystd(obs)));
  if absmax <= 2.4, absmax = 3.0; end
  atip1 = find(abs(ystd) > 1.0); atip1 = size(atip1,1);
  atip2 = find(abs(ystd) > 2.0); atip2 = size(atip2,1);
  miss = size(ystd,1) - size(obs,1);
  incbin = -absmax:(absmax/10):absmax; 
  if titf
     titstr = ['Histogram of standardized series: ' deblank(tit(k,:)) ' '];
  else  
     titstr = ['Histogram of standardized series # ' int2str(k) ' '];
  end
  figure;
  [freqs, nn] = hist(ystd,incbin);
  if mode
       str1 = ['Absolute frequencies'];
  else
       freqs   = freqs/nobs;
       str1 = ['Relative frequencies'];
  end
  bar(nn,freqs,'k');
  title(titstr);
  xlabel(sprintf('%d outside (-1,+1), %d outside (-2,+2), %d missing', ...
             atip1, atip2, miss));
  ylabel(str1);
  axis([floor(-absmax), ceil(absmax), 0, max(freqs)]);
end
