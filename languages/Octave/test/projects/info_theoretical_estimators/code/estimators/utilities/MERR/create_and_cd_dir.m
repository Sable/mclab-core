function [dir_present] = create_and_cd_dir(dir_save)
%function [dir_present] = create_and_cd_dir(dir_save)
%Creates directory save_dir (if it does not exist), adds it to the PATH,
%saves the present working directory (pwd), cd-s to the save_dir, and returns the
%saved pwd.

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

if ~exist(dir_save)
    mkdir(dir_save);
    addpath(dir_save);    
end
dir_present = pwd; cd(dir_save);

