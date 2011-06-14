% =========================================================================== %
%                                                                             %
% Copyright 2011 Anton Dubrau and McGill University.                          %
%                                                                             %
%   Licensed under the Apache License, Version 2.0 (the "License");           %
%   you may not use this file except in compliance with the License.          %
%   You may obtain a copy of the License at                                   %
%                                                                             %
%       http://www.apache.org/licenses/LICENSE-2.0                            %
%                                                                             %
%   Unless required by applicable law or agreed to in writing, software       %
%   distributed under the License is distributed on an "AS IS" BASIS,         %
%   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  %
%   See the License for the specific language governing permissions and       %
%  limitations under the License.                                             %
%                                                                             %
% =========================================================================== %

function set=mandelbrot(N, Nmax)
%n=integer?
%nmax=parameter
%x,c are complex
%ya,yb,xa,xb,dx,dy
% computes mandelbrot set with N elements and Nmax iterations

  side = round(sqrt(N));
  ya = -1;
  yb = 1;
  xa = -1.5;
  xb = .5;
  dx = (xb-xa)/(side-1);
  dy = (yb-ya)/(side-1);
  set = zeros(side,side);
  s = (side-1);
  for x=0:s
     for y=0:s
        set(y+1,x+1) = iterations(xa+x*dx+i*(ya+y*dy),Nmax);
     end
  end
end


function out = iterations(x,max)
  c = x;
  i = 0;
  cond = (abs(x) < 2 & i < max);
  while cond
    x = x*x + c;
    i = i+1;
    cond = (abs(x) < 2 & i < max);
  end
  out = i;
end

