function [] = ITE_add_to_path(ITE_code_dir)
%function [] = ITE_add_to_path(ITE_code_dir)
%function [] = ITE_add_to_path
%Adds the ITE code directory to the Matlab/Octave PATH. The function can be
%useful if you had installed ITE, exited from Matlab/Octave, and the ITE
%code directory is not on your the PATH in the newly opened Matlab/Octave session. In this case it is sufficient to
%call 'ITE_add_to_path(ITE_code_dir)' to be able to use ITE; there is no need to issue 'ITE_install(ITE_code_dir)' again.
%
%INPUT:
%   ITE_code_dir: directory containing the ITE package, e.g., 'C:\ITE\code'. When the function is called without arguments, ITE_code_dir is set to the current directory (ITE_code_dir = pwd).
%EXAMPLE:
%   The typical usage is to first 'cd' to the the 'code' directory in Matlab/Octave, and call 'ITE_add_to_path(pwd)', or just simply 'ITE_add_to_path'.

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

if nargin == 0 %the function was called without arguments, , ITE_code_dir is set to the current directory
    ITE_code_dir = pwd;
end

addpath(genpath(ITE_code_dir));