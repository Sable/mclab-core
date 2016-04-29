function [e,de] = sample_subspaces_3D_geom(num_of_comps,num_of_samples)
%function [e,de] = sample_subspaces_3D_geom(num_of_comps,num_of_samples)
%Sampling from the '3D-geom' dataset; number of subspaces: num_of_comps; number of samples: num_of_samples.
%
%OUTPUT:
%   e: e(:,t) is the t^th sample. size(e,2) = num_of_samples.
%   de: subspace dimensions.
%EXAMPLE:
%   e = sample_subspaces_3D_geom(6,1000);
%
%REFERENCE: 
%   Barnabas Poczos, Andras Lorincz. Independent Subspace Analysis Using k-nearest Neighborhood Distances. International Conference on Artificial Neural Networks (ICANN), pp. 163-168, 2005.

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

max_num_of_comps = 6;

if num_of_comps > max_num_of_comps 
    error(strcat('The number of components must be <=',num2str(max_num_of_comps),'.'));
else
    e = zeros(3*num_of_comps,num_of_samples);%preallocation
    for k = 1 : num_of_comps
        switch k
            case 1
                e_temp = sig3D_1(num_of_samples);
            case 2
                e_temp = sig3D_2(num_of_samples);
            case 3
                e_temp = sig3D_3(num_of_samples);
            case 4
                e_temp = sig3D_4(num_of_samples);
            case 5
                e_temp = sig3D_5(num_of_samples);
            case 6
                e_temp = sig3D_6(num_of_samples);
        end
        e(3*k-2:3*k,:) = e_temp;
    end 
    de = ones(num_of_comps,1) * 3;
end

%------------------------------------------------------
function [s] = sig3D_1(num_of_samples)
    resolution = 10000;
    t = linspace(0,1,resolution);
    x1 = t;
    y1 = 0*ones(1,resolution);
    z1 = 0*ones(1,resolution);
 
    x2 = 0*ones(1,resolution);
    y2 = t;
    z2 = 0*ones(1,resolution);

    x3 = 0*ones(1,resolution);
    y3 = 0*ones(1,resolution);
    z3 = t;

    x4 = t;
    y4 = 0*ones(1,resolution);
    z4 = ones(1,resolution);

    x5 = 0*ones(1,resolution);
    y5 = t;
    z5 = ones(1,resolution);

    x6 = 0*ones(1,resolution);
    y6 = ones(1,resolution);
    z6 = t;

    x7 = ones(1,resolution);
    y7 = ones(1,resolution);
    z7 = t;

    x8 = ones(1,resolution);
    y8 = t;
    z8 = ones(1,resolution);

    x9 = t;
    y9 = ones(1,resolution);
    z9 = ones(1,resolution);

    x10 = ones(1,resolution);
    y10 = 0*ones(1,resolution);
    z10 = t;

    x11 = t;
    y11 = ones(1,resolution);
    z11 = 0*ones(1,resolution);

    x12 = ones(1,resolution);
    y12 = t;
    z12 = 0*ones(1,resolution);

     dat_mtx = [x1,x2,x3,x4,x5,x6,x7,x8,x9,x10,x11,x12;...
                y1,y2,y3,y4,y5,y6,y7,y8,y9,y10,y11,y12; ...
                z1,z2,z3,z4,z5,z6,z7,z8,z9,z10,z11,z12];
 
     ind = round(unifrnd(1,size(dat_mtx,2),1,num_of_samples));
     s(1:3,:) = dat_mtx(:,ind); 
     s(1:3,:) = -orth(randn(3))*s(1:3,:);
     s(1:3,:) = s(1:3,randperm(num_of_samples));
%---------------------------------------------------
function [s] = sig3D_2(num_of_samples)
    resolution = 10000;
    t = linspace(0,2*pi,resolution);
    x1 = cos(20*t/(2*pi));
    y1 = sin(20*t/(2*pi));
    z1 = t;
    dat_mtx = [x1;y1;z1];

    ind = round(unifrnd(1,size(dat_mtx,2),1,num_of_samples));
    s(1:3,:) = dat_mtx(:,ind);
    s(1:3,:) = -orth(randn(3))*s(1:3,:);
    s(1:3,:) = s(1:3,randperm(num_of_samples));
%------------------------------------------------
function [s] = sig3D_3(num_of_samples)
    resolution = 10000;
    t = linspace(-1,1,resolution);
    x1 = t;
    y1 = 0*ones(1,resolution);
    z1 = 0*ones(1,resolution);

    x2 = 0*ones(1,resolution);
    y2 = t;
    z2 = 0*ones(1,resolution);

    x3 = 0*ones(1,resolution);
    y3 = 0*ones(1,resolution);
    z3 = t;

    dat_mtx = [x1,x2,x3; y1,y2,y3; z1,z2,z3];

    ind = round(unifrnd(1,size(dat_mtx,2),1,num_of_samples));
    s(1:3,:) = dat_mtx(:,ind);
    s(1:3,:) = -orth(randn(3))*s(1:3,:);
    s(1:3,:) = s(1:3,randperm(num_of_samples));
%------------------------------------------------
function [s] = sig3D_4(num_of_samples)
    resolution = 10000;
    t = linspace(0,2*pi,resolution);
    x1 = cos(t);
    y1 = sin(t);
    z1 = 0*ones(1,resolution);
    x2 = sin(t);
    y2 = 0*ones(1,resolution);
    z2 = cos(t);
    dat_mtx = [x1,x2; y1,y2; z1,z2];

    ind = round(unifrnd(1,size(dat_mtx,2),1,num_of_samples));
    s(1:3,:) = dat_mtx(:,ind);
    s(1:3,:) = -orth(randn(3))*s(1:3,:);
    s(1:3,:) = s(1:3,randperm(num_of_samples));
%-----------------------------------------------
function [s] = sig3D_5(num_of_samples)
    resolution = 10000;
    t = linspace(0,2*pi,resolution);
    x1 = cos(t);
    y1 = sin(t);
    z1 = 0*ones(1,resolution);
    x2 = cos(t);
    y2 = sin(t);
    z2 = ones(1,resolution);

    x3 = 1/sqrt(2)*ones(1,resolution);
    y3 = 1/sqrt(2)*ones(1,resolution);
    z3 = t/(2*pi);

    x4 = -1/sqrt(2)*ones(1,resolution);
    y4 = 1/sqrt(2)*ones(1,resolution);
    z4 = t/(2*pi);

    x5 = 1/sqrt(2)*ones(1,resolution);
    y5 = -1/sqrt(2)*ones(1,resolution);
    z5 = t/(2*pi);

    x6 = -1/sqrt(2)*ones(1,resolution);
    y6 = -1/sqrt(2)*ones(1,resolution);
    z6 = t/(2*pi);

    dat_mtx = [x1,x2,x3,x4,x5,x6; y1,y2,y3,y4,y5,y6; z1,z2,z3,z4,z5,z6];

    ind = round(unifrnd(1,size(dat_mtx,2),1,num_of_samples));
    s(1:3,:)=dat_mtx(:,ind);
    s(1:3,:)=-orth(randn(3))*s(1:3,:);
    s(1:3,:)=s(1:3,randperm(num_of_samples));
%---------------------------------------------------
function [s] = sig3D_6(num_of_samples)
    v = exprnd(1.5,1,num_of_samples);
    s(1,:) = 3*abs(sin(2*pi*v)+6*cos(2*pi*v)); 
    s(2,:) = sin(2*pi*v)+4*cos(2*pi*v); 
    s(3,:) = 3*sin(2*pi*v)+6*cos(2*pi*v); 
    s(1:3,:) = -orth(randn(3))*s(1:3,:);
    s(1:3,:) = s(1:3,randperm(num_of_samples));
