function [adft] = augdft(y, p, trend);
% augdft   - Computes de augmented Dickey-Fuller test and 
%            asociated statistics.
%           [adft] = augdft(y, p, trend);
% For each series y(t) in y matrix following regression
% is computed by OLS (Hamilton, 19XX, pp. 529):
%    y(t) = rho*y(t-1) + alpha + b(1)*z(t-1) + ... + b(p-1)*z(t-p+1) + e(t)
% The resulting statistics are:
%  - rho estimate and a t-test for H0: rho=1
%  - alpha estimate and a t-test for H0: alpha=0
%  - Joint F test for H0: rho=1 & alpha=0
% If optional trend=1 is specified, in the regression an additional
% term  delta*t is added. The resulting statistics are:
%  - rho estimate and a t-test for H0: rho=1
%  - alpha estimate and a t-test for H0: alpha=0
%  - delta estimate and a t-test for H0: delta=0
%  - Joint F test for H0: rho=1 & delta=0
%
% If an output argument is specied, no display of results is given.
%

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
if nargin == 1
   p = fix(sqrt(n));
   trend = 0;
elseif nargin == 2
   trend = 0;
end
if p <= 0, e4warn(2); p = fix(sqrt(n)); end
if p > round(n/3), e4warn(2); p = fix(sqrt(n)); end

adft = NaN*zeros(m,8);
for i=1:m
    yi    = y(:,i);
    ydum  = yi;
    yt    = ydum(p+1:n);
    yt1   = ydum(p:n-1);
    dy    = transdif(ydum, 1, 1);
    
    [dyl, dyc] = lagser(dy, 1:p-1);
    if trend
        X     = [yt1 ones(size(yt1)) (1:size(yt,1))' dyl];
    else
        X     = [yt1 ones(size(yt1)) dyl];
    end
%    [yt X]
    beta  = X\yt;
    u     = yt - X*beta;
    Fdf   = size(X,1)-size(beta,1);
    v     = ((u'*u)/Fdf)*inv(X'*X);
    rho   = beta(1);
    trho  = (rho-1.0)/sqrt(v(1,1)); % H0: rho = 1.0
    alpha = beta(2);
    talpha= alpha/sqrt(v(2,2));     % H0: alpha = 0.0
    
    if ~trend
        holg  = [rho-1;alpha]; vholg = v(1:2,1:2);
        Ftest = holg'*inv(vholg)*holg;
        delta = NaN;
        tdelta= NaN;
    else
        delta = beta(3);
        tdelta= delta/sqrt(v(3,3));     % H0: delta = 0.0
        holg  = [rho-1;delta]; vholg = v(2:3,2:3);
        Ftest = holg'*inv(vholg)*holg;
    end
    adft(i,:) = [p rho trho alpha talpha delta tdelta Ftest];
end

if ~nargout
    disp(' ');
    disp(sprintf('Augmented Dickey-Fuller results, p = %3d', adft(i,1) ));
    for i=1:m
        disp(' ');
        disp(sprintf('  rho   = %8.4f, t-test (rho=1)  = %8.4f', adft(i,2), adft(i,3)));
        disp(sprintf('  alpha = %8.4f, t-test (alpha=0)= %8.4f', adft(i,4), adft(i,5)));
        if trend
            disp(sprintf('  delta = %8.4f, t-test (delta=0)= %8.4f', adft(i,6), adft(i,7)));
            disp(sprintf('  F test (rho=1,delta=0) = %8.4f, d.f. = 2, %3d', adft(i,8),  Fdf ));
        else
            disp(sprintf('  F test (rho=1,alpha=0) = %8.4f, d.f. = 2, %3d', adft(i,8),  Fdf ));
        end
    end
end
