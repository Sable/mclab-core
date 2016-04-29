function [gs] = create_PNL_mixing_functions(D)
%function [gs] = create_PNL_mixing_functions(D)
%Generates D pieces of PNL mixing functions randomly acting on
%D-dimensional vectors. The result is returned in cell array gs.

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

gs = {};
for k = 1 : D
    %parameters:
        sig = 2*((rand>0.5)-1/2);%RND:-1/1
        offset = 2*rand;%U[0,2]
        g1_dil = rand*0.5;%U[0,.05]
        g2_dil = rand*5;%U[0,5]
        %note: without picking out these terms, g would be different for every evaluation (because of the 'rand' term.
    gs{k} = @(t)(offset+sig*(g1_dil*t+tanh(g2_dil*t)));
end

