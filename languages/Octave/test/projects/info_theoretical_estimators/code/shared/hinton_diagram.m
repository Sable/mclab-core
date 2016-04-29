function [h] = hinton_diagram(M,fig_name)
%function [h] = hinton_diagram(M,fig_name)
%Plots matrix M in Hinton diagram form: the sizes of the squares are
%proportional to the magnitudes; color indicates sign (white=positive, black=negative); and sets the name of the resulting figure (fig_name). 
%
%OUTPUT:
%   h: handle of the figure;
%EXAMPLE:
%   h = hinton_diagram(randn(5),'');

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

%parameters:
    gap = 0.01;
    
%computation of the patch ingredients:
    s = 0.45 / max(max(sqrt(abs(M))));%scaling factor

    sizeM1 = size(M,1);
    sizeM2 = size(M,2);

    %magnitude:
        m = sqrt(abs(M)) * s;
        m = m(:).';%magnitude
    %x,y:
        [K1,K2] = meshgrid([1:sizeM2],[1:sizeM1]);%center: (k1,k2)
        k1 = K1(:).';
        k2 = K2(:).';
        x = [k1-m;k1+m;k1+m;k1-m];
        y = [-k2-m;-k2-m;-k2+m;-k2+m];
    %col(or):
        if all(all(real(M)==M))%real
            col = 0.5*(sign(M(:)) + 1).'; %-1->0,+1->1;
        else%complex
            col = 0.5*(real(sign(M(:))) + 1).';
        end
        
%plot:
    h = figure('Color', [0.5;0.5;0.5], 'Colormap', [0,0,0; 1,1,1], 'Name',fig_name);
    set(gca, 'Position', [0+gap 0+gap 1-2*gap 1-2*gap]);
    colormap(gray);
    if working_environment_Matlab%Matlab
        patch(x,y,col,'Edgecolor','none');        
    else %Octave
        vertices = [x(:),y(:)]; faces = reshape(1:prod(size(x)),4,size(x,2)).';
        patch('Faces',faces,'Vertices',vertices,'FaceVertexCData', col');
    end
    axis equal;
    axis off;
