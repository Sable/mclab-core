function [nq,yq]=plotqqs(y,tit)
% plotqqs  - Plots the quantile graphs under normality for a series set.
%    [nq, yq] = plotqqs(y, tit)
% y   > (nxm) m series of n observations each.
% tit > (mx?) matrix which contains the names of the series.
% yq  <  nxm sorted empirical quantiles.
% nq  <  nxk sorted N(0,1) theoretical quantiles.
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

% Based on qq_plot.m, by Peter R. Shaw

global E4OPTION
prog  = E4OPTION(18);

if nargin == 1
   titf = 0; modo = 0;
elseif nargin == 2
   titf = 1;
   if (size(tit,1) > 0) & (size(tit,1) ~= size(y,2))
     e4warn(1); titf = 0;
   elseif (size(tit,1) == 0)
     titf = 0;
  end
end

if size(y,1) <= 1, e4error(7); end
[n,m]=size(y);

figure; if ~prog, whitebg('w'); end; close;
for i=1:m
    yi   = y(:,i); obs = find(~isnan(yi));
    ystd = (yi-mean(yi(obs)))/std(yi(obs));
    yq   = sort(ystd);
    ivec = ((1:n)'-.5)./n;
    pos  = (ivec>=.5);
    neg  = ~pos;
    nq   = zeros(n,1);
    nq(neg) = -sqrt(2)*erfinv(2*(.5-ivec(neg)));
    nq(pos) = sqrt(2)*erfinv(2*(ivec(pos)-.5));

    beta = [ones(size(yq)) nq]\yq;
    bstr = sprintf('beta = %7.4f', beta(2));
    
    if titf
       titstr = ['QQ plot of std. ' deblank(tit(i,:))];
    else  
       titstr = ['QQ plof of std. series # ' int2str(i) ];
    end
    figure;
    plot(nq,yq,'ko', [-3,3],[-3,3],'r--'); % grid
    title(titstr)
    xlabel(['Theoretical quantile N(0,1), ' bstr])
    ylabel('Ordered values')
end
hold off