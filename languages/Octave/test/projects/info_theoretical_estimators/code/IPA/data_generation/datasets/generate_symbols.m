function [symbols] = generate_symbols(data_type)
%function [symbols] = generate_symbols(data_type)
%Generates the symbol set(symbols) to a given alphabet data type ('ABC','GreekABC','Aw')

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

switch data_type
    case 'ABC'
        symbols = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'}; %English alphabet(26)
    case 'GreekABC'
        symbols = {'alpha','beta','gamma','delta','epsilon','zeta','eta',...
                   'theta','iota','kappa','lambda','mu','nu','xi','o','pi',...
                   'rho','sigma','tau','upsilon','phi','chi','psi','omega'};  %Greek alphabet(24)
    case 'Aw'
        symbols = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z',...
                   'alpha','beta','gamma','delta','epsilon','zeta','eta',...
                   'theta','iota','kappa','lambda','mu','nu','xi','o','pi',...
                   'rho','sigma','tau','upsilon','phi','chi','psi','omega'};  %English(26) + Greek alphabet(24)
    otherwise
        error('data type=?');
end