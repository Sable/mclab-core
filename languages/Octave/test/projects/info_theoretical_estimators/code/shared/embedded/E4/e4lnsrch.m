function [x,f,check] = e4lnsrch(func,xold,index,fold,g,p,stpmax,P1,P2,P3,P4,P5,P6,P7,P8)
% E4LNSRCH - Minimizes a function in a given direction.
%    [x, f, check] = e4lnsrch(func,xold,index,fold,g,p,stpmax,P1,P2,P3,P4,P5,P6,P7,P8)
% This function is used by E4MIN to compute the optimal step length.
%  xold   > (nx1) start vector.
%  func   > string with the function name.
%  g      > value of the gradient of FUNC in XOLD.
%  p      > search direction vector.
%  stpmax > maximum step length in the p direction.
%  x      < (nx1) new vector.
%  f      < value of FUNC in x.
%  check  < is false (0) in normal output and is true (1) when x is too
%          near to xold.
%
% 11/3/97
% Copyright (c) Jaime Terceiro, 1997
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


% Based on Numerical Recipes in C (2nd. edition). pp. 385-386

% This function is to be called by e4min only, so no input check
parms = getparms(nargin-7);
alf   = 1.0e-4;
tolx  = 1.0e-7;
check = 0;
suma  = sqrt(p'*p);
if (suma > stpmax),  p = p.*(stpmax/suma); end
slope = g'*p;
test  = max( abs(p)./max(abs(xold(index,1)),1.0) );
alamin= tolx/test;
alam  = 1.0;
x = xold;
e4disp(7, p);
while 1
   if norm(p) < norm(alam*p)
       check = 2;
       x = x(index,1);
       return;
   end
   x(index,1) = xold(index,1) + alam*p;
   if any(isnan(x(index,1))) % FObj degenerated
      x = xold(index,1);
      e4error(28);
      return
   end
   f = eval( [func '(x' parms ')' ] );
   if (alam < alamin)
      x     = xold(index,1);
      check = 1;
      return;
   elseif (f <= fold+alf*alam*slope)
      x = x(index,1);
      return;
   else
      if (alam == 1.0)
         tmplam = -slope/(2.0*(f-fold-slope));
      else
         rhs1 = f - fold - alam*slope;
         rhs2 = f2- fold2- alam2*slope;
         a    = (rhs1/(alam*alam) - rhs2/(alam2*alam2))/ ...
                (alam-alam2);
         b    = (-alam2*rhs1/(alam*alam) + alam*rhs2/(alam2*alam2))/ ...
                (alam - alam2);
         if (a == 0.0)
             tmplam = -slope/(2.0*b);
         else
             disc   = b*b - 3.0*a*slope;
             if (disc < 0.0)
                check = 2;
                x = x(index,1);
                e4error(28);
                return;
             else
                tmplam = (-b+sqrt(disc))/(3.0*a);
             end
         end
         if (tmplam > 0.5*alam), tmplam = 0.5*alam; end
      end
   end
   alam2 = alam;
   f2    = f;
   fold2 = fold;
   alam  = max(tmplam, 0.1*alam);
end