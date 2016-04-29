function [ccfd] = plotccf(macf, indv, nn, tit)
% plotccf  - Plots the set of cross-correlation functions for a series
% given the multiple autocorrelation function calculated from MIDENTS.
%    [ccf] = plotccf(macf, i, n, tit)
%  macf  > (lag*mxm) matrix of multiple autocorrelation function obtained
%          from MIDENTS.
%  i     > integer vector, the column of the output series.
%  n     > observations (used to compute std. deviations).
%  tit   > (mx?) matrix which contains the names of the m series.
%  ccf   < (lagxm) values of the m CCF's.
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

if nargin < 2, e4error(3); end
if any(size(macf,1) <= 0), e4error(7); end
titf   = 0;
[n, m] = size(macf);
lag    = fix(n/m);
indv   = fix(indv(:))';
ccfd   = [];

if (min(indv) <= 0) | (max(indv) > m), e4error(11); end
if lag < 1, e4error(11); end

if nargin == 2, nn = 0; end
if nargin == 4
   titf = 1;
   if (size(tit,1) > 0) & (size(tit,1) ~= m)
     e4warn(1); titf = 0;
   elseif (size(tit,1) == 0)
     titf = 0;
   end
end

figure; if ~prog, whitebg('w'); end; close;
if   nn > 0, stdacf = 2/sqrt(nn);
else stdacf = 0; end

for ii=indv
    for i=1:m
    ii, i
       if titf, titstr = [deblank(tit(ii,:)) ' leads ' deblank(tit(i,:))];
       else     titstr = ['series # ' int2str(ii) ' leads series # ' int2str(i)]; end
       ccfd = [ccfd, macf(i:m:m*lag,ii)];
       figure;
       [xx,yy] = bar(1:lag,ccfd(:,i)); unos = ones(size(xx));
       plot(xx,yy,'k-', xx, stdacf*unos,'r:', ...
          xx, -stdacf*unos,'r:', xx, 0*unos, 'k-');
       axis([1, lag, -1.0, 1.0]);
       title(['C.C.F. of ' titstr]);
    end
end