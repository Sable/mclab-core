function [acf, pacf, qs] = uidents(y,lag,tit,s,graphics)
% UIDENTS  - Computes the autocorrelation and partial autocorrelation functions
% for a set of series.
%    [acf, pacf, qs] = uidents(y, lag, tit, s,graphics)
%  y     > (nxm) m series of n observations each.
%  lag   > maximum lag for the ACF and PACF (default: n/4).
%  tit   > (mx?) matrix which contains the names of the series.
%  s     > seasonal period (optional).
%  graphics > boolean indicator for displaying graphics (default value = true)
%  acf   < (mxlag) values of the ACF.
%  pacf  < (mxlag) values of the PACF.
%  qs    < (1xm) values of the Ljung-Box statistic.
%
% 6/3/97
% Copyright (c) Jaime Terceiro, 1997

global E4OPTION
prog  = E4OPTION(18);
if prog, stylok = ');'; stylom = stylok; else, stylok = ',''k'');'; stylom = ',''m'');'; end 

if nargin < 5, graphics = 1; end
if nargin < 1, e4error(7); end
if any(size(y,1) <= 0), e4error(7); end
if any(any(isnan(y))), e4error(16); end

if nargin < 4, s = 1; end

[n, m] = size(y);
titf = 0;
if nargin == 1
   lag = round(size(y,1)/4);
elseif nargin >= 3
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
acf = zeros(lag,m); pacf = acf;

for k=1:m
   for j = 1:lag
      acf(j,k) = ys(j+1:n,k)'*ys(1:n-j,k)/(n-j);
      if j==1, ylagg = ys(1:n-1,k);
      else     ylagg = [ylagg(2:n-j+1,:) ys(1:n-j,k)];
      end
      phis  = ylagg\ys(j+1:n,k);
      pacf(j,k) = phis(j);    
   end
end
%qs = n*(n+2)*sum( (acf.^2)./(n - ((1:lag)'*ones(1,m))) ); No corresponde a la distribucion del acf previo
qs = (n+2)/n*sum( (acf.^2).*(n - ((1:lag)'*ones(1,m))) );
if ~graphics, return; end    
    
disp(' ');
disp(['Std. deviations for ACF and PACF are computed as 1/T^0.5 = ' sprintf('%6.2f', 1/sqrt(n))]);
disp(' ');

figure; if ~prog, whitebg('w'); end; close;
stdacf = 2/sqrt(n);
for i=1:m
   if titf, titstr = deblank(tit(i,:));
   else     titstr = ['series # ' int2str(i) ]; end
   figure;
   h1= axes('position',[0.1 0.55 0.8 0.35]);
   hold on;
   set(h1, 'Box','on');
   axis([1, lag, -1.0, 1.0]);
   vlag = (1:lag)';      
   eval(['bar(vlag,acf(:,i)' stylok]); unos = ones(lag,1);
   if s > 1
      acf1 = NaN*ones(lag,1);
      acf1(s:s:lag) = acf(s:s:lag,i);
      eval(['bar(vlag,acf1' stylom]);
   end
   plot(vlag, stdacf*unos,'r:', vlag, -stdacf*unos,'r:', vlag, 0*unos, 'k-');
   title(['A.C.F. of ' titstr ', LBQ = ' sprintf('%6.2f',qs(i))]);
   h2 = axes('position',[0.1 0.075 0.8 0.35]);
   hold on;
   set(h2, 'Box','on');
   axis([1 lag -1.0 1.0]);
   eval(['bar(vlag,pacf(:,i)' stylok]);
   if s > 1
      acf1 = NaN*ones(lag,1);
      acf1(s:s:lag) = pacf(s:s:lag,i);
      eval(['bar(vlag,acf1' stylom]);      
   end

   plot(vlag, stdacf*unos,'r:', vlag, -stdacf*unos,'r:', vlag, 0*unos, 'k-');
   title(['P.A.C.F. of ' titstr]);
end
hold off;
