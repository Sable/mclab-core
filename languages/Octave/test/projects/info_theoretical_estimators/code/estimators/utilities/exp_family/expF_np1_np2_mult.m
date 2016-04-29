function [innerp] = expF_np1_np2_mult(np1,np2)
%function [innerp] = expF_np1_np2_mult(np1,np2)
%Inner product of the 'natural parameter' variables, np1 and np2. (np1,np2: structures with the same fields; example: np1.t1, np1.t2, np2.t1, np2.t2).

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

innerp = 0;
F1 = fieldnames(np1);
for n = 1 : length(F1)
    aF = F1{n}; %field-name of F1 = field-name of F2 (<== assumption)
    innerp = innerp + ip(np1.(aF),np2.(aF));
end
