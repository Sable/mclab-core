function [medstip, stdstip] = rmedser(y, len, tit)
% rmedser  - Displays the standardized mean/std. deviation plot of a series set.
%    [med, dts] = rmedser(y, len, tit);
%  Missing data are excluded.
%  y    > nxm, m series of n observations each.
%  len  > number of observations in each group (default value 5).
%  tit  > (mx?) matrix which contains the names of the series.
%  med  < series of averages.
%  dts  < series of sample standard deviations.
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

if nargin == 1
   titf = 0; len = 5;
elseif nargin == 2
   titf = 0;
   if (len <= 0), e4warn(5); len = 5; end
elseif nargin == 3
   if (len <= 0), e4warn(5); len = 5; end
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
if len > round(size(y,1)/4), len = round(size(y,1)/4); end
if len < 1, e4error(7); end

letras = ['ABCDEFGHIJLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz'];
figure; whitebg('w'); close;
m = size(y,2); nobs = size(y,1); punt = fix(nobs/len);
for k=1:m
  for i = 1:punt
     y1 = y((i-1)*len+1:i*len,k); obs = find(~isnan(y1));
     if size(obs,1) >= 2
        stds(i) = std(y1(obs));
        meds(i) = mean(y1(obs));
     else
        stds(i) = NaN; meds(i) = NaN;
     end
  end
  obsm = find(~isnan(meds));  obss = find(~isnan(stds));
  medstip = (meds-mean(meds(obsm)))/std(meds(obsm));
  stdstip = (stds-mean(stds(obss)))/std(stds(obss));
  absmax  = max([max(abs(medstip)) max(abs(stdstip))]);
  if absmax < 2.4, absmax = 3.0; end
  if titf
     titstr = ['Standardized mean/std. dev. plot of ' deblank(tit(k,:)) ' '];
  else  
     titstr = ['Standardized mean/std. deviation plot of series # ' int2str(k) ' '];
  end
  figure;
  plot(medstip,stdstip,'k.');
  if punt <= 52
     for i=1:punt, text(medstip(i),stdstip(i),letras(i));end
  else
     for i=1:punt, text(medstip(i),stdstip(i),int2str(i));end
  end  
  title(titstr);  axis([-absmax, absmax, -absmax, absmax]);
  xlabel('Means');
  ylabel('Standard deviations');
end
