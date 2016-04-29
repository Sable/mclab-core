function [stats, aval1, avect1] = descser(y, tit)
% descser  - Computes and displays descriptive statistics on a series set.
%    stats = descser(y, tit)
%  y      > (nxm) m series of n observations each.
%  tit    > (mx?) matrix which contains the names of the series.
%  stats  < (mx14) matrix of statistics. Includes number of valid
%           (non-missing) observations, mean, standard deviations,
%           skewness, excess kurtosis, quartiles, lowest value, observation
%           with the lowest value, largest value and observation with the
%           largest value, Jarque-Bera and Dickey-Fuller statistics.
%  aval1  < sorted eigenvalues of the variance-covariance matrix
%  avect1 < sorted eigenvectors of the variance-covariance matrix
%           (principal components).
% The correlation matrix and the eigenstructure is only computed if
% the sample contains no missing data.
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

[n,m] = size(y);
if n <= 1, e4error(7); end

if     nargin == 1, titf = 0;
elseif nargin == 2
   titf = 1;
   if (size(tit,1) > 0) & (size(tit,1) ~= m)
     e4warn(1); titf = 0;
   elseif (size(tit,1) == 0)
     titf = 0;
   end
end

for i=1:m
   missndx  = find(isnan(y(:,i)));
   yi       = y(~isnan(y(:,i)),i);
   nobs(i)  = size(yi,1);

   q50(i)   = median(yi);
   ydum     = yi(yi < q50(i));  q25(i) = median(ydum);  
   ydum     = yi(yi >= q50(i)); q75(i) = median(ydum);  
  
   meany(i) = mean(yi);
   stdy(i)  = std(yi);
   sime(i)  = mean( (yi-meany(i)).^3 )/stdy(i)^3;
   curt(i)  = mean( (yi-meany(i)).^4 )/stdy(i)^4 - 3;
   jb(i)    = nobs(i)*( (sime(i)^2)/6  +  (curt(i)^2)/24  );
  
   if size(missndx,1)
      yi = y(:,i);
      yi(missndx) = meany(i)*ones(size(missndx));
      dft1(i) = NaN;
      dft2(i) = NaN;
   else
      nolag1=fix(sqrt(size(yi,1)));
      dft = augdft(yi,nolag1);
      dft1(i) = dft(3);
      nolag2=1;
      dft = augdft(yi,nolag2);
      dft2(i) = dft(3);
   end
   [maxy(i), imaxy(i)] = max( yi );
   [miny(i), iminy(i)] = min( yi );
end

disp(' ');
disp('*****************  Descriptive statistics  *****************');
disp(' ');
for i=1:m
    if titf
       titstr = ['---  Statistics of ' deblank(tit(i,:)) ' ---'];
    else  
       titstr = ['---  Statistics of series # ' int2str(i) ' ---'];
    end
    disp(titstr);
    disp(sprintf('Valid observations = %4d', nobs(i) ));
    disp(sprintf('Mean               = %8.4f, t test = %8.4f', ...
        meany(i), meany(i)/(stdy(i)/sqrt(nobs(i))) ));
    disp(sprintf('Standard deviation = %8.4f', stdy(i) ));
    disp(sprintf('Skewness           = %8.4f', sime(i) ));            
    disp(sprintf('Excess Kurtosis    = %8.4f', curt(i) ));
    disp(sprintf('Quartiles          = %8.4f, %8.4f, %8.4f', q25(i), q50(i), q75(i) ));
    disp(sprintf('Minimum value      = %8.4f, obs. # %4d', miny(i), iminy(i) ));
    disp(sprintf('Maximum value      = %8.4f, obs. # %4d', maxy(i), imaxy(i) ));
    disp(sprintf('Jarque-Bera        = %8.4f', jb(i)  ));
    disp(sprintf('Dickey-Fuller      = %8.4f, computed with %3.0f lags', dft1(i), nolag1));
    disp(sprintf('Dickey-Fuller      = %8.4f, computed with %3.0f lags', dft2(i), nolag2));
 
    yi = y(:,i);
    missndx  = find(isnan(yi));
    if size(missndx,1)
      yi(missndx) = meany(i)*ones(size(missndx));
    end
    
    atipl = find(abs(yi-meany(i)) > 2*stdy(i));
    disp('Outliers list');
    disp(' Obs #         Value');
    for j=1:size(atipl,1)
        disp(sprintf('%4d     %12.4f', atipl(j), yi(atipl(j))) );
    end
    disp(' ');
end

disp(' ');
if (~any(any(isnan(y)))) & (m > 1);
    disp('Sample correlation matrix');
    corrm = corrcoef(y);
    disp(corrm);
    
    varm  = cov(y);
    [avect1, aval1] = eig(corrm); 
    [aval1, ind] = sort(diag(aval1)); 
    aval1  = flipud(aval1);
    avect1 = fliplr(avect1(:,ind'));
    suma = sum(aval1);

%    disp(' ');
    disp(['Eigen structure of the correlation matrix']);
    disp('  i eigenval  %var | Eigen vectors');
    for i=1:m
       str1 = sprintf('%3d %8.4f %3.2f | ', i, aval1(i), aval1(i)/suma); 
       str2 = sprintf(' %7.4f', avect1(:,i));
       disp([str1 str2]);
    end
end
disp('************************************************************');

stats = [nobs' meany' stdy' sime' curt' q25' q50' q75' maxy' imaxy' miny' iminy' jb' dft1' dft2'];
