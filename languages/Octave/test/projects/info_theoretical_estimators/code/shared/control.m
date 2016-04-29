function [u] = control(control_code,P,m,Du,x,u_size)
%function [u] = control(control_code,P,m,Du,x,u_size)
%Compute control u, given:
%
%INPUT:
%   control_code: 'parameter-optimal' (D-optimal in terms of the parameters), 'noise-optimal' (D-optimal in terms of the noise), 'random' (uniform random control choice)
%   P: inverse of matrix K.
%   m: dimension of the previous Ls observation and Lu-1 control values (= Dx * Ls + Du * Lu).
%   Du: control dimension.
%   x: concatenation of the previous Ls observations and Lu-1 control values.
%   u_size: %size of the control; box constraint, i.e., |u_i| <= u_size.

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

switch lower(control_code)
    case 'parameter-optimal'
        IK = P;
        IK11 = IK(1:m-Du,1:m-Du);%%<->r_pre,u_pre
        IK12 = IK(1:m-Du,m-Du+1:end);
        IK21 = IK(m-Du+1:end,1:m-Du); 
        IK22 = IK(m-Du+1:end,m-Du+1:end);
        IK22 = (IK22+IK22')/2; % for numerical stability
        if working_environment_Matlab%Matlab
            u = quadprog(-IK22,-IK21*x,[],[],[],[],-u_size*ones(Du,1),u_size*ones(Du,1),rand(Du,1),optimset('Display','off'));
        else%Octave
            u = qp(rand(Du,1),-IK22,(-IK21*x),[],[],-u_size*ones(Du,1),u_size*ones(Du,1));
        end
     case 'noise-optimal'
        IK = P;
        IK11 = IK(1:m-Du,1:m-Du);%%<->r_pre,u_pre
        IK12 = IK(1:m-Du,m-Du+1:end);
        IK21 = IK(m-Du+1:end,1:m-Du); 
        IK22 = IK(m-Du+1:end,m-Du+1:end);
        IK22 = (IK22+IK22')/2; % for numerical stability
        if working_environment_Matlab%Matlab        
            u = quadprog(IK22,IK21*x,[],[],[],[],-u_size*ones(Du,1),u_size*ones(Du,1),rand(Du,1),optimset('Display','off'));
        else%Octave
            u = qp(rand(Du,1),IK22,(IK21*x),[],[],-u_size*ones(Du,1),u_size*ones(Du,1));
        end
      case 'random'
        u = u_size * (2*rand(Du,1)-1); %U[-u_size,u_size], coordinate-wise
end          

