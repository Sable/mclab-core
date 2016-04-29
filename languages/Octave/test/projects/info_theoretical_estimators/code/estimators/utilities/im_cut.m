function [I2] = im_cut(I,S1,S2)
%function [I2] = im_cut(I,S1,S2)
%Cut off the S1xS2 center part of image I, the result is I2.

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

%initialization:
    [size1,size2] = size(I);
    
%verification:
    if (size1 < S1) | (size2 < S2)
        error('Incorrect size(s) for cut.');
    end
    
%beforei,afteri (i=1,2):
    before1 = floor((size1-S1)/2);
    after1 = ceil((size1-S1)/2);
    before2 = floor((size2-S2)/2);
    after2 = ceil((size2-S2)/2);
    
I2 = I(before1+1:before1+S1,before2+1:before2+S2);
