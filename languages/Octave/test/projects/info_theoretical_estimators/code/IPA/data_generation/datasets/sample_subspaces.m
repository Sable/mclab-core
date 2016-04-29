function [e,de] = sample_subspaces(data_type,num_of_comps,num_of_samples)
%function [e,de] = sample_subspaces(data_type,num_of_comps,num_of_samples)
%Generates independent subspaces of given data type (data_type), number of components (num_of_comps) and number of samples (num_of_samples).
%
%INPUT:
%   data_type:
%   1)if data_type is a string, it can be:
%      '3D-geom','Aw','ABC','GreekABC','mosaic','IFS','ikeda','lorenz','all-k-independent'(k>=2, e.g.,'all-3-independent'),
%      'multiD-geom'(D>=2, e.g., 'multi5-geom'), 'multiD1-...-DM-geom'
%      (Dm>=2, e.g., 'multi2-3-4-geom'), 'multiD-spherical'(D>=2, e.g.,
%      'multi4-spherical'), 'multiD1-...-DM-spherical' (Dm>=2, e.g., 'multi2-3-4-spherical').
%   2)if data_type is a cell array, than its elements belong to 1). The
%      corresponding numbers of components can be found in num_of_comps.
%OUTPUT: 
%   e: size(e,2) = num_of_samples, that is e(:,t) is the t^th sample; whitened (except for data_type = 'lorenz'; without loss of generality).
%   de: subspace dimensions.
%EXAMPLE:
%   [e,de] = sample_subspaces('3D-geom',6,1000);
%   [e,de] = sample_subspaces('Aw',50,1000);
%   [e,de] = sample_subspaces('ABC',26,1000);
%   [e,de] = sample_subspaces('GreekABC',24,1000);
%   [e,de] = sample_subspaces('mosaic',4,200*1000);
%   [e,de] = sample_subspaces('IFS',9,10000);
%   [e,de] = sample_subspaces('ikeda',2,1000);
%   [e,de] = sample_subspaces('lorenz',3,5000);
%   [e,de] = sample_subspaces('all-2-independent',4,1000);
%   [e,de] = sample_subspaces('multi3-geom',4,1000);
%   [e,de] = sample_subspaces('multi2-spherical',3,1000);
%   [e,de] = sample_subspaces('multi2-3-5-geom',3,1000);
%   [e,de] = sample_subspaces('multi2-3-5-spherical',3,1000);
%-----------
%  [e,de] = sample_subspaces({'ABC','3D-geom'},[2,3],1000); %combine datasets.

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

if strcmp(class(data_type),'char')%example: data_type = '3D-geom'
    switch data_type 
        case '3D-geom'
            [e,de] = sample_subspaces_3D_geom(num_of_comps,num_of_samples);
        case 'ABC'
            [e,de] = sample_subspaces_ABC(num_of_comps,num_of_samples);
        case 'GreekABC'
            [e,de] = sample_subspaces_GreekABC(num_of_comps,num_of_samples);
        case 'Aw'
            [e,de] = sample_subspaces_Aw(num_of_comps,num_of_samples);
        case 'mosaic'
            [e,de] = sample_subspaces_mosaic(num_of_comps,num_of_samples);
        case 'IFS'
            [e,de] = sample_subspaces_IFS(num_of_comps,num_of_samples);
        case 'ikeda'
            [e,de] = sample_subspaces_ikeda(num_of_comps,num_of_samples);
        case 'lorenz'
            [e,de] = sample_subspaces_lorenz(num_of_comps,num_of_samples);
        otherwise %sources with scalable subspaces (in terms of their dimensions): 'all-k-independent',... 
                  %Use it precisely, e.g., 'all-2-independent',
                  %'multi3-geom','multi2-3-geom', 'multi6-spherical', 'multi3-4-spherical'
            k = k_separate_all_k_independent(data_type); %test for 'all-k-independent'
            if ~isempty(k) %found a k value, which is supposed to be >=2.
                %disp('all-k-independent');
                [e,de] = sample_subspaces_all_k_independent(num_of_comps,num_of_samples,k);
            else
                de = de_separate_multiD_geom(data_type);%test for 'multiD-geom' and 'multiD1-D2-...-DM-geom'
                if length(de)==1 %'multiD-geom'
                    de = ones(num_of_comps,1) * de;
                    e = sample_subspaces_multiD_geom(num_of_samples,de);
                elseif length(de)>1 %'multiD1-D2-...-DM-geom'
                    if num_of_comps == length(de)
                        e = sample_subspaces_multiD_geom(num_of_samples,de);
                    else
                        error('The number of components is not compatible with the given data type.')
                    end
                else
                    de = de_separate_multiD_spherical(data_type);%test for 'multiD-spherical' and 'multiD1-D2-...-DM-spherical'
                    if length(de)==1%'multiD-spherical'
                        de = ones(num_of_comps,1) * de;
                        e = sample_subspaces_multiD_spherical(num_of_samples,de);
                    elseif length(de)>1 %'multiD1-D2-...-spherical'
                        if num_of_comps == length(de)
                            e = sample_subspaces_multiD_spherical(num_of_samples,de);
                        else
                            error('The number of components is not compatible with the given data type.')
                        end
                    else
                        error('data type=?');
                    end
                end
            end
    end
    disp(strcat('Source generation (',data_type,'): ready.'));
else%data_type is a cell array; 
    e = []; de = [];
    for k = 1 : length(data_type)%assumption: length(data_type)==length(num_of_comps)
        [e_temp,de_temp] = sample_subspaces(data_type{k},num_of_comps(k),num_of_samples);
        e = [e;e_temp];
        de = [de;de_temp];
    end
    %The subspaces are sorted in increasing order with respect to their dimensions -- without loss of generality:
        [de,per] = sort_subspaces_dimensions(de);
        e = e(per,:);
end
%h = plot_subspaces(s,data_type,'hidden subspaces');   
if strcmp(class(data_type),'char') && ~strcmp(data_type,'lorenz')%in case of data_type = 'lorenz' do not perform whitening
    e = whiten(e,size(e,1));%without any loss of generality; the search for A(=mixing matrix) and W(=demixing matrix) can be restricted to the orthogonal group.
end

%--------------------------------
function [k] = k_separate_all_k_independent(data_type)
%Takes the part between 'all-' and '-independent', e.g., data_type = 'all-2-independent' -> k=2.

st = data_type;
I_all = findstr(st,'all-'); 
I_independent = findstr(st,'-independent'); 
if (isempty(I_all)|isempty(I_independent))%could not find the surrounding words: ERROR
    k = [];
else
    st_k = st((I_all+4):(I_independent-1)); 
    k = str2num(st_k);
end
%--------------------------------
function [de] = de_separate_multiD_spherical(data_type)%test for 'multiD1-D2-...-DM-spherical'
%Takes the part between 'multi' and '-spherical', e.g., data_type = 'multi2-3-4-spherical' -> de=[2;3;4].

st = data_type;
I_multi = findstr(st,'multi'); 
I_spherical = findstr(st,'spherical'); 
if (isempty(I_multi)|isempty(I_spherical))%could not find the surrounding words: ERROR
    de = [];
else
    st_d = st((I_multi+5):(I_spherical-1)); %multi2-3-4-spherical ->'2-3-4'
    %'2-3-4' -> de=[2,3,4]
    I_minus = findstr(st_d,'-'); 
    de = zeros(length(I_minus),1);%preallocation    
    I_minus = [0,I_minus];
    for k = 2:length(I_minus)
        st_temp = st_d(I_minus(k-1)+1:I_minus(k)-1);
        de(k-1) = str2num(st_d(I_minus(k-1)+1:I_minus(k)-1));
    end
end
%---------------------------
function [de] = de_separate_multiD_geom(data_type)%test for 'multiD1-D2-...-DM-geom'
%Takes the part between 'multi' and '-geom', e.g., data_type = 'multi2-3-4-geom' -> de=[2;3;4].

st = data_type;
I_multi = findstr(st,'multi'); 
I_geom = findstr(st,'geom'); 
if (isempty(I_multi)|isempty(I_geom))%could not find the surrounding words: ERROR
    de = [];
else
    st_d = st((I_multi+5):(I_geom-1)); %multi2-3-4-geom ->'2-3-4'
    %'2-3-4' -> de=[2,3,4]
    I_minus = findstr(st_d,'-'); 
    de = zeros(length(I_minus),1);%preallocation    
    I_minus = [0,I_minus];
    for k = 2:length(I_minus)
        st_temp = st_d(I_minus(k-1)+1:I_minus(k)-1);
        de(k-1) = str2num(st_d(I_minus(k-1)+1:I_minus(k)-1));
    end
end
