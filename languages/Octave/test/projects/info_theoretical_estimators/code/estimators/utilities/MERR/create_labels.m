function [Tick,TickLabel] = create_labels(iv,base)
%function [Tick,TickLabel] = create_labels(iv,base)
%Creates ticks (Tick) and tick labels (TickLabel) for plot, where the interval (iv) is an exponential function of the base; example: base = 10; iv = base.^[-4:3].

%Copyright (C) 2012- Zoltan Szabo ("http://www.gatsby.ucl.ac.uk/~szabo/", "zoltan (dot) szabo (at) gatsby (dot) ucl (dot) ac (dot) uk")
%
%This file is part of the ITE (Information Theoretical Estimators) toolbox.
%
%ITE is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by
%the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
%
%This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
%MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
%
%You should have received a copy of the GNU General Public License along with ITE. If not, see <http://www.gnu.org/licenses/>.

L = length(iv);
Tick = [1:L];
TickLabel = cell(L,1);
f = strcat('log',num2str(base)); %example: 'log10'
for n = Tick
    TickLabel{n} = num2str(feval(f,iv(n)));
end   