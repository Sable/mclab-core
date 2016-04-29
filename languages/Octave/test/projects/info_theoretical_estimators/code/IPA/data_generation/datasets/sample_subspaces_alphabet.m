function [e,de] = sample_subspaces_alphabet(symbols,num_of_comps,num_of_samples)
%function [e,de] = sample_subspaces_alphabet(symbols,num_of_comps,num_of_samples)
%Sampling from the 'ABC'/GreekABC'/'Aw', dataset; using the cell arra symbol set (symbols). number of subspaces: num_of_comps; number of samples: num_of_samples.
%
%OUTPUT:
%   e: e(:,t) is the t^th sample. size(e,2) = num_of_samples.
%   de: subspace dimensions.

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

max_num_of_comps = length(symbols);

if num_of_comps > max_num_of_comps 
    error(strcat('The number of components must be <=',num2str(max_num_of_comps),'.'));
else
    e = zeros(2*num_of_comps,num_of_samples);
    for k = 1 : num_of_comps %actual symbol we are generating (x,y) samples from
        %load the image of the actual symbol ('A'/'B'/...)
            FN = strcat(symbols{k},'.mat');
            load(FN,'I');
            %figure; imshow(I);
        %find the positions occupied by the letters:
            [x,y] = find(I); 
            len = size(x,1); %letter width=letter height
        %uniform sampling on the 'letters':
            indices = round(unifrnd(1,len,num_of_samples,1));
            %plot(y(indices),x(indices),'.');
            e_temp = [y(indices)'; x(indices)'];%samples from the actual letter
        %add the new samples to e:
            e(2*k-1:2*k,:) = e_temp;
    end
    de = 2 * ones(num_of_comps,1);
end
