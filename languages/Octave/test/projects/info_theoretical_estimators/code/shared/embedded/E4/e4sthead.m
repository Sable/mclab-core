function din = e4sthead(type, m, r, s, n, np, usflg, userf, innov, szpriv)
% e4sthead - Builds the common header of din.
%    din = e4sthead(type, m, r, s, n, np, usflg, userf, innov, szpriv)
%
% type   < model type
% m      < number of endogenous variables
% r      < number of exogenous variables
% s      < seasonal period
% n      < number of states
% np     < number of parameters
% usflg  < flag for user models
% innov  < innov(1) flag for innovation models,
%          innovation models: innov(2) Q matrix position in din, innov(3) void
%          non innovation models: innov(2) size of Q matrix, innov(3) size of R matrix
% szpriv < szpriv(1) size of private din, szpriv(2) size of private header
% din    > common header of din for any model

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

if nargin < 9, e4error(3); end
if usflg > 1
   userf1 = userf(1,:);
   userf2 = userf(2,:);
elseif usflg > 0
   userf1 = userf(1,:);
   userf2 = [];
else
   userf1 = [];
   userf2 = [];
end

innov = [innov(:);zeros(3-max(size(innov)),1)];
szpriv= [szpriv(:);zeros(2-max(size(szpriv)),1)];
 
userf1 = [userf1(:)+0;ones(8-size(userf1,2),1)*32];
userf2 = [userf2(:)+0;ones(8-size(userf2,2),1)*32];
din = [type;m;r;s;n;np;usflg;userf1;userf2;innov;szpriv];