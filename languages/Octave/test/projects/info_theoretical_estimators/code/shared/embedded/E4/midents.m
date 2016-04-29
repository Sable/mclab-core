function [acf, pacf, Qus] = midents(y, lag, tit)
% midents  - Multiple autocorrelation and partial autoregression function of a
% vector of series.
%    [macf, mparf,Qus] = midents(y, lag, tit)
%  y     > (nxm) m series of n observations each.
%  lag   > maximum lag for the ACF and PARF (default: n/4).
%  tit   > (mx?) matrix which contains the names of the series.
%  macf  < (mxm·lag) values of the multiple ACF.
%  mparf < (mxm·lag) values of the multiple PARF.
%  Qus   < (mxm) matrix of Box-Ljung Q statistics.
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

if nargin < 1, e4error(7); end
if any(size(y,1) <= 0), e4error(7); end
if any(any(isnan(y))), e4error(16); end

[n, m] = size(y);
titf = 0;
if nargin == 1
   lag = round(size(y,1)/4);
elseif nargin == 3
   titf = 1;
   if (size(tit,1) > 0) & (size(tit,1) ~= size(y,2))
     e4warn(1); titf = 0;
   elseif (size(tit,1) == 0)
     titf = 0;
   end
end

if lag <= 0, e4warn(2); lag = round(n/4); end
if lag > round(n/3), e4warn(2); lag = round(n/3); end

ys = (y - ones(n,1)*mean(y)) ./ (ones(n,1)*std(y));
ym = (y - ones(n,1)*mean(y));

acf = zeros(lag*m,m); pacf = acf; vars = acf; dtphis = acf; 
ev  = zeros(lag*m,1); Qus = zeros(m,m);

Sp  = ym'*ym;
dSp = det(Sp);

for i = 1:lag
    rows = (i-1)*m+1:i*m;
    acf(rows,:) = ys(i+1:n,:)'*ys(1:n-i,:)/(n-i);
    Qus = Qus + (acf(rows,:).^2)/(n-i);
    
    [yl, yc] = lagser(y, 1:i);
    X    = [ones(size(yl,1),1) yl];
    phis = X\yc;
    u    = yc - X*phis;
    Sp1  = u'*u;
    dSp1 = det(Sp1);
    Sigma= Sp1/n;
    vphi = kron(Sigma,inv(X'*X));
    dtphi= reshape(sqrt(diag(vphi)),size(phis,1),size(phis,2));
    
    chitst(i) = -(n - 0.5 - m*i)*log(dSp1/dSp);
    aic(i) = (n*log(det(Sigma)) + 2*(i*m*m + m))/n;
    sbc(i) = (n*log(det(Sigma)) + (i*m*m + m)*log(n))/n;

    pacf(rows,:)   = phis(rows+1,:)';
    dtphis(rows,:) = dtphi(rows+1,:)';
    vars(rows,:)   = Sigma;
    ev(rows,1)     = eig(Sigma);
    
    dSp = dSp1;
end
Qus = n*(n+2)*Qus;

% Now, display matrix
stdacf = 1/sqrt(n);
acfstr = e4strmat(ones(size(acf))*abs('.')); pacfstr = acfstr;

ndx1 = (acf >= 2*stdacf); ndx2 = (acf <= -2*stdacf);
acfstr(ndx1) = e4strmat(abs('+')*ones(sum(sum(ndx1)),1));
acfstr(ndx2) = e4strmat(abs('-')*ones(sum(sum(ndx2)),1));

ndx1 = (pacf >= 2*dtphis); ndx2 = (acf <= -2*dtphis);
pacfstr(ndx1) = e4strmat(abs('+')*ones(sum(sum(ndx1)),1));
pacfstr(ndx2) = e4strmat(abs('-')*ones(sum(sum(ndx2)),1));

sep = e4strmat(ones(m,3)*abs(' ')); sep(:,2) = e4strmat(ones(m,1)*abs('|'));

if ~titf
    tit = [];
    for i=1:m; tit = e4strmat(tit, ['ser #' int2str(i) ]); end
    tit = tit(2:m+1,:);
end
if size(tit,2) < 10, tit = [tit e4strmat(ones(m,10-size(tit,2))*32)]; end
tits = [tit(:,1:10) e4strmat(ones(m,2)*32)];

blank = e4strmat(ones(1,4+m + 10+m*16+1)*32);
%         1         2         3         4         5         6
%123456789012345678901234567890123456789012345678901234567890

blank(13:16) = 'MACF'; blank(13+m+3:13+m+7) = 'MPARF';
blank(15+2*m+6:15+2*m+9) = 'MACF'; blank(17+2*m+6*m+6:17+2*m+6*m+10) = 'MPARF';

disp(' ');
disp('******** Autocorrelation and partial autoregression functions ********');
disp(blank);
for i = 1:lag
    a = []; b = []; c = []; d = [];
    rows = (i-1)*m+1:(i-1)*m+m;
    for j=rows
        a = [a; sprintf('%6.2f ', acf(j,1:m))];
        b = [b; '|' sprintf('%6.2f ', pacf(j,1:m)) ];
        % c = [c; '|' sprintf('%8.3f ', vars(j,1:m)) ];
        % d = [d; '|' sprintf('%8.3f ', ev(j,1))];
    end
    chistr = sprintf('Chi(k) = %6.2f', chitst(i));
    aicstr = sprintf('AIC(k) = %6.2f', aic(i));
    sbcstr = sprintf('SBC(k) = %6.2f', sbc(i));
    disp([' k = ' int2str(i) ', ' chistr ', ' aicstr ', ' sbcstr]);
    disp([tits acfstr((i-1)*m+1:i*m, :) sep pacfstr((i-1)*m+1:i*m, :) sep a b d]);
    disp(' ');
end
disp('The (i,j) element of the lag k matrix is the cross correlation (MACF)');
disp('or partial autoregression (MPACF) estimate when series j leads series i.');
disp(' ');

disp('********************* Cross correlation functions *********************');
if lag > 12, tits1 = [tits e4strmat(ones(m,lag-11)*32)];
else         tits1 = tits; end
disp(['            ' reshape(tits1',1,size(tits1,1)*size(tits1,2))] );
for i=1:m
    str = [tits(i,:)];
    for j=1:m
        str1 = acfstr(i:m:m*lag,j)';
        if size(str1,2) < 12, str1 = [str1 e4strmat(ones(1,12-size(str1,2))*32)]; end
        str = [str str1 ];
    end
    disp(str);
end

disp('Each row is the cross correlation function when the column variable leads');
disp('the row variable.');
disp(' ');
disp('Ljung-Box Q statistic for previous cross-correlations');
disp(['            ' reshape(tits1',1,size(tits1,1)*size(tits1,2))] );
for i=1:m
    disp([tits(i,:) sprintf('%6.2f      ',Qus(i,:))] );
end

disp(' ');
disp('Summary in terms of +.-');
disp(['   For MACF std. deviations are computed as 1/T^0.5 = ' sprintf('%6.2f', 1/sqrt(n))]);
disp('   For MPARF std. deviations are computed from VAR(k) model');
disp('***********************************************************************');
disp(' ');
