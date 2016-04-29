function [FN] = filename_of_HRenyi_constant(d,co)
%function [FN] = filename_of_HRenyi_constant(d,co)
%Returns the filename (FN) of the precomputed additive constants for Renyi entropy estimators 'Renyi_kNN_1tok', 'Renyi_kNN_S', 'Renyi_MST' and 'Renyi_GSF'. See 'estimate_HRenyi_constant.m'.
%
%INPUT:
%   co: cost object.
%   d: dimension.

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

switch co.name
    case 'Renyi_kNN_1tok'
        FN = strcat('constant_H',co.name,'_d',num2str(d),'_k',num2str(co.k),'_alpha',num2str(co.alpha),'.mat');%co.kNNmethod (and its parameters) are not saved to the filename
    case 'Renyi_kNN_S'
        FN = strcat('constant_H',co.name,'_d',num2str(d),'_k',Sstr(co.k),'_alpha',num2str(co.alpha),'.mat');%co.kNNmethod (and its parameters) are not saved to the filename
    case 'Renyi_MST'
        FN = strcat('constant_H',co.name,'_d',num2str(d),'_alpha',num2str(co.alpha),'.mat'); %co.MSTmethod is not saved to the filename
    case 'Renyi_GSF'
        FN = strcat('constant_H',co.name,'_d',num2str(d),'_k',num2str(co.k),'_alpha',num2str(co.alpha),'.mat');%co.kNNmethod (and its parameters), co.GSFmethod are not saved to the filename
    otherwise
        error('cost name=?');
end

function [str] = Sstr(ks)
%Returns the string version (str) of the k values given in vector 'ks'. Example: ks = [1,2,4] -> str='1_2_4'.

L = length(ks);
str = '';
for j = 1 : L
    str = strcat(str,num2str(ks(j)));
    if j<L
        str = strcat(str,'_');
    end
end
