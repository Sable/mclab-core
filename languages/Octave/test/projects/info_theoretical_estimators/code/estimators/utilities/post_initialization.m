function [co] = post_initialization(co,post_init)
%Post initialization of cost object co with given (name,value) pairs in the
%cell array post_init = {field_name1,field_value1,field_name2,field_value2,...}. Note: this
%solution makes it possible for the user to override default field values;
%it is also used in meta estimators, where certain field values have to be
%inherited.
%
%EXAMPLE:
%   co = post_initialization(co,{'alpha',0.9});
%   co = post_initialization(co,{'alpha',0.9,'k',4});
%   co = post_initialization(co,{'kNNmethod','ANN','k',3,'epsi',0});

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

%verification:
    if ~all_even(length(post_init))%|post_init|==even
        error('The length of the post initializating cell array must be even: it defines (name, value) pairs.')
    end
    
for k = 1 : length(post_init)/2
    field_name = post_init{2*k-1};
    field_value = post_init{2*k};
    co.(field_name) = field_value;
end
