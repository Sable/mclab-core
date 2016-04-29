function [co] = I_initialization(cost_name,mult,post_init)
%function [co] = I_initialization(cost_name,mult)
%function [co] = I_initialization(cost_name,mult,post_init)
%Initialization of a I (mutual information) estimator. The estimator is treated as a cost object (co). 
%
%INPUT:
%   cost_name: cost name.
%   mult: is a multiplicative constant relevant (needed) in the estimation; '=1' means yes (='exact' estimation), '=0' no (=estimation up to 'proportionality').
%   post_init: {field_name1,field_value1,field_name2,field_value2,...}; cell array containing the names and the values of the cost object fields that are to be used (instead of their default values). For further details, see 'post_initialization.m'.
%OUTPUT:
%   co: cost object (structure).

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

%Here, we make use of the naming convention 'I<name>_initialization':
   if nargin == 2
   	   eval(['co=I',cost_name,'_initialization(mult);']); %example: co = IGV_initialization(mult);
   elseif nargin == 3
   	   eval(['co=I',cost_name,'_initialization(mult,post_init);']); %example: co = IGV_initialization(mult,post_init);
   else
       error('Wrong number of arguments.');
   end
   %attach a function handle to the cost object; it will be used in 'I_estimation.m':
       co.function_handle = eval(strcat('@I',co.name,'_estimation')); %example: co.function_handle = @IGV_estimation
   %display the initialized cost object:
       co, 
   
disp('I initialization: ready.');
