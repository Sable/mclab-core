function [thopt, iter, fnew, g, h] = e4estagg(theta, din, yaggr, y, idxagg, typeagg, seasons)
%
% E4ESTAGG - Optimices the likelihood function for a high frequency model with
%            temporal aggregation constraints on data
% [thopt, iter, fnew, g, h] = e4estagg(theta, din, yaggr, y, idxagg, typeagg, seasons)
%
% theta, din> high-frequency model in theta-din format
% yaggr     > matrix of aggregated data
% y         > (optional) matrix of disaggregated data. Its columns include first the values of 
%             disaggregated endogenous variables and second all the exogenous variables.
% idxagg    > (optional) mx1 vector, where m is the number of endogenous variables.
%              If idxagg(i) == 1, the i-th endogenous variable is aggregated.
%              Default: idxagg = ones(m,1);
% typeagg   > (optional) type of aggregation: 0 flow (default), 1 stock. Stock figures
%             are assumed to be the last high-frequency value in the aggregation period 
% seasons   > (optional) the number of seasons (high-frequency periods) that add up to
%             a low-frequency sampling period (default: the seasonal period defined in din)
%  thopt    < maximum-likelihood estimates of the model parameters.
%  iter     < number of iterations.
%  fnew     < value of the objective function at the optimum.
%  g        < gradient at the optimum.
%  h        < hessian (or numerical approximation) at the optimum.
%
% 7/7/04

% Copyright (C) 2004 José Casals, Miguel Jerez, Sonia Sotoca
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

% Checks the input and sets variables to its default values

  if nargin < 3, e4error(4); end
  [H_D, type, m, r, s, n, np, userflag, userf, innov, szpriv] = e4gthead(din);
  if nargin < 7
     seasons = s;
     if nargin < 6
        typeagg = 0;
        if nargin < 5
           idxagg = ones(m,1);           
           if nargin < 4, y = []; end
        end
     end   
  end
  
  idxagg = idxagg(:) ~= 0;
  N = size(y,1);
  ns = size(y,2);
  magg = sum(idxagg);
  mnoagg = m-magg;

  if size(idxagg,1) ~= m, e4error(11); end
  if size(yaggr,2) ~= magg, e4error(11); end
  if ns ~= mnoagg+r, e4error(11); end
  if mnoagg & magg & N / seasons ~= size(yaggr,1), e4error(11); end

% Prepares data
  
  N2 = max(N / seasons, size(yaggr,1));
  
  Y = zeros(N2, magg + mnoagg*seasons);
  k = cumsum(idxagg + (~idxagg)*seasons);
  
  if magg, Y(:,k(idxagg)) = yaggr; end
  
  if mnoagg 
     Y2 = reshape(y(:,1:mnoagg)',mnoagg*seasons, N2)';
     k = k(~idxagg);
     for i=1:mnoagg
         Y(:,k(i)-seasons+1:k(i)) = Y2(:,i:mnoagg:mnoagg*seasons);
     end
  end
     
  if r
     Y = [Y reshape(y(:,ns-r+1:ns)',r*seasons, N2)'];
  end 
  
  dinl = e4sthead(type, magg+mnoagg*seasons, r*seasons, seasons, n, np, userflag, userf, [0;0;0], [size(din,1)+m+1;m+1]);
  dinl = [dinl;[typeagg;idxagg];din];
  dinl = touser(dinl,'ufaggr');

  if sum(sum(isnan(Y))), func = 'lfmiss'; else func = 'lffast'; end  
  [thopt, iter, fnew, g, h] = e4min(func,theta,'',dinl,Y);